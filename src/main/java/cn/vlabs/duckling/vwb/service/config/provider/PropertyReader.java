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

package cn.vlabs.duckling.vwb.service.config.provider;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.vlabs.duckling.vwb.service.config.impl.IPropertyReader;

/**
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public class PropertyReader implements IPropertyReader {
	public PropertyReader(Properties prop) {
		m_props = prop;
	}

	private Properties m_props;

	@Override
	public Map<String, String> getPropteryStartWith(int siteId, String prefix) {
		HashMap<String, String> result = new HashMap<String, String>();
		if (prefix != null) {
			Set<Object> names = propertyNames(siteId);
			for (Object keyObj : names) {
				String key = (String)keyObj;
				if (key.startsWith(prefix)) {
					result.put(key, getProperty(siteId, key));
				}
			}
		}
		return result;
	}

	@Override
	public String getProperty(int siteId, String key) {
		return m_props.getProperty(key);
	}

	@Override
	public Set<Object> propertyNames(int siteId) {
		return m_props.keySet();
	}

	@Override
	public boolean containsKey(int siteId, String key) {
		return m_props.containsKey(key);
	}

	@Override
	public void setProperty(int siteId, Properties prop) {
		Enumeration<?> iter = prop.propertyNames();
		while (iter.hasMoreElements()) {
			String key = (String) iter.nextElement();
			String value = prop.getProperty(key);
			m_props.setProperty(key, value);
		}
	}

	@Override
	public void setProperty(int siteId, String key, String value) {
		if (key != null)
			m_props.setProperty(key, value);
	}

	@Override
	public void removeProperty(int siteId, String key) {
		if (key != null)
			m_props.remove(key);
	}
}
