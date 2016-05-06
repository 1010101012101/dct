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

/**
 * Introduction Here.
 * @date 2010-3-29
 * @author Fred Zhang (fred@cnic.cn)
 */
public class DCTConnection {
	private ServiceClient client = null;
	private String url = null;
	private String user = null;
	private String password = null;
	public DCTConnection(String url, String user, String password) throws DCTRsiServiceException {
		this.url = url;
		this.user = user;
		this.password = password;
        this.login();
	}

	private void login() throws DCTRsiServiceException {
		ServiceContext context = new ServiceContext(url);
		client = new ServiceClient(context);
		UserInfo info = new UserInfo();
		info.setUserName(user);
		info.setPassword(password);
		try {
			client.sendService("loginService", info);
		} catch (ServiceException e) {
			DCTRsiServiceException ce = ExceptionUtil.changeException(e);
			throw ce;
		}
	}

	public Object sendService(String servcie, Object message)
			throws DCTRsiServiceException {
		int count=0;
		
		Object result =null;
		boolean flag = true;
		do{
			count++;
			try {
				result = client.sendService(servcie, message);
				flag=false;
			} catch (ServiceException e) {
				DCTRsiServiceException ce = ExceptionUtil.changeException(e);
				if(ce instanceof LoginException)
				{
				   login();
				}else
				{
					throw ce;
				}
			}
		}while (flag&&count<2);
		return result;
	}

}
