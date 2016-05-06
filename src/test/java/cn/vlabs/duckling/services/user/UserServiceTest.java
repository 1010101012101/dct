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

package cn.vlabs.duckling.services.user;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.vlabs.duckling.SpringManage;
import cn.vlabs.duckling.vwb.service.user.IUserService;


/**
 * Introduction Here.
 * @date 2010-2-2
 * @author euniverse
 */
public class UserServiceTest {
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
		IUserService userService = (IUserService) manager.getFactory().getBean("userService");
		System.out.println(userService.getTrueName("admin@root.umt"));
	}
}
