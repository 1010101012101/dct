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
package cn.vlabs.duckling.util;

import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
 
import cn.vlabs.duckling.SpringManage;

public class BaseDAOTest {
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
	public void testSetJdbcTemplate() {
		//测试状态下手动注入
		JdbcTemplate template = (JdbcTemplate) manager.getFactory().getBean("jdbcTemplate");
		TestDAO td = new TestDAO();
		td.setJdbcTemplate(template);
		assertTrue(td.getCount()>0);
	}

	private class TestDAO extends SiteBaseDAO{
		public int getCount(){
			int count =	(Integer)getJdbcTemplate().query("show tables", new ResultSetExtractor(){

				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					int count=0;
					while (rs.next()){
						count++;
					}
					System.out.println(count+" tables found");
					return count;
				}
				
			});
			return count;
		}
	}
}
