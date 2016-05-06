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

package org.apache.pluto.driver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Process logout from the Pluto portal.
 *
 *
 */
public class PortalDriverLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Processes logout by invalidating the session, creating a new session
	 * and forwards to the login (home) page.
	 *
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		session.invalidate();
	    req.getSession(true);
	    resp.sendRedirect(logoutURL(req));
//	    req.getRequestDispatcher(logoutURL(req)).forward(req, resp);
	}
	
	private String logoutURL(HttpServletRequest request){
		String redirectURL = "http://localhost/umt/sso/ssologout.jsp?Locale=zh_cn";
        String url;
        if (request.getServerPort() != 80){
            url = "http://" +  request.getServerName()+ ":" + request.getServerPort();
        }
        else
            url = "http://" +  request.getServerName();
        url += request.getContextPath(); 
        try {
			url =  java.net.URLEncoder.encode( url, "UTF-8");
	        url =  redirectURL + "&WebServerURL=" + url;
	        return url;
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}
}
