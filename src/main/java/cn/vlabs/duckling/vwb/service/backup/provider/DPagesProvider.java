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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.vwb.service.backup.pagenation.CurrentPage;
import cn.vlabs.duckling.vwb.service.backup.pagenation.DPagesRowMapper;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PaginationHelper;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.DPageItem;
import cn.vlabs.duckling.vwb.service.resource.config.Resources;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class DPagesProvider extends BaseTemplateProvider {
	
	private static final String FILENAME = "/dpages.xml";
	private static final String PAGEFOLDER = "/pages/";
	private static final String SUFFIX = ".txt";
		
	@Override
	public boolean backup (int siteId,String templatePath) {
		Resources resource = getResources(siteId,templatePath);
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

	private Resources getResources(int siteId,String templatePath) {
		Resources resource = new Resources();
		String sqlCountRows = "select count(*) from vwb_resource_info where type = 'DPage' and siteId="+siteId;
		String sqlFetchRows = "select * from vwb_resource_info where type = 'DPage' and siteId="+siteId;
		
		PaginationHelper<ConfigItem> ph = new PaginationHelper<ConfigItem>();
		int pageNo = 0;
		CurrentPage<ConfigItem> cp;
		do {
			pageNo ++ ;
			cp = ph.fetchPage(getJdbcTemplate(), sqlCountRows,
					sqlFetchRows, null, pageNo, PAGESIZE, new DPagesRowMapper());
			List<ConfigItem> items = cp.getPageItems();
			for(ConfigItem ci : items) {
				DPageItem dpage = (DPageItem) ci;
				int id = dpage.getId();
				String fileName = getFileName(templatePath, id,siteId);
				dpage.setFile(fileName);
				resource.addItem(dpage);
			}
		} while(cp.getPagesAvailable() > cp.getPageNumber());
		
		return resource;
	}
	
	private String getFileName(final String templatePath, int id,int siteId) {
		String cSql = "select * from vwb_dpage_content_info where resourceId = " + id + " and siteId="+siteId+" order by version DESC;";
		return (String)getJdbcTemplate().query(cSql, new ResultSetExtractor<String>(){
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				String fileName = "";
				if(rs.next()) {
					fileName = rs.getString("resourceId") + SUFFIX;
					String content = rs.getString("content");
					String filePath = templatePath + PAGEFOLDER;
					File dir = new File(filePath);
					if(!dir.exists()) {
						dir.mkdirs();
					}
					FileUtil.writeFile(new File(filePath + fileName), content);
				}
				return fileName;
			}
			
		});
	}
	
}
