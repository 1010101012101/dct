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

package cn.vlabs.duckling.vwb.ui.accclb;

import cn.vlabs.duckling.util.Utility;

public class AccessCLBHelper {
	private String clbServiceURL = "";

	private String appName = "";

	private String appPwd = "";

	private String username = "";


	/**
	 * 获取dct应用信息，保存在本地，供连接clb服务器使用 其中username为用户登陆的userId
	 */
	public AccessCLBHelper(String clbServiceURL, String appName, String appPwd,
			String username) {
		this.clbServiceURL = clbServiceURL;
		this.appName = appName;
		this.appPwd = appPwd;
		this.username = username;

	}

	public String getClbServiceURL() {
		return clbServiceURL;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppPwd() {
		return appPwd;
	}

	public String getUsername() {
		return username;
	}

	public static String getHashId(String title, String docid, String baseURL) {
		String str = title;
		String hashid = "";
		if (str.lastIndexOf(".") != -1)
			hashid = "clb:clb:" + str.substring(str.lastIndexOf(".") + 1) + ":"
					+ docid;
		else
			hashid = "clb:clb::" + docid;
		hashid = Utility.getBASE64(hashid);

		hashid = baseURL + hashid;
		return hashid;
	}
}
