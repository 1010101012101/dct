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
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

import cn.vlabs.duckling.api.umt.rmi.user.UMTUser;
import cn.vlabs.duckling.api.umt.rmi.user.UserService;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.ui.UserNameUtil;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;

/**
 * @date 2010-11-30
 * @author Fred Zhang (fred@cnic.cn)
 */
public class RegistAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null || name.equals("")) {
			name = "viewRegister";
		}
		return super.dispatchMethod(mapping, form, request, response, name);
	}

	public ActionForward viewRegister(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.createContext(request, VWBContext.NONE);
		if (context.getVWBSession().isAuthenticated()) {
			UserRegisterState userState = new UserRegisterState();
			userState.setName(((UserPrincipal) context.getCurrentUser())
					.getName());
			userState.setName(((UserPrincipal) context.getCurrentUser())
					.getFullName());
			for (Principal p : context.getPrincipals()) {
				if (p instanceof GroupPrincipal) {
					userState.setCurrentVO(true);
					break;
				}
			}
			if (!userState.isCurrentVO()) {

				if (VWBContext
						.getContainer()
						.getUserService()
						.isApplyInVo(context.getVO(),
								context.getCurrentUser().getName())) {
					userState.setPending(true);
				}
			}
			request.setAttribute("userState", userState);
		}
		return layout(context, "/jsp/register.jsp");
	}

	public ActionForward saveRegister(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (validate(request)) {
			VWBContext context = VWBContext.createContext(request,
					VWBContext.NONE);
			String voName = context.getVO();
			String userName = request.getParameter("email");
			String displayName = request.getParameter("trueName");
			String password = request.getParameter("pass");
			VWBContainerImpl.findContainer().getUserService()
					.applyUserToVo(voName, userName, displayName, password);
			List<java.security.Principal> prins = VWBContainerImpl
					.findContainer().getUserService()
					.getUserPrincipal(userName, context.getVO());
			try {
				LoginAction.saveToPortal(request, prins);
				Subject subject = LoginAction.convertPrincipal(prins);
				VWBSession vwbsession = VWBSession.findSession(request);
				vwbsession.setSubject(VWBSession.AUTHENTICATED, subject);
				VWBContext.getContainer().getAuthenticationService().login(context.getSiteId(),request);
				log.error("regist sucessfully");
			} catch (VWBException e) {
				e.printStackTrace();
			}
		}
		return this.viewRegister(mapping, form, request, response);
	}

	private boolean validate(HttpServletRequest request) {
		String userName = request.getParameter("email");

		// TODO 后台验证
		return UserNameUtil.verifyEmail(userName);
	}

	public ActionForward applayRegister(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.createContext(request, VWBContext.NONE);
		if (context.getVWBSession().isAuthenticated()) {
			String umtURL = this.getUMTServiceURL();
			UserService umtUserService = new UserService(umtURL);
			UMTUser user = umtUserService.getUMTUser(context.getCurrentUser()
					.getName());
			VWBContainerImpl
					.findContainer()
					.getUserService()
					.applyUserToVo(context.getVO(), user.getUsername(),
							user.getTruename(), "");
			LoginAction loginAction = LoginAction.createLoginAction(request,
					response);

			String localURL = loginAction.getSaveProfileURL();
			String redirectURL = loginAction.makeSSOLoginURL(localURL);
			response.sendRedirect(redirectURL);
			return null;
		} else {
			return this.viewRegister(mapping, form, request, response);
		}
	}

	private String getUMTServiceURL() {
		String umtURL = VWBContainerImpl.findContainer().getConfig()
				.getProperty("duckling.umt.service.url");
		if (umtURL == null || umtURL.trim().equals("")) {
			umtURL = VWBContainerImpl.findContainer().getConfig()
					.getProperty("duckling.umt.site");
			if (!umtURL.endsWith("/")) {
				umtURL = umtURL + "/";
			}
			umtURL = umtURL + "ServiceServlet";
		}
		return umtURL;
	}

	public ActionForward validateDupRegister(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String umtURL = this.getUMTServiceURL();
		UserService umtUserService = new UserService(umtURL);
		JSONObject obj = new JSONObject();
		String email = request.getParameter("email");

		if (umtUserService.isExist(email)) {
			obj.put("result", "userExists");
		} else {
			obj.put("result", "ok");
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(obj.toString());
		return null;
	}

}
