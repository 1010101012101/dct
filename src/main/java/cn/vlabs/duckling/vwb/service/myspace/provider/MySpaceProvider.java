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

package cn.vlabs.duckling.vwb.service.myspace.provider;

import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;

/**
 * Introduction Here.
 * 
 * @date 2010-3-25
 * @author Fred Zhang (fred@cnic.cn)
 */
public class MySpaceProvider extends ContainerBaseDAO {
	private static final String CREATE_SQL = "insert into vwb_myspace (eid,resourceId, siteId) values(?,?,?)";
	private static final String QUERY_SQL = "select * from vwb_myspace where siteId=? and eid=?";
	private static final String removeSQL = "delete from vwb_myspace where siteId=? and resourceId=?";
	private static final String selectMySpace = "select * from vwb_myspace where siteId=? and resourceId=?";

	public void createMySpace(MySpace main) {
		this.getJdbcTemplate().update(
				CREATE_SQL,
				new Object[] { main.getUser(), main.getResourceId(),
						main.getSiteId() });
	}

	public MySpace getMySpace(int siteId, String user) {
		MySpace mySpace = null;
		try {
			mySpace = (MySpace) getJdbcTemplate().queryForObject(QUERY_SQL,
					new Object[] { siteId, user }, new MySpaceMapper());
		} catch (EmptyResultDataAccessException e) {
		}
		return mySpace;
	}

	public void deleteMySpace(int siteId, int resourceId) {
		getJdbcTemplate()
				.update(removeSQL, new Object[] { siteId, resourceId });
	}

	public MySpace getMySpace(int siteId, int resourceId) {
		MySpace mySpace = null;
		try {
			mySpace = (MySpace) getJdbcTemplate().queryForObject(selectMySpace,
					new Object[] { siteId, resourceId }, new MySpaceMapper());
		} catch (EmptyResultDataAccessException e) {
		}
		return mySpace;
	}

}
