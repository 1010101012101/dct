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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.service.config.IContainerConfig;

/**
 * 容器全局配置
 * 
 * @author Yong Ke(keyong@cnic.cn)
 */
public class ContainerConfig implements IContainerConfig {
	private IPropertyReader m_globalProps;
	private IPropertyReader m_systemProps;
	private String configFile;
	private ConfigClass m_config;
	private static final int GLOBAL_SITE_ID=0;

	public void init() {
		m_config = new ConfigClass();
		m_config.addProvider(m_globalProps);
		m_config.addProvider(m_systemProps);
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setSystemProperty(IPropertyReader reader) {
		m_systemProps = reader;
	}

	public void setGlobalProperty(IPropertyReader reader) {
		m_globalProps = reader;
	}

	public void destroy() {
		this.m_config = null;
	}

	@Override
	public String getProperty(String key) {
		return m_config.getProperty(GLOBAL_SITE_ID,key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String value = m_config.getProperty(GLOBAL_SITE_ID,key);
		if (value != null)
			return value;
		else
			return defaultValue;
	}

	@Override
	public void setProperty(Properties props) {
		if (props != null && props.size() != 0) {
			m_globalProps.setProperty(GLOBAL_SITE_ID,props);
			writeToFile(props);
		}

	}

	private void writeToFile(Properties prop) {
		FileInputStream in;
		try {
			PropertyWriter pw = new PropertyWriter();
			in = new FileInputStream(configFile);
			pw.load(in);
			in.close();
			Enumeration<?> iter = prop.propertyNames();
			while (iter.hasMoreElements()) {
				String key = (String) iter.nextElement();
				pw.setProperty(key, prop.getProperty(key));
			}

			FileOutputStream out = new FileOutputStream(configFile);
			pw.store(out);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	@Override
	public void setProperty(String key, String value) {
		String oldValue = m_globalProps.getProperty(GLOBAL_SITE_ID,key);
		if (!StringUtils.equals(oldValue, value)) {

			if (StringUtils.isEmpty(value)) {
				m_globalProps.removeProperty(GLOBAL_SITE_ID,key);
			} else {
				m_globalProps.setProperty(GLOBAL_SITE_ID,key, value);
			}
			writeToFile(key, value);
		}
	}

	private void writeToFile(String key, String value) {
		FileInputStream in;
		try {
			PropertyWriter pw = new PropertyWriter();
			in = new FileInputStream(configFile);
			pw.load(in);
			in.close();

			if (StringUtils.isEmpty(value)) {
				pw.remove(key);
			} else {
				pw.setProperty(key, value);
			}
			FileOutputStream out = new FileOutputStream(configFile);
			pw.store(out);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	@Override
	public Map<String, String> getPropertyStartWith(String prefix) {
		return m_config.getPropertyStartWith(GLOBAL_SITE_ID,prefix);
	}

	@Override
	public boolean getBool(String key, boolean b) {
		return m_config.getBool(GLOBAL_SITE_ID,key, b);
	}

	@Override
	public int getInt(String key, int i) {
		return m_config.getInt(GLOBAL_SITE_ID,key, i);
	}
}
