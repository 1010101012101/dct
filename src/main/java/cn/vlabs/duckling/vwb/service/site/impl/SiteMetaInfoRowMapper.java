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

package cn.vlabs.duckling.vwb.service.site.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.vwb.service.site.PublishState;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;


/**
 * Introduction Here.
 * @date 2010-5-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteMetaInfoRowMapper implements RowMapper<SiteMetaInfo> {

	public SiteMetaInfo mapRow(ResultSet rs, int index) throws SQLException {
		SiteMetaInfo smi = new SiteMetaInfo();
		smi.setId(rs.getInt("id"));
		smi.setState(SiteState.valueOf(rs.getString("state")));
		smi.setCreateTime(rs.getTimestamp("create_time"));
		smi.setPublished(PublishState.valueOf(rs.getString("published")));
		return smi;
	}

}
