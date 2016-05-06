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

package cn.vlabs.duckling.vwb.service.resource;

import java.io.Serializable;

/**
 * Introduction Here.
 * 
 * @date Feb 26, 2010
 * @author xiejj@cnic.cn
 */
public abstract class Resource implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int NONE = 0;

	public static final String TYPE_DPAGE = "DPage";

	public static final String TYPE_FUNCTION = "JSP";

	public static final String TYPE_PORTAL = "Portal";

	private int m_id;

	private String m_title;
	private int siteId;

	public Resource() {
		m_title = null;
		m_id = NONE;
	}

	public Resource(String title, int id) {
		this.m_id = id;
		this.m_title = title;
	}

	public int getResourceId() {
		return m_id;
	}

	public int getSiteId() {
		return siteId;
	}

	public String getTitle() {
		return m_title;
	}

	public abstract String getType();

	public void setResourceId(int id) {
		m_id = id;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public void setTitle(String title) {
		this.m_title = title;
	}
}
