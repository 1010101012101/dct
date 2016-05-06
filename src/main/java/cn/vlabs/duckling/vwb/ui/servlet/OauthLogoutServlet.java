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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date Mar 6, 2010
 * @author xiejj@cnic.cn
 */
public class OauthLogoutServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(OauthLogoutServlet.class);
	private static final long serialVersionUID = 1L;
	public OauthLogoutServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		boolean isSubLogout = StringUtils.equals(request.getParameter("sub"), "1");
		
		if(isSubLogout){
			String state=request.getParameter("state");
			response.sendRedirect(state);
		}else{
			VWBContext context=VWBContext.createContext(request,VWBCommand.LOGOUT, null);
			VWBContext adminSiteContext=getAdminLogoutContext(request);
			VWBSession vwbsession = context.getVWBSession();
			if (vwbsession.isAuthenticated()) {
				String localURL="";
				String next = request.getParameter("next");
				if (next != null) {
					localURL = next;
				} else {
					String referer = request.getHeader("Referer");
					if (referer != null) {
						localURL = referer;
					} else {
						localURL = context.getFrontPage();
					}
				}
				
				if (!localURL.startsWith("http")) {
					String baseURL = context.getBaseURL().replaceAll(context.getBasePath(),"");
					localURL = baseURL + localURL;
				}
				
				localURL=adminSiteContext.getURL(VWBContext.LOGOUT, "/logout", "sub=1&state="+URLEncoder.encode(localURL,"UTF-8"), true);
				String redirectURL = makeSSOLogoutURL(adminSiteContext, localURL);
				VWBContext.getContainer().getAuthenticationService().logout(request);
				response.sendRedirect(redirectURL);
			}else {
				log.info("User's session is invalid, just redirect to home page.");
				response.sendRedirect(context.getFrontPage());
			}
		}
	}
	
	private VWBContext getAdminLogoutContext(HttpServletRequest request){
		VWBContext context=VWBContext.getContext(request);
		VWBContext adminSiteContext=VWBContext.createContext(1, request,VWBCommand.LOGOUT, null);
		request.setAttribute("vwb.context", context);
		return adminSiteContext;
	}
	
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
		
	
	
	
}
