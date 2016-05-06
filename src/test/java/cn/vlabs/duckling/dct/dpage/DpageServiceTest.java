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

package cn.vlabs.duckling.dct.dpage;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.SpringManage;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.dpage.provider.DPageProvider;


/**
 * Introduction Here.
 * @date 2010-2-4
 * @author euniverse
 */
public class DpageServiceTest {
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
			DPageService dpageServiceInterface = (DPageService) manager.getFactory().getBean("dpageService");
			DPage dto  = dpageServiceInterface.getLatestDpageByResourceId(1,2);
			 dto  = dpageServiceInterface.getLatestDpageByResourceId(1,2);
			System.out.println(dto.getResourceId());
			
			
			DPageProvider providerInterface = (DPageProvider) manager.getFactory().getBean("dpageProviderCache");
			 List<LightDPage> dpages = providerInterface.getDpagesSinceDate(1,new Date());
			
			System.out.println(dpages.size());
			
		
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
