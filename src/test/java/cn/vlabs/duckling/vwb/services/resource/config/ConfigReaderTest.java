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

package cn.vlabs.duckling.vwb.services.resource.config;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigReader;


/**
 * Introduction Here.
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public class ConfigReaderTest {
	private ConfigReader reader;
	@Before
	public void setUp() throws Exception {
		reader = new ConfigReader();
	}

	@After
	public void tearDown() throws Exception {
		reader = null;
	}

	@Test
	public void testFromXML() throws FileNotFoundException {
		FileInputStream in = new FileInputStream("WebRoot/WEB-INF/pages/dpages.xml");
		List<ConfigItem> items = reader.fromXML(in);
		assertNotNull(items);
		for (ConfigItem item:items){
			System.out.println(item.getId()+":leftmenu="+item.getLeftmenu());
		}
	}
	
	@Test
	public void testReadFunctions() throws FileNotFoundException {
		FileInputStream in = new FileInputStream("WebRoot/WEB-INF/conf/functions.xml");
		List<ConfigItem> items = reader.fromXML(in);
		assertNotNull(items);
		for (ConfigItem item:items){
			System.out.println(item.getId()+":leftmenu="+item.getLeftmenu());
		}
	}
	
	@Test
	public void testReadPortals() throws FileNotFoundException {
		FileInputStream in = new FileInputStream("WebRoot/WEB-INF/conf/portals.xml");
		List<ConfigItem> items = reader.fromXML(in);
	}
}
