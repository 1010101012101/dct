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

package org.apache.pluto.driver.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.driver.AttributeKeys;
import org.apache.pluto.driver.PortalDriverServlet;
import org.apache.pluto.driver.config.DriverConfiguration;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.url.impl.NumberUtil;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.spi.VWBContainer;

public class PageAdminPortlet extends GenericPlutoPortlet {
	private static final Log LOG = LogFactory.getLog(PageAdminPortlet.class);
	private static final String JSP_DIR = "/WEB-INF/fragments/admin/page/";
	private static final String VIEW_PAGE = JSP_DIR + "view.jsp";
	private static final String EDIT_PAGE = JSP_DIR + "edit.jsp";
	private static final String HELP_PAGE = JSP_DIR + "help.jsp";

	private static final String KEY_ADD_PORTLET = "addportlet";
	private static final String KEY_REMOVE_PORTLET = "removeportlet";
	private static final String KEY_ADD_PAGE = "addpage";
	private static final String KEY_REMOVE_PAGE = "removepage";
	private static final String KEY_CHANGE_PORTLET = "changeportlet";

	private boolean isSame(String key, String command) {
		return key.equals(command);
	}

	public void processAction(ActionRequest request, ActionResponse response) {
		LOG.info("System.Locale=" + request.getLocale());
		LOG.info("Command=" + request.getParameter("commandval"));
		String command = request.getParameter("commandval");
		VWBContext context = VWBContext
				.createContext(request, VWBContext.ADMIN);
		if (context.hasAccess(null, false)) {
			try {
				if (isSame(KEY_ADD_PORTLET, command)) {
					doAddPortlet(request);
				} else if (isSame(KEY_REMOVE_PORTLET, command)) {
					doRemovePortlet(request);
				} else if (isSame(KEY_ADD_PAGE, command)) {
					doAddPage(request, response);
				} else if (isSame(KEY_REMOVE_PAGE, command)) {
					doRemovePage(context.getSite(), request);
				} else if (isSame(KEY_CHANGE_PORTLET, command)) {
					doChangePortlet(request, response);
				}
			} catch (IOException e) {
				String msg = "Problem persisting configuration changes. Changes will not be persisted.";
				LOG.error(msg, e);
				// TODO: send message back to UI
			}
		}
		// TODO: send 'success' message back to UI
	}

	private void doChangePortlet(ActionRequest request, ActionResponse response) {
		String page = request.getParameter("page");
		String[] pageportlets = request.getParameterValues("placedPortlets");
		AppPortlet[] portlets = null;
		if (pageportlets != null) {
			portlets = new AppPortlet[pageportlets.length];
			for (int i = 0; i < pageportlets.length; i++) {
				portlets[i] = AppPortlet.valueOf(pageportlets[i]);
			}
		}

		PageConfig pageConfig = getPageConfig(request, page);
		if (pageConfig != null) {
			PageConfig newone = pageConfig.copyWithOutPortlets();
			if (portlets != null) {
				for (AppPortlet portlet : portlets) {
					newone.addPortlet(portlet.context, portlet.portlet);
				}
			}
			SiteMetaInfo site = VWBContainerImpl.findSite(request);
			VWBContainer container = VWBContainerImpl.findContainer();
			container.getPortalPageService().updatePage(site.getId(), newone);
		}
		response.setRenderParameter("page", page);
	}

	public static class AppPortlet {
		public AppPortlet(String app, String vPortlet) {
			this.context = "/" + app;
			this.portlet = vPortlet;
		}

		public static AppPortlet valueOf(String appportlets) {
			if (appportlets != null && appportlets.trim().length() > 0) {
				int index = appportlets.indexOf('.');
				if (index != -1) {
					String app = appportlets.substring(0, index);
					String portlet = appportlets.substring(index + 1);
					boolean old = false;
					if (portlet.indexOf('!') != -1) {
						portlet = portlet.substring(0, portlet.indexOf('!'));
						old = true;
					}
					AppPortlet ap = new AppPortlet(app, portlet);
					if (old)
						ap.portletid = appportlets;
					return ap;
				}
			}
			return null;
		}

		public String portletid;
		public String context;
		public String portlet;
	}

	public void doAddPortlet(ActionRequest request) {
		String page = request.getParameter("page");
		String applicationName = request.getParameter("applications");
		String portletName = request.getParameter("availablePortlets");

		LOG.info("Request: Add [applicationName=" + applicationName
				+ ":portletName=" + portletName + "] to page '" + page + "'");

		String contextPath = applicationName;
		if (contextPath.length() > 0) {
			contextPath = "/" + contextPath;
		}
		PageConfig config = getPageConfig(request, page);
		config.addPortlet(contextPath, portletName);

	}

	/**
	 * Adds a page to the portal via the <code>RenderConfigService</code>.
	 * 
	 * This does not add portlets to the new page. Do that when the page is
	 * created using the Add Portlet button.
	 * 
	 * @param request
	 *            The action request.
	 */
	public void doAddPage(ActionRequest request, ActionResponse response) {
		String page = request.getParameter("newPage");// newPage text input
														// element
		// Check if page is null or empty
		if (page == null || page.equals("")) {
			LOG.warn("Page parameter is null or empty. Page addition will be ignored.");
			// TODO: send message back to UI
			return;
		}

		// add by kevin to select themes
		String pagetypes = request.getParameter("pagetypes");

		String uri = mapUri(pagetypes);

		if (uri == null) {
			uri = PortalDriverServlet.DEFAULT_PAGE_URI;
		}

		PageConfig pageConfig = new PageConfig();
		pageConfig.setTitle(page);
		pageConfig.setUri(uri);

		VWBContext context = getContext(request);

		ViewPort vp = new ViewPort();
		vp.setParent(2501);
		vp.setCreateTime(new Date());
		vp.setCreator(context.getCurrentUser().getName());
		vp.setType(Resource.TYPE_PORTAL);
		vp.setTitle(page);
		if ("left_menu".equals(pagetypes)) {
			vp.setLeftMenu(Constant.DEFAULT_LEFT_MENU);
		}

		int resourceId = VWBContext.getContainer().getViewPortService()
				.createViewPort(context.getSite().getId(), vp);

		pageConfig.setResourceId(resourceId);
		VWBContext.getContainer().getPortalPageService()
				.addPage(context.getSite().getId(), pageConfig);
		response.setRenderParameter("page", Integer.toString(resourceId));
	}

	private String mapUri(String pageTypes) {
		if ("two_line".equals(pageTypes)) {
			return PortalDriverServlet.DEFAULT_PAGE_DIR + "2-theme.jsp";
		}
		if ("one_line".equals(pageTypes) || "left_menu".equals(pageTypes)) {
			return PortalDriverServlet.DEFAULT_PAGE_DIR + "1-theme.jsp";
		}
		return null;
	}

	private VWBContext getContext(PortletRequest request) {
		return (VWBContext) request.getAttribute(VWBContext.CONTEXT_KEY);
	}

	/**
	 * Removes a page from the portal ignoring any requests to remove the
	 * default page or the Pluto Admin page.
	 * 
	 * The page's portlets are still available, but no longer associated with
	 * the deleted page.
	 * 
	 * @param request
	 *            The action request.
	 * @throws IOException
	 *             If a problem occurs accessing the config file.
	 */
	public void doRemovePage(SiteMetaInfo site, ActionRequest request)
			throws IOException {
		String page = request.getParameter("page");
		// make sure we are not deleting the default page
		String defaultPage = getDefaultPage(request);
		if (page.equalsIgnoreCase(defaultPage)) {
			LOG.warn("Trying to delete the default page. Page deletion will be ignored.");
			return;
		}
		int pageId = parsePageId(page);
		if (pageId == -1) {
			LOG.warn("Trying to delete the page " + page
					+ ". it is not exist and page deleteion will be ignored.");
			return;
		}
		// make sure we are not deleting the system Page
		if (isReserved(pageId)) {
			LOG.warn("Trying to delete the reserved page. Page deletion will be ignored.");
			return;
		}
		PageConfig pageConfig = new PageConfig();
		pageConfig.setResourceId(pageId);
		VWBContainer container = VWBContainerImpl.findContainer();
		container.getPortalPageService().removePage(site.getId(),pageConfig.getResourceId());
		container.getViewPortService().removeViewPort(site.getId(), pageId);
	}

	private boolean isReserved(int pageid) {
		return (pageid > 2500 && pageid < 5000);
	}

	private int parsePageId(String pageid) {
		try {
			return Integer.parseInt(pageid);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public void doRemovePortlet(ActionRequest request) {
		String page = request.getParameter("page");
		String portletId = request.getParameter("placedPortlets");

		LOG.info("Request: Remove [portletId=" + portletId + "] from page '"
				+ page + "'");

		PageConfig config = getPageConfig(request, page);
		config.removePortlet(portletId);
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBContainer container = VWBContainerImpl.findContainer();
		container.getPortalPageService().updatePage(site.getId(), config);
	}

	private PageConfig getPageConfig(PortletRequest request, String page) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBContainer container = VWBContainerImpl.findContainer();
		return container.getPortalPageService().getPageConfig(site.getId(),
				NumberUtil.parsePageId(page));
	}

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		request.setAttribute("availablePages", getAvailablePages(request));
		request.setAttribute("selectpage", request.getParameter("page"));
		super.doView(request, response);
	}

	public String getViewPage() {
		return VIEW_PAGE;
	}

	public String getEditPage() {
		return EDIT_PAGE;
	}

	public String getHelpPage(RenderRequest request) {
		String incPage = HELP_PAGE;
		String page = request.getParameter("helpPage");
		if (page != null) {
			incPage = JSP_DIR + page;
		}
		return incPage;
	}

	public Collection<Page> getAvailablePages(PortletRequest request) {
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		ArrayList<Page> list = new ArrayList<Page>();
		VWBContainer container = VWBContainerImpl.findContainer();
		Iterator<PageConfig> it = container.getPortalPageService()
				.getAllPages(site.getId()).iterator();
		while (it.hasNext()) {
			PageConfig config = (PageConfig) it.next();
			ArrayList<Placement> portlets = new ArrayList<Placement>();
			Iterator<String> pids = config.getPortletIds().iterator();
			while (pids.hasNext()) {
				String pid = pids.next().toString();
				String name = PortletWindowConfig.parsePortletName(pid);
				portlets.add(new Placement(pid, name));
			}
			list.add(new Page(Integer.toString(config.getResourceId()), config
					.getTitle(), portlets));
		}

		return list;
	}

	/**
	 * Get the page name of the default page from
	 * pluto-portal-driver-config.xml.
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getDefaultPage(PortletRequest request) throws IOException {
		DriverConfiguration driverConfig = (DriverConfiguration) getPortletContext()
				.getAttribute(AttributeKeys.DRIVER_CONFIG);
		return driverConfig.getRenderConfigService().getDefaultPage();
	}

	public static class Page {
		private String id;
		private String name;
		private Collection<Placement> portlets;

		public Page(String pageId, String pageName,
				Collection<Placement> portlets) {
			this.id = pageId;
			this.name = pageName;
			this.portlets = portlets;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Collection<Placement> getPortlets() {
			return portlets;
		}

		public void setPortlets(Collection<Placement> portlets) {
			this.portlets = portlets;
		}
	}

	public class Placement {
		private String id;
		private String portletName;

		public Placement(String id, String portletName) {
			this.id = id;
			this.portletName = portletName;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPortletName() {
			return portletName;
		}

		public void setPortletName(String portletName) {
			this.portletName = portletName;
		}
	}

}
