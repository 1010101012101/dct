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

package cn.vlabs.duckling.vwb.service.config.provider;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.config.impl.IPropertyReader;

/**
 * @date 2013-6-5
 * @author xiejj
 */
public class CachedReader implements IPropertyReader {
	private VWBCacheService cache;
	private VWBPropertiesProvider provider;
	@Override
	public boolean containsKey(int siteId,String key) {
		if (cache.getFromCache(siteId, key)!=null){
			return true;
		}else{
			return provider.getProperty(siteId, key)!=null;
		}
	}
	@Override
	public String getProperty(int siteId,String key) {
		String value = (String)cache.getFromCache(siteId, key);
		if (value==null){
			value = provider.getProperty(siteId, key);
			cache.putInCache(siteId, key, value);
		}
		return value;
	}
	@Override
	public Map<String, String> getPropteryStartWith(int siteId,String prefix) {
		return provider.getPropertyStartWith(siteId, prefix);
	}

	@Override
	public Set<Object> propertyNames(int siteId) {
		return provider.readAllProperties(siteId).keySet();
	}

	@Override
	public void removeProperty(int siteId,String key) {
		cache.removeEntry(siteId, key);
		provider.removeProperty(siteId, key);
	}

	public void setCacheService(VWBCacheService cache){
		this.cache = cache;
		this.cache.setModulePrefix("sconfig");
	}

	public void setPropertiesProvider(VWBPropertiesProvider provider){
		this.provider=provider;
	}

	@Override
	public void setProperty(int siteId,Properties props) {
		Set<Object> updateNames = propertyNames(siteId);
		updateNames.retainAll(props.keySet());
		
		Properties updateSet = new Properties();
		Properties insertSet = new Properties();
		for (Object key:props.keySet()){
			cache.removeEntry(siteId, key);
			if (updateNames.contains(key)){
				updateSet.put(key, props.get(key));
			}else{
				insertSet.put(key, props.get(key));
			}
		}
		if (insertSet.size() != 0) {
			provider.insertProperty(siteId, insertSet);
		}
		if (updateSet.size() != 0) {
			provider.updateVWBPpt(siteId, updateSet);
		}
	}

	@Override
	public void setProperty(int siteId,String key, String value) {
		cache.removeEntry(siteId, key);
		provider.removeProperty(siteId, key);
		provider.insertProperty(siteId, key, value);
	}
}
