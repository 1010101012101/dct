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

import java.util.ArrayList;
import java.util.List;

import cn.vlabs.duckling.vwb.service.site.SiteState;

/**
 * Introduction Here.
 * 
 * @date 2010-5-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteItem {
	private int siteId;
	private String mainDomain;
	private List<String> auxiliaryDomain = new ArrayList<String>();
	private String siteName;
	private SiteState state;
	private String createTime;


	public int getSiteId() {
		return siteId;
	}


	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getMainDomain() {
		return mainDomain;
	}

	public void setMainDomain(String mainDomain) {
		this.mainDomain = mainDomain;
	}
	public List<String> getAuxiliaryDomain() {
		return auxiliaryDomain;
	}

	public void setAuxiliaryDomain(List<String> auxiliaryDomain) {
		this.auxiliaryDomain = auxiliaryDomain;
	}
	public void setAuxiliaryDomain(String domain)
	{
		auxiliaryDomain.add(domain);
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public SiteState getState() {
		return state;
	}

	public void setState(SiteState state) {
		this.state = state;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
