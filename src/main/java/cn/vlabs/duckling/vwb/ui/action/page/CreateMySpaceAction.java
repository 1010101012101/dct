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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;
import cn.vlabs.duckling.vwb.service.myspace.MySpaceService;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;

/**
 * Introduction Here.
 * 
 * @date 2010-3-25
 * @author Fred Zhang (fred@cnic.cn)
 */
public class CreateMySpaceAction extends BaseAction {
	public ActionForward execute(ActionMapping actionmapping,
			ActionForm actionform, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (VWBSession.findSession(request).isAuthenticated()) {
			String useremail = request.getParameter("user");
			VWBContext context = VWBContext.createContext(request, VWBContext.EDIT);
			MySpaceService sm = VWBContainerImpl.findContainer().getMySpaceService();
			MySpace main = new MySpace();
			UserPrincipal user = (UserPrincipal) VWBSession
					.findSession(request).getCurrentUser();
			main.setCreator(user.getFullName() + "(" + user.getName() + ")");
			main.setUser(useremail);
			main.setSiteId(context.getSiteId());
			sm.createMySpace(main);
			response.sendRedirect(context.getEditURL(main.getResourceId()));
		}
		return null;
	}
}