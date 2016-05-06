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
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageConstants;
import cn.vlabs.duckling.vwb.service.i18n.DucklingMessage;
import cn.vlabs.duckling.vwb.service.resource.IResourceService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.url.UrlService;
import cn.vlabs.duckling.vwb.service.url.impl.UrlServiceImpl;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.Command;
import cn.vlabs.duckling.vwb.ui.command.CommandResolver;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.vwb.driver.services.container.SiteEnviromentServiceImpl;

/**
 * 代表当前请求的类
 * 
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 28, 2010 9:14:35 AM
 */
public class VWBContext implements Command, Cloneable {
	public static final String VIEW = DPageCommand.VIEW.getAction();

	public static final String DIFF = DPageCommand.DIFF.getAction();

	public static final String INFO = DPageCommand.INFO.getAction();

	public static final String PREVIEW = DPageCommand.PREVIEW.getAction();

	public static final String CONFLICT = DPageCommand.CONFLICT.getAction();

	public static final String EDIT = DPageCommand.EDIT.getAction();
	public static final String SHARE = DPageCommand.SHARE.getAction();
	public static final String DELETE = DPageCommand.DELETE.getAction();

	public static final String FIND = VWBCommand.FIND.getAction();

	public static final String ERROR = VWBCommand.ERROR.getAction();

	public static final String ATTACH = VWBCommand.ATTACH.getAction();

	public static final String PORTLET = PortalCommand.VIEW.getAction();

	public static final String SIMPLE = PortalCommand.SIMPLE.getAction();

	public static final String LOGIN = VWBCommand.LOGIN.getAction();

	public static final String NONE = VWBCommand.NONE.getAction();

	public static final String LOGOUT = VWBCommand.LOGOUT.getAction();

	public static final String CREATE_RESOURCE = VWBCommand.CREATE_RESOURCE
			.getAction();

	public static final String CONTEXT_KEY = "vwb.context";

	public static final int LATEST_VERSION = DPageConstants.LATEST_VERSION_FLAG;

	public static final String ADMIN = VWBCommand.ADMIN.getAction();

	private static Logger log = Logger.getLogger(VWBContext.class);

	private Map<String, Object> m_variables = new HashMap<String, Object>();

	private VWBSession m_session;

	private SiteMetaInfo m_site;

	private Command m_command;

	private HttpServletRequest m_request;

	private Resource m_vp;

	private String wysiwygEditorMode;

	public static final String EDITOR_MODE = "D.E.";
	private UrlService urlConstructor;

	public static final String PLAIN = VWBCommand.PLAIN.getAction();

	private boolean useDData = false;

	public boolean isUseDData() {
		return useDData;
	}

	public void setUseDData(boolean useDData) {
		this.useDData = useDData;
	}

	public String getWysiwygEditorMode() {
		return wysiwygEditorMode;
	}

	public void setWysiwygEditorMode(String wysiwygEditorMode) {
		this.wysiwygEditorMode = wysiwygEditorMode;
	}

	public VWBContext(HttpServletRequest request, SiteMetaInfo site,
			Command command, Resource vp) {
		this.m_request = request;
		this.m_site = site;
		if (command != null) {
			this.m_command = command.targetCommand(vp);
		}

		this.m_vp = vp;

		this.m_session = VWBSession.findSession(request);
		//主站点csp.escience.cn 子站点a.csp.escience.cn  b.csp.escience.cn  ie内核浏览器 浏览主站点 然后会议查询点开子站点a并登陆 
		//然后点开b站点 则a中的权限会串到b 原因是由于ie内核浏览器会使用朱站点sessionId来访问子站点
		//将站点的权限替换
		if(m_site.getId()!=m_session.getSiteId()){
			VWBSession vwbsession = VWBSession.findSession(request);
			if(vwbsession.isAuthenticated()){
				VWBContainer container = VWBContainerImpl.findContainer();
				String vo=container.getSiteConfig().getVO(site.getId());
				List<java.security.Principal> prins = container.getUserService()
						.getUserPrincipal(vwbsession.getCurrentUser().getName(), vo);
				vwbsession.setPrincipals(prins);
				vwbsession.setSiteSession(SiteEnviromentServiceImpl.createSiteSession(site.getId()));
				vwbsession.setFullMode(false);
				this.m_session = vwbsession;
			}
		}
		parseInputMode(request);
		this.urlConstructor = new UrlServiceImpl(site.getId(), getContainer()
				.getSiteConfig());
	}

	public VWBContext(SiteMetaInfo site, VWBSession vwbsession,
			Command command, Resource resource, String displayMode) {
		this.m_site = site;
		this.m_session = vwbsession;
		if (command != null) {
			this.m_command = command.targetCommand(resource);
		}
		this.m_vp = resource;
		parseDisplayMode(displayMode);
		this.urlConstructor = new UrlServiceImpl(site.getId(), getContainer()
				.getSiteConfig());
	}

	public static VWBContext createContext(HttpServletRequest request,
			Command command, Resource vp) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBContext context = new VWBContext(request, site, command, vp);
		request.setAttribute(CONTEXT_KEY, context);
		return context;
	}

	public static VWBContext createContext(int siteId,
			HttpServletRequest request, Command command, Resource vp) {
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(siteId);
		VWBContext context = new VWBContext(request, site, command, vp);
		request.setAttribute(CONTEXT_KEY, context);
		return context;
	}

	public static VWBContext createContext(PortletRequest request,
			String requestContext) {// No Saving
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBSession session = VWBSession.findSession(request);
		IResourceService resourceService = getContainer().getResourceService();
		return new VWBContext(site, session, VWBCommand.ADMIN,
				resourceService.getResource(site.getId(),
						Constant.DEFAULT_FRONT_PAGE), request.getParameter("m"));
	}

	public static VWBContext createContext(HttpServletRequest request,
			String requestContext) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		Command command = CommandResolver.findCommand(requestContext);
		IResourceService resourceService = getContainer().getResourceService();
		VWBContext context = new VWBContext(request, site, command,
				resourceService.getResource(site.getId(),
						Constant.DEFAULT_FRONT_PAGE));
		request.setAttribute(CONTEXT_KEY, context);
		return context;
	}

	public static VWBContext getContext(HttpServletRequest request) {
		return (VWBContext) request.getAttribute(CONTEXT_KEY);
	}

	public String getAction() {
		return m_command.getAction();
	}

	public Permission getRequiredPermission() {
		return m_command.getRequiredPermission();
	}

	public Object getTarget() {
		return m_command.getTarget();
	}

	public Resource getResource() {
		return m_vp;
	}
	
	public boolean hasAccess(){
		return checkPermission(m_command.getRequiredPermission());
	}
	
	public boolean hasAccess(HttpServletResponse response, boolean redirect) {// more
																				// parameters
																				// to
																				// avoid
																				// wrong
																				// usage
																				// of
																				// hasAccess
		if (redirect) {
			return hasAccess(response);
		} else {
			return checkPermission(m_command.getRequiredPermission());
		}
	}

	public static boolean checkPermission(HttpServletRequest request,
			Permission permission) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBSession session = VWBSession.findSession(request);
		return getContainer().getAuthorizationService().checkPermission(
				site.getId(), session, permission);
	}

	public boolean checkPermission(Permission permission) {
		return getContainer().getAuthorizationService().checkPermission(
				getSiteId(), m_session, permission);
	}

	public boolean hasAccess(HttpServletResponse response) {
		boolean allowed = checkPermission(m_command.getRequiredPermission());
		if (!allowed) {
			Principal currentUser = m_session.getCurrentUser();

			try {
				if (m_session.isAuthenticated()) {
					log.info("User " + currentUser.getName()
							+ " has no access - forbidden (permission="
							+ getRequiredPermission() + ")");
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				} else {
					log.info("User " + currentUser.getName()
							+ " has no access - redirecting (permission="
							+ getRequiredPermission() + ")");

					String pageurl = (m_vp != null) ? Integer.toString(m_vp
							.getResourceId()) : "";
					String requesturl = (String) m_request
							.getAttribute(Attributes.REQUEST_URL);
					if (requesturl == null) {
						requesturl = getRequestURL(m_request);
					}
					m_request.getSession().setAttribute(Attributes.REQUEST_URL,
							requesturl);
					response.sendRedirect(getURL(VWBContext.LOGIN, pageurl,
							null, false));
				}
			} catch (IOException e) {
				log.error("Redirect failed for:" + e.getMessage());
				throw new InternalVWBException(e.getMessage());
			}
		}
		return allowed;
	}

	// FIX: http://${domain}/site/${id}/attach/${hash}方式下，如果没有权限无法正确重定向的Bug
	private String getRequestURL(HttpServletRequest request) {
		String url = getBaseURL() + request.getServletPath();

		if (request.getPathInfo() != null)
			url += request.getPathInfo();

		if (request.getQueryString() != null)
			url = url + "?" + request.getQueryString();
		return url;
	}

	public int getSiteId() {
		return getSite().getId();
	}

	private String baseurl = null;

	public String getBaseURL() {
		if (baseurl == null) {
			baseurl = urlConstructor.getBaseURL();
		}
		if (StringUtils.isEmpty(baseurl)) {
			int port = m_request.getLocalPort();
			String url = m_request.getScheme() + "://"
					+ m_request.getRequestURI();
			if (port != 80 || port != 443) {
				url = url + ":" + port;
			}
			url = url + m_request.getContextPath();
			baseurl = url;
		}
		return baseurl;
	}

	public String getCLBBaseURL() {
		String baseurl = getProperty("duckling.clb.url");
		if (StringUtils.isEmpty(baseurl)) {
			int port = m_request.getLocalPort();
			String url = m_request.getScheme() + "://"
					+ m_request.getRequestURI();
			if (port != 80 || port != 443) {
				url = url + ":" + port;
			}
			url = url + m_request.getContextPath();
			baseurl = url;
		}
		return baseurl;
	}

	public HttpServletRequest getHttpRequest() {
		return m_request;
	}

	public Command getCommand() {
		return m_command;
	}

	public String getTemplatePath() {
		return null;
	}

	public VWBSession getVWBSession() {
		if (m_session == null) {
			m_session = VWBSession.findSession(m_request);
		}
		return m_session;
	}

	@SuppressWarnings("unchecked")
	public ResourceBundle getBundle(String bundle) {
		return DucklingMessage.getBundle(bundle, m_request.getLocale(),
				m_request.getLocales());
	}

	public Principal[] getPrincipals() {
		return getVWBSession().getPrincipals();
	}

	public Principal getCurrentUser() {
		return getVWBSession().getCurrentUser();
	}

	public Object getVariable(String key) {
		return m_variables.get(key);
	}

	public void setVariable(String key, Object val) {
		m_variables.put(key, val);
	}

	public static VWBContainer getContainer() {
		return VWBContainerImpl.findContainer();
	}

	public SiteMetaInfo getSite() {
		return m_site;
	}

	public boolean resourceExists(int id) {
		return getContainer().getResourceService().getResource(getSiteId(), id) != null;
	}

	public Command targetCommand(Object target) {
		m_command = m_command.targetCommand(target);
		m_vp = (Resource) target;
		return m_command;
	}

	public String getContentJSP() {
		return m_command.getContentJSP();
	}

	public boolean isFullMode() {
		if (m_session != null)
			return m_session.isFullMode();
		else
			return false;
	}

	private void parseInputMode(HttpServletRequest request) {
		String mode = null;
		if (request != null)
			mode = request.getParameter("m");
		parseDisplayMode(mode);
	}

	private void parseDisplayMode(String mode) {
		if (mode != null) {
			if ("1".equals(mode)) {
				m_session.setFullMode(true);
			} else
				m_session.setFullMode(false);
		}
	}

	public DPage getPage() {
		return (DPage) getResource();
	}

	public String getURL(String action, String pageName, String params) {
		return urlConstructor.getURL(action, pageName, params);
	}

	public String getURLPattern() {
		return m_command.getURLPattern();
	}

	public String getURL(String action, String pageName) {
		return urlConstructor.getURL(action, pageName, null);
	}

	public String getVO() {
		return getContainer().getSiteConfig().getVO(getSiteId());
	}

	public Object clone() {
		VWBContext context = null;
		try {
			context = (VWBContext) super.clone();
			context.m_command = this.m_command;
			context.m_request = this.m_request;
			context.m_session = this.m_session;
			context.m_site = this.m_site;
			context.m_variables = new HashMap<String, Object>();
			context.m_variables.putAll(this.m_variables);
			context.m_vp = this.m_vp;
		} catch (CloneNotSupportedException cnsex) {
			return null;
		}

		return context;
	}

	public String getURL(String action, int resourceId) {
		return urlConstructor
				.getURL(action, Integer.toString(resourceId), null);
	}

	/**
	 * 站点的字符编码类型
	 * 
	 * @return 站点字符编码
	 */
	public String getContentEncoding() {
		return getProperty(KeyConstants.ENCODING);
	}

	public String getHTML(String pagename) {
		int id = 0;
		if (pagename != null) {
			try {
				id = Integer.valueOf(pagename);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		DPage dpage = getContainer().getDpageService()
				.getLatestDpageByResourceId(getSiteId(), id);
		return getHTML(dpage);
	}

	/**
	 * add by diyanliang 2010-2-26 Returns the converted HTML of the page using
	 * a different context than the default context.
	 * 
	 * @param context
	 * 
	 * @param page
	 *            WikiPage reference.
	 * @return HTML-rendered version of the page.
	 */
	public String getHTML(DPage page) {
		int ver = page.getVersion();
		if (ver == 0)
			ver = Constant.DPAGE_LATEST_VERSION;
		return getContainer().getRenderingService().getHTML(this,
				page.getContent());
	}

	public String getHTML(String pagename, int version) {
		int id = 0;
		if (pagename != null) {
			try {
				id = Integer.valueOf(pagename);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		DPage dpage = getContainer().getDpageService().getDpageVersionContent(
				getSiteId(), id, version);
		return getHTML(dpage);
	}

	public String getSiteName() {
		return getProperty(KeyConstants.SITE_NAME_KEY);
	}

	public String getFrontPage() {
		ISiteConfig siteConfig = getContainer().getSiteConfig();
		String strdefaultpage = siteConfig.getProperty(getSiteId(),
				"duckling.defaultpage");
		if (strdefaultpage == null || ("").equals(strdefaultpage)) {
			strdefaultpage = Integer.toString(Constant.DEFAULT_FRONT_PAGE);
		}
		return urlConstructor.makeURL(VWBContext.VIEW, strdefaultpage, null, true);
	}

	public String getVOGroup() {
		return getContainer().getUserService().getVOGroup(
				getProperty(KeyConstants.SITE_UMT_VO_KEY));
	}

	public String getProperty(String key) {
		return getContainer().getSiteConfig().getProperty(getSiteId(), key);
	}

	public String getBasePath() {
		return urlConstructor.getBasePath();
	}

	public String getProperty(String key, String defaultValue) {
		return getContainer().getSiteConfig().getProperty(getSiteId(), key,
				defaultValue);
	}

	public String getURL(String action, String pageName, String params,
			boolean absolute) {
		return urlConstructor.makeURL(action, pageName, params, absolute);
	}

	public String getEditURL(int resourceid) {
		return getURL(VWBContext.EDIT, Integer.toString(resourceid), null);
	}

	public String getViewURL(int resourceid) {
		return getURL(VWBContext.VIEW, Integer.toString(resourceid), null);
	}
}
