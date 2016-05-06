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

import cn.vlabs.duckling.vwb.service.backup.pagenation.CurrentPage;
import cn.vlabs.duckling.vwb.service.backup.pagenation.MySpaceRowMapper;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PaginationHelper;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;

/**
 * Introduction Here.
 * 
 * @date May 7, 2010
 * @author zzb
 */
public class MySpaceProvider extends BaseTemplateProvider {

	private static final String FILENAME = "/myspace.xml";

	@Override
	public boolean backup(int siteId, String templatePath) {
		String filePath = templatePath + FILENAME;
		File file = new File(filePath);
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file),
					Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			return false;
		}
		List<MySpace> spaces = getSpaces(siteId);
		XSTREAM.toXML(spaces, writer);
		return true;
	}

	private List<MySpace> getSpaces(int siteId) {
		String sqlCountRows = "select count(*) from vwb_myspace where siteId="
				+ siteId;
		String sqlFetchRows = "select * from vwb_myspace where siteId="
				+ siteId;

		List<MySpace> spaces = new ArrayList<MySpace>();

		PaginationHelper<MySpace> ph = new PaginationHelper<MySpace>();
		int pageNo = 0;
		CurrentPage<MySpace> cp;
		do {
			pageNo++;
			cp = ph.fetchPage(getJdbcTemplate(), sqlCountRows, sqlFetchRows,
					null, pageNo, PAGESIZE, new MySpaceRowMapper());
			List<MySpace> items = cp.getPageItems();
			spaces.addAll(items);
		} while (cp.getPagesAvailable() > cp.getPageNumber());

		return spaces;
	}

}