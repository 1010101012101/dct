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

package cn.vlabs.duckling.vwb.service.url;

/**
 * @date 2013-5-28
 * @author xiejj
 */
public interface UrlService {
	/**
	 * @param siteId
	 * @param m_pageId
	 * @return
	 */
	String getViewURL(int m_pageId);

	/**
	 * @param siteId
	 * @return
	 */
	String getBaseURL();

	/**
	 * @param diff
	 * @param string
	 * @param string2
	 * @return
	 */
	String getURL(String context, String page, String params);

	/**
	 * @param action
	 * @param name
	 * @param params
	 * @param absolute
	 * @return
	 */
	String makeURL(String action, String name, String params, boolean absolute);

	/**
	 * @param siteId
	 * @return
	 */
	String getBasePath();
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	String getFrontPage();
	/**
	 * @param siteId
	 * @param action
	 * @param resourceId
	 * @param params
	 * @return
	 */
	String makeURL(String action, String resourceId, String params);
}
