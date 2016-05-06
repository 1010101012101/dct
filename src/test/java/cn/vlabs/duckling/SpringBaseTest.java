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

package cn.vlabs.duckling;

import junit.framework.TestCase;

import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Introduction Here.
 * @date May 10, 2010
 * @author zzb
 */
public class SpringBaseTest extends TestCase {
	
	protected FileSystemXmlApplicationContext factory;
	
	@Override
	protected void setUp() throws Exception {
		System.setProperty("dct.root", "WebRoot");
		String contextxml ="WebRoot/WEB-INF/conf/VWBContext.xml";
		factory= new FileSystemXmlApplicationContext(contextxml);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		factory = null;
		super.tearDown();
	}

}
