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

package cn.vlabs.duckling.vwb.service.skin.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.skin.RemoteSkin;

/**
 * 皮肤对象数据库操作
 * 
 * @date 2013-3-29
 * @author xiejj@cstnet.cn
 */
public class SkinDAO extends ContainerBaseDAO {
	private RowMapper<RemoteSkin> rowmapper = new RowMapper<RemoteSkin>() {
		@Override
		public RemoteSkin mapRow(ResultSet rs, int index) throws SQLException {
			RemoteSkin skin = new RemoteSkin();
			skin.setId(rs.getInt("id"));
			skin.setClbId(rs.getInt("clbId"));
			skin.setSiteId(rs.getInt("siteId"));
			skin.setName(rs.getString("name"));
			skin.setSpace(rs.getString("space"));
			skin.setTemplate(rs.getString("forTemplate"));
			return skin;
		}
	};

	public void delete(int siteId, String name) {
		String sql = "delete from vwb_skin where siteId=? and name=?";
		getJdbcTemplate().update(sql, new Object[] { siteId, name });
	};

	public List<RemoteSkin> getAllSkin(int siteId) {
		String sql = "select * from vwb_skin where siteId=?";
		return getJdbcTemplate().query(sql, new Object[] { siteId }, rowmapper);
	};
	
	public List<RemoteSkin> getAllTemplateSkin(int siteId,String forTemplate) {
		String sql = "select * from vwb_skin where siteId=? and forTemplate=?";
		return getJdbcTemplate().query(sql, new Object[] { siteId, forTemplate}, rowmapper);
	};

	public RemoteSkin getSkin(int siteId, String name) {
		String sql = "select * from vwb_skin where siteId=? and name=?";
		List<RemoteSkin> skins = getJdbcTemplate().query(sql,
				new Object[] { siteId, name }, rowmapper);
		if (skins.size() == 1) {
			return skins.get(0);
		} else {
			return null;
		}
	}

	public int insert(final RemoteSkin skin) {
		final String sql = "insert into vwb_skin(siteId, name, clbId, space,forTemplate) values(?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, skin.getSiteId());
				ps.setString(2, skin.getName());
				ps.setInt(3, skin.getClbId());
				ps.setString(4, skin.getSpace());
				ps.setString(5, skin.getTemplate());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}
	
	public void removeAll(int siteId) {
		String sql = "delete from vwb_skin where siteId=?";
		getJdbcTemplate().update(sql, new Object[]{siteId});
	};
}
