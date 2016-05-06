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

package cn.vlabs.duckling.vwb.ui.map;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.duckling.util.HttpUtil;
import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * 
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public abstract class AbstractRequestMapper implements IRequestMapper {
	private boolean readonly;

	protected abstract void doMap(Resource vp, String[] params,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	protected void forward(HttpServletRequest request,
			HttpServletResponse response, String url) throws ServletException,
			IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, response);
	}

	protected boolean isReadonly() {
		return this.readonly;
	}

	protected abstract String getType();

	protected void showReadonlyPage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		forward(request, response, "/readonly.jsp");
	}

	public void init(ServletContext context) {
		// Do nothing

	}

	public void map(Resource vp, String[] params, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(Attributes.VIEW_PORT, vp);
		// TO fix url error after forward.
		request.setAttribute(Attributes.REQUEST_URL,
				HttpUtil.getRequestURL(request));
		doMap(vp, params, request, response);
	}

	public void setRegistable(Registable reposity) {
		reposity.regist(getType(), this);
	}

	public void setReadOnly(boolean readonly) {
		this.readonly = readonly;
	}
}