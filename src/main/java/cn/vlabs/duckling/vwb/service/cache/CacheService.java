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

package cn.vlabs.duckling.vwb.service.cache;

import java.util.List;

import net.duckling.falcon.api.cache.ICacheService;

import org.apache.log4j.Logger;

/**
 * @date May 12, 2010
 * @author xiejj@cnic.cn
 */
public class CacheService implements VWBCacheService {
	private static final String KEY_FMT = "%s:%d:%s";
	private static final Logger log = Logger.getLogger(CacheService.class);
	private ICacheService m_cache;
	private String modulePrefix;

	private String decorateKey(int siteId, Object key) {
		return String.format(KEY_FMT, modulePrefix, siteId, key.toString());
	}

	@Override
	public Object getFromCache(int siteId, Object key) {
		String realKey = decorateKey(siteId, key);
		if (ThreadLocalCache.containKey(realKey)){
			return ThreadLocalCache.get(realKey);
		}else{
			log.debug("get from cache key="+realKey);
			Object result = m_cache.get(realKey);
			ThreadLocalCache.put(realKey,result);
			return result;
		}
	}

	@Override
	public void putInCache(int siteId, Object key, Object obj) {
		String realKey = decorateKey(siteId, key);
		ThreadLocalCache.put(realKey,obj);
		
		if (obj != null) {
			m_cache.set(realKey, obj);
		}
	}

	@Override
	public void removeBulk(int siteId, List<?> keys) {
		for (Object key:keys){
			removeEntry(siteId,key);
		}
	}

	@Override
	public void removeEntry(int siteId, Object key) {
		String realKey = decorateKey(siteId, key);
		m_cache.remove(realKey);
		ThreadLocalCache.remove(realKey);
	}

	public void setCacheProvider(ICacheService falconCache) {
		this.m_cache = falconCache;
	}

	public void setModulePrefix(String prefix) {
		this.modulePrefix = prefix;
	}
}
