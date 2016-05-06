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

package cn.vlabs.duckling.vwb.service.viewport.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.impl.ViewPortProvider;

/**
 * Introduction Here.
 * 
 * @date Feb 3, 2010
 * @author Yong Ke(keyong@cnic.cn)
 */
public class ViewPortProviderImp extends ContainerBaseDAO implements
		ViewPortProvider {
	private static final String sCreateSql = "INSERT INTO vwb_resource_info(id, siteId, type,"
			+ "title,parent, trail,left_menu, top_menu,footer,banner,create_time,creator,acl)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String sDeleteSql = "DELETE  FROM vwb_resource_info WHERE siteId=? and id=?";
	private static final String sQueryByTitle = "SELECT id,siteId, title,type FROM vwb_resource_info where siteId=? and title like ? group by id order by id desc";
	private static final String sQuerySonSql = "SELECT id FROM vwb_resource_info WHERE siteId=? and parent=?";
	private static final String sQuerySql = "SELECT * FROM vwb_resource_info WHERE siteId=? and id=?";
	private static final String sUpdateSonSql = "UPDATE vwb_resource_info SET parent=? WHERE siteId=? and parent=?";
	private static final String sUpdateSql = "UPDATE vwb_resource_info SET type=?,"
			+ "title=?, parent=?, trail=?, left_menu=?, top_menu=?,"
			+ "footer=?,banner=?,create_time=?,creator=?, acl=?"
			+ " WHERE siteId=? and id=?";

	protected static final Logger log = Logger
			.getLogger(ViewPortProviderImp.class);

	private VWBCacheService d_cache;

	private RowMapper<ViewPort> rowMapper =new RowMapper<ViewPort>(){
		@Override
		public ViewPort mapRow(ResultSet rs, int index)
				throws SQLException {
			ViewPort vp = new ViewPort();
			vp.setSiteId(rs.getInt("siteId"));
			vp.setBanner(rs.getInt("banner"));
			vp.setCreator(rs.getString("creator"));
			vp.setLeftMenu(rs.getInt("left_menu"));
			vp.setTrail(rs.getInt("trail"));
			vp.setTopMenu(rs.getInt("top_menu"));
			vp.setCreateTime(rs.getTimestamp("create_time"));
			vp.setParent(rs.getInt("parent"));
			vp.setFooter(rs.getInt("footer"));
			vp.setTrail(rs.getInt("trail"));
			vp.setId(rs.getInt("id"));
			vp.setTitle(rs.getString("title"));
			vp.setType(rs.getString("type"));
			vp.setAclPolicy(rs.getInt("acl"));
			return vp;
		}
		
	};

	@Override
	public int create(final ViewPort vp) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement statement = conn.prepareStatement(sCreateSql);
				int i = 0;
				statement.setInt(++i, vp.getId());
				statement.setInt(++i, vp.getSiteId());
				statement.setString(++i, vp.getType());
				statement.setString(++i, vp.getTitle());
				statement.setInt(++i, vp.getParent());
				statement.setInt(++i, vp.getTrail());
				statement.setInt(++i, vp.getLeftMenu());
				statement.setInt(++i, vp.getTopMenu());
				statement.setInt(++i, vp.getFooter());
				statement.setInt(++i, vp.getBanner());
				if (vp.getCreateTime() == null) {
					statement.setTimestamp(++i, new Timestamp(Calendar
							.getInstance().getTimeInMillis()));
				} else {
					statement.setTimestamp(++i, new Timestamp(vp
							.getCreateTime().getTime()));
				}
				statement.setString(++i, vp.getCreator());
				statement.setInt(++i, vp.getAclPolicy());
				return statement;
			}
		});
		d_cache.putInCache(vp.getSiteId(), String.valueOf(vp.getId()), vp);
		return vp.getId();
	}

	@Override
	public List<ViewPort> getAllViewPort(int siteId) {
		String sql = "select * from vwb_resource_info where siteId=?";
		return getJdbcTemplate().query(sql, new Object[]{siteId}, rowMapper);
	}

	@Override
	public int getMaxId(int siteId) {
		String sql = "select max(id) from vwb_resource_info where siteId=?";
		List<Integer> result = getJdbcTemplate().query(sql,
				new Object[] { siteId }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int index)
							throws SQLException {
						return rs.getInt(1);
					}
				});
		if (result.size() != 0) {
			return result.get(0);
		} else {
			return 0;
		}
	}

	@Override
	public ViewPort getViewPort(int siteId, int id) {
		ViewPort view = (ViewPort) d_cache.getFromCache(siteId, String.valueOf(id));

		if (view == null) {
			List<ViewPort> viewports =getJdbcTemplate().query(sQuerySql,
					new Integer[] { siteId, id }, rowMapper);
			if (viewports.size()>0){
				view = viewports.get(0);
			}
			
			if (view!=null){
				d_cache.putInCache(siteId, Integer.toString(id), view);
			}
		}

		if (view != null)
			return (ViewPort) view.clone();
		else
			return null;
	}

	@Override
	public void remove(int siteId, int vid) {
		getJdbcTemplate().update(sDeleteSql, new Object[] { siteId, vid });
		d_cache.removeEntry(siteId, String.valueOf(vid));
	}

	@Override
	public List<ViewPort> searchResourceByTitle(int siteId, String title) {
		title = title + "%";
		List<ViewPort> dpages = getJdbcTemplate().query(sQueryByTitle,
				new Object[] { siteId, title }, new RowMapper<ViewPort>() {
					public ViewPort mapRow(ResultSet rs, int index)
							throws SQLException {
						ViewPort viewport = new ViewPort();
						viewport.setId(rs.getInt("id"));
						viewport.setSiteId(rs.getInt("siteId"));
						viewport.setTitle(rs.getString("title"));
						viewport.setType(rs.getString("type"));
						return viewport;
					}
				});
		return dpages;
	}

	public void setCache(VWBCacheService cache) {
		this.d_cache = cache;
		this.d_cache.setModulePrefix("vp");
	}
	@Override
	public void update(ViewPort vp) {
		getJdbcTemplate().update(
				sUpdateSql,
				new Object[] { vp.getType(), vp.getTitle(), vp.getParent(),
						vp.getTrail(), vp.getLeftMenu(), vp.getTopMenu(),
						vp.getFooter(), vp.getBanner(), vp.getCreateTime(),
						vp.getCreator(), vp.getAclPolicy(), vp.getSiteId(),
						vp.getId() });
		d_cache.putInCache(vp.getSiteId(), String.valueOf(vp.getId()), vp);
	}
	@Override
	public void updateSon(int siteId, int parentId, int newParent) {
		List<Integer> listSonIDs = getJdbcTemplate().query(sQuerySonSql,
				new Object[] { siteId, parentId }, new RowMapper<Integer>() {
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});

		getJdbcTemplate().update(sUpdateSonSql,
				new Object[] { newParent, siteId, parentId });

		if (listSonIDs != null) {
			for (Integer iSon : listSonIDs) {
				d_cache.removeEntry(siteId, iSon.toString());
			}
		}
	}
}
