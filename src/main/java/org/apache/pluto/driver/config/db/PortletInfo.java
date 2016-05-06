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

import org.apache.pluto.driver.services.portal.PortletWindowConfig;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class PortletInfo {
	private String contextPath;
	private String name;
	public static PortletInfo fromPortletId(String portletId){
		String contextPath = PortletWindowConfig.parseContextPath(portletId);
		String name = PortletWindowConfig.parsePortletName(portletId);
		return new PortletInfo(contextPath, name);
	}
	public PortletInfo(String contextPath,String name){
		this.contextPath=contextPath;
		this.name=name;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
