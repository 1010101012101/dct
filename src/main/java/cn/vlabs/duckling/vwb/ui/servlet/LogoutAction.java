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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * @date 2010-8-19
 * @author Fred Zhang (fred@cnic.cn)
 */
public class LogoutAction {
	private static final Logger log = Logger.getLogger(VWBContext.LOGIN);

	private static String makeSSOLogoutURL(VWBContext context, String loginUrl) {
		if (!loginUrl.startsWith("http")) {
			String baseURL = context.getBaseURL().replaceAll(context.getBasePath(),
					"");
			loginUrl = baseURL + loginUrl;

		}
		String ssourl = context.getProperty("duckling.umt.logout");
		try {
			return ssourl
					+ "?WebServerURL="
					+ URLEncoder.encode(loginUrl, "UTF-8")
					+ "&appname="
					+ URLEncoder.encode(
							context.getProperty("duckling.dct.localName", "dct"),
							"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return ssourl;
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param next
	 * @throws IOException
	 */
	public static void logoutFacade(HttpServletRequest request,
			HttpServletResponse response, String next) throws IOException {
		VWBContext vwbcontext = VWBContext.createContext(request,
				VWBCommand.LOGOUT, null);
		VWBSession vwbsession = vwbcontext.getVWBSession();
		if (vwbsession.isAuthenticated()) {
			String umtSsoLogout = request.getParameter("umtSsoLogout");
			if (umtSsoLogout == null) {
				String localURL = null;
				if (next != null) {
					localURL = next;
				} else {
					String referer = request.getHeader("Referer");
					if (referer != null) {
						localURL = referer;
					} else {
						localURL = vwbcontext.getFrontPage();
					}
				}
				String redirectURL = makeSSOLogoutURL(vwbcontext, localURL);
				VWBContext.getContainer().getAuthenticationService()
						.logout(request);
				response.sendRedirect(redirectURL);
			}
		} else {
			log.info("User's session is invalid, just redirect to home page.");
			response.sendRedirect(vwbcontext.getFrontPage());
		}
	}
}
