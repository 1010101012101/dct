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
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.backup.pagenation.CurrentPage;
import cn.vlabs.duckling.vwb.service.backup.pagenation.FunctionsRowMapper;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PaginationHelper;
import cn.vlabs.duckling.vwb.service.resource.FunctionService;
import cn.vlabs.duckling.vwb.service.resource.JSPFunction;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.FunctionItem;
import cn.vlabs.duckling.vwb.service.resource.config.Resources;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class FunctionsProvider extends BaseTemplateProvider {
	
	private static final String FILENAME = "/functions.xml";
	
	public static Logger log = Logger.getLogger(FunctionsProvider.class);
	
	@Override
	public boolean backup (int siteId,String templatePath) {
		Resources resource = getResources(siteId);
		new File(templatePath).mkdirs();
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
		String sqlCountRows = "select count(*) from vwb_resource_info where type = 'JSP' and siteId="+siteId;
		String sqlFetchRows = "select * from vwb_resource_info where type = 'JSP' and siteId="+siteId;
		
		PaginationHelper<ConfigItem> ph = new PaginationHelper<ConfigItem>();
		int pageNo = 0;
		CurrentPage<ConfigItem> cp;
		do {
			pageNo ++ ;
			cp = ph.fetchPage(getJdbcTemplate(), sqlCountRows,
					sqlFetchRows, null, pageNo, PAGESIZE, new FunctionsRowMapper());
			List<ConfigItem> items = cp.getPageItems();
			FunctionService functionService = VWBContainerImpl.findContainer().getFunctionService();
			
			for(ConfigItem ci : items) {
				FunctionItem fi = (FunctionItem) ci;
				JSPFunction function = functionService.getFunction(siteId,fi.getId());
				if (function!=null){
					String url = function.getURL();
					fi.setUrl(url);
					resource.addItem(fi);
				}
			}
		} while(cp.getPagesAvailable() > cp.getPageNumber());
		
		return resource;
	}
	
}
