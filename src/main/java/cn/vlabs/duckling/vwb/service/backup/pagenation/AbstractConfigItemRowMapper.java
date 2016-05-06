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

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;

/**
 * Introduction Here.
 * 
 * @date May 10, 2010
 * @author zzb
 */
public abstract class AbstractConfigItemRowMapper implements
		ParameterizedRowMapper<ConfigItem> {
	private static final String INHERIT = "INHERIT";
	private static final String INHERITSKIN = "INHERITSKIN";
	private static final String DISABLED = "NO";
	private static final String ENABLED = "YES";
	int id;
	int parent;
	String title;
	String leftMenu;
	String topMenu;
	String banner;
	String footer;
	String trail;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java
	 * .sql.ResultSet, int)
	 */
	public ConfigItem mapRow(ResultSet rs, int row) throws SQLException {
		// rs.relative(row);
		id = rs.getInt("id");
		parent = rs.getInt("parent");
		title = rs.getString("title");
		int iLeftMenu = rs.getInt("left_menu");
		int iTopMenu = rs.getInt("top_menu");
		int iBanner = rs.getInt("banner");
		int iFooter = rs.getInt("footer");
		int iTrail = rs.getInt("trail");

		// 把数字的值转换成代表的意思
		if (iLeftMenu > 0) {
			leftMenu = Integer.toString(iLeftMenu);
		} else if (iLeftMenu == 0) {
			leftMenu = INHERIT;
		} else {
			leftMenu = DISABLED;
		}

		if (iTopMenu > 0) {
			topMenu = Integer.toString(iTopMenu);
		} else if (iTopMenu == 0) {
			topMenu = INHERIT;
		} else {
			topMenu = DISABLED;
		}

		if (iBanner > 0) {
			banner = Integer.toString(iBanner);
		} else if (iBanner == 0) {
			banner = INHERIT;
		} else if (iBanner == -2) {
			banner = INHERITSKIN;
		} else {
			banner = DISABLED;
		}

		if (iFooter > 0) {
			footer = Integer.toString(iFooter);
		} else if (iFooter == 0) {
			footer = INHERIT;
		} else {
			footer = DISABLED;
		}

		if (iTrail > 0) {
			trail = ENABLED;
		} else if (iTrail == 0) {
			trail = INHERIT;
		} else {
			trail = DISABLED;
		}

		return null;
	}

}
