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

package org.apache.pluto.driver.services.impl.resource;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.pluto.driver.config.DriverConfigurationException;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.RenderConfigService;

/**
 * Default implementation of all of the portal Services.
 * Utilizes resource configuration from
 * <code>pluto-portal-driver-config.xml</code>
 *
 * @since Aug 10, 2005
 */
public class RenderConfigServiceImpl
    implements RenderConfigService {


    private ResourceConfig config;

//
// Lifecycle Methods
//
    /**
     * Initialization Lifecycle Method
     * @param ctx
     */
    public void init(ServletContext ctx) {
//    	BeanFactory factory =(BeanFactory) ctx.getAttribute(Attributes.APPLICATION_CONTEXT_KEY);
//    	pageService=(DBPortalPageService)factory.getBean("PortalPageService");
        try {
            InputStream in = ctx.getResourceAsStream(ResourceConfigReader.CONFIG_FILE);
            config = ResourceConfigReader.getFactory().parse(in);
        }
        catch(Exception e) {
            throw new DriverConfigurationException(e);
        }
    }

    /**
     * Shutdown the ResourceService.
     */
    public void destroy() {
        config = null;
    }


    public String getPortalName() {
        return config.getPortalName();
    }

    public String getPortalVersion() {
        return config.getPortalVersion();
    }

    public String getContainerName() {
        return config.getContainerName();
    }

    public Set getSupportedPortletModes() {
        return config.getSupportedPortletModes();
    }

    public Set getSupportedWindowStates() {
        return config.getSupportedWindowStates();
    }

    public List getPages() {
//        return pageService.getAllPages();
        return null;
    }

    public String getDefaultPage() {
    	return config.getRenderConfig().getDefaultPageId();
//        return pageService.getPageConfig(parsePageId(defaultPageId));
    }

    public PageConfig getPage(String id) {
    	return null;
//    	return pageService.getPageConfig(parsePageId(id));
    }

    public void addPage(PageConfig pageConfig) {
//    	pageService.addPage(pageConfig);
    }
    
    public void removePage(PageConfig pageConfig){
//    	pageService.removePage(pageConfig.getResourceId());
    }

    public void updatePage(PageConfig pageConfig){
//    	pageService.updatePage(pageConfig);
    }
}
