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

package cn.vlabs.duckling.vwb.services;

import org.junit.After;
import org.junit.Before;

import cn.vlabs.duckling.SpringManage;
import junit.framework.TestCase;

/**
 * Introduction Here.
 * @date Feb 3, 2010
 * @author Yong Ke(keyong@cnic.cn)
 */
public class TestService extends TestCase {
	protected SpringManage manager;
	@Before
	public void setUp() throws Exception {
		manager= new SpringManage();
		manager.init();
	}

	@After
	public void tearDown() throws Exception {
		manager.destroy();
	}
}
