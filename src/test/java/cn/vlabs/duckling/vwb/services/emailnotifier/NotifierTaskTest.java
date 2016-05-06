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

package cn.vlabs.duckling.vwb.services.emailnotifier;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import cn.vlabs.duckling.vwb.service.timer.Task;
import cn.vlabs.duckling.vwb.services.TestService;

/**
 * Introduction Here.
 * @date Mar 16, 2010
 * @author xiejj@cnic.cn
 */
public class NotifierTaskTest extends TestService{

	@Test
	public void testExecute() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		Task task = getTask();
		task.execute(new Date());
		
	}
	private Task getTask(){
		return (Task)manager.getFactory().getBean("subscribeTask");
	}
}
