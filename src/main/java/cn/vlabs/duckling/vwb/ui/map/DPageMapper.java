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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * 页面请求转发。
 * 
 * @date Feb 5, 2010
 * @author xiejj@cnic.cn
 */
public class DPageMapper extends AbstractRequestMapper {
	public void doMap(Resource vp, String[] params, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = DEFAULT_URL;
		String action = request.getParameter("a");
		if (action != null) {
			action = translate(action);
			action = action + "Page.do";
			url = action;
		}
		if ("editPage.do".equals(url) && isReadonly()) {
			showReadonlyPage(request, response);
			return;
		} else {
			String servlet = request.getParameter("s");
			if (servlet != null) {
				servlet = translate(action);
				url = servlet;
			}
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
			request.setAttribute("page", vp);
			forward(request, response, url);
		}
	}

	protected String getType() {
		return Resource.TYPE_DPAGE;
	}

	private String translate(String action) {
		if (action != null) {
			action = action.replaceAll("\\.", "\\/");
		}
		return action;
	}

	private String DEFAULT_URL = "/viewPage.do";
}
