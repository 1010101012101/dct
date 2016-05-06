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
import java.security.Permission;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.driver.AttributeKeys;
import org.apache.pluto.driver.core.PortalRequestContext;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.internal.impl.PortletURLImpl;
import org.springframework.beans.factory.BeanFactory;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.service.config.DomainNameService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.url.impl.UrlServiceImpl;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;
import cn.vlabs.duckling.vwb.ui.map.IRequestMapper;
import cn.vlabs.duckling.vwb.url.URLParser;

/**
 * Introduction Here.
 * 
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public class VWBDriverServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger("VWB Driver:");

	private static final long serialVersionUID = 1L;

	private VWBContainer container;

	private FetchToSession fetcher;
	private WelcomePageIgnore ignore;
	private PortletContainer portletContainer;

	private String buildURL(URLParser info, HttpServletRequest request) {
		boolean isHttp;
		StringBuffer buff = new StringBuffer();
		buff.append(request.getScheme() + "://");
		isHttp = "http".equalsIgnoreCase(request.getScheme());

		buff.append(request.getServerName());
		if (isHttp) {
			if (request.getServerPort() != 80)
				buff.append(":" + request.getServerPort());
		} else if (request.getServerPort() != 443) {
			buff.append(":" + request.getServerPort());
		}

		buff.append(info.getURI());
		if (request.getQueryString() != null) {
			buff.append("?" + request.getQueryString());
		}
		return buff.toString();
	}

	private PortletWindowImpl findPortletWindow(HttpServletRequest request,
			HttpServletResponse response, URLParser url, PageConfig pageConfig) {
		int sequence;
		if (url.isSequenceSpecified()) {
			sequence = url.getSequence();
		} else {
			sequence = 0;
		}
		String portletId = pageConfig.findPortletId(sequence);
		if (portletId != null) {

			PortletWindowConfig windowConfig = PortletWindowConfig
					.fromId(portletId);
			PortalRequestContext portalRequestContext = new PortalRequestContext(
					getServletContext(), request, response);
			PortalURL portalURL = portalRequestContext.getRequestedPortalURL();
			return new PortletWindowImpl(portletContainer, windowConfig,
					portalURL);
		}
		return null;
	}

	private SiteMetaInfo findSiteByDomain(HttpServletRequest request) {
		return VWBContainerImpl.findSite(request);
	}

	private SiteMetaInfo findSiteById(HttpServletRequest request, String siteid) {
		if (siteid != null) {
			try {
				int id = Integer.parseInt(siteid);
				SiteMetaInfo site = container.getSite(id);
				if (site != null){
					request.setAttribute(Attributes.REQUEST_SITE_KEY, site);
				}
				return site;
			} catch (NumberFormatException e) {
				log.warn("Site " + siteid
						+ " is requested, but it can't be found.");
			}
		}
		return null;
	}

	private String getFrontPage(HttpServletRequest request) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		UrlServiceImpl urlConstructor = new UrlServiceImpl(site.getId(),  VWBContainerImpl.findContainer().getSiteConfig());
		return urlConstructor.getFrontPage();
	}

	private boolean hasEditPermission(HttpServletRequest request, SiteMetaInfo site,
			int pageId) {
		Resource resource = VWBContainerImpl.findContainer()
				.getResourceService().getResource(site.getId(), pageId);
		if(resource==null){
			return false;
		}
		Permission p;
		if (Resource.TYPE_DPAGE.equals(resource.getType())) {
			p = DPageCommand.EDIT.targetCommand(resource)
					.getRequiredPermission();
		} else if (Resource.TYPE_PORTAL.equals(resource.getType())) {
			p = PortalCommand.CONFIG.targetCommand(resource)
					.getRequiredPermission();
		} else {
			return true;
		}

		return VWBContext.checkPermission(request, p);
	}

	private void processSimpleURL(URLParser url, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		if (url.getPage() != null) {
			PageConfig pageConfig = VWBContainerImpl
					.findContainer()
					.getPortalPageService()
					.getPageConfig(site.getId(),
							Integer.parseInt(url.getPage()));
			if (pageConfig != null) {
				PortletWindowImpl portletWindow = findPortletWindow(request,
						response, url, pageConfig);

				PortletURLImpl portletURL = new PortletURLImpl(
						portletContainer, portletWindow, request, response,
						false);
				portletURL.getParameterMap().clear();
				if (request.getParameterMap() != null) {
					for (Object paramName : request.getParameterMap().keySet()) {
						Object paramValue = request.getParameterMap().get(
								paramName);
						if (paramValue instanceof String[]) {
							portletURL.setParameter((String) paramName,
									(String[]) paramValue);
						} else {
							portletURL.setParameter((String) paramName,
									(String) paramValue);
						}
					}
				}
				response.sendRedirect(portletURL.toString());
				return;
			}
		}
		response.sendRedirect(getFrontPage(request));
	}

	private boolean requireRedirect(HttpServletRequest request,
			URLParser parser, int siteId) {
		DomainNameService domainService = container.getDomainService();
		if (domainService.getSiteDefaultDomain(siteId) == null) {
			// use use siteid identifier
			if (StringUtils.isBlank(parser.getSiteId())) {
				return true;
			}
			if (!StringUtils.equals(domainService.getDefaultDomain(),
					request.getServerName())) {
				return true;
			}
		} else {
			// use domain identifier.
			if (!StringUtils.isBlank(parser.getSiteId())) {
				return true;
			}
			if (!StringUtils.equalsIgnoreCase(domainService.getSiteDefaultDomain(siteId),
					request.getServerName())) {
				return true;
			}
		}
		return false;
	}

	private void sendError(HttpServletRequest request,
			HttpServletResponse response, Throwable e) throws IOException {
		log.error(e.getMessage(),e);
		request.getSession().setAttribute(Attributes.EXCEPTION_KEY, e);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private boolean showWelcome(HttpServletRequest request, SiteMetaInfo site,
			int pageId) {
		if (site.isPublished() || ignore.isIgnored(pageId)
				|| hasEditPermission(request, site, pageId)) {
			return false;
		} else {
			return true;
		}
	}

	public void destroy() {
		container = null;
		portletContainer = null;
		fetcher = null;
		ignore = null;
		super.destroy();
	}

	public void init() throws ServletException {
		BeanFactory factory = (BeanFactory) this.getServletContext()
				.getAttribute(Attributes.APPLICATION_CONTEXT_KEY);
		container = (VWBContainer) factory.getBean("container");
		container.getMapFactory().init(getServletContext());
		portletContainer = (PortletContainer) getServletContext().getAttribute(
				AttributeKeys.PORTLET_CONTAINER);
		fetcher = container.getFetcher();
		ignore = new WelcomePageIgnore(container.getConfig().getProperty(
				"welcome.ignore.list"));
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		URLParser info = URLParser.getParser(request);
		if (info.hasMoreSlash()) {
			response.sendRedirect(buildURL(info, request));
			return;
		}
		SiteMetaInfo site = findSiteById(request, info.getSiteId());

		if (site == null){
			site = findSiteByDomain(request);
		}

		if (site == null) {
			log.info("Neither site find by site id or domain. redirect to default site.");
			site = container.getSite(VWBContainer.ADMIN_SITE_ID);
			response.sendRedirect(getFrontPage(request));
			return;
		}
		if (requireRedirect(request, info, site.getId())) {
			response.sendRedirect(getFrontPage(request));
			return;
		}

		if (info.isPlainURL()) {
			// Plain URL just forward.
			// support for Site id based url (login/logout/JSON-RPC etc.).
			if (info.getPage() != null) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher(info.getPage());
				dispatcher.forward(request, response);
				return;
			}
		}

		if (info.isSimpleURL()) {
			processSimpleURL(info, request, response);
			return;
		}

		if(info.getPage() == null){
			response.sendRedirect(getFrontPage(request));
			return;
		}
		// Proceeding viewport...
		int vid = Constant.DEFAULT_FRONT_PAGE;

		vid = Integer.parseInt(info.getPage());
		Resource vp = VWBContainerImpl.findContainer().getResourceService().getResource(site.getId(), vid);
		if(vp==null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		try {
			if (showWelcome(request, site, vid)) {
				vid = Constant.WELCOME_FRONT_PAGE;
				vp = VWBContainerImpl.findContainer().getResourceService().getResource(site.getId(), vid);
			}
		} catch (NumberFormatException e) {
			response.sendRedirect(getFrontPage(request));
			return;
		}

		
		request.setAttribute("contextPath", request.getContextPath());
		try {
			IRequestMapper mapper = container.getMapFactory()
					.getRequestMapper(vp.getType());
			if (mapper != null) {
				log.debug(vp.getType()+" is requested.");
				fetcher.saveToSession(request);
				mapper.map(vp, info.getParams(), request, response);
			} else {
				log.debug("not support type is requested.");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
		} catch (ServletException e) {
			if (e.getRootCause() != null) {
				sendError(request, response, e.getRootCause());
			} else
				sendError(request, response, e);
		} catch (Throwable e) {
			sendError(request, response, e);
		}
	}
}
