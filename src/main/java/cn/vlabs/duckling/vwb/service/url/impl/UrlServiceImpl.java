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

package cn.vlabs.duckling.vwb.service.url.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.url.UrlService;
import cn.vlabs.duckling.vwb.ui.command.Command;
import cn.vlabs.duckling.vwb.ui.command.CommandResolver;
import cn.vlabs.duckling.vwb.url.SiteURLParser;

/**
 * @date 2013-5-28
 * @author xiejj
 */
public class UrlServiceImpl implements UrlService {
	private String viewPort = "page";
	private String baseUrl;
	private String domain;
	private String defaultPage;
	private ISiteConfig siteConfig;
	private int siteId;

	public UrlServiceImpl(int siteId, ISiteConfig siteConfig) {
		this.siteConfig = siteConfig;
		this.siteId = siteId;
	}

	private String readBaseUrl() {
		if (this.baseUrl == null) {
			this.baseUrl = siteConfig.getProperty(siteId,
					KeyConstants.SITE_BASEURL_KEY);
		}
		return this.baseUrl;
	}

	private String readDomain() {
		if (this.domain == null) {
			this.domain = siteConfig.getProperty(siteId,
					KeyConstants.SITE_DOMAIN_KEY);
		}
		return this.domain;
	}

	private String readDefaultPage() {
		if (this.defaultPage == null) {
			this.defaultPage = siteConfig.getProperty(siteId,
					"duckling.defaultpage");
		}
		return this.defaultPage;
	}

	/**
	 * 合成URL URLPattern中包含以下内容时: %u 使用相对地址时使用basepath替换,使用绝对地址时使用baseURL替换 %U
	 * 使用绝对地址BaseURL替换 %p 用basePath替换 %n 用page替换 %v 用ViewPort替换(page/) %s 用站点替换
	 * 
	 * @param urlpattern
	 *            url的模式
	 * @param page
	 *            访问的页面
	 * @param absolute
	 *            是否使用绝对地址
	 * @return 合成以后的URL
	 */
	private String doReplace(String urlpattern, String page,
			boolean absolute) {
		String baseUrl = getBaseURL();
		String basePath = getBasePath();
		String url = urlpattern;
		if (absolute){
			url = TextUtil.replaceString(url, "%u", baseUrl);
		}else{
			url = TextUtil.replaceString(url, "%u", basePath);
		};
		url = TextUtil.replaceString(url, "%U", baseUrl);
		url = TextUtil.replaceString(url, "%p", basePath);
		url = TextUtil.replaceString(url, "%n", page);
		url = TextUtil.replaceString(url, "%v", viewPort);
		return url;
	}

	@Override
	public String getBasePath() {
		String basePath;
		try {
			URL url = new URL(getBaseURL());
			basePath = url.getPath();
		} catch (MalformedURLException e) {
			basePath = "/dct";
		}
		return basePath;
	}

	@Override
	public String getBaseURL() {
		String newURL = readBaseUrl();
		if (StringUtils.isBlank(readDomain())) {
			newURL = newURL + "/" + SiteURLParser.getSite(siteId);
		}
		if (newURL.endsWith("/")) {
			newURL = newURL.substring(0, newURL.length() - 1);
		}
		return newURL;
	}

	@Override
	public String getURL(String context, String page, String params) {
		return makeURL(context, page, params, false);
	}

	@Override
	public String getViewURL(int pageId) {
		return getURL(VWBContext.VIEW, Integer.toString(pageId), null);
	}

	@Override
	public String makeURL(String action, String resourceId,
			String params) {
		return makeURL(action, resourceId, params, false);
	}

	@Override
	public String getFrontPage() {
		String strdefaultpage = readDefaultPage();
		if (strdefaultpage == null || ("").equals(strdefaultpage)) {
			strdefaultpage = Integer.toString(Constant.DEFAULT_FRONT_PAGE);
		}
		return makeURL(VWBContext.VIEW, strdefaultpage, null,true);
	}

	@Override
	public String makeURL(String action, String name,
			String params, boolean absolute) {
		if (action.equals(VWBContext.NONE)) {
			if (!StringUtils.isEmpty(params))
				params = (name.indexOf('?') != -1) ? "&amp;" : "?" + params;
		}

		Command command = CommandResolver.findCommand(action);
		if (command == null)
			command = CommandResolver.findCommand(VWBContext.VIEW);
		String url = doReplace(command.getURLPattern(), name, absolute);
		if (!StringUtils.isEmpty(params)) {
			if (url.indexOf('?') != -1) {
				url = url + "&" + params;
			} else {
				url = url + "?" + params;
			}
		}

		return url;
	}
}