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

package cn.vlabs.duckling.vwb.service.dpage.provider;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;

/**
 * Introduction Here.
 * @date 2010-2-8
 * @author euniverse
 */
public class DPageMapper implements RowMapper<DPagePo> {
	public DPagePo mapRow(ResultSet rs, int index) throws SQLException {
		DPagePo dpage = new DPagePo();
		dpage.setId(rs.getInt("id"));
		dpage.setSiteId(rs.getInt("siteId"));
		dpage.setResourceId(rs.getInt("resourceid"));
		dpage.setVersion(rs.getInt("version"));
		dpage.setTime(rs.getTimestamp("change_time"));
		dpage.setContent(rs.getString("content"));
		dpage.setCreator(rs.getString("change_by"));
		dpage.setTitle(rs.getString("title"));
		if(dpage.getContent()!=null)
		{
			dpage.setSize(dpage.getContent().length());
		}
		return dpage;
	}

}
