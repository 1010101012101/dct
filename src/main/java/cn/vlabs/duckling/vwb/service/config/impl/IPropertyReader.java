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
import java.util.Set;

/**
 * 配置属性的读取接口
 * 
 * @date 2013-6-5
 * @author xiejj@cstnet.cn
 */
public interface IPropertyReader {
	/**
	 * 删除属性
	 * @param key	属性对应的关键字
	 */
	void removeProperty(int siteId,String key);
	/**
	 * 设置属性值
	 * @param key	属性关键字
	 * @param value	属性值
	 */
	void setProperty(int siteId,String key, String value);
	/**
	 * 判断是否包含某个关键字
	 * @param key	关键字
	 * @return		包含该关键字返回true，否则返回false
	 */
	boolean containsKey(int siteId,String key);
	/**
	 * 获得所有的属性关键字
	 * @return	属性关键字
	 */
	Set<Object> propertyNames(int siteId);
	/**
	 * 读取属性
	 * @param key	属性的关键字
	 * @return		属性的取值
	 */
	String getProperty(int siteId,String key);
	/**
	 * 读取关键字前缀为prefix的所有属性
	 * @param prefix	关键字的前缀
	 * @return			所有符合条件的属性
	 */
	Map<String, String> getPropteryStartWith(int siteId,String prefix);
	/**
	 * 批量设置一批属性
	 * @param prop	被设置的属性集
	 */
	void setProperty(int siteId,Properties prop);
}
