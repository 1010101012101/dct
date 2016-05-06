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

import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletWindow;

import cn.vlabs.vwb.container.spi.PortletWindowLocateService;
import cn.vlabs.vwb.container.spi.PortletWindowLocator;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 8, 2010 9:39:04 AM
 */
public class PortletWindowLocateServiceImpl implements
		PortletWindowLocateService {
	public PortletWindowLocator creatLocator(HttpServletRequest request, PortletContainer container, PortletWindow currentWindow) {
		return new PortletWindowLocatorImpl(request, container, currentWindow);
	}

}
