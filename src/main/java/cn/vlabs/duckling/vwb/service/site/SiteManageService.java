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

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteManageService implements ISiteManageService {
	protected static final Logger log = Logger
			.getLogger(SiteManageService.class);
	private ISiteMetaInfoProvider siteMetaInfoProvider;
	private VWBCacheService cache;
	public void setCacheService(VWBCacheService cacheService){
		this.cache = cacheService;
		this.cache.setModulePrefix("smeta");
	}
	@Override
	public void changePublishState(int siteId, PublishState state) {
		SiteMetaInfo meta = getSiteInfo(siteId);
		meta.setPublished(state);
		updateSiteMeta(meta);
	}

	@Override
	public SiteMetaInfo createSiteMetaInfo(SiteMetaInfo smi) {
		return siteMetaInfoProvider.createSiteMetaInfo(smi);
	}

	@Override
	public void deleteSiteInfo(int siteId) {
		cache.removeEntry(0, siteId);
		siteMetaInfoProvider.delete(siteId);
	}

	@Override
	public List<SiteMetaInfo> getAllSites() {
		return siteMetaInfoProvider.getAllSites();
	}

	@Override
	public SiteMetaInfo getSiteInfo(int siteId) {
		SiteMetaInfo meta = (SiteMetaInfo) cache.getFromCache(0, siteId);
		if (meta==null){
			meta=siteMetaInfoProvider.getSiteMetaInfoById(siteId);
			cache.putInCache(0, siteId, meta);
		}
		return meta;
	}

	public void setSiteMetaInfoProvider(ISiteMetaInfoProvider siteMetaInfoProvider) {
		this.siteMetaInfoProvider = siteMetaInfoProvider;
	}

	@Override
	public void updateSiteMeta(SiteMetaInfo smi) {
		cache.removeEntry(0, smi.getId());
		siteMetaInfoProvider.updateSiteMetaInfo(smi);
	}
}
