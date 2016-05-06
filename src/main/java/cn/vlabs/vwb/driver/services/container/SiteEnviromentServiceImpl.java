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

package cn.vlabs.vwb.driver.services.container;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.pluto.PortletWindow;
import org.apache.pluto.driver.core.PortletWindowImpl;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.vwb.SiteContext;
import cn.vlabs.vwb.SiteSession;
import cn.vlabs.vwb.container.spi.SiteEnviromentService;
import cn.vlabs.vwb.container.spi.SiteLoginService;
import cn.vlabs.vwb.driver.internal.SiteContextImpl;
import cn.vlabs.vwb.driver.internal.SiteSessionImpl;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 11, 2010 8:24:50 PM
 */
public class SiteEnviromentServiceImpl implements SiteEnviromentService {
	public static final String SITE_SESSION_KEY = "cn.vlabs.vwb.SiteSession";

	private static SiteContext createSiteContext(HttpServletRequest request) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		return new SiteContextImpl(site.getId());
	}

	public static SiteSessionImpl getSiteSession(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();
		SiteSessionImpl siteSession = (SiteSessionImpl) httpSession
				.getAttribute(SITE_SESSION_KEY);
		if (siteSession == null) {
			siteSession = new SiteSessionImpl();
			siteSession.setSiteContext(createSiteContext(request));
			httpSession.setAttribute(SITE_SESSION_KEY, siteSession);
		} else {
			SiteContextImpl siteContext = (SiteContextImpl) siteSession
					.getSiteContext();
			siteContext.setSite(VWBContainerImpl.findSite(request).getId());
		}
		return siteSession;
	}
	
	public static SiteSessionImpl createSiteSession(int siteId){
		SiteSessionImpl siteSession = new SiteSessionImpl();
		siteSession.setSiteContext(new SiteContextImpl(siteId));
		return siteSession;
	}

	public SiteSession createSiteSession(HttpServletRequest request,
			HttpSession httpSession) {
		return getSiteSession(request);
	}

	public int getPageId(PortletWindow window) {
		String renderPath = ((PortletWindowImpl) window).getPortalURL()
				.getRenderPath();
		renderPath = renderPath.substring(1);
		return Integer.parseInt(renderPath);
	}

	public SiteContext getSiteContext(HttpServletRequest request) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		return new SiteContextImpl(site.getId());
	}

	public SiteLoginService getSiteLoginService(HttpServletRequest request,
			HttpServletResponse response) {
		return new SiteLoginServiceImpl(request, response);
	}
}
