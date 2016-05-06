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
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 9, 2011 5:54:26 PM
 */
public class SimpleURLParser extends WrapperedURLParser{
	public static final String PORTAL_URI_PREFIX = "portal";
	public static final int UNSPECIFIED=-1;
	private int sequence=UNSPECIFIED;
	@Override
	public String getSiteId() {
		return null;
	}

	@Override
	public void parse(HttpServletRequest request) {
		m_context=request.getContextPath();
		m_servletName="/page";
		sequence = UNSPECIFIED;
		modifyURI(request.getRequestURI());
		
		if (!hasMoreSlash()){
			parsePageAndPath(request,"/portal");
		}
	}

	private void parsePageAndPath(HttpServletRequest request, String servletPath){
		String uri = request.getRequestURI();
		
		int pageIdStartFrom = uri.indexOf(servletPath) + servletPath.length();
		if (uri.length() > pageIdStartFrom) {
			String pageidAndSeq = uri.substring(pageIdStartFrom + 1);
			
			String[] parts = pageidAndSeq.split("/");
			if (parts.length >= 1) {
				pageid = parts[0];
				m_pathinfo="/"+pageid;
				if (parts.length >= 2) {// sequence found
					try {
						sequence = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
					}
				}
			}
		}
	}
	@Override
	public void parse(String sitePrefix, HttpServletRequest request) {
		m_context=request.getContextPath();
		m_servletName=sitePrefix+"/page";
		parsePageAndPath(request, sitePrefix+"/portal");
	}

	public boolean isSequenceSpecified() {
		return false;
	}

	public int getSequence() {
		return sequence;
	}

	@Override
	public boolean isSimpleURL() {
		return true;
	}
}
