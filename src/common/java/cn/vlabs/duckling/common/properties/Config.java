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
package cn.vlabs.duckling.common.properties;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config extends Properties{
	/**
	 * 从配置文件中获取一个字符串值
	 * @param key	该配置项的关键字
	 * @param defaultval 缺省值
	 * @return	如果找到了该配置项，则返回读取的内容。否则返回缺省值
	 */
	public String getProperty(String key, String defaultValue) {
		String value = super.getProperty(key);
		if (value != null) {
			return replace(value);
		} else
			return defaultValue;
	}
	
	/**
	 * 从配置文件中获取一个字符串值
	 * @param key	该配置项的关键字
	 * @param defaultval 缺省值
	 * @return	如果找到了该配置项，则返回读取的内容。否则返回缺省值
	 */
	public String getProperty(String key) {
		return (getProperty(key, null));
	}

	private String replace(String input) {
		int dollerPos = input.indexOf('$');
		if (dollerPos != -1) {
			String beforeVar = input.substring(0, dollerPos);
			Matcher matcher = pattern.matcher(input.substring(dollerPos));
			if (matcher.matches()) {
				String value = getProperty(matcher.group(1));
				if (value == null)
					value = System.getProperty(matcher.group(1));
				if (value != null) {
					if (matcher.groupCount() == 2)
						return (beforeVar + value + matcher.group(2));
					else
						return (beforeVar + value);
				}
			}
		}
		return input;
	}
	
	private static final long serialVersionUID = 1L;
	private Pattern pattern = Pattern.compile("\\x24\\x7B(.*)\\x7D(.*)");
}
