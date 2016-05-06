/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date 2010-3-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class InsertPage extends AbstractDPagePlugin {
	private static final String PARAM_PAGENAME = "page";
	private static final String PARAM_STYLE = "style";
	private static final String PARAM_MAXLENGTH = "maxlength";
	private static final String PARAM_CLASS = "class";
	private static final String PARAM_DEFAULT = "default";

	private static final String DEFAULT_STYLE = "";

	private String ATTR_RECURSE = InsertPage.class.getName()+".recurseCheck";
	public String execute(VWBContext context, Map<String,String> params)
			throws PluginException {

		StringBuffer res = new StringBuffer();

		String clazz = (String) params.get(PARAM_CLASS);
		String includedPage = (String) params.get(PARAM_PAGENAME);
		String style = (String) params.get(PARAM_STYLE);
		String defaultstr = (String) params.get(PARAM_DEFAULT);
		int maxlen = TextUtil.parseIntParameter(
				(String) params.get(PARAM_MAXLENGTH), -1);

		if (style == null)
			style = DEFAULT_STYLE;

		if (maxlen == -1)
			maxlen = Integer.MAX_VALUE;

		if (includedPage != null) {
			int iIncludedPage = 0;
			try {
				iIncludedPage = Integer.parseInt(includedPage);
			} catch (Throwable e) {
				return "";
			}
			DPage page = VWBContext
					.getContainer()
					.getDpageService()
					.getLatestDpageByResourceId(getSite().getId(),
							iIncludedPage);
			if (page != null) {
				//
				// Check for recursivity
				//

				@SuppressWarnings("unchecked")
				List<String> previousIncludes = (List<String>) context
						.getVariable(ATTR_RECURSE);

				if (previousIncludes != null) {
					if (previousIncludes.contains(includedPage)) {
						return "<span class=\"error\">Error: Circular reference - you can't include a page in itself!</span>";
					}
				} else {
					previousIncludes = new ArrayList<String>();
				}
				previousIncludes.add(includedPage);
				context.setVariable(ATTR_RECURSE, previousIncludes);

				//
				// Check for permissions
				//

				AuthorizationService authorizationService = VWBContext.getContainer().getAuthorizationService();

				if (!authorizationService.checkPermission(context.getSite()
						.getId(), VWBSession.findSession(context
						.getHttpRequest()), PermissionFactory
						.getPagePermission(page, "view"))) {
					res.append("<span class=\"error\">You do not have permission to view this included page.</span>");
					return res.toString();
				}

				String pageData = page.getContent();
				String moreLink = "";

				String contextPath = context.getHttpRequest().getContextPath();
				if (pageData.length() > maxlen) {
					pageData = pageData.substring(0, maxlen) + " ...";
					moreLink = "<p><a href=\"" + contextPath + "/page"
							+ includedPage + "\">More...</a></p>";
				}

				res.append("<div style=\"" + style + "\""
						+ (clazz != null ? " class=\"" + clazz + "\"" : "")
						+ ">");
				RenderingService renderS = VWBContext.getContainer().getRenderingService();
				res.append(renderS.getHTML(context, pageData));
				res.append(moreLink);
				res.append("</div>");

				//
				// Remove the name from the stack; we're now done with this.
				//
				previousIncludes.remove(includedPage);
				context.setVariable(ATTR_RECURSE, previousIncludes);
			} else {
				if (defaultstr != null) {
					res.append(defaultstr);
				} else {
					ViewPortService vs = VWBContainerImpl.findContainer()
							.getViewPortService();
					ViewPort vp = vs.getViewPort(context.getSite().getId(),
							iIncludedPage);
					res.append("There is no page called '" + includedPage
							+ "'.  Would you like to ");
					if (vp != null && Resource.TYPE_DPAGE.equals(vp.getType())) {

						res.append("<a href=\""
								+ context.getEditURL(iIncludedPage)
								+ "\">create it?</a>");
					}
				}
			}
		} else {
			res.append("<span class=\"error\">");
			res.append("You have to define a page!");
			res.append("</span>");
		}

		return res.toString();
	}

}
