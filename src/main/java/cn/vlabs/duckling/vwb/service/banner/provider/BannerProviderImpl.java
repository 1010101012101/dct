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

package cn.vlabs.duckling.vwb.service.banner.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.impl.BannerProvider;

/**
 * Introduction Here.
 * @date 2010-2-26
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerProviderImpl extends ContainerBaseDAO implements
		BannerProvider {
	public static final String createBannerSQL = "insert into vwb_banner "
			+ "(siteId,dirName,name,status,type,creator,createdTime,leftPictureClbId,rightPictureClbId,middlePictureClbId,cssClbId,pageId,ownedtype,bannerProfile) "
			+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String deleteSql = "delete from vwb_banner where id=?";

	protected static final Logger log = Logger
			.getLogger(BannerProviderImpl.class);

	private static final String selectBanner = "select * from vwb_banner where siteId=?";

	private static final String selectBannerById = "SELECT * FROM vwb_banner where id=?";
	
	private static final String selectBySiteId = "SELECT * FROM vwb_banner where siteId=? order by id";

	private static final String selectDefaultBanner = "select * from vwb_banner where status=2 and siteId=?";

	private static final String selectPublic = "select * from vwb_banner where (creator=? or ownedtype='system' or ownedtype='public') and siteId=?";

	private static final String updateBanner = "update vwb_banner set siteId=?,dirName=?,name=?,status=?,type=?,creator=?,leftPictureClbId=?,rightPictureClbId=?,middlePictureClbId=?,pageId=?,ownedtype=?,bannerProfile=? where id=?";

	public Banner createBanner(final int siteId,Banner banner) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final Banner tempBanner = banner;
		tempBanner.setBannerProfile();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(
						createBannerSQL,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(++i, siteId);
				ps.setString(++i, tempBanner.getDirName());
				ps.setString(++i, tempBanner.getName());
				ps.setInt(++i, tempBanner.getStatus());
				ps.setInt(++i, tempBanner.getType());
				ps.setString(++i, tempBanner.getCreator());
				ps.setTimestamp(++i, new Timestamp(tempBanner.getCreatedTime()
						.getTime()));
				ps.setInt(++i, tempBanner.getLeftPictureClbId());
				ps.setInt(++i, tempBanner.getRightPictureClbId());
				ps.setInt(++i, tempBanner.getMiddlePictureClbId());
				ps.setInt(++i, tempBanner.getCssClbId());
				ps.setInt(++i, tempBanner.getBannerTitle());
				ps.setString(++i, tempBanner.getOwnedtype());
				ps.setString(++i,tempBanner.getBannerProfile());
				return ps;
			}
		}, keyHolder);
		banner.setId(Integer.valueOf(keyHolder.getKey().intValue()));
		return banner;

	}

	public void delete(int id) {
		getJdbcTemplate().update(deleteSql, new Object[] { id });
	}

	public List<Banner> getAllBanners(int siteId) {
		List<Banner> banners = new ArrayList<Banner>();
		banners = getJdbcTemplate().query(selectBanner,new Object[]{siteId},
				new BannerRowMapper());
		return banners;
	}

	public List<Banner> getAllBannersByUser(String user,int siteId) {
		List<Banner> banners = new ArrayList<Banner>();
		banners = getJdbcTemplate().query(selectPublic,
				new Object[] { user,siteId }, new BannerRowMapper());
		return banners;
	}

	public Banner getBannerById(int id) {
		Banner banner =null;
		
		try {
			banner = (Banner) getJdbcTemplate().queryForObject(
					selectBannerById, new Object[] { id },
					new BannerRowMapper());
		} catch (EmptyResultDataAccessException e) {
			log.info("No banner( id = " + id + ") are found");
		}
		return banner;
	}
	
	public Banner getFirstBanner(int siteId) {
		try {
			List<Banner> banners = (List<Banner>) getJdbcTemplate().query(
					selectBySiteId, new Object[] { siteId },
					new BannerRowMapper());
			if(banners==null||banners.isEmpty()){
				return null;
			}
			return banners.get(0);
		} catch (EmptyResultDataAccessException e) {
			log.info("No banner( siteId = " + siteId + ") are found");
		}
		return null;
	}

	public Banner getDefaultBanner(int siteId) {
		Banner banner = null;
		List<Banner> banners = new ArrayList<Banner>();
		banners = getJdbcTemplate().query(selectDefaultBanner,new Object[]{siteId},
				new BannerRowMapper());
		if (banners != null && banners.size() > 0) {
			banner = banners.get(0);
		}else{
			banner=getFirstBanner(siteId);
		}
		return banner;
	}

	public void updateBanner(final int siteId,Banner banner) {
		final Banner temp = banner;
		temp.setBannerProfile();
		getJdbcTemplate().update(updateBanner,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int i = 0;
						ps.setInt(++i, siteId);
						ps.setString(++i, temp.getDirName());
						ps.setString(++i, temp.getName());
						ps.setInt(++i, temp.getStatus());
						ps.setInt(++i, temp.getType());
						ps.setString(++i, temp.getCreator());
						ps.setInt(++i, temp.getLeftPictureClbId());
						ps.setInt(++i, temp.getRightPictureClbId());
						ps.setInt(++i, temp.getMiddlePictureClbId());
						ps.setInt(++i, temp.getBannerTitle());
						ps.setString(++i, temp.getOwnedtype());
						ps.setString(++i, temp.getBannerProfile());
						ps.setInt(++i, temp.getId());
						
					}
				});
	}

}