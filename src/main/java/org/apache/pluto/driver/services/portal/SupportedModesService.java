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

package org.apache.pluto.driver.services.portal;

import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;

import org.apache.pluto.PortletContainerException;

/**
 * Allows clients to determine if a particular PortletMode is supported
 * by the portal, a particular portlet, or both.
 *
 * @version $Id: SupportedModesService.java,v 1.3 2009/05/21 01:44:07 kevin Exp $
 * @since September 9, 2006
 * @see PortletMode
 */
public interface SupportedModesService extends DriverConfigurationService {
    
    /**
     * Returns true if the portlet and the portal support the supplied mode.
     * @param portletId the id uniquely identifiying the portlet
     * @param mode the portlet mode
     * @return true if the portlet and portal both support the supplied mode
     */
    boolean isPortletModeSupported(String portletId, String mode);
    
    /**
     * Returns true if the portal supports the supplied mode.
     * @param mode the portlet mode
     * @return true if the portal supports the supplied mode
     */
    boolean isPortletModeSupportedByPortal(String mode);
    
    /**
     * Returns true if the portlet supports the supplied mode.
     * @param portletId the id uniquely identifying the portlet
     * @param mode the portlet mode
     * @return true if the portlet supports the supplied mode.
     */
    boolean isPortletModeSupportedByPortlet(String portletId, String mode);

    /**
     * For PTL.8.4 implementation of portlet-managed modes, defined in
     * portlet.xml where portlet child element custom-portlet-mode/portal-managed 
     * value is false.
     * 
     * @param portletId the ID of the portlet
     * @param mode the portlet mode as defined in the custom-portlet-mode/portlet-mode
     * and supports/portlet-mode elements.
     * @return
     */
    boolean isPortletManagedMode(String portletId, String mode);    

	/**
	 * Gets all modes supported by a portlet that are defined in the portlet's supports child element 
	 * in portlet.xml.
	 * 
     * @param portletId Id of the portlet of interest
	 * @return all portlet modes supported by a portlet.
	 * @throws PortletContainerException
     */
    Set<PortletMode> getSupportedPortletModes(String portletId) throws PortletContainerException;

	PortletConfig getPortletConfig(String portletId) throws PortletContainerException;

}
