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

package cn.vlabs.duckling.vwb.service.config;

import java.util.Collection;


/**
 * 域名服务，该服务为全局的容器服务
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public interface DomainNameService {
	/**
	 * 通过域名查询站点的ID
	 * @param domain 被检查的域名
	 * @return 使用该域名的ID,如果未找到则返回-1
	 */
	int findSite(String domain);
	/**
	 * 查询所有站点的信息
	 * @return
	 */
	Collection<SiteItem> getAllSiteItems();
	/**
	 * 查询系统的缺省域名
	 * @return
	 */
	String getDefaultDomain();
	/**
	 * 获得站点的缺省域名
	 * @param siteId
	 * @return
	 */
	String getSiteDefaultDomain(int siteId);
	/**
	 * 查询站点所使用的域名
	 * 
	 * @param siteId
	 *            站点的ID
	 * @return
	 */
	String[] getUsedDomain(int siteId);
	/**
	 * 判断域名是否已被使用
	 * @param domain 被检查的域名
	 * @return 如果已被使用返回true，否则返回false
	 */
	boolean isDomainUsed(String domain);
	/**
	 * 更新站点的域名
	 * @param siteId	被更新的站点ID
	 * @param domains	站点的新域名
	 * @return 如果更新的域名中与现有的域名冲突则返回false，更新成功返回true
	 */
	boolean updateSiteDomains(int siteId, String[] domains);
	/**
	 * 更新站点的主域名
	 * @param siteId	被更新的站点ID
	 * @param domains	站点的新域名
	 * @return 如果更新的域名中与现有的域名冲突则返回false，更新成功返回true
	 */
	boolean updateSiteMainDomain(int siteId, String domain);
	/**
	 * @param siteId
	 * @return
	 */
	boolean removeSiteDomains(int siteId);
}
