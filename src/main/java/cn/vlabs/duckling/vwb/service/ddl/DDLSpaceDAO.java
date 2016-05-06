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

package cn.vlabs.duckling.vwb.service.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;

/**
 * @date 2015-12-24
 * @author xiejj@cstnet.cn
 */
public class DDLSpaceDAO extends ContainerBaseDAO {
	private RowMapper<DDLSpace> mapper = new RowMapper<DDLSpace>(){
		@Override
		public DDLSpace mapRow(ResultSet rs, int index)
				throws SQLException {
			DDLSpace space = new DDLSpace();
			space.setSiteId(rs.getInt("siteId"));
			space.setCode(rs.getString("code"));
			return space;
		}
	};
	
	public DDLSpace find(int siteId){
		String sql = "select * from vwb_ddl_space where siteId=?";
		List<DDLSpace> spaces = getJdbcTemplate().query(sql, new Object[]{siteId}, mapper);
		if (spaces.size()>0){
			return spaces.get(0);
		}else{
			return null;
		}
	}
	
	
	public void update(DDLSpace space){
		String sql = "update vwb_ddl_space set code=? where siteId=?";
		getJdbcTemplate().update(sql, space.getCode(), space.getSiteId());
	}
	
	public void save(DDLSpace space){
		String sql = "insert into vwb_ddl_space(siteId, code) values(?,?)";
		getJdbcTemplate().update(sql, space.getSiteId(), space.getCode());
	}
	
	public void delete(int siteId){
		String sql = "delete from vwb_ddl_space where siteId=?";
		getJdbcTemplate().update(sql, siteId);
	}
}
