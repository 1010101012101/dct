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
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.springframework.beans.BeansException;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.InvalidDPageDtoException;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;

/**
 * Introduction Here.
 * 
 * @date 2010-3-12
 * @author Fred Zhang (fred@cnic.cn)
 */
public class NewPageAction extends BaseDispatchAction {
	public ActionForward createNewPage(ActionMapping p_mapping,
			ActionForm p_form, HttpServletRequest p_request,
			HttpServletResponse p_response) throws IOException, BeansException,
			InvalidDPageDtoException {
		final DynaActionForm form = (DynaActionForm) p_form;
		if (VWBSession.findSession(p_request).isAuthenticated()) {
			String parentPage = form.getString("parentPage");
			String newPage = form.getString("newPage");
			UserPrincipal principal = (UserPrincipal) VWBSession.findSession(
					p_request).getCurrentUser();
			String user = principal.getFullName() + "(" + principal.getName()
					+ ")";
			if (newPage != null && !newPage.trim().equals("")) {
				VWBContext context = VWBContext.createContext(p_request, VWBContext.EDIT);
				ViewPortService vs = VWBContainerImpl.findContainer().getViewPortService();
				ViewPort vp = new ViewPort();
				vp.setTitle(newPage.trim());
				vp.setCreator(user);
				vp.setType(Resource.TYPE_DPAGE);
				Date time = Calendar.getInstance().getTime();
				vp.setCreateTime(time);
				if (parentPage != null && !parentPage.trim().equals("")) {
					try {
						vp.setParent(Integer.parseInt(parentPage));
					} catch (Throwable e) {
						log.error("parese parentid error :" + parentPage);
					}
				}
				vp.setSiteId(context.getSiteId());
				int resourceid = vs.createViewPort(context.getSiteId(),vp);
				DPage page = new DPage();
				page.setTitle(newPage);
				page.setResourceId(resourceid);
				page.setAuthor(user);
				page.setTime(time);
				page.setContent("");
				page.setSiteId(context.getSiteId());
				VWBContainerImpl.findContainer().getDpageService().createDpage(page);
				p_response.sendRedirect(context.getEditURL(resourceid));
			}
		}
		return null;
	}
}
