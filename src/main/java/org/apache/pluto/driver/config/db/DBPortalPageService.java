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

package org.apache.pluto.driver.config.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.pluto.driver.config.DriverConfigurationException;
import org.apache.pluto.driver.services.portal.PageConfig;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class DBPortalPageService {
	public static Collection<PortletInfo> convert(Collection<String> c){
		if (c!=null){
			ArrayList<PortletInfo> portlets = new ArrayList<PortletInfo>();
			for (String portletId:c){
				portlets.add(PortletInfo.fromPortletId(portletId));
			}
			return portlets;
		}
		return null;
	}
	private VWBCacheService cache;
	
	private PortalPageProvider m_pages;
	private PagePortletProvider m_portlets;
	private void fillPortlets(int siteId,PageConfig page) {
		Collection<PortletInfo> c = m_portlets.getPagePortlets(siteId,page.getResourceId());
		if (c!=null){
			for (Object portlet:c){
				PortletInfo pi = (PortletInfo)portlet;
				page.addPortlet(pi.getContextPath(), pi.getName());
			}
		}
	}
	private PageConfig getFromCache(int siteId,int resourceId) {
		return (PageConfig)cache.getFromCache(siteId, Integer.toString(resourceId));
	}
	private void removeFromCache(int siteId,int resourceId) {
		cache.removeEntry(siteId, Integer.toString(resourceId));
	}

	private void saveToCache(int siteId,int resourceId, PageConfig page) {
			cache.putInCache(siteId,Integer.toString(resourceId), page);
	}
	
	public void addPage(int siteId,PageConfig config)throws DriverConfigurationException{
		m_pages.creatPage(siteId,config);
		m_portlets.removePagePortlets(siteId,config.getResourceId());
		m_portlets.insertPagePortlets(siteId,config.getResourceId(), convert(config.getPortletIds()));
	}
	
	public List<PageConfig> findByPortlet(int siteId,String context, String portlet){
		List<Integer> pageids = m_portlets.findBy(siteId,context, portlet);
		if (pageids!=null && pageids.size()>0){
			LinkedList<PageConfig> pages = new LinkedList<PageConfig>();
			for (Integer pageid:pageids){
				pages.add(getPageConfig(siteId,pageid));
			}
			return pages;
		}
		return null;
		
	}

	public List<PageConfig> getAllPages(int siteId){
		List<PageConfig> pages = m_pages.getAllPages(siteId);
		if (pages!=null){
			for(Object p:pages){
				fillPortlets(siteId,(PageConfig)p);
			}
		}
		return pages;
	}

	public List<PageConfig> getAppPages(int siteId,String context){
		List<Integer> pageids= m_portlets.getAppPages(siteId,context);
		if (pageids!=null && pageids.size()>0){
			List<PageConfig> pages = m_pages.getAppPages(siteId,pageids);
			for (PageConfig page:pages){
				fillPortlets(siteId,page);
			}
			return pages;
		}else{
			return new ArrayList<PageConfig>();
		}
	}
	
	public PageConfig getPageConfig(int siteId,int resourceId){
		PageConfig page = null;
		page = getFromCache(siteId,resourceId);
		if (page==null){
			page = m_pages.getPageConfig(siteId,resourceId);
			if (page!=null){
				fillPortlets(siteId,page);
				saveToCache(siteId,resourceId, page);
			}
		}
		return page;
	}
	public boolean pageExist(int siteId,int resourceId){
		return m_pages.pageExist(siteId,resourceId);
	}
	
	public void removePage(int siteId,int resourceId){
		removeFromCache(siteId,resourceId);
		m_portlets.removePagePortlets(siteId,resourceId);
		m_pages.removePage(siteId,resourceId);
	}
	public void setCacheService(VWBCacheService cacheService){
		this.cache = cacheService;
		this.cache.setModulePrefix("portal");
	}
	
	public void setPageProvider(PortalPageProvider provider){
		this.m_pages=provider;
	}
	
	public void setPortletsProvider(PagePortletProvider provider){
		this.m_portlets=provider;
	}
	
	public void updatePage(int siteId,PageConfig pageConfig) {
		removeFromCache(siteId,pageConfig.getResourceId());
		m_pages.updatePage(siteId,pageConfig);
		m_portlets.removePagePortlets(siteId,pageConfig.getResourceId());
		m_portlets.insertPagePortlets(siteId,pageConfig.getResourceId(), convert(pageConfig.getPortletIds()));
	}
}
