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

package cn.vlabs.duckling.vwb.service.share.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.vwb.service.share.AccessRecord;

/**
 * @date May 24, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class SharePageAccessMapper implements RowMapper<AccessRecord> {
	public AccessRecord mapRow(ResultSet rs, int index) throws SQLException {
		AccessRecord ar = new AccessRecord();
		ar.setID(rs.getInt("ID"));
		ar.sethash(rs.getString("hash"));
		ar.setURL(rs.getString("URL"));
		ar.setAccessTime(rs.getLong("accessTime"));
		ar.setShareTime(rs.getLong("shareTime"));
	       
	    return ar;
	}
}
