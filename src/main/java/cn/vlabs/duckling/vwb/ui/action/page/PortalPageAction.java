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

package cn.vlabs.duckling.vwb.ui.action.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.driver.portlets.PageAdminPortlet.AppPortlet;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;

/**
 * Introduction Here.
 * 
 * @date 2010-4-16
 * @author Fred Zhang (fred@cnic.cn)
 */
public class PortalPageAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null)
			name = "show";
		return super.dispatchMethod(mapping, form, request, response, name);
	}

	public ActionForward show(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				PortalCommand.CONFIG, res);
		if (context.hasAccess(p_response)) {
			p_request.setAttribute("portalResource", res);
			PageConfig page = (PageConfig) res;
			Iterator<?> pids = page.getPortletIds().iterator();
			List<PortletItem> portlets = new ArrayList<PortletItem>();
			while (pids.hasNext()) {
				String pid = pids.next().toString();
				String name = PortletWindowConfig.parsePortletName(pid);
				portlets.add(new PortletItem(pid, name));
			}
			p_request.setAttribute("portal_portlets", portlets);
			return this.layout(context, "/jsp/page/portalsetting.jsp");
		}
		return null;
	}

	public ActionForward save(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException, ViewPortNotExistException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				PortalCommand.CONFIG, res);
		if (context.hasAccess(p_response)) {
			String[] pageportlets = p_request
					.getParameterValues("p_pagePortlets");

			AppPortlet[] portlets = null;
			if (pageportlets != null) {
				portlets = new AppPortlet[pageportlets.length];
				for (int i = 0; i < pageportlets.length; i++) {
					portlets[i] = AppPortlet.valueOf(pageportlets[i]);
				}
			}
			PageConfig pageConfig = (PageConfig) res;
			if (pageConfig != null) {
				PageConfig newone = pageConfig.copyWithOutPortlets();
				if (portlets != null) {
					for (AppPortlet portlet : portlets) {
						newone.addPortlet(portlet.context, portlet.portlet);
					}
				}
				VWBContext.getContainer().getPortalPageService().updatePage(context.getSite().getId(),newone);
			}
			ViewPort dpagevp = VWBContext.getContainer().getViewPortService().getViewPort(context.getSite().getId(),
					res.getResourceId());
			dpagevp.setTitle(p_request.getParameter("portaltitle"));
			VWBContext.getContainer().getViewPortService()
					.updateViewPort(context.getSite().getId(), dpagevp);
			p_response.sendRedirect(p_request.getContextPath() + "/page/"
					+ res.getResourceId());
			return null;
		}
		return null;
	}

}
