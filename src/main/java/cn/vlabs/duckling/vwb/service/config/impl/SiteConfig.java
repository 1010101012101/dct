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

package cn.vlabs.duckling.vwb.service.config.impl;

import java.util.Map;
import java.util.Properties;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author dylan
 */
public class SiteConfig implements ISiteConfig {
	private ConfigClass m_config;

	/**
	 * 全局的配置文件
	 */
	private IPropertyReader globalProps;
	/**
	 * 站点本地的配置
	 */
	private IPropertyReader dbProps;
	/**
	 * Java环境变量
	 */
	private IPropertyReader systemProps;
	@Override
	public boolean getBool(int siteId, String key, boolean defaultValue) {
		return m_config.getBool(siteId, key, defaultValue);
	}

	@Override
	public Map<String, String> getInteranlPeopertyStartWith(int siteId,
			String prefix) {
		return dbProps.getPropteryStartWith(siteId, prefix);
	}

	@Override
	public String getInternalProperty(int siteId, String key) {
		return dbProps.getProperty(siteId, key);
	}

	@Override
	public String getProperty(int siteId, String key) {
		return m_config.getProperty(siteId, key);
	}

	@Override
	public String getProperty(int siteId, String key, String defaultValue) {
		String value = getProperty(siteId, key);
		return value != null ? value : defaultValue;
	}

	@Override
	public Map<String, String> getPropertyStartWith(int siteId, String prefix) {
		return m_config.getPropertyStartWith(siteId, prefix);
	}

	@Override
	public String getVO(int siteId) {
		return getProperty(siteId, KeyConstants.SITE_UMT_VO_KEY);
	}

	public void init() {
		m_config = new ConfigClass();
		m_config.addProvider(dbProps);
		m_config.addProvider(globalProps);
		m_config.addProvider(systemProps);
	}

	@Override
	public boolean isHasDBEmail(int siteId) {
		return (dbProps.getProperty(siteId, "email.mail.smtp.host") != null);
	}

	@Override
	public void removeProperty(int siteId, String strName) {
		dbProps.removeProperty(siteId, strName);
	}

	public void setGlobalProperty(IPropertyReader file) {
		globalProps = file;
	}

	public void setDbProperty(IPropertyReader prop) {
		this.dbProps = prop;
	}

	@Override
	public void setProperty(int siteId, Properties prop) {
		dbProps.setProperty(siteId, prop);
	}

	@Override
	public void setProperty(int siteId, String key, String value) {
		dbProps.setProperty(siteId, key, value);
	}

	public void setSystemProperty(IPropertyReader reader) {
		this.systemProps = reader;
	}
}
