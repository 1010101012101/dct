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

package org.apache.pluto.driver.config.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.pluto.driver.services.portal.PageConfig;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class PortalPageProvider extends ContainerBaseDAO {
	private static final String queryPage = "select * from vwb_portal_page where siteId=? and resourceId=?";
	private static final String queryAll = "select * from vwb_portal_page where siteId=?";
	private static final String removePage = "delete from vwb_portal_page where siteId=? and resourceId=?";
	private static final String insertPage="insert into vwb_portal_page(siteId,resourceId, title, uri) values(?,?, ?,?)";
	private static final String existPage = "select count(*) from vwb_portal_page where siteId=? and resourceId=?";
	private static final String updatePage = "update vwb_portal_page set title=?, uri=? where siteId=? and resourceId=?";
	private static final String selectPages = "select * from vwb_portal_page where siteId=? and resourceId in";
	public PageConfig getPageConfig(int siteId,int resourcId){
		return (PageConfig)getJdbcTemplate().query(queryPage, new Object[]{siteId,resourcId}, new ResultSetExtractor(){
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next())
					return readPageConfig(rs);
				else
					return null;
			}
		});
	}
	public void removePage(int siteId,int resourceId){
		getJdbcTemplate().update(removePage, new Object[]{siteId,resourceId});
	}
	public void creatPage(int siteId, PageConfig page){
		getJdbcTemplate().update(insertPage, new Object[]{siteId,page.getResourceId(),page.getTitle(), page.getUri()});
	}
	
	public void updatePage(int siteId,PageConfig page) {
		getJdbcTemplate().update(updatePage, new Object[]{page.getTitle(), page.getUri(),siteId, page.getResourceId()});
	}
	
	@SuppressWarnings("unchecked")
	public List<PageConfig> getAllPages(int siteId){
		return getJdbcTemplate().query(queryAll,new Object[]{siteId}, new RowMapper(){
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				return readPageConfig(rs);
			}
			
		});
	}
	public boolean pageExist(int siteId,int resourceId){
		return getJdbcTemplate().queryForInt(existPage, new Object[]{siteId,resourceId})>0;
	}
	private PageConfig readPageConfig(ResultSet rs) throws SQLException {
		PageConfig config = new PageConfig();
		config.setTitle(rs.getString("title"));
		config.setUri(rs.getString("uri"));
		config.setResourceId(rs.getInt("resourceId"));
		return config;
	}
	
	@SuppressWarnings("unchecked")
	public List<PageConfig> getAppPages(int siteId, List<Integer> pageids) {
		String sql = selectPages+set2Sql(pageids);
		return (List<PageConfig>)getJdbcTemplate().query(sql,new Object[]{siteId}, new RowMapper(){
			public Object mapRow(final ResultSet rs, final int index) throws SQLException {
				return readPageConfig(rs);
			}
		});
	}
	private String set2Sql(List<Integer> ids){
		String sql = "(";
		boolean first=true;
		for (Integer id:ids){
			if (!first){
				sql+=","+id;
			}else{
				sql+=id;
				first=false;
			}
		}
		sql+=")";
		return sql;
	}
}
