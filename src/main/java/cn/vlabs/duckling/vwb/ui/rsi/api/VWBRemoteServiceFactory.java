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

import cn.vlabs.duckling.vwb.ui.rsi.api.dpage.DPageService;
import cn.vlabs.duckling.vwb.ui.rsi.api.dpage.RemoteDPageService;

/**
 * 使用例子：<br>
 * String appKeysLocation = "c:\\keys";<br>
 * String vmtKeyServiceUrl = "http://localhost:8080/vmt/keyServiceServlet";<br>
 * String localAppName = "测试";<br>
 * DucklingKeyManager m = DucklingKeyManager.getInstatnce(appKeysLocation,vmtKeyServiceUrl,localAppName);<br>
 * VMT2AuthInfo info = new VMT2AuthInfo(m.getAppSignatureEnvelope(),"admin@root.umt");<br>
 * VWBAppConnection connection = new VWBAppConnection("http://localhost:8080/dct/ServiceServlet",info);<br>
 * DPageService dpageService = VWBRemoteServiceFactory.getDPageService(connection);<br>
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VWBRemoteServiceFactory {
	/**
	 * 获取操作dpage的服务对象
	 * @param conn
	 * @return
	 */
	public static DPageService getDPageService(VWBAppConnection conn){
		return new RemoteDPageService(conn);
	}
	/**
	 * 获取对站点操作的服务对象
	 * @param conn
	 * @return
	 */
	public static RemoteSiteService getRemoteSiteService(VWBAppConnection conn)
	{
		return new RemoteSiteServiceImpl(conn);
	}

}
