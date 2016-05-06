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

package cn.vlabs.duckling.vwb.service.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cn.vlabs.duckling.vwb.KeyConstants;

/**
 * 站点创建模板
 * 
 * @date 2013-4-1
 * @author xiejj@cstnet.cn
 */
public class SiteTemplate {
	public static final String TYPE_BACKUP = "backup";
	public static final String TYPE_TEMPLATE = "template";
	private String name;

	private String path;

	private String type;

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	public boolean isLocal() {
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDefaultSkin() throws IOException{
		File sitePropertiesFile = new File(path + "/site.properties");
		Properties siteProperties = new Properties();
		if (sitePropertiesFile.exists()) {
			siteProperties.load(new FileInputStream(sitePropertiesFile));
		}
		return siteProperties.getProperty(KeyConstants.SKIN_NAME);
	}

	public boolean isTemplate() {
		return TYPE_TEMPLATE.equals(type);
	}
}
