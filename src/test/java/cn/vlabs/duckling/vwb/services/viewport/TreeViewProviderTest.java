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

package cn.vlabs.duckling.vwb.services.viewport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.SpringManage;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortType;
import cn.vlabs.duckling.vwb.service.viewport.impl.TreeViewProvider;


/**
 * Introduction Here.
 * @date 2010-2-4
 * @author euniverse
 */
public class TreeViewProviderTest {
	private SpringManage manager;
	@Before
	public void setUp() throws Exception {
		manager= new SpringManage();
		manager.init();
	}
	@After
	public void tearDown() throws Exception {
		manager.destroy();
	}
	@Test
	public void testUserService() {
		//测试状态下手动注入
		try{
		TreeViewProvider treeViewProvider = (TreeViewProvider) manager.getFactory().getBean("treeViewProvider");
		int invokeparent = treeViewProvider.get(ViewPortType.TRAIL,1, 3);
		System.out.println(invokeparent);
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
