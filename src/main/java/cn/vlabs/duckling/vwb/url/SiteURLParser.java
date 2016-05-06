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
public class SiteURLParser extends URLParser {
	private static final String SITE_URI_PREFIX = "site/";

	public static String getSite(int siteid) {
		return SITE_URI_PREFIX + siteid;
	}

	private String m_sitePart = "";

	private String siteid = null;

	private WrapperedURLParser subParser;

	public String getPage() {
		if (subParser != null) {
			return subParser.getPage();
		} else {
			return null;
		}
	}

	public String[] getParams() {
		return subParser.getParams();
	}

	public String getPathInfo() {
		return subParser.getPathInfo();
	}

	public String getServletName() {
		return subParser.getServletName();
	}

	public String getSiteContext() {
		return subParser.getContextPath() + m_sitePart;
	}

	public String getSiteId() {
		return siteid;
	}

	public boolean isPlainURL() {
		return subParser != null && subParser.isPlainURL();
	}

	public boolean isSequenceSpecified() {
		return subParser != null && subParser.isSequenceSpecified();
	}

	public int getSequence() {
		return subParser.getSequence();
	}

	@Override
	public String getContextPath() {
		return subParser.getContextPath();
	}

	public void parse(HttpServletRequest request) {
		String siteServletName = request.getServletPath();

		modifyURI(request.getRequestURI());
		if (!hasMoreSlash()) {
			String uri = getURI();
			int index = uri.indexOf(siteServletName) + siteServletName.length();
			if (uri.length() > index) {
				String pageids = uri.substring(uri.indexOf(siteServletName)
						+ siteServletName.length() + 1);
				String[] ids = pageids.split("/");
				if (ids.length >= 1) {
					siteid = ids[0];
					m_sitePart = siteServletName + "/" + siteid;
					if (ids.length >= 2) {
						subParser = URLParser.createWrappered("/" + ids[1]);
						subParser.parse(m_sitePart, request);
					}
				}
			}
		}
	}

	@Override
	public boolean isSimpleURL() {
		return subParser != null && subParser.isSimpleURL();
	}
}
