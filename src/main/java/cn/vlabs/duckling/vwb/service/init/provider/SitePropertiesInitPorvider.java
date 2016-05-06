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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.init.InitProvider;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public class SitePropertiesInitPorvider extends ContainerBaseDAO implements InitProvider{
	public boolean init(int siteId, Map<String, String> params,
			String templatePath,String defaultDataPath) throws IOException {
		ISiteConfig siteConfig = VWBContainerImpl.findContainer().getSiteConfig();
		File sitePropertiesFile = new File(templatePath + "/site.properties");
		Properties siteProperties = new Properties();
		if (sitePropertiesFile.exists()) {
			siteProperties.load(new FileInputStream(sitePropertiesFile));
		}
		replaceSiteProperties(siteId, siteConfig,params, siteProperties);
		siteProperties.put("duckling.allowanonymous", "true");
		VWBContainerImpl.findContainer().getSiteConfig().setProperty(siteId,siteProperties);
		return false;
	}

	private void replaceSiteProperties(int siteId, ISiteConfig siteConfig,Map<String, String> params,
			Properties siteProperties) {
		params.put(
				KeyConstants.DLOG_APPID_KEY,
				siteConfig.getProperty(siteId,KeyConstants.APPID_PREFIX_KEY, "")
						+ siteId);

		Set<String> keyset = params.keySet();
		for (String key : keyset) {
			String value = (String) params.get(key);
			if (StringUtils.isNotEmpty(siteConfig.getProperty(siteId,key))
					&& StringUtils.isNotEmpty(value)
					|| key.startsWith(KeyConstants.SITE_DOMAIN_KEY)) {
				siteProperties.setProperty(key, value);
			}
		}
	}
}
