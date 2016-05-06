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

package org.apache.pluto.driver.services.portal.admin;

import org.apache.pluto.driver.config.DriverConfigurationException;
import org.apache.pluto.driver.services.portal.PageConfig;

/**
 *
 *
 * @author <a href="mailto:ddewolf@apache.org">David H. DeWolf</a>:
 * @version 1.0
 * @since Nov 30, 2005
 * @deprecated using PortalPageService
 */
public interface RenderConfigAdminService {
	/**
	 * Add a portal page to configuration.
	 * @param config The portal page's configuration infomation.
	 * @throws DriverConfigurationException while Page has exist, it will throw off this Exception
	 * @deprecated
	 */
    void addPage(PageConfig config) throws DriverConfigurationException;
    /**
     * Remove a portal page from configuration.
     * @param pageConfig The PageConfig
     * @deprecated
     */
    void removePage(PageConfig pageConfig);
    /**
     * Update a portal page to configuration.
     * @param pageConfig The portal page's config.
     * @deprecated
     */
    void updatePage(PageConfig pageConfig);
}
