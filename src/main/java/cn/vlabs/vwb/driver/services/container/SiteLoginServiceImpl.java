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

package cn.vlabs.vwb.driver.services.container;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.vwb.LoginContext;
import cn.vlabs.vwb.container.spi.SiteLoginService;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 11, 2010 9:24:45 PM
 */
public class SiteLoginServiceImpl implements SiteLoginService {
	private HttpServletRequest request;

	private HttpServletResponse response;

	public SiteLoginServiceImpl(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public void login(LoginContext context, ActionResponse action) throws ServletException {
		LoginAction loginAction = LoginAction.createLoginAction(request, response);
		try {
			loginAction.redirectToUMT(context, action);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}
}
