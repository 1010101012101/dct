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

package cn.vlabs.duckling.vwb.service.backup.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import cn.vlabs.duckling.vwb.service.config.impl.PropertyWriter;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class SiteProvider extends BaseTemplateProvider {
	
	private static final String FILENAME = "/site.properties";
	@Override
	public boolean backup (int siteId,String templatePath) {
		String filePath = templatePath + FILENAME;
		File file = new File(filePath);

		try {
			PropertyWriter pw = getContent(siteId);
			pw.store(new FileOutputStream(file));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private PropertyWriter getContent(int id) {
		String cSql = "select * from vwb_properties where iSiteNum = " + id + ";";
		SqlRowSet csrs = this.getJdbcTemplate().queryForRowSet(cSql);
		PropertyWriter pw = new PropertyWriter();
		csrs.beforeFirst();
		while(csrs.next()) {
			String key = csrs.getString("strName");
			String value = csrs.getString("strValue");
			if(!key.contains("duckling.domain") 
					|| key.equalsIgnoreCase("duckling.domain")) {
				pw.setProperty(key, value);
			}
		}
		return pw;
	}
	
}
