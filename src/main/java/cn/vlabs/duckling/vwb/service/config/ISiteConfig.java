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

package cn.vlabs.duckling.vwb.service.config;

import java.util.Map;
import java.util.Properties;

/**
 * @date 2013-5-27
 * @author xiejj
 */
public interface ISiteConfig {

	boolean isHasDBEmail(int siteId);

	void removeProperty(int siteId, String strName);

	void setProperty(int siteId, String key, String value);

	void setProperty(int siteId, Properties prop);

	Map<String, String> getPropertyStartWith(int siteId, String prefix);

	Map<String, String> getInteranlPeopertyStartWith(int siteId, String prefix);

	String getProperty(int siteId, String key, String defaultValue);

	String getInternalProperty(int siteId, String key);

	String getProperty(int siteId, String key);

	boolean getBool(int siteId, String key, boolean defaultValue);
	String getVO(int siteId);
}
