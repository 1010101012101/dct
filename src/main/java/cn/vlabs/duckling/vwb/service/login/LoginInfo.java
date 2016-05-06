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

import java.io.Serializable;

import cn.vlabs.vwb.LoginContext;

/**
 * @date 2013-6-13
 * @author xiejj
 */
public class LoginInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public static LoginInfo valueOf(LoginContext context) {
		LoginInfo info = new LoginInfo();
		info.setPassword(context.getPassword());
		info.setUserName(context.getUserName());
		info.setFailUrl(context.getFailURL());
		info.setSuccessUrl(context.getSuccessURL());
		return info;
	}

	private String failUrl;
	private String password;
	private String ssoUrl;
	private String successUrl;
	private String userName;

	public LoginInfo() {
	};

	public String getFailUrl() {
		return failUrl;
	}

	public String getPassword() {
		return password;
	}

	public String getSsoUrl() {
		return ssoUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setFailUrl(String failUrl) {
		this.failUrl = failUrl;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSsoUrl(String url) {
		this.ssoUrl = url;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
