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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.site.ISiteMetaInfoProvider;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteMetaInfoProvider extends ContainerBaseDAO implements ISiteMetaInfoProvider{
	protected static final Logger log = Logger
			.getLogger(SiteMetaInfoProvider.class);

	@Override
	public void updateSiteMetaInfo(SiteMetaInfo smi) {
		getJdbcTemplate().update(
				"update vwb_site set published=?, state=? where id=?",
				new Object[] { smi.getPublished().getValue(), smi.getState().getValue(), smi.getId() });
	}

	@Override
	public List<SiteMetaInfo> getAllSites() {
		String sql = "SELECT * FROM vwb_site order by create_time desc";
		return getJdbcTemplate().query(sql,	new SiteMetaInfoRowMapper());
	}

	@Override
	public SiteMetaInfo getSiteMetaInfoById(int id) {
		SiteMetaInfo smi = null;
		try {
			String sql = "SELECT * FROM vwb_site where id=" + id
					+ "  order by id";
			smi = getJdbcTemplate().queryForObject(sql, new SiteMetaInfoRowMapper());
		} catch (EmptyResultDataAccessException e) {
			log.info("No site( id = " + id + ") are found");
		}
		return smi;
	}

	@Override
	public SiteMetaInfo createSiteMetaInfo(final SiteMetaInfo smi) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = "insert into vwb_site (state,create_time,published) values(?,?,?)";
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(++i, smi.getState().getValue());
				ps.setTimestamp(++i, new Timestamp(Calendar.getInstance()
						.getTimeInMillis()));
				ps.setString(++i, smi.getPublished().getValue());
				return ps;
			}
		}, keyHolder);
		smi.setId(Integer.valueOf(keyHolder.getKey().intValue()));
		return smi;
	}

	private final static String deleteSiteMeta = "delete from vwb_site where id=?";

	@Override
	public void delete(int siteId) {
		getJdbcTemplate().update(deleteSiteMeta, new Object[] { siteId });
	}
}
