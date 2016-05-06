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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.driver.config.db.DBPortalPageService;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.url.PortalURL;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.vwb.container.spi.PortletWindowLocator;

/**
 * PortletWindow查找工具
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 8, 2010 12:31:50 PM
 */
public class PortletWindowLocatorImpl implements PortletWindowLocator {
	private DBPortalPageService pageService;
	private PortletContainer container;
	private PortalURL portalURL;
	private int siteId;
	
	private static Logger log = Logger.getLogger(PortletWindowLocator.class);
	public PortletWindowLocatorImpl(HttpServletRequest request, PortletContainer container, PortletWindow window){
		SiteMetaInfo site  = VWBContainerImpl.findSite(request);
		this.siteId = site.getId();
		this.pageService=VWBContainerImpl.findContainer().getPortalPageService();
		this.container = container;
		this.portalURL = ((PortletWindowImpl)window).getPortalURL();
	}
	public List<PortletWindow> locate(String context, String portlet) {
		List<PageConfig> pages = pageService.findByPortlet(siteId,context, portlet);
		LinkedList<PortletWindow> portletWindows = new LinkedList<PortletWindow>();
		if (pages!=null){
			for (PageConfig page:pages){
				Collection<String> portletIds = page.find(context, portlet);
				for (String portletId:portletIds){
					PortletWindow window = createPortletWindow(page, portletId);
					portletWindows.add(window);
				}
			}
		}
		return portletWindows;
	}
	
	private PortletWindow createPortletWindow(PageConfig page, String portletId) {
		PortalURL newOne = (PortalURL)portalURL.clone();
		newOne.setRenderPath(Integer.toString(page.getResourceId()));
		PortletWindowImpl window = new PortletWindowImpl(container,
				PortletWindowConfig.fromId(portletId),
				newOne);
		return window;
	}
	
	public List<PortletWindow>  locate(String pageid, String context, String portlet) {
		LinkedList<PortletWindow> portletWindows = new LinkedList<PortletWindow>();
		try{
			int ipageid = Integer.parseInt(pageid);
			PageConfig page = pageService.getPageConfig(siteId,ipageid);
			if (page!=null){
				Collection<String> portletIds = page.find(context, portlet);
				for (String portletId:portletIds){
					PortletWindow window = createPortletWindow(page, portletId);
					portletWindows.add(window);
				}
			}else{
				log.warn("PortalPage of "+pageid+" is not found.");
			}
		}catch(NumberFormatException e){
			log.warn("PortalPage of "+pageid+" is not found.");
		}
		return portletWindows;
	}
	
	public PortletWindow locate(String pageid, String context, String portlet, int sequence) {
		try{
			int ipageid = Integer.parseInt(pageid);
			PageConfig page = pageService.getPageConfig(siteId,ipageid);
			if (page != null){
				String portletId = page.find(context, portlet, sequence);
				return createPortletWindow(page, portletId);
			}
		}catch(NumberFormatException e){
			log.warn("PortalPage of "+pageid+" is not found.");
		}
		return null;
	}

}
