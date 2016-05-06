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

package cn.vlabs.duckling.vwb.service.login;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * Manages authentication activities for a WikiEngine: user login, logout, and
 * credential refreshes. This class uses JAAS to determine how users log in.
 */
public final class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger log = Logger
			.getLogger(AuthenticationServiceImpl.class);
	private VWBCacheService cacheService;

	private Map<String, Principal> getOnlineMap(int siteId) {
		@SuppressWarnings("unchecked")
		Map<String, Principal> onlineMap = (Map<String, Principal>) cacheService
				.getFromCache(siteId, "map");
		if (onlineMap == null) {
			onlineMap = new HashMap<String, Principal>();
		}
		return onlineMap;
	}

	private void userOnline(int siteId, String sessionId, Principal userInfo) {
		Map<String, Principal> onlineMap = getOnlineMap(siteId);
		onlineMap.put(sessionId, userInfo);
		cacheService.putInCache(0, sessionId, siteId);
		cacheService.putInCache(siteId, "map", onlineMap);
	}

	public Collection<Principal> getOnlineUsers(int siteId) {
		Map<String, Principal> onlineMap = getOnlineMap(siteId);
		return onlineMap.values();
	}

	public final boolean login(int siteId, HttpServletRequest request)
			throws VWBException {
		if (request == null) {
			throw new IllegalStateException(
					"Wiki context's HttpRequest may not be null");
		}

		VWBSession session = VWBSession.findSession(request);
		if (session == null) {
			throw new IllegalStateException(
					"VWBContext's VWBSession may not be null");
		} else {
			userOnline(siteId, request.getSession().getId(),
					session.getCurrentUser());
		}

		return true;
	}

	public final void logout(HttpServletRequest request) {
		if (request == null) {
			log.error("No HTTP reqest provided; cannot log out.");
			return;
		}

		HttpSession session = request.getSession();
		String sid = (session == null) ? "(null)" : session.getId();
		if (log.isDebugEnabled()) {
			log.debug("Invalidating WikiSession for session ID=" + sid);
		}
		VWBSession vwbsession = VWBSession.findSession(request);
		vwbsession.invalidate();
		if (session != null) {
			session.invalidate();
		}

	}

	public void setCacheService(VWBCacheService cacheService) {
		this.cacheService = cacheService;
		this.cacheService.setModulePrefix("online");
	}

	public void userOffline(String sessionId) {
		Integer siteId = (Integer) cacheService.getFromCache(0, sessionId);
		if (siteId != null) {
			Map<String, Principal> onlineMap = getOnlineMap(siteId);
			onlineMap.remove(sessionId);
			cacheService.putInCache(siteId, "map", onlineMap);
			cacheService.removeEntry(0, sessionId);
		}
	}
}
