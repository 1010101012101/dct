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
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.dpage.TempPage;

/**
 * @date 2013-5-29
 * @author xiejj
 */
public class TempPageProvider extends ContainerBaseDAO {
	public void save(int siteId,int resourceId,String author,String content){
		String sql = "insert into vwb_temp_page(siteId, resourceId,author, content, last_modify) values(?,?,?,?,now())";
		getJdbcTemplate().update(sql, new Object[]{siteId,resourceId, author, content});
	}	
	public void remove(int siteId, int resourceId, String author){
		String sql = "delete from vwb_temp_page where siteId=? and resourceId=? and author=?";
		getJdbcTemplate().update(sql,new Object[]{siteId, resourceId, author});
	}
	
	public boolean exists(int siteId, int resourceId,String author){
		String sql = "select count(*) from vwb_temp_page where siteId=? and resourceId=? and author=?";
		Integer result = (Integer)getJdbcTemplate().queryForObject(sql, new Object[]{siteId, resourceId, author}, Integer.class);
		return (result.intValue()>0);
	}

	public TempPage getTempPage(int siteId, int resourceId, String author) {
		List<TempPage> page = getJdbcTemplate().query("select * from vwb_temp_page where siteId=? and resourceId=? and author=?", new Object[]{siteId, resourceId, author},new RowMapper<TempPage>(){
			@Override
			public TempPage mapRow(ResultSet rs, int index) throws SQLException {
				TempPage page = new TempPage();
				page.setAuthor(rs.getString("author"));
				page.setContent(rs.getString("content"));
				page.setLastModifier(rs.getDate("last_modify"));
				page.setResourceId(rs.getInt("resourceId"));
				return page;
			}});
		if (page.size()>0){
			return page.get(0);
		}else{
			return null;
		}
	}

	public void update(int siteId, int resourceId, String author, String content) {
		String sql = "update vwb_temp_page set content=?, last_modify=now() where siteId=? and resourceId=? and author=?";
		getJdbcTemplate().update(sql, new Object[]{content,siteId,resourceId, author});
	}
}
