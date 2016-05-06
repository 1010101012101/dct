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

package cn.vlabs.duckling.vwb.service.flexsession;

import java.security.Principal;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * @date 2013-6-7
 * @author xiejj
 */
public class FlexSessionServiceImpl implements FlexSessionService {
	private static final int GLOBAL_SITE_ID = 0;
	private VWBCacheService cache;
	public void setCacheService(VWBCacheService cache){
		this.cache = cache;
		this.cache.setModulePrefix("flex");
	}
	@Override
	public Principal getCurrentUser(String sessionId) {
		return (Principal)cache.getFromCache(GLOBAL_SITE_ID, sessionId);
	}

	@Override
	public void save(String sessionId, Principal currentUser) {
		cache.putInCache(GLOBAL_SITE_ID, sessionId, currentUser);
	}

	@Override
	public void remove(String sessionId) {
		cache.removeEntry(GLOBAL_SITE_ID, sessionId);
	}
	@Override
	public boolean contains(String sessionId) {
		return getCurrentUser(sessionId)!=null;
	}
	@Override
	public void saveToken(String sessionId, String token) {
		cache.putInCache(GLOBAL_SITE_ID, sessionId+"_token", token);
	}
	
	
	@Override
	public String getToken(String sessionId) {
		return (String)cache.getFromCache(GLOBAL_SITE_ID, sessionId+"_token");
	}
	
}
