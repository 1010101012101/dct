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

package cn.vlabs.duckling.vwb.ui.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.login.AuthenticationService;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.UserInfo;

/**
 * @date 2014-10-28
 * @author xiejj
 */
public class OAuthLoginServletImpl extends AbstractLoginServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger
			.getLogger(OAuthLoginServletImpl.class);

	private VWBContext getVwbcontext(HttpServletRequest request) {
		return VWBContext.createContext(request, VWBCommand.LOGIN, null);
	}

	private String calcReturnUrl(HttpServletRequest request) {
		String returnUrl = (String) request.getSession().getAttribute(
				Attributes.REQUEST_URL);
		if (returnUrl == null) {
			returnUrl = request.getParameter("returnUrl");
		}
		if (returnUrl == null) {
			returnUrl = request.getHeader("Referer");
		}
		if (returnUrl == null) {
			returnUrl =getVwbcontext(request).getFrontPage();
		}
		return returnUrl;
	}

	@Override
	protected String preLogin(HttpServletRequest request,
			HttpServletResponse response) {
		String returnUrl = calcReturnUrl(request);
		VWBContext context = getVwbcontext(request);
		try {
			return context.getURL(VWBContext.LOGIN, "/login", "sub=1&state="+URLEncoder.encode(returnUrl,"UTF-8"), true);
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			return null;
		}
	}

	@Override
	protected void hasLogin(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect(getVwbcontext(request).getFrontPage());
	}

	private UserPrincipal replaceUserPrincipal(List<Principal> principals, UserInfo userp) {
		if (principals != null) {
			for (int i = 0; i < principals.size(); i++) {
				if (principals.get(i) instanceof cn.vlabs.commons.principal.UserPrincipal) {
					UserPrincipal up = (UserPrincipal) principals.get(i);
					UserPrincipal newup = new UserPrincipal(
							userp.getCstnetId(), up.getDisplayName(),
							up.getEmail(), userp.getType());
					principals.set(i, newup);
					return newup;
				}
			}
		}
		return null;
	}

	private Collection<Principal> parseCredential(HttpServletRequest request,
			UserInfo user) {
		String name = user.getCstnetId();
		String voName = getVwbcontext(request).getProperty("duckling.umt.vo");
		List<Principal> principals = VWBContainerImpl.findContainer()
				.getUserService().getUserPrincipal(name, voName);

		if (principals != null && principals.size() > 0) {
			UserPrincipal userPrincipal = replaceUserPrincipal(principals, user);
			if (userPrincipal!=null){
				request.getSession().setAttribute("currentUser", userPrincipal);
			}
			return principals;
		} else {
			LinkedList<Principal> result = new LinkedList<Principal>();
			UserPrincipal principal = new UserPrincipal(user.getCstnetId(),
					user.getCstnetId(), user.getCstnetId(), user.getType());
			result.add(principal);
			request.getSession().setAttribute("currentUser", principal);
			return result;
		}
	}

	private void updateOnlineUser(HttpServletRequest request) {
		try {
			int siteId = getVwbcontext(request).getSiteId();
			AuthenticationService mgr = VWBContext.getContainer()
					.getAuthenticationService();
			mgr.login(siteId, request);
		} catch (VWBException vwbe) {
			log.error("User online error:" + vwbe.getMessage());
		}
	}

	@Override
	protected void postLogin(HttpServletRequest request,
			HttpServletResponse response, AccessToken token, String viewUrl)
			throws IOException {
		VWBContext context = getVwbcontext(request);
		String url = context.getFrontPage();
		if (token != null) {
			UserInfo userinfo = token.getUserInfo();
			if (userinfo != null) {
				Collection<Principal> principals = parseCredential(request,
						userinfo);
				VWBSession vwbSession = context.getVWBSession();
				if (principals == null || principals.size() == 0) {
					log.info("Failed to authenticate user.");
					vwbSession.setStatus(
							VWBSession.ANONYMOUS);
				} else {
					log.info("Login success " + userinfo.getCstnetId());
					vwbSession.setPrincipals(principals);
					vwbSession.setToken(token.getAccessToken());
					updateOnlineUser(request);
					url = request.getParameter("state");
					response.sendRedirect(url);
				}
				return;
			}
		}
		log.error("Login attempt failed(code=" + request.getParameter("code")
				+ ").");
		response.sendRedirect(url);
	}
}
