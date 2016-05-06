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

package cn.vlabs.duckling.vwb.ui.rsi.api;

import cn.vlabs.rest.ServiceClient;
import cn.vlabs.rest.ServiceContext;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.stream.IResource;

/**
 * dct通信连接器
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VWBAppConnection {
	private ServiceClient client;
	private String url = null;
	private AuthInfo auth = null;
	/**
	 * 
	 * 
	 * @param url dct 连接入口地址 例子：http://localhost:8080/dct/ServiceServlet
	 * @param auth 客户身份
	 * @throws DCTRsiServiceException
	 */
	public VWBAppConnection(String url, AuthInfo auth) throws DCTRsiServiceException {
		this.url  = url;
		this.auth =  auth;
		login(); 
		
	}
	private void login() throws DCTRsiServiceException {
		ServiceContext context = new ServiceContext(url);
		context.setServerURL(url);
		client = new ServiceClient(context);
		try {
			client.sendService("loginAppService", auth);
		} catch (ServiceException e) {
			DCTRsiServiceException ce = ExceptionUtil.changeException(e);
			throw ce;
		}
	}
	public Object sendService(String servcie, Object message)
			throws DCTRsiServiceException {
		int count = 0;

		Object result = null;
		boolean flag = true;
		do {
			count++;
			try {
				result = client.sendService(servcie, message);
				flag = false;
			} catch (ServiceException e) {
				DCTRsiServiceException ce = ExceptionUtil.changeException(e);
				if (ce instanceof LoginException) {
					login();
				} else {
					throw ce;
				}
			}
		} while (flag && count < 2);
		return result;
	}
	public Object sendService(String service, Object param, IResource si) throws DCTRsiServiceException {
		int count = 0;
		Object result = null;
		boolean flag = true;
		do {
			count++;
			try {
				result = client.sendService(service, param,si);
				flag = false;
			} catch (ServiceException e) {
				DCTRsiServiceException ce = ExceptionUtil.changeException(e);
				if (ce instanceof LoginException) {
					login();
				} else {
					throw ce;
				}
			}
		} while (flag && count < 2);
		return result;
	}
}
