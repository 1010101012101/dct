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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.duckling.vwb.ui.form.PropertiesForm;

/**
 * Introduction Here.
 * 
 * @date 2010-5-6
 * @author dylan
 */
public class VWBPropertiesAction extends BaseDispatchAction {

	private static String DUCKLING_DOMAIN = "duckling.domain";

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "jsp/vwbproperties/vwbppt.jsp");
	}

	private void saveFavIcon(ActionForm form, int siteId) throws IOException {
		PropertiesForm pForm = (PropertiesForm) form;
		FormFile iconFile = pForm.getIcon();
		if (iconFile.getFileSize() > 0) {
			InputStream in = iconFile.getInputStream();
			try {
				VWBContainerImpl.findContainer().getFaviconService()
						.save(siteId, in, iconFile.getFileSize());
			} finally {
				in.close();
			}
		}
	}

	/*
	 * 增加数据时候检查数据唯一性
	 */
	public ActionForward check(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String domainname = request.getParameter("domainname");
		if (VWBContext.getContainer().getDomainService().isDomainUsed(domainname)){
			response.getOutputStream().print("true");
		}else{
			response.getOutputStream().print("false");
		}
		return null;
	}

	/*
	 * 增加数据
	 */
	public ActionForward insert(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);
		String strName = request.getParameter("strName");
		String strValue = request.getParameter("strValue");
		VWBContext.getContainer().getSiteConfig()
				.setProperty(context.getSite().getId(), strName, strValue);
		return unspecified(mapping, form, request, response);
	}

	/*
	 * 默认方法 显示内容用
	 * 
	 * @see
	 * org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts
	 * .action.ActionMapping, org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(response)) {
			// domain name 部分
			VWBContainer container = VWBContext.getContainer();
			Map<String, String> domainmap = container.getSiteConfig()
					.getPropertyStartWith(context.getSiteId(),
							"duckling.domain");
			List<Entry<String, String>> domainList = new ArrayList<Entry<String, String>>();
			Iterator<Entry<String, String>> it = domainmap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String tempstr = entry.getKey();
				if (!DUCKLING_DOMAIN.endsWith(tempstr))
					domainList.add(entry);
			}

			boolean isHasDBEmail = container.getSiteConfig().isHasDBEmail(
					context.getSite().getId());
			Collections.sort(domainList,new Comparator<Entry<String, String>>(){
				@Override
				public int compare(Entry<String, String> o1,
						Entry<String, String> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
				
			});
			request.setAttribute("domainList", domainList);
			request.setAttribute("domainListSize", domainList.size());
			// end
			// 全部
			Map<String, String> allmap = container.getSiteConfig()
					.getPropertyStartWith(context.getSiteId(), "");
			request.setAttribute("allmap", allmap);
			request.setAttribute("isHasDBEmail", isHasDBEmail);
		} else {
			return null;
		}
		return doLayout(context);
	}

	/*
	 * 修改数据
	 */
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(response)) {
			Map<String, String[]> mReq = request.getParameterMap();
			Iterator<Entry<String, String[]>> it = mReq.entrySet().iterator();
			String defaultemailype = request.getParameter("defaultemailype");
			String listentype = request.getParameter("listentype");

			VWBContainer container = VWBContext.getContainer();
			ISiteConfig siteConfig = container.getSiteConfig();
			int siteId = context.getSiteId();
			if ("DomainConfig".equals(listentype)) {
				ArrayList<String> domains =new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();
				keys.addAll(mReq.keySet());
				Collections.sort(keys);
				for (String strName:keys){
					String strValue=request.getParameter(strName);
					if (!"type".equals(strName) && !"".equals(strValue)
							&& !"defaultemailype".equals(strName)
							&& !"listentype".equals(strName)) {
						domains.add(strValue);
					}
				}
				if (!container.getDomainService().updateSiteDomains(siteId, domains.toArray(new String[domains.size()]))){
					request.setAttribute("error", "duckling.domain.conflict");
				}
			} else {
				while (it.hasNext()) {
					Map.Entry<String, String[]> entry = it.next();
					String strName = (String) entry.getKey();
					String strValue = (String) request.getParameter(strName);
					if ("true".equals(defaultemailype)) {
						if (!"type".equals(strName)
								|| !"defaultemailype".equals(strName)
								|| !"listentype".equals(strName))
							siteConfig.removeProperty(
									siteId, strName);
					} else {
						if (!"type".equals(strName) && !"".equals(strValue)
								&& !"defaultemailype".equals(strName)
								&& !"listentype".equals(strName)) // prop.put(strName,
																	// strValue);
							siteConfig.setProperty(siteId,
									strName, strValue);
					}

				}
				if ("SysConfig".equals(listentype)) {
					String duckling_defaultpage = request
							.getParameter("duckling.defaultpage");
					if (duckling_defaultpage == null
							|| "".equals(duckling_defaultpage)) {
						siteConfig.setProperty(siteId,
								"duckling.defaultpage", "1");
					} else {
						try {
							int id = Integer.valueOf(duckling_defaultpage);
							siteConfig.setProperty(siteId,
									"duckling.defaultpage",
									new Integer(id).toString());
						} catch (Exception e) {
							siteConfig.setProperty(siteId,
									"duckling.defaultpage", "1");
						}
					}

					String duckling_allowanonymous = request
							.getParameter("duckling.allowanonymous");
					if ("true".equals(duckling_allowanonymous)) {
						container.getUserService()
								.addAnonymousToVO(context.getVO());
					} else {
						container.getUserService()
								.removeAnonymousFromVO(context.getVO());
					}

					saveFavIcon(form, siteId);
				}
			}

		} else {
			return null;
		}
		return unspecified(mapping, form, request, response);
	}

}