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

package cn.vlabs.duckling.vwb.ui.rsi.api.dpage;

import java.util.List;

import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiServiceException;
import cn.vlabs.duckling.vwb.ui.rsi.api.DPageEmptyException;


/**
 * 
 * 页面服务接口。
 * 使用例子：<br>
 * String appKeysLocation = "c:\\keys";<br>
 * String vmtKeyServiceUrl = "http://localhost:8080/vmt/keyServiceServlet";<br>
 * String localAppName = "测试";<br>
 * DucklingKeyManager m = DucklingKeyManager.getInstatnce(appKeysLocation,vmtKeyServiceUrl,localAppName);<br>
 * VMT2AuthInfo info = new VMT2AuthInfo(m.getAppSignatureEnvelope(),"admin@root.umt");<br>
 * VWBAppConnection connection = new VWBAppConnection("http://localhost:8080/dct/ServiceServlet",info);<br>
 * DPageService dpageService = VWBRemoteServiceFactory.getDPageService(connection);<br>
 * DPageInfo info = dpageService.getContent(1,7);<br>
 * @date Apr 19, 2010
 * @author fred@cnic.cn
 */
public interface DPageService {
	/**
	 * 查询获取页面的内容。
	 * @param siteId 站点的ID。
	 * @param pageid 站点页面的ID。
	 * @param version 页面的版本。
	 * @return 如果页面能找到，则返回页面的内容。
	 * @see DPageInfo
	 * @throws 
	 * 		DPageEmptyException 如果无此页面抛出该异常。
	 * 		AccessControlException 如果无权访问该页面，抛出此异常。
	 */
	DPageInfo getContent(int siteId,int pageid, int version) throws DCTRsiServiceException;
	/**
	 * 查询获取页面的内容。
	 * @param siteId 站点的ID。
	 * @param pageid 站点页面的ID号。
	 * @return 如果pageid对应的页面可以找到，返回页面的内容
	 * @throws 
	 * 		DPageEmptyException 如果无此页面抛出该异常。
	 * 		AccessControlException 如果无权访问该页面，抛出此异常。
	 */
	DPageInfo getContent(int siteId,int pageid) throws DCTRsiServiceException;
	/**
	 * 更新页面信息
	 * @param siteId 站点的ID。
	 * @param pageid 页面的ID号
	 * @param title 页面的标题
	 * @param content 页面的内容
	 * @throws DCTRsiServiceException 
	 */
	void updateContent(int siteId,int pageid, String title, String content) throws DCTRsiServiceException;
	/**
	 * 更新页面信息
	 * @param siteId 站点的ID。
	 * @param pageid 页面的ID号
	 * @param title 页面的标题
	 * @param content 页面的内容
	 * @param force 是否强制更新 
	 * @throws DCTRsiServiceException 
	 */
	void updateContent(int siteId,int pageid, String title, String content, boolean force) throws DCTRsiServiceException;

	/**
	 * 创建页面
	 * @param siteId 站点的ID。
	 * @param title 页面的标题
	 * @param content 页面的内容。
	 * @return 新建页面的ID号。
	 * @throws DCTRsiServiceException 
	 */
	int createDPage(int siteId,String title, String content) throws DCTRsiServiceException;
	
	/**
	 * 获取下级页面
	 * @param siteId 站点的ID。
	 * @param pageId 父页面的ID
	 * @return 下级页面列表
	 * @throws DCTRsiServiceException 
	 */
	List<BasedDPage> getSubPages(int siteId,int pageId) throws DCTRsiServiceException;
	
}
