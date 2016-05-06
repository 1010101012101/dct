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
package cn.vlabs.duckling.util;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SiteBaseDAO implements SiteIdentify{
	public final static String RESERVED_CHAR = "\\$vwb"; 
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	
	private JdbcTemplate  jdbcTemplate;
	private String siteKey;
	protected String getSiteKey() {
		return siteKey;
	}
	public void setSiteKey(String siteKey) {
		this.siteKey = siteKey;
	}
	public String getSql(String sql)
	{
		if(sql!=null)
		{
			return sql.replaceAll(RESERVED_CHAR, getSiteKey());
		}
		return null;
	}
	
}
