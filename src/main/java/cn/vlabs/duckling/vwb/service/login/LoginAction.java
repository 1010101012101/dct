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

package cn.vlabs.duckling.vwb.service.login;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.portlet.ActionResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.vwb.LoginContext;
import cn.vlabs.vwb.driver.internal.SiteSessionImpl;
import cn.vlabs.vwb.driver.services.container.SiteEnviromentServiceImpl;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 11, 2010 10:19:19 PM
 */
public abstract class LoginAction {
	protected static final Logger log = Logger.getLogger(LoginAction.class);

	public static Subject convertPrincipal(
			Collection<java.security.Principal> prins)
			throws UnkownCredentialException {
		if (prins != null && prins.size() == 0) {
			return null;
		}

		ArrayList<Principal> result = new ArrayList<Principal>();
		Principal newprin = null;
		Principal user = null;
		for (java.security.Principal prin : prins) {
			newprin = null;
			if (prin instanceof cn.vlabs.commons.principal.UserPrincipal) {
				cn.vlabs.commons.principal.UserPrincipal oldu = (cn.vlabs.commons.principal.UserPrincipal) prin;
				newprin = new UserPrincipal(oldu.getName(),
						oldu.getDisplayName(), UserPrincipal.LOGIN_NAME);
				user = newprin;
			}
			if (prin instanceof cn.vlabs.commons.principal.GroupPrincipal) {
				cn.vlabs.commons.principal.GroupPrincipal oldg = (cn.vlabs.commons.principal.GroupPrincipal) prin;
				newprin = new GroupPrincipal(oldg.getName());
			}
			if (prin instanceof cn.vlabs.commons.principal.RolePrincipal) {
				cn.vlabs.commons.principal.RolePrincipal oldr = (cn.vlabs.commons.principal.RolePrincipal) prin;
				String roleName = oldr.getGroupName() + "."
						+ oldr.getShortName();
				newprin = new Role(roleName);
			}
			if (newprin != null)
				result.add(newprin);
		}
		result.add(Role.AUTHENTICATED);
		result.add(Role.ALL);
		if (user == null)
			throw new UnkownCredentialException("User Principal not found");
		return new Subject(user, result);
	}

	public static LoginAction createLoginAction(HttpServletRequest request,
			HttpServletResponse response) {
		return new UMT2LoginAction(request, response);
	}

	public static String getRegisterURL(VWBContext context) {
		String registerFlag = context.getProperty("duckling.register.type");
		String registerURL = null;
		if (registerFlag != null && "new".equals(registerFlag.trim())) {
			registerURL = context.getURL(VWBContext.VIEW, "5029", "", true);
		} else {
			registerURL = context.getProperty("duckling.umt.link.regist")
					+ "&voName=" + context.getVO();
		}
		return registerURL;
	}

	public static void saveToPortal(HttpServletRequest req,
			Collection<java.security.Principal> prs_col) {
		Principal[] prins = prs_col.toArray(new Principal[prs_col.size()]);
		SiteSessionImpl siteSession = SiteEnviromentServiceImpl
				.getSiteSession(req);
		siteSession.setPrincipals(prins);
	}

	private VWBContext vwbcontext;

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	public LoginAction(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	private boolean isLocalLogin(LoginContext login) {
		return login != null && login.getUserName() != null;
	}

	private void parseAndSave() throws UnkownCredentialException {
		Collection<Principal> principals = parseCredential();
		if (principals == null || principals.size() == 0) {
			log.info("Failed to authenticate user.");
			getVwbcontext().getVWBSession().setStatus(VWBSession.ANONYMOUS);
		} else {
			getVwbcontext().getVWBSession().setPrincipals(principals);
			updateOnlineUser(getVwbcontext().getSiteId());
			log.debug("Successfully authenticated user.");
		}
	}

	private String calcReturnUrl() {
		String returnUrl = (String) request.getSession().getAttribute(
				Attributes.REQUEST_URL);
		if (returnUrl == null) {
			returnUrl = request.getParameter("returnUrl");
		}
		if (returnUrl == null) {
			returnUrl = request.getHeader("Referer");
		}
		if (returnUrl == null) {
			returnUrl = getVwbcontext().getFrontPage();
		}
		return returnUrl;
	}

	private void updateOnlineUser(int siteId) {
		try {
			AuthenticationService mgr = VWBContext.getContainer()
					.getAuthenticationService();
			mgr.login(siteId, request);
		} catch (VWBException vwbe) {
			log.error("User online error:" + vwbe.getMessage());
		}
	}

	protected String getLoginPage() {
		return getVwbcontext().getBaseURL() + "/umtlogin.jsp";
	}

	protected VWBContext getVwbcontext() {
		if (vwbcontext == null) {
			vwbcontext = VWBContext.createContext(request, VWBCommand.LOGIN,
					null);
		}
		return vwbcontext;
	}

	protected abstract Collection<Principal> parseCredential()
			throws UnkownCredentialException;

	public String getSaveProfileURL() {
		return getVwbcontext().getURL(VWBContext.LOGIN, "login",
				"action=saveprofile");
	}

	public abstract String makeSSOLoginURL(String localURL);

	public void redirectToFail() throws IOException {
		String failURL = null;
		LoginInfo info = getLoginInfo();
		if (info != null) {
			failURL = info.getFailUrl();
		}
		if (failURL != null) {
			response.sendRedirect(failURL);
		} else {
			ResourceBundle rb = getVwbcontext().getBundle("CoreResources");
			response.sendError(HttpServletResponse.SC_FORBIDDEN,
					rb.getString("login.error.noaccess"));
			return;
		}
	}

	private LoginInfo getLoginInfo() {
		return (LoginInfo)VWBContext.getContainer().getLoginSessionService()
				.getCurrentLoginContext(request.getSession().getId());
	}
	private void removeLoginInfo() {
		VWBContext.getContainer().getLoginSessionService()
				.remove(request.getSession().getId());
	}

	private void saveLoginInfo(LoginInfo info) {
		VWBContext.getContainer().getLoginSessionService()
				.save(request.getSession().getId(), info);
	}

	public void redirectToUMT() throws IOException, ServletException {
		String saveProfileURL = getVwbcontext().getURL(VWBContext.LOGIN,
				"login", "action=saveprofile");
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setSuccessUrl(calcReturnUrl());
		saveLoginInfo(loginInfo);
		response.sendRedirect(makeSSOLoginURL(saveProfileURL));
	}

	public void redirectToUMT(LoginContext login, ActionResponse actionResponse)
			throws IOException, ServletException {
		String saveProfileURL = getSaveProfileURL();
		if (isLocalLogin(login)) {
			LoginInfo info = LoginInfo.valueOf(login);
			info.setSsoUrl(makeSSOLoginURL(saveProfileURL));
			saveLoginInfo(info);
			actionResponse.sendRedirect(getLoginPage());
		} else {
			actionResponse.sendRedirect(makeSSOLoginURL(saveProfileURL));
		}
	}

	public void saveProfile() throws IOException {
		String viewUrl = null;
		String failURL = null;
		
		LoginInfo info = getLoginInfo();
		if (info!=null){
			viewUrl=info.getSuccessUrl();
			failURL=info.getFailUrl();
		}else{
			viewUrl=getVwbcontext().getFrontPage();
		}
		
		try {
			parseAndSave();

			if (getVwbcontext().getVWBSession().isAuthenticated()) {
				// Redirect!
				log.debug("Redirecting user to " + viewUrl);
				response.sendRedirect(viewUrl);
			} else {
				if (failURL != null) {
					response.sendRedirect(failURL);
				} else {
					ResourceBundle rb = getVwbcontext().getBundle(
							"CoreResources");
					response.sendError(HttpServletResponse.SC_FORBIDDEN,
							rb.getString("login.error.noaccess"));
					return;
				}
			}
		} catch (UnkownCredentialException e) {
			log.error("User login error:" + e.getMessage());
		}finally{
			removeLoginInfo();
		}
	}
}