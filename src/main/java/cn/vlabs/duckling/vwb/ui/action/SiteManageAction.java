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

package cn.vlabs.duckling.vwb.ui.action;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.SiteItem;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date May 6, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class SiteManageAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null || name.equals("")) {
			name = "init";
			request.getSession().setAttribute("siteManageNotify", null);
		}
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);

		if (context.hasAccess(response)) {
			return super.dispatchMethod(mapping, form, request, response, name);
		} else
			return null;
	}

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		Collection<SiteItem> sites = VWBContext.getContainer().getAllSiteItems();
		request.setAttribute("allSites", sites);
		return doManageLayout(context);
	}

	public ActionForward createTempate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		int templateSiteId = Integer.parseInt(request
				.getParameter("templateSiteId"));
		String templateName = request.getParameter("templateName");
		if (templateName != null && templateName.trim().length() > 0) {
			SiteMetaInfo site = VWBContext.getContainer().getSite(templateSiteId);
			if (site != null) {
				VWBContext.getContainer().createTemplate(site.getId(),
						templateName.trim());
				request.getSession().setAttribute(
						"siteManageNotify",
						"站点" + String.valueOf(templateSiteId) + "的新模板"
								+ templateName + "已经保存");
			}
		}
		return this.init(mapping, form, request, response);
	}

	public ActionForward destroySite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String siteIds = request.getParameter("destroySiteId");

		if (siteIds != null) {
			siteIds = siteIds.trim();
			String[] siteIdsArr = siteIds.split(",");

			for (int i = 0; i < siteIdsArr.length; i++) {
				int destroySiteId = Integer.parseInt(siteIdsArr[i]);
				if (destroySiteId != 1) {
					VWBContext.getContainer().destroySite(destroySiteId);
				}
			}
			request.getSession().setAttribute("siteManageNotify",
					"站点" + siteIds + "已经被销毁.");
		}

		return this.init(mapping, form, request, response);
	}

	public ActionForward deactivateSite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String siteIds = request.getParameter("deactivateSiteId").trim();
		String[] siteIdsArr = siteIds.split(",");

		for (int i = 0; i < siteIdsArr.length; i++) {
			int deactivateSiteId = Integer.parseInt(siteIdsArr[i]);
			if (deactivateSiteId != 1) {
				VWBContainer container = VWBContext.getContainer();
				SiteMetaInfo smi = container
						.getSiteManagerService().getSiteInfo(deactivateSiteId);
				smi.setState(SiteState.HANGUP);
				container.getSiteManagerService()
						.updateSiteMeta(smi);
			}
		}

		request.getSession().setAttribute("siteManageNotify",
				"站点" + siteIds + "已经冻结.");
		return this.init(mapping, form, request, response);
	}

	public ActionForward activateSite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);

		String siteIds = request.getParameter("activateSiteId").trim();
		String[] siteIdsArr = siteIds.split(",");

		for (int i = 0; i < siteIdsArr.length; i++) {
			activeSite(context, Integer.parseInt(siteIdsArr[i]));
		}

		request.getSession().setAttribute("siteManageNotify",
				"站点" + siteIds + "已经激活.");
		return this.init(mapping, form, request, response);
	}

	private void activeSite(VWBContext context, int siteId) {
		VWBContainer container = VWBContext.getContainer();
		SiteMetaInfo smi = container.getSiteManagerService()
				.getSiteInfo(siteId);
		smi.setState(SiteState.WORK);
		container.getSiteManagerService().updateSiteMeta(smi);
	}

	public ActionForward dumpDomain(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		int siteId = Integer.parseInt(request.getParameter("dumpDomainSiteId"));
		VWBContainer container = VWBContext.getContainer();
		SiteMetaInfo site = container.getSite(siteId);
		if (site == null) {
			site = container.getInactiveSite(siteId);
		}
		if (site != null) {
			container.getDomainService().updateSiteDomains(siteId, new String[]{});
			request.getSession().setAttribute("siteManageNotify",
					"站点" + String.valueOf(siteId) + "的所有域名已被清空.");
		}
		return this.init(mapping, form, request, response);
	}

	private ActionForward doManageLayout(VWBContext context) {
		return layout(context, "/jsp/SiteManage.jsp");
	}

}
