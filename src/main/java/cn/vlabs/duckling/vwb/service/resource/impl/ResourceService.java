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

package cn.vlabs.duckling.vwb.service.resource.impl;

import org.apache.pluto.driver.config.db.DBPortalPageService;

import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.resource.FunctionService;
import cn.vlabs.duckling.vwb.service.resource.IResourceService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date Feb 26, 2010
 * @author xiejj@cnic.cn
 */

public class ResourceService implements IResourceService {
	private DPageService m_dpages;

	private FunctionService m_functions;

	private DBPortalPageService m_portalPages;

	private ViewPortService m_viewports;

	public Resource getResource(int siteId, int id) {
		ViewPort vp = m_viewports.getViewPort(siteId, id);
		if (vp == null)
			return null;
		Resource resource = null;
		if (Resource.TYPE_DPAGE.equals(vp.getType())) {
			resource = m_dpages.getLatestDpageByResourceId(siteId, id);
		}
		if (Resource.TYPE_FUNCTION.equals(vp.getType())) {
			resource = m_functions.getFunction(siteId, id);
		}
		if (Resource.TYPE_PORTAL.equals(vp.getType())) {
			resource = m_portalPages.getPageConfig(siteId, vp.getId());
		}
		if (resource != null)
			resource.setTitle(vp.getTitle());
		return resource;
	}

	public Resource getResource(int siteId, int id, int version) {
		ViewPort vp = m_viewports.getViewPort(siteId, id);
		if (vp == null)
			return null;
		if (Resource.TYPE_DPAGE.equals(vp.getType())) {
			return m_dpages.getDpageVersionContent(siteId, id, version);
		}
		throw new IllegalArgumentException("Resource(id=" + id
				+ ") do not support version");
	}

	public void setDpageService(DPageService dpageService) {
		this.m_dpages = dpageService;
	}

	public void setFunctionService(FunctionService func) {
		this.m_functions = func;
	}

	public void setPortalPageService(DBPortalPageService portalService) {
		this.m_portalPages = portalService;
	}

	public void setViewportService(ViewPortService v) {
		this.m_viewports = v;
	}
}
