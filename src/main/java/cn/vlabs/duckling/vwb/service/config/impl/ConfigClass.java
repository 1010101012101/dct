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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public class ConfigClass {
	private static final Pattern pattern = Pattern
			.compile("([^\\}]*)\\$\\{([^}]*)\\}(.*)");

	private ArrayList<IPropertyReader> providers = new ArrayList<IPropertyReader>();

	public void addProvider(IPropertyReader reader) {
		if (reader != null && !providers.contains(reader)) {
			providers.add(reader);
		}
	}

	public String getProperty(int siteId, String key) {
		String value = readFromProvider(siteId, key);
		if (value != null) {
			return replace(siteId, value);
		} else
			return null;
	}

	public Map<String, String> getPropertyStartWith(int siteId, String prefix) {
		HashMap<String, String> result = new HashMap<String, String>();
		Map<String, String> props = null;
		for (IPropertyReader reader : providers) {
			props = reader.getPropteryStartWith(siteId, prefix);
			for (String key : props.keySet()) {
				if (!result.containsKey(key)) {
					result.put(key, props.get(key));
				}
			}
		}
		return result;
	}

	public boolean getBool(int siteId, String key, boolean b) {
		String value = getProperty(siteId, key);
		if (value != null)
			return Boolean.valueOf(value);
		else
			return b;
	}

	public int getInt(int siteId, String key, int i) {
		String value = getProperty(siteId, key);
		try {
			if (value != null)
				return Integer.parseInt(value);
		} catch (NumberFormatException e) {

		}
		return i;
	}

	private String replace(int siteId, String input) {
		input = input.trim();
		int dollerPos = input.indexOf('$');
		if (dollerPos != -1) {
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches()) {
				String left = matcher.group(1);

				String value = getProperty(siteId, matcher.group(2));
				if (value == null)
					value = matcher.group(2);

				String right = replace(siteId, matcher.group(3));
				return left + value + right;
			}
		}
		return input;
	}

	private String readFromProvider(int siteId, String key) {
		for (IPropertyReader reader : providers) {
			String value = reader.getProperty(siteId, key);
			if (value != null)
				return value;
		}
		return null;
	}
}
