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

package cn.vlabs.commons.config;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 配置文件读取工具
 * 	Properties文件读取工具
 *	该工具支持以${varname}形式的变量替换。
 *	变量包含配置文件中自身定义的名称，也可以从System.getProperty的空间获得。
 * @author 谢建军(xiejj@cnic.cn)
 * @created Apr 28, 2010
 */
public class PropertiesReader extends Properties{
	private static final long serialVersionUID = 1L;
	
	private Pattern pattern = Pattern.compile("([^\\}]*)\\$\\{([^}]*)\\}(.*)");
	
	public PropertiesReader() {
		super();
	}
	
	public PropertiesReader(Properties prop){
		super(prop);
	}


	/**
	 * 从配置文件中读取一个布尔值
	 * @param key	该配置项的关键字
	 * @param defaultValue 缺省值
	 * @return 如果找到了该配置项，并且能转换成布尔值，则返回读取的内容。否则返回缺省值
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String value =  getProperty(key, null);
		if (value != null)
			return Boolean.parseBoolean(value);
		else
			return defaultValue;
	}

	/**
	 * 从配置文件中读取一个整数值
	 * @param key	该配置项的关键字
	 * @param defaultval	缺省值
	 * @return 如果找到了该配置项，并且能转换成整数值，则返回读取的内容。否则返回缺省值
	 */
	public int getInt(String key, int defaultval) {
		String value = getProperty(key, null);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return defaultval;
			}
		}
		return defaultval;
	}

	/**
	 * 从配置文件中获取一个字符串值
	 * @param key	该配置项的关键字
	 * @param defaultval 缺省值
	 * @return	如果找到了该配置项，则返回读取的内容。否则返回缺省值
	 */
	public String getProperty(String key, String defaultval) {
		String value = super.getProperty(key);
		if (value != null) {
			return replace(value);
		} else
			return defaultval;
	}
	/**
	 * 从配置文件读取一个配置项
	 * @param key 该配置项的关键字
	 * @return 如果有该配置项，则返回该配置的值，否则返回null。
	 */
	public String getProperty(String key) {
		String value = super.getProperty(key);
		if (value != null) {
			return replace(value);
		} else
			return null;
	}
	private String replace(String input) {
		input = input.trim();
		int dollerPos = input.indexOf('$');
		if (dollerPos != -1) {
			Matcher matcher  = pattern.matcher(input);
			if (matcher.matches()){
				String left= matcher.group(1);
				
				String value = getProperty(matcher.group(2), null);
				if (value==null)
					value=System.getProperty(matcher.group(2));
				if (value==null)
					value=matcher.group(2);
				
				String right=replace(matcher.group(3));
				return left+value+right;
			}
		}	
		return input;
	}
}