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

package cn.vlabs.duckling.vwb.service.template.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.template.RemoteTemplate;
import cn.vlabs.duckling.vwb.service.template.SiteTemplate;

/**
 * 模板DAO
 * @date 2013-4-1
 * @author xiejj@cstnet.cn
 */
public class TemplateDAO extends ContainerBaseDAO {
	private RowMapper<RemoteTemplate> rowmapper = new RowMapper<RemoteTemplate>(){
		@Override
		public RemoteTemplate mapRow(ResultSet rs, int index) throws SQLException {
			RemoteTemplate template = new RemoteTemplate();
			template.setClbId(rs.getInt("clbId"));
			template.setName(rs.getString("name"));
			template.setType(rs.getString("type"));
			return template;
		}		
	};
	/**
	 * 查询所有的存放于CLB中的模板
	 * @return	模板的信息
	 */
	public List<RemoteTemplate> getAllTemplate(){
		String sql = "select * from vwb_template where type=?";
		return getJdbcTemplate().query(sql,new Object[]{SiteTemplate.TYPE_TEMPLATE},rowmapper);
	}

	public void create(RemoteTemplate template) {
		String sql = "insert into vwb_template(name, clbId,type) values(?,?,?)";
		getJdbcTemplate().update(sql,new Object[]{template.getName(), template.getClbId(),template.getType()});
	}
	
	public RemoteTemplate getTemplate(String name){
		String sql = "select * from vwb_template where name=?";
		List<RemoteTemplate> templates = getJdbcTemplate().query(sql, new Object[]{name}, rowmapper);
		if (templates.size()>0){
			return templates.get(0);
		}else{
			return null;
		}
	}

	public void update(RemoteTemplate template) {
		String sql = "update vwb_template set clbId=? and type=?  where name=?";
		getJdbcTemplate().update(sql, new Object[]{template.getClbId(), template.getType(), template.getName()});
	}

	public void deleteTemplate(String templateName) {
		String sql = "delete from vwb_template where name=?";
		getJdbcTemplate().update(sql,new Object[]{templateName});
	}
}