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
package cn.vlabs.duckling.vwb;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.cache.ThreadLocalCache;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.url.URLParser;

public class VWBFilter implements Filter {
	private static final String COOKIE_NAME = "JSESSIONID";
	private String encoding;
	private String[] ignoreList;

	public void destroy() {

	}

	public void init(FilterConfig config) throws ServletException {
		encoding = config.getInitParameter("charset");
		if (encoding == null)
			encoding = "UTF-8";
		String ignore = config.getInitParameter("ignore");
		if (ignore != null) {
			ignoreList = ignore.split(";");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		ThreadLocalCache.init(100);
		if (request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			String localeString = getLocaleString(req);
			Locale locale = getLocale(localeString);
			WrapperedRequest wrappered;
			if (locale != null) {
				wrappered = new WrapperedRequest(req,
						(HttpServletResponse) response, locale);
			} else {
				wrappered = new WrapperedRequest(req,
						(HttpServletResponse) response);
			}
			filterChain.doFilter(wrappered, response);
		}
		ThreadLocalCache.clear();
	}

	private boolean isIgnored(String servletPath) {
		if (ignoreList != null && ignoreList.length > 0) {
			for (String ignorePath : ignoreList) {
				if (StringUtils.equals(ignorePath, servletPath))
					return true;
			}
		}
		return false;
	}

	private static Map<String, Locale> locales = new ConcurrentHashMap<String, Locale>();

	private static Locale getLocale(String lstr) {
		if (lstr == null || lstr.length() < 1) {
			return null;
		}
		Locale locale = locales.get(lstr.toLowerCase());
		if (locale != null) {
			return locale;
		} else {
			StringTokenizer localeTokens = new StringTokenizer(lstr, "_");
			String lang = null;
			String country = null;
			if (localeTokens.hasMoreTokens()) {
				lang = localeTokens.nextToken();
			}
			if (localeTokens.hasMoreTokens()) {
				country = localeTokens.nextToken();
			}
			locale = new Locale(lang, country);
			Locale crtls[] = Locale.getAvailableLocales();
			for (int i = 0; i < crtls.length; i++) {
				if (crtls[i].equals(locale)) {
					locale = crtls[i];
					break;
				}
			}
			locales.put(lstr.toLowerCase(), locale);
		}
		return locale;
	}

	private class WrapperedRequest extends HttpServletRequestWrapper {
		private static final String JSTL_FMT_STRING = "javax.servlet.jsp.jstl.fmt.locale.request";
		private static final String STRUTS_LOCALE = "org.apache.struts.action.LOCALE";
		private Locale m_locale;
		private HttpServletResponse response;

		public WrapperedRequest(HttpServletRequest request,
				HttpServletResponse response, Locale locale) {
			super(request);
			m_locale = locale;
			if (locale != null) {
				request.setAttribute(JSTL_FMT_STRING, locale);
				request.setAttribute(STRUTS_LOCALE, locale);
			}
			this.response = response;
		}

		public WrapperedRequest(HttpServletRequest request,
				HttpServletResponse response) {
			super(request);
			VWBContainer container = VWBContainerImpl.findContainer();
			SiteMetaInfo site = VWBContainerImpl.findSite(request);
			if (site != null) {
				String localeString = container.getSiteConfig().getProperty(site.getId(),"default.language");
				if (!"default".equalsIgnoreCase(localeString)) {
					Locale locale = VWBFilter.getLocale(localeString);
					if (locale != null) {
						m_locale = locale;
						request.setAttribute(JSTL_FMT_STRING, locale);
						request.setAttribute(STRUTS_LOCALE, locale);
					}
				}
			}
			this.response = response;
		}

		public Locale getLocale() {
			if (m_locale != null)
				return m_locale;
			else
				return super.getLocale();
		}

		public HttpSession getSession() {
			return getSession(true);
		}

		public HttpSession getSession(boolean create) {
			boolean rewriteCookie = (super.getSession(false) == null);
			HttpSession session = super.getSession(create);
			if (session != null && rewriteCookie) {
				// Hack for MultiSite
				if (StringUtils.equals(VWBContainerImpl.findContainer().getDomainService()
						.getDefaultDomain(), getServerName())) {
					if (!isIgnored(getServletPath())) {
						removeGlobalCookie(this, response, session);

						URLParser parser = URLParser.getParser(this);
						if (parser.getSiteId() != null) {// Site is found
							addSiteCookie(session, parser);
						} else {
							log.info("A Global session is abandoned.");
							log.info("It's created on access:"
									+ getRequestURL());
						}
					}
				}
			}
			return session;
		}

		private void addSiteCookie(HttpSession session, URLParser parser) {
			Cookie cookie = new Cookie(COOKIE_NAME, session.getId());
			cookie.setPath(parser.getSiteContext());
			response.addCookie(cookie);
		}
	}

	public static void removeGlobalCookie(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Cookie oldCookie = new Cookie(COOKIE_NAME, session.getId());
		oldCookie.setPath(request.getContextPath());
		oldCookie.setMaxAge(0);
		response.addCookie(oldCookie);
	}

	private static String getLocaleString(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("Portal.Locale")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	private static Logger log = Logger.getLogger(VWBFilter.class);
}
