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

package cn.vlabs.duckling.vwb.service.dpage.provider;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.dpage.data.SearchResult;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author euniverse
 */
public class DPageProviderCache implements DPageProvider {
	protected static final Logger log = Logger
			.getLogger(DPageProviderCache.class);
	private DPageProvider dpageProvider;
	private VWBCacheService cache;

	public void setCache(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("dpage");
	}

	public void setDpageProvider(DPageProvider dpageProvider) {
		this.dpageProvider = dpageProvider;
	}

	public DPagePo create(DPagePo dpage) {
		cache.removeEntry(dpage.getSiteId(), dpage.getResourceId().toString());
		return dpageProvider.create(dpage);
	}

	public void deleteByResourceId(int siteId, int resourceId) {
		dpageProvider.deleteByResourceId(siteId, resourceId);
		cache.removeEntry(siteId, String.valueOf(resourceId));
	}

	public void deleteByResourceId(int siteId, int[] resourceIds) {
		dpageProvider.deleteByResourceId(siteId, resourceIds);
		for (int resourceId : resourceIds) {
			cache.removeEntry(siteId, String.valueOf(resourceId));
		}
	}

	public List<DPagePo> getHistoryByResourceId(int siteId, int resourceId) {
		return dpageProvider.getHistoryByResourceId(siteId, resourceId);
	}

	public List<DPagePo> getHistoryByResourceId(int siteId, int resourceId,
			int offset, int pageSize) {
		return dpageProvider.getHistoryByResourceId(siteId, resourceId, offset,
				pageSize);
	}

	public DPagePo getLatestByResourceId(int siteId, int resourceId) {
		String key = String.valueOf(resourceId);
		DPagePo dpage = (DPagePo) cache.getFromCache(siteId, key);
			
		if (dpage==null){
			dpage = dpageProvider.getLatestByResourceId(siteId, resourceId);
			cache.putInCache(siteId, key, dpage);
		}
		return dpage;
	}

	public List<DPagePo> getLatestByResourceIds(int siteId, int[] resourceIds) {
		return dpageProvider.getLatestByResourceIds(siteId, resourceIds);
	}

	public void update(DPagePo dpage) {
		cache.removeEntry(dpage.getSiteId(), dpage.getResourceId());
		dpageProvider.update(dpage);
	}

	public int getDpageCount(int siteId) {
		return dpageProvider.getDpageCount(siteId);
	}

	public List<LightDPage> getDpagesSinceDate(int siteId, Date date) {
		return dpageProvider.getDpagesSinceDate(siteId, date);
	}

	public DPagePo getVersionContent(int siteId, int resourceId, int version) {
		return dpageProvider.getVersionContent(siteId, resourceId, version);
	}

	public boolean isDpageExist(int siteId, int resourceId) {
		String key = String.valueOf(resourceId);
		DPagePo dpage= (DPagePo) cache.getFromCache(siteId, key);
		if (dpage != null) {
			return true;
		}
		if (dpageProvider.isDpageExist(siteId, resourceId)) {
			return true;
		}
		return false;
	}

	public List<DPageNodeInfo> getSubPages(int siteId, int resourceId) {
		return dpageProvider.getSubPages(siteId, resourceId);
	}

	public List<DPageNodeInfo> getRootPages(int siteId) {

		return dpageProvider.getRootPages(siteId);
	}

	public SearchResult searchSubDpages(int siteId, int resourceId,
			Map<String, Object> searchedConditions) {
		return dpageProvider.searchSubDpages(siteId, resourceId,
				searchedConditions);
	}

	public List<DPagePo> searchPages(int siteId, Map<String,Object> searchedConditions) {
		return dpageProvider.searchPages(siteId, searchedConditions);
	}

	public List<LightDPage> searchDpageByTitle(int siteId, String title) {
		return dpageProvider.searchDpageByTitle(siteId, title);
	}

	public List<LightDPage> getAllPages(int siteId) {
		return dpageProvider.getAllPages(siteId);
	}

	public List<DPagePo> getAllWeightPages(int siteId) {
		List<DPagePo> ps = null;
		try {
			ps = dpageProvider.getAllWeightPages(siteId);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return ps;
	}
}
