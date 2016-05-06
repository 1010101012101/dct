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

package cn.vlabs.duckling.vwb.services.init;

import static org.junit.Assert.*;

import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.init.SQLReader;
import cn.vlabs.duckling.vwb.service.init.WrongSQL;

/**
 * Introduction Here.
 * @date Feb 23, 2010
 * @author xiejj@cnic.cn
 */
public class SQLReaderTest {
	private SQLReader reader;
	@Before
	public void setUp() throws Exception {
		reader = new SQLReader (new FileInputStream("WebRoot/WEB-INF/conf/dct.sql"), "UTF-8");
	}

	/**
	 * Brief Intro Here
	 * @param
	 */
	@After
	public void tearDown() throws Exception {
		reader.close();
		reader = null;
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.init.SQLReader#next()}.
	 * @throws WrongSQL 
	 */
	@Test
	public void testNext() throws WrongSQL {
		int count =0;
		String sql;
		while ((sql = reader.next())!=null){
			count++;
			System.out.println(sql);
		}
		assertTrue(count>10);
	}

}
