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

package org.apache.pluto.driver.services.container;

import javax.portlet.PortalContext;

import org.apache.pluto.OptionalContainerServices;
import org.apache.pluto.RequiredContainerServices;
import org.apache.pluto.core.PlutoContainerServices;
import org.apache.pluto.driver.config.DriverConfiguration;
import org.apache.pluto.spi.CCPPProfileService;
import org.apache.pluto.spi.ContainerInvocationService;
import org.apache.pluto.spi.PortalCallbackService;

import cn.vlabs.vwb.container.spi.PortletWindowLocateService;
import cn.vlabs.vwb.container.spi.SiteEnviromentService;
import cn.vlabs.vwb.driver.services.container.PortletWindowLocateServiceImpl;
import cn.vlabs.vwb.driver.services.container.SiteEnviromentServiceImpl;

/**
 * The Portal Driver's <code>PortletContainerServices</code> implementation. The
 * <code>PortletContainerServices</code> interface is the main integration point
 * between the pluto container and the surrounding portal.
 * @version 1.0
 * @since Sep 21, 2004
 */
public class ContainerServicesImpl extends PlutoContainerServices implements RequiredContainerServices, OptionalContainerServices 
{
    private DriverConfiguration driverConfig;

    private PortletWindowLocateService portletWindowService;
    
    private SiteEnviromentService siteEnviromentService;
    /**
     * Default Constructor.
     */
    public ContainerServicesImpl(PortalContext context,
                                 DriverConfiguration driverConfig,
                                 CCPPProfileService ccppProfileService,
                                 ContainerInvocationService containerInvocation) 
    {
        super(context, ccppProfileService, containerInvocation, driverConfig.getPortalCallbackService());
        this.driverConfig = driverConfig;
    }

    
    /**
     * The PortletPreferencesService provides access to the portal's
     * PortletPreference persistence mechanism.
     * @return a PortletPreferencesService instance.
     */
//    public PortletPreferencesService getPortletPreferencesService() 
//    {
//        return driverConfig.getPortletPreferencesService();
//    }

    /**
     * The PortalCallbackService allows the container to communicate
     * actions back to the portal.
     * @return a PortalCallbackService implementation.
     */
    public PortalCallbackService getPortalCallbackService() 
    {
        return driverConfig.getPortalCallbackService();
    }

    public PortletWindowLocateService getPortletWindowLocateService(){
    	if (portletWindowService==null){
    		portletWindowService = new PortletWindowLocateServiceImpl();
    	}
		return portletWindowService;
    }
    
    public  SiteEnviromentService getSiteEnviromentService(){
    	if (siteEnviromentService==null){
    		siteEnviromentService = new SiteEnviromentServiceImpl();
    	}
    	return siteEnviromentService;
    }
}

