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

package cn.vlabs.duckling.vwb.service.site;

import java.util.List;

/**
 * @date 2013-6-4
 * @author xiejj
 */
public interface ISiteManageService {
	/**
	 * 更新站点元信息
	 * 
	 * @param smi
	 */
	void updateSiteMeta(SiteMetaInfo smi);
	/**
	 * 获得站点的元信息
	 * @param siteId
	 * @return
	 */
	SiteMetaInfo getSiteInfo(int siteId);
	/**
	 * 获得所有的站点
	 * @return
	 */
	List<SiteMetaInfo> getAllSites();
	/**
	 * 删除站点
	 * @param siteId
	 */
	void deleteSiteInfo(int siteId);
	/**
	 * 创建站点
	 * @param smi
	 * @return
	 */
	SiteMetaInfo createSiteMetaInfo(SiteMetaInfo smi);
	/**
	 * 修改站点发布状态
	 * @param siteId
	 * @param state
	 */
	void changePublishState(int siteId, PublishState state);
}
