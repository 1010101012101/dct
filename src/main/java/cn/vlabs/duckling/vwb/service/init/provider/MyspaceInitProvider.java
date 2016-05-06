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

package cn.vlabs.duckling.vwb.service.init.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigReader;

import com.thoughtworks.xstream.XStream;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public class MyspaceInitProvider extends ContainerBaseDAO implements InitProvider {
	private static final String insertMyspace = "insert into vwb_myspace(eid,siteId,resourceId) values(?,?,?)";

	private void createMySpace(int siteId,MySpace myspace) {
		getJdbcTemplate().update(
				insertMyspace,
				new Object[] {myspace.getUser(),siteId,
						myspace.getResourceId() });

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(int siteId, Map<String,String> params,String templatePath,String defaultDataPath) throws IOException {
		String myspacefilename = templatePath + "/myspace.xml";
		if (new File(myspacefilename).exists()) {
			XStream xstream = new ConfigReader().getStream();
			Reader reader = new InputStreamReader(new FileInputStream(
					myspacefilename), Charset.forName("UTF-8"));
			List<MySpace> myspacelist = new ArrayList<MySpace>();
			myspacelist = (List<MySpace>) xstream.fromXML(reader);
			for (MySpace myspace : myspacelist) {
				createMySpace(siteId,myspace);
			}
		}
		return true;
	}
}