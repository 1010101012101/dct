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

package cn.vlabs.vwb.driver.internal;

import java.io.Serializable;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.url.UrlService;
import cn.vlabs.duckling.vwb.service.url.impl.UrlServiceImpl;
import cn.vlabs.vwb.SiteContext;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 11, 2010 9:03:46 PM
 */
public class SiteContextImpl implements SiteContext, Serializable {
	private static final long serialVersionUID = 1L;
	private int siteId;

	public SiteContextImpl(int siteId) {
		this.siteId = siteId;
	}

	public void setSite(int siteId) {
		this.siteId = siteId;
	}

	private ISiteConfig getSiteConfig() {
		return VWBContainerImpl.findContainer().getSiteConfig();
	}

	private UrlService getUrlService() {
		return new UrlServiceImpl(siteId,getSiteConfig());
	}

	public String getEditURL(int pageid, String params) {
		return getUrlService().getURL(VWBContext.EDIT,
				Integer.toString(pageid), params);
	}

	public String getJSPURL(String jsp, String params) {
		return getUrlService().getURL(VWBContext.PLAIN, jsp, params);
	}

	public String getViewURL(int pageid, String params) {
		return getUrlService().getURL(VWBContext.VIEW,
				Integer.toString(pageid), params);
	}

	public int getSiteId() {
		return siteId;
	}

	public String getVO() {
		return getSiteConfig().getVO(siteId);
	}

	public String getSimplePortalURL(int pageid, String params) {
		return getUrlService().getURL(VWBContext.SIMPLE,
				Integer.toString(pageid), params);
	}
}
