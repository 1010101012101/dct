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

import cn.vlabs.duckling.vwb.Attributes;

/**
 * @date May 11, 2010
 * @author xiejj@cnic.cn
 */
public abstract class URLParser {
	public static URLParser getParser(HttpServletRequest request) {
		if (request.getAttribute(Attributes.URL_PARSER) != null) {
			return (URLParser) request.getAttribute(Attributes.URL_PARSER);
		} else {
			URLParser parser = createParser(request.getServletPath());
			parser.parse(request);
			request.setAttribute(Attributes.URL_PARSER, parser);
			return parser;
		}
	}

	protected static URLParser createParser(String servletPath) {
		if ("/site".equals(servletPath)) {
			return new SiteURLParser();
		} else {
			return createWrappered(servletPath);
		}

	}

	protected static WrapperedURLParser createWrappered(String servletPath) {
		if ("/page".equals(servletPath)) {
			return new PageURLParser();
		}
		if ("/portal".equals(servletPath)) {
			return new SimpleURLParser();
		}
		return new PlainURLParser();
	}

	private boolean replaced = false;

	private String uri;

	public abstract String getContextPath();

	public abstract String getPage();

	public abstract String[] getParams();

	public abstract String getPathInfo();

	public abstract String getServletName();

	public abstract String getSiteContext();

	public abstract String getSiteId();
	
	public abstract boolean isSequenceSpecified();
	public abstract int getSequence();
	public String getURI() {
		return uri;
	}

	public boolean hasMoreSlash() {
		return replaced;
	}

	public abstract boolean isPlainURL();
	
	public abstract boolean isSimpleURL();

	public abstract void parse(HttpServletRequest request);

	protected void modifyURI(String uri) {
		if (uri != null) {
			this.uri = uri.replaceAll("\\/+", "/");
			replaced = !this.uri.equals(uri);
		}
	}
}
