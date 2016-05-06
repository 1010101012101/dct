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

package cn.vlabs.duckling.vwb.url;

import javax.servlet.http.HttpServletRequest;

/**
 * @date May 11, 2010
 * @author xiejj@cnic.cn
 */
public class PageURLParser extends WrapperedURLParser{
	public static final String PAGE_URI_PREFIX = "page";
	public String getSiteId() {
		return null;
	}

	public void parse(HttpServletRequest request){
		m_context=request.getContextPath();
		m_servletName=request.getServletPath();
		
		modifyURI(request.getRequestURI());
		if (!hasMoreSlash()){
			parsePageAndPath(request);
		}
	}

	@Override
	public void parse(String sitePrefix, HttpServletRequest request) {
		m_context=request.getContextPath();
		m_servletName=sitePrefix+"/page";
		parsePageAndPath(request);
		m_pathinfo =m_pathinfo.replaceFirst("\\/[0-9]+\\/page", "");
	}
	private void parsePageAndPath(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int pageidIndex=uri.indexOf(m_servletName)+m_servletName.length()+1;
		if (pageidIndex<uri.length()){
			String pageids = uri.substring(pageidIndex);
			String[] ids = pageids.split("/");
			if (ids.length>=1){
				pageid=ids[0];
				copyParams(ids, 1);
				m_pathinfo = request.getPathInfo();
			}
		}
	}
}
