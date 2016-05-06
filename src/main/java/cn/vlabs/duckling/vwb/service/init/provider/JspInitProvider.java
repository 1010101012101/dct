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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;

/**
 * 系统功能的初始化
 * @date 2013-3-31
 * @author xiejj
 */
public class JspInitProvider extends AbstractViewPortInitProvider {
	@Override
	public boolean init(int siteId,  Map<String,String> params,String templatePath,String defaultDataPath) throws IOException {
		String jspFile = templatePath + "/functions.xml";
		List<ConfigItem> dpages = initItemFromXML(jspFile);
		if (dpages != null) {
			for (ConfigItem item : dpages) {
				create(siteId,convertToViewPort(item));
			}
		}
		return true;
	}

}
