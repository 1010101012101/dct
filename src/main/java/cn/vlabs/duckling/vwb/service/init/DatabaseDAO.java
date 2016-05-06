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

package cn.vlabs.duckling.vwb.service.init;

import cn.vlabs.duckling.util.ContainerBaseDAO;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public class DatabaseDAO extends ContainerBaseDAO {
	private static final String[] TABLES=new String[]{
		"vwb_attach",
		"vwb_banner",
		"vwb_dpage_content_info",
		"vwb_email_notify",
		"vwb_myspace",
		"vwb_policy",
		"vwb_portal_portlets",
		"vwb_portal_page",
		"vwb_resource_acl",
		"vwb_skin",
		"vwb_resource_info",
		"vwb_temp_page"
	};
	public void dropTables(int siteId) {
		for (String table:TABLES){
			deleteDataInTable(table,siteId);
		}
		getJdbcTemplate().update(
				"delete from vwb_properties where iSiteNum = " + siteId);
		getJdbcTemplate().update("delete from vwb_site where id = " + siteId);
	}

	private void deleteDataInTable(String tableName, int siteId) {
		getJdbcTemplate().update(
				"delete from `" + tableName + "` where siteId=?;",
				new Object[] { siteId });
	}
}
