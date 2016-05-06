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

package cn.vlabs.duckling.vwb.ui.base;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.render.DView;
import cn.vlabs.duckling.vwb.service.render.Rendable;
import cn.vlabs.duckling.vwb.service.render.impl.JSPRendable;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public class BaseFunctions {
	public ActionForward layout(VWBContext context){
		prepareView(context, null);
		return new ActionForward("/layout.jsp");
	}
	
	public void layout(HttpServletRequest request, HttpServletResponse response, VWBContext context) throws ServletException, IOException{
		prepareView(context, null);
		request.getRequestDispatcher("/layout.jsp").forward(request, response);
	}
	public void layout(HttpServletRequest request, HttpServletResponse response, VWBContext context, String jsp) throws ServletException, IOException{
		prepareView(context, new JSPRendable(jsp, context.getResource().getResourceId()));
		request.getRequestDispatcher("/layout.jsp").forward(request, response);
	}
	
	public void layout(HttpServletRequest request, HttpServletResponse response, VWBContext context, Rendable content) throws ServletException, IOException{
		prepareView(context, content);
		request.getRequestDispatcher("/layout.jsp").forward(request, response);
	}
	
	public ActionForward layout(VWBContext context, String jsp){
		return layout(context , new JSPRendable(jsp, context.getResource().getResourceId()));
	}
	
	public ActionForward layoutShare(VWBContext context, String jsp){
		return layoutShare(context , new JSPRendable(jsp, context.getResource().getResourceId()));
	}
	
	public ActionForward layout(VWBContext context, Rendable content){
		prepareView(context, content);
		return new ActionForward("/layout.jsp");
	}
	
	public ActionForward layoutShare(VWBContext context, Rendable content){
		prepareShareView(context, content);
		return new ActionForward("/layout.jsp");
	}

	private void prepareView(VWBContext context, Rendable content) {
		DView view = getView(context, context.getResource().getResourceId());
		HttpServletRequest request = context.getHttpRequest();
		request.setAttribute(Attributes.RENDER_DVIEW, view);
		if (content!=null){
			view.setContent(content);
		}
	}
	
	private void prepareShareView(VWBContext context, Rendable content) {
		DView view = getView(context, context.getResource().getResourceId());
		view.setShowUserbox(false);
		view.setShowSearchbox(false);
		
		HttpServletRequest request = context.getHttpRequest();
		request.setAttribute(Attributes.RENDER_DVIEW, view);
		if (content!=null){
			view.setContent(content);
		}
	}
	
	public DView getView(VWBContext context, int vid){
		ViewPort vp = VWBContext.getContainer().getViewPortService().getMappedView(context.getSite().getId(),vid);
		return VWBContext.getContainer().getRenderFactory().createView(vp);
	}
	
	public Resource getSavedViewPort(HttpServletRequest request){
		return (Resource) request.getAttribute(Attributes.VIEW_PORT);
	}
	
	public ActionForward showException(Throwable e, HttpServletRequest request)  {
		request.setAttribute(Attributes.EXCEPTION_KEY, e);
		request.setAttribute(Attributes.MESSAGE_KEY, "message.exception.happend");
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBContext vwbcontext = VWBContext.createContext(request, VWBCommand.ERROR, getResource(site.getId(), MESSAGE_VIEWPORT));
		return layout(vwbcontext);
	}
	
	public ActionForward showMessage(String msgkey, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(Attributes.MESSAGE_KEY, msgkey);
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		VWBContext vwbcontext = VWBContext.createContext(request, VWBCommand.ERROR, getResource(site.getId(), MESSAGE_VIEWPORT));
		return layout(vwbcontext);
	}
	public Resource getResource(int siteId, int vid){
		return VWBContainerImpl.findContainer().getResourceService().getResource(siteId, vid);
	}
	
	protected int MESSAGE_VIEWPORT = Constant.DEFAULT_MESSAGE_PAGE;
}
