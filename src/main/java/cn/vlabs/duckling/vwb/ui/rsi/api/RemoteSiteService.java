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

import java.util.List;

import cn.vlabs.duckling.vwb.ui.rsi.api.banner.BannerItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.SiteStatus;
import cn.vlabs.duckling.vwb.ui.rsi.api.mail.MailSettingItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.template.TemplateItem;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * 使用例子：<br>
 * String appKeysLocation = "c:\\keys";<br>
 * String vmtKeyServiceUrl = "http://localhost:8080/vmt/keyServiceServlet";<br>
 * String localAppName = "测试";<br>
 * DucklingKeyManager m =
 * DucklingKeyManager.getInstatnce(appKeysLocation,vmtKeyServiceUrl
 * ,localAppName);<br>
 * VMT2AuthInfo info = new
 * VMT2AuthInfo(m.getAppSignatureEnvelope(),"admin@root.umt");<br>
 * VWBAppConnection connection = new
 * VWBAppConnection("http://localhost:8080/dct/ServiceServlet",info);<br>
 * RemoteSiteService siteService =
 * VWBRemoteServiceFactory.getRemoteSiteService(connection);<br>
 * SiteStatus status = siteService.checkSiteStatus(1);<br>
 * 
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public interface RemoteSiteService {
	/**
	 * 创建站点
	 * 
	 * @param params
	 *            站点的初始化数据。
	 * @return 站点id等相关参数
	 * @throws ParametersInvalidException
	 *             初始化参数错误。 AccessControlException 没有权限创建站点。
	 */
	SiteResponseItem createSite(SiteRequestItem params)
			throws DCTRsiServiceException;

	/**
	 * 更新站点名称
	 * 
	 * @param siteName
	 *            站点名称。
	 * @param siteId
	 *            站点id。
	 * @throws DCTRsiServiceException
	 */
	void updateSiteName(String siteName, int siteId)
			throws DCTRsiServiceException;

	/**
	 * 更新站点根资源的Banner
	 * 
	 * @param siteId
	 *            站点id。
	 * @param bannerName
	 *            banner名称
	 * @param in
	 *            banner输入流
	 * @throws DCTRsiServiceException
	 */
	void updateRootBanner(int siteId, String bannerName, StreamInfo in)
			throws DCTRsiServiceException;

	/**
	 * 更新站点根资源的Banner
	 * 
	 * @param siteId
	 *            站点id
	 * @param banner
	 *            banner对象
	 * @throws DCTRsiServiceException
	 */
	void cspCreateBanner(int siteId, BannerItem banner)
			throws DCTRsiServiceException;

	/**
	 * 更新站点根资源的Banner
	 * 
	 * @param siteId
	 *            站点id
	 * @param banner
	 *            banner对象
	 * @throws DCTRsiServiceException
	 */
	void updateBannerNoImg(int siteId, BannerItem banner)
			throws DCTRsiServiceException;

	/**
	 * 更新站点特定资源的Banner
	 * 
	 * @param siteId
	 *            站点id。
	 * @param resourceId
	 *            资源id
	 * @param bannerName
	 *            banner名称
	 * @param in
	 *            banner输入流
	 * @throws DCTRsiServiceException
	 */
	void updateBanner(int siteId, int resourceId, String bannerName,
			StreamInfo in) throws DCTRsiServiceException;

	/**
	 * 判定域名是否被占用
	 * 
	 * @param domain
	 *            域名。
	 * @return true 域名已经被占用 false 域名未被占用
	 * @throws DCTRsiServiceException
	 */
	boolean isDomainUsed(String domain) throws DCTRsiServiceException;

	/**
	 * 获取站点使用的域名
	 * 
	 * @param siteId
	 *            被查询的站点编号
	 * @return 站点使用的域名，其中第一是缺省域名，如无域名返回0长度的域名
	 * @throws DCTRsiServiceException
	 */
	String[] getSiteDomains(int siteId) throws DCTRsiServiceException;

	/**
	 * 更新站点使用的域名
	 * 
	 * @param siteId
	 *            被更新的站点编号
	 * @param domains
	 *            站点使用的域名，输入的域名顺序，就是最后实际使用的域名顺序，如果序列为空，则清空现有的域名
	 * @throws DCTRsiServiceException
	 */
	void updateSiteDomains(int siteId, String[] domains)
			throws DCTRsiServiceException;

	/**
	 * 更新站点域名
	 * 
	 * @param siteId
	 * @param domain
	 * @throws DCTRsiServiceException
	 */
	void updateDomain(int siteId, String domain) throws DCTRsiServiceException;

	/**
	 * 以某资源作为左菜单，获取相应的菜单项
	 * 
	 * @param siteId
	 *            站点id。
	 * @param resourceId
	 *            资源id
	 * @return 以资源resourcid为左菜单的菜单项
	 * @throws DCTRsiServiceException
	 */
	List<MenuItem> getMenu(int siteId, int resourceId)
			throws DCTRsiServiceException;

	/**
	 * 以某资源作为左菜单，更新相应的菜单项
	 * 
	 * @param siteId
	 *            站点id
	 * @param items
	 *            菜单项
	 * @throws DCTRsiServiceException
	 */
	void updateMenu(int siteId, int resourceId, List<MenuItem> items)
			throws DCTRsiServiceException;

	/**
	 * 获取站点的邮件设置参数
	 * 
	 * @param siteId
	 *            站点id。
	 * @return 邮件参数
	 * @throws DCTRsiServiceException
	 */
	MailSettingItem getMailSettingItem(int siteId)
			throws DCTRsiServiceException;

	/**
	 * 更新站点邮件设置参数
	 * 
	 * @param siteId
	 *            站点id
	 * @param mailItem
	 *            邮件参数
	 * @throws DCTRsiServiceException
	 */
	void updateMainlSettingItem(int siteId, MailSettingItem mailItem)
			throws DCTRsiServiceException;

	/**
	 * 获取站点的所有skin，包括全局的和站点本身的
	 * 
	 * @param siteId
	 *            站点id。
	 * @return 皮肤列表
	 * @throws DCTRsiServiceException
	 */
	List<SkinItem> getAllSkins(int siteId) throws DCTRsiServiceException;

	/**
	 * 更新站点皮肤
	 * 
	 * @param siteId
	 *            站点id
	 * @param skinName
	 *            皮肤的名称
	 * @throws DCTRsiServiceException
	 */
	void setSkin(int siteId, String skinName) throws DCTRsiServiceException;

	/**
	 * 获取站点的默认banner
	 * 
	 * @param siteId
	 *            站点id
	 * @return
	 * @throws DCTRsiServiceException
	 */
	BannerItem getDefaultBanner(int siteId) throws DCTRsiServiceException;

	/**
	 * 根据资源id，获取相应的banner
	 * 
	 * @param siteId
	 * @param resourceId
	 * @return
	 * @throws DCTRsiServiceException
	 */
	BannerItem getBanner(int siteId, int resourceId)
			throws DCTRsiServiceException;

	/**
	 * 获取站点默认语言
	 * 
	 * @param siteId
	 * @return
	 * @throws DCTRsiServiceException
	 */
	String getSiteDefaultLanguage(int siteId) throws DCTRsiServiceException;

	/**
	 * 设置站点默认语言
	 * 
	 * @param siteId
	 * @param language
	 * @throws DCTRsiServiceException
	 */
	void setSiteDefaultLanguage(int siteId, String language)
			throws DCTRsiServiceException;

	/**
	 * 销毁站点
	 * 
	 * @param siteId
	 * @throws DCTRsiServiceException
	 */
	void destroySite(int siteId) throws DCTRsiServiceException;

	/**
	 * 挂起站点
	 * 
	 * @param siteId
	 * @throws DCTRsiServiceException
	 */
	void deactivateSite(int siteId) throws DCTRsiServiceException;

	/**
	 * 激活站点
	 * 
	 * @param siteId
	 * @throws DCTRsiServiceException
	 */
	void activateSite(int siteId) throws DCTRsiServiceException;

	/**
	 * 获取站点的状态
	 * 
	 * @param siteId
	 * @return
	 * @throws DCTRsiServiceException
	 */
	SiteStatus checkSiteStatus(int siteId) throws DCTRsiServiceException;

	/**
	 * 发布站点
	 * 
	 * @param siteId
	 * @return
	 * @throws DCTRsiServiceException
	 */
	boolean publishSite(int siteId) throws DCTRsiServiceException;
	/**
	 * 查询站点是否已发布
	 * @param siteId
	 * @return
	 * @throws DCTRsiServiceException
	 */
	boolean isPublishedSite(int siteId) throws DCTRsiServiceException;
	/**
	 * 更新站点的Banner图片
	 * @param siteId
	 * @param banner
	 * @param in
	 * @throws DCTRsiServiceException
	 */
	void updateBannerWithImg(int siteId, BannerItem banner, StreamInfo in)
			throws DCTRsiServiceException;

	/**
	 * @return
	 * @throws DCTRsiServiceException
	 */
	List<TemplateItem> getAllTemplate() throws DCTRsiServiceException;
}
