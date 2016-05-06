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

package cn.vlabs.duckling.vwb.services.config;

import cn.vlabs.duckling.vwb.service.config.IContainerConfig;
import cn.vlabs.duckling.vwb.services.TestService;



/**
 * Introduction Here.
 * @date Feb 3, 2010
 * @author Yong Ke(keyong@cnic.cn)
 */
public class ConfigTest extends TestService {
	public void testGet(){
		IContainerConfig cfg = (IContainerConfig)manager.getFactory().getBean("configService");
		assertEquals(cfg.getProperty("i18n.bundle_name",""), "CoreResources");
		assertEquals(cfg.getInt("c3p0.initialPoolSize",-1), 10);
		
	}
	
	public void testReplace(){
		IContainerConfig cfg = (IContainerConfig)manager.getFactory().getBean("configService");
		assertEquals(cfg.getProperty("duckling.umt",""),cfg.getProperty("duckling.umt.site","")+"/votree/VOTreeServlet");
	}
	
	public void testSet(){
		IContainerConfig cfg = (IContainerConfig)manager.getFactory().getBean("configService");
		cfg.setProperty("i18n.bundle_name", "CoreResources");
		assertEquals("CoreResources",cfg.getProperty("i18n.bundle_name"));
	}
}
