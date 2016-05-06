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

package cn.vlabs.duckling.vwb.service.backup.pagenation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONObject;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.vlabs.duckling.vwb.service.banner.Banner;

/**
 * Introduction Here.
 * @date May 10, 2010
 * @author zzb
 */
public class VWBBannerRowMapper implements ParameterizedRowMapper<Banner> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public Banner mapRow(ResultSet rs, int row) throws SQLException {
		Banner banner = new Banner();
		banner.setId(rs.getInt("id"));
		banner.setDirName(rs.getString("dirName"));
		banner.setStatus(rs.getInt("status"));
		banner.setBannerTitle(rs.getInt("pageId"));
		banner.setCreatedTime(new java.util.Date(rs.getTimestamp("createdTime")
				.getTime()));
		banner.setCreator(rs.getString("creator"));
		banner.setType(rs.getInt("type"));
		banner.setOwnedtype(rs.getString("ownedtype"));
		banner.setName(rs.getString("name"));
		banner.setBannerProfile(rs.getString("bannerProfile"));
		banner.setLeftPictureClbId(rs.getInt("leftPictureClbId"));
		banner.setRightPictureClbId(rs.getInt("rightPictureClbId"));
		banner.setMiddlePictureClbId(rs.getInt("middlePictureClbId"));
		banner.setCssClbId(rs.getInt("cssClbId"));
		try {
			if(banner.getBannerProfile()!=null){
			JSONObject json = new JSONObject(banner.getBannerProfile());
			if(!json.isNull("firstTitle"))
				banner.setFirstTitle(json.getString("firstTitle"));
			if(!json.isNull("secondTitle"))
				banner.setSecondTitle(json.getString("secondTitle"));
			if(!json.isNull("thirdTitle"))
				banner.setThirdTitle(json.getString("thirdTitle"));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return banner;
	}

}
