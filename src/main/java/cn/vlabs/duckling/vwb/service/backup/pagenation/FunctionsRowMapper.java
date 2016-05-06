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
import cn.vlabs.duckling.vwb.service.resource.config.FunctionItem;

/**
 * Introduction Here.
 * @date May 10, 2010
 * @author zzb
 */
public class FunctionsRowMapper extends AbstractConfigItemRowMapper implements ParameterizedRowMapper<ConfigItem> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public ConfigItem mapRow(ResultSet rs, int row) throws SQLException {
		super.mapRow(rs, row);
		FunctionItem function = new FunctionItem();
		function.setId(id);
		function.setParent(parent);
		function.setTitle(title);
		function.setBanner(banner);
		function.setFooter(footer);
		function.setLeftMenu(leftMenu);
		function.setTopMenu(topMenu);
		function.setTrail(trail);
		return function;
	}

}
