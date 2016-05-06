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

package cn.vlabs.vwb.driver;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.PortletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.driver.AttributeKeys;
import org.apache.pluto.driver.core.PortalRequestContext;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.om.portlet.PortletDefinition;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseFunctions;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;
import cn.vlabs.vwb.container.spi.SiteEnviromentService;
import cn.vlabs.vwb.driver.internal.SiteSessionImpl;

/**
 * The controller servlet used to drive the Portal Driver. All requests mapped
 * to this servlet will be processed as Portal Requests.
 *
 * @version 1.0
 * @since Sep 22, 2004
 */
public class PortalDriver{

	/** Internal Logger. */
    private static final Logger LOG = Logger.getLogger(PortalDriver.class);
    
    /** The Portal Driver sServlet Context */
    private ServletContext servletContext = null;
    
    private BaseFunctions functions;
    
    /** Is the SupportedModesService initialized? */
//    private boolean isSupportedModesServiceInitialized = false;
        
    public static final String DEFAULT_PAGE_URI =
    		"/WEB-INF/themes/pluto-default-theme.jsp";
    
    public static final String DEFAULT_PAGE_DIR =
        "/WEB-INF/themes/"; 
    
    /** The portlet container to which we will forward all portlet requests. */
    protected PortletContainer container = null;

    /** Character encoding and content type of the response */
    private String contentType = "";

    // HttpServlet Impl --------------------------------------------------------
    
    public String getServletInfo() {
        return "Pluto Portal Driver Servlet";
    }
    
    /**
     * Initialize the Portal Driver. This method retrieves the portlet container
     * instance from the servlet context scope.
     * @see PortletContainer
     */
    public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
        container = (PortletContainer) servletContext.getAttribute(
        		AttributeKeys.PORTLET_CONTAINER);        
        contentType = "text/html;";
        functions = new BaseFunctions();
    }
    
    private void saveSiteSession(HttpServletRequest request){
        SiteEnviromentService env = container.getOptionalContainerServices().getSiteEnviromentService();
        SiteSessionImpl siteSession =  (SiteSessionImpl)env.createSiteSession(request, request.getSession());
		VWBContainerImpl.findContainer().getFetcher().fetchToPortal(request.getSession(), siteSession);
    }
    /**
     * Handle all requests. All POST requests are passed to this method.
     * @param request  the incoming HttpServletRequest.
     * @param response  the incoming HttpServletResponse.
     * @throws ServletException  if an internal error occurs.
     * @throws IOException  if an error occurs writing to the response.
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (LOG.isDebugEnabled()) {
        	LOG.debug("Start of PortalDriverServlet.doGet() to process portlet request . . .");
        }
        PortalRequestContext portalRequestContext =
            new PortalRequestContext(servletContext, request, response);

        PortalURL portalURL = null;
        
        try {
        	portalURL = portalRequestContext.getRequestedPortalURL();
        } catch(Exception ex) {
        	String msg = "Cannot handle request for portal URL. Problem: "  + ex.getMessage();
        	LOG.error(msg, ex);
        	throw new ServletException(msg, ex);
        }
        
        
        //Add by xiejj for support multispace
        Resource resource = functions.getSavedViewPort(request);
        VWBContext context = VWBContext.createContext(request, PortalCommand.VIEW, resource);
        if (!context.hasAccess(response)){
        	return;
        }
        
        saveSiteSession(request);
       
        String actionWindowId = portalURL.getActionWindow();
        String resourceWindowId = portalURL.getResourceWindow();
        
        PortletWindowConfig actionWindowConfig = null;
        PortletWindowConfig resourceWindowConfig = null;
        
		if (resourceWindowId != null){
			resourceWindowConfig = PortletWindowConfig.fromId(resourceWindowId);
		} else if(actionWindowId != null){
			 actionWindowConfig = PortletWindowConfig.fromId(actionWindowId);
		}
		
		//如果不是资源模式，则设置输出为配置的字符编码
		//资源模式的字符编码自己设置。
		if (resourceWindowConfig==null){
	        if ( !"".equals(contentType)) {
	            response.setContentType( contentType );
	        }
		}
        // Action window config will only exist if there is an action request.
        if (actionWindowConfig != null) {
            PortletWindowImpl portletWindow = new PortletWindowImpl(container,
            		actionWindowConfig, portalURL);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing action request for window: "
                		+ portletWindow.getId().getStringId());
            }
            try {
                container.doAction(portletWindow, request, response);
            } catch (PortletContainerException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new ServletException(ex);
            } catch (PortletException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new ServletException(ex);
            }
            if (LOG.isDebugEnabled()) {
            	LOG.debug("Action request processed.\n\n");
            }
        }
        //Resource request
        else if (resourceWindowConfig != null) {
        	try {
        		if (request.getParameterNames().hasMoreElements())
        			setPublicRenderParameter(request, portalURL, portalURL.getResourceWindow());
			} catch (PortletContainerException e) {
				LOG.error(e);
				throw new ServletException(e);
			}
            PortletWindowImpl portletWindow = new PortletWindowImpl(container,
                               resourceWindowConfig, portalURL);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing resource Serving request for window: "
                               + portletWindow.getId().getStringId());
            }
            try {
                container.doServeResource(portletWindow, request, response);
            } catch (PortletContainerException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new ServletException(ex);
            } catch (PortletException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new ServletException(ex);
            }
            if (LOG.isDebugEnabled()) {
               LOG.debug("Resource serving request processed.\n\n");
            }
        }
        // Otherwise (actionWindowConfig == null), handle the render request.
        else {
        	if (LOG.isDebugEnabled()) {
        		LOG.debug("Processing render request.");
        	}
            PageConfig pageConfig = portalURL.getPageConfig(request);
            if (pageConfig == null)
            {
            	String renderPath = (portalURL == null ? "" : portalURL.getRenderPath());
                String msg = "PageConfig for render path [" + renderPath + "] could not be found.";
                LOG.error(msg);
                throw new ServletException(msg);
            }
            
            request.setAttribute(AttributeKeys.CURRENT_PAGE, pageConfig);
            String uri = (pageConfig.getUri() != null)
            		? pageConfig.getUri() : DEFAULT_PAGE_URI;
            if (LOG.isDebugEnabled()) {
            	LOG.debug("Dispatching to: " + uri);
            }
            functions.layout(request, response, context);
            if (LOG.isDebugEnabled()) {
            	LOG.debug("Render request processed.\n\n");
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void setPublicRenderParameter(HttpServletRequest request, PortalURL portalURL, String portletID)throws ServletException, PortletContainerException {    		
		String applicationId = PortletWindowConfig.parseContextPath(portletID);
        String applicationName = applicationId;
        if (applicationName.length() >0 )
        {
            applicationName = applicationName.substring(1);
        }

		String portletName = PortletWindowConfig.parsePortletName(portletID);
		PortletDefinition portletDD = container.getOptionalContainerServices().getPortletRegistryService()
								.getPortlet(applicationName, portletName);    		
		Enumeration<String> parameterNames = request.getParameterNames();
		if (parameterNames != null){
			while(parameterNames.hasMoreElements()){
				String parameterName = parameterNames.nextElement();
				if (portletDD.getSupportedPublicRenderParameters() != null){
					if (portletDD.getSupportedPublicRenderParameters().contains(parameterName)){
						String value = request.getParameter(parameterName);
						portalURL.addPublicParameterActionResourceParameter(parameterName, value);
					}	
				}
			}
		}
    }
}

