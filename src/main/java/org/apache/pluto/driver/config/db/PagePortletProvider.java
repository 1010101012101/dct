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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class PagePortletProvider extends ContainerBaseDAO{
	private static final String removePortlet="delete from vwb_portal_portlets where siteId=? and resourceId=?";
	private static final String insertPortlet="insert into vwb_portal_portlets(siteId,resourceId, context, name) values(?,?, ?, ?)";
	private static final String selectPortlet="select * from vwb_portal_portlets where siteId=? and resourceId=?";
	private static final String selectByContext_Portlet="select * from vwb_portal_portlets where siteId=? and context=? and name=?";
	private static final String selectAppPages="select distinct(resourceId) from vwb_portal_portlets where siteId=? and context=?";
	
	@SuppressWarnings("unchecked")
	public List<PortletInfo> getPagePortlets(int siteId,int resourceId){
		return getJdbcTemplate().query(selectPortlet, new Object[]{siteId,resourceId}, new RowMapper(){
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				String context= rs.getString("context");
				String name = rs.getString("name");
				return new PortletInfo(context, name);
			}
		});
	}
	public void removePagePortlets(int siteId,int resourceId){
		getJdbcTemplate().update(removePortlet,new Object[]{siteId,resourceId});
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findBy(int siteId,String context, String portletName){
		return getJdbcTemplate().query(selectByContext_Portlet
				, new Object[] {siteId,context, portletName}
				, new RowMapper(){
					public Object mapRow(ResultSet rs, int index) throws SQLException {
						return rs.getInt("resourceId");
					}
		});
	}
	public void insertPagePortlets(final int siteId,final int resourceId, final Collection<PortletInfo> c){
		if (c!=null&&c.size()>0){
			getJdbcTemplate().batchUpdate(insertPortlet, new BatchPreparedStatementSetter(){
				private Iterator<PortletInfo> iter=c.iterator();
				public int getBatchSize() {
					return c.size();
				}
	
				public void setValues(PreparedStatement ps, int count) throws SQLException {
					PortletInfo portletId= iter.next();
					int i=0;
					ps.setInt(++i, siteId);
					ps.setInt(++i, resourceId);
					ps.setString(++i, portletId.getContextPath());
					ps.setString(++i, portletId.getName());
				}
				
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAppPages(int siteId,String context) {
		if (!context.startsWith("/"))
			context = "/"+context;
		return (List<Integer>)getJdbcTemplate().query(selectAppPages, new Object[]{siteId,context}, new RowMapper(){
			public Object mapRow(final ResultSet rs, final int index) throws SQLException {
				return rs.getInt(1);
			}
		});
		
	}
}
