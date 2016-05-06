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

package cn.vlabs.duckling.vwb.ui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.render.impl.JSPRendable;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.base.BaseServlet;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 20, 2011 11:46:19 AM
 */
public class ErrorServlet extends BaseServlet {
	private VWBContainer container;

	public void init() {
		container = VWBContainerImpl.findContainer();
	}

	private static final long serialVersionUID = 1L;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String error = request.getParameter("e");
		if (error == null) {
			error = "500";
		}
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		if (site == null) {
			site = container.getAdminSite();
		}
		request.setAttribute("contextPath", request.getContextPath());
		VWBContext context = VWBContext.createContext(
				request,
				VWBCommand.ERROR,
				VWBContainerImpl
						.findContainer()
						.getResourceService()
						.getResource(site.getId(),
								Constant.DEFAULT_MESSAGE_PAGE));
		layout(request, response, context,
				findRendable(error, Constant.DEFAULT_MESSAGE_PAGE));
	}

	private JSPRendable findRendable(String error, int resourceId) {
		String jsp;
		if ("404".equals(error) || "403".equals(error)) {
			jsp = "/error/" + error + ".jsp";
		} else {
			jsp = "/error/500.jsp";
		}
		return new JSPRendable(jsp, resourceId);
	}
}
