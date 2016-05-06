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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.config.DomainNameService;
import cn.vlabs.duckling.vwb.service.skin.SkinService;
import cn.vlabs.duckling.vwb.service.template.SiteTemplate;
import cn.vlabs.duckling.vwb.service.template.TemplateService;
import cn.vlabs.duckling.vwb.spi.VWBContainer;

/**
 * To provide function to initialize the basic data to database.
 * 
 * @date Feb 5, 2010
 * @author Yong Ke(keyong@cnic.cn)
 */
public class SiteInitService {
	private final static Logger log = Logger.getLogger(SiteInitService.class);

	private DatabaseDAO databaseDao;
	private String defaultDataPath = "";
	private List<InitProvider> providers;

	private VWBContainer getContainer() {
		return VWBContainerImpl.findContainer();
	}

	public void destroy(int siteId) {
		SkinService skinService = getContainer().getSkinService();
		skinService.removeAllSkins(siteId);
		//删除域名缓存
		DomainNameService domainService=getContainer().getDomainService();
		domainService.removeSiteDomains(siteId);
		
		this.databaseDao.dropTables(siteId);
		
	}

	public void init(int siteId, Map<String, String> params) {
		try {
			TemplateService templateService = getContainer()
					.getTemplateService();
			SiteTemplate template = templateService.checkAndDownload(params
					.get(KeyConstants.SITE_TEMPLATE_KEY));
			String templatePath = template.getPath();
			for (InitProvider provider : providers) {
				provider.init(siteId, params, templatePath, defaultDataPath);
			}
		} catch (Throwable e) {
			log.error("系统初始化出错", e);
		}
	}

	public void setDatabaseDAO(DatabaseDAO databaseDao) {
		this.databaseDao = databaseDao;
	}

	public void setDefaultDataPath(String defaultDataPath) {
		this.defaultDataPath = defaultDataPath;
	}

	public void setProviders(List<InitProvider> providers) {
		this.providers = providers;
	}
}