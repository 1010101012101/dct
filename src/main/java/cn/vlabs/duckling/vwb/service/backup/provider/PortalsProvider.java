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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import cn.vlabs.duckling.vwb.service.backup.pagenation.CurrentPage;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PaginationHelper;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PortalsRowMapper;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.PortalItem;
import cn.vlabs.duckling.vwb.service.resource.config.PortletItem;
import cn.vlabs.duckling.vwb.service.resource.config.Resources;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class PortalsProvider extends BaseTemplateProvider {
	
	private static final String FILENAME = "/portals.xml";
	
	@Override
	public boolean backup (int siteId,String templatePath) {
		Resources resource = getResources(siteId);
		String filePath = templatePath + FILENAME;
		File file = new File(filePath);
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			return false;
		}
		XSTREAM.toXML(resource, writer);
		return true;
	}
	private Resources getResources(int siteId) {
		Resources resource = new Resources();
		String sqlCountRows = "select count(*) from vwb_resource_info where type = 'Portal' and siteId="+siteId;
		String sqlFetchRows = "select * from vwb_resource_info where type = 'Portal' and siteId="+siteId;
		
		PaginationHelper<ConfigItem> ph = new PaginationHelper<ConfigItem>();
		int pageNo = 0;
		CurrentPage<ConfigItem> cp;
		do {
			pageNo ++ ;
			//System.out.println(pageNo);
			cp = ph.fetchPage(getJdbcTemplate(), sqlCountRows,
					sqlFetchRows, null, pageNo, PAGESIZE, new PortalsRowMapper());
			List<ConfigItem> items = cp.getPageItems();
			for(ConfigItem ci : items) {
				PortalItem pi = (PortalItem) ci;
				int id = pi.getId();
				String uri = getURI(id, siteId);
				List<PortletItem> portlets = getPortletItems(id,siteId);
				pi.setUri(uri);
				pi.setPortlets(portlets);
				resource.addItem(pi);
			}
		} while(cp.getPagesAvailable() > cp.getPageNumber());
		
		return resource;
	}
	
	private List<PortletItem> getPortletItems(int id,int siteId) {
		List<PortletItem> items = new ArrayList<PortletItem>();
		String cSql = "select * from vwb_portal_portlets where resourceId = ? and siteId=?";
		SqlRowSet csrs = this.getJdbcTemplate().queryForRowSet(cSql, new Object[]{id,siteId});
		csrs.beforeFirst();
		while(csrs.next()) {
			PortletItem pi = new PortletItem();
			String context = csrs.getString("context");
			String name = csrs.getString("name");
			pi.setContext(context);
			pi.setName(name);
			items.add(pi);
		}
		return items;
	}
	
	private String getURI(int id, int siteId) {
		String cSql = "select * from vwb_portal_page where resourceId =? and siteId=?";
		SqlRowSet csrs = this.getJdbcTemplate().queryForRowSet(cSql,new Object[]{id, siteId});
		String uri = "";
		csrs.beforeFirst();
		if(csrs.next()) {
			uri = csrs.getString("uri");
		}
		return uri;
	}
	
}
