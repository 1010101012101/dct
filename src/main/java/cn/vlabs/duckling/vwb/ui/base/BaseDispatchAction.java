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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.actions.DispatchAction;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.render.DView;
import cn.vlabs.duckling.vwb.service.render.Rendable;
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * 
 * @date Feb 5, 2010
 * @author xiejj@cnic.cn
 */
public abstract class BaseDispatchAction extends DispatchAction {
	
	protected ActionForward layoutShare(VWBContext context, String jsp) {
		return getFunctions().layoutShare(context, jsp);
	}
	
	protected ActionForward layout(VWBContext context) {
		return getFunctions().layout(context);
	}

	protected ActionForward layout(VWBContext context, String jsp) {
		return getFunctions().layout(context, jsp);
	}
	
	protected ActionForward layout(VWBContext context, Rendable content) {
		return getFunctions().layout(context, content);
	}
	
	protected DView getView(VWBContext context, int vid) {
		return getFunctions().getView(context, vid);
	}

	protected Resource getSavedViewPort(HttpServletRequest request) {
		return getFunctions().getSavedViewPort(request);
	}

	protected ActionForward showException(Throwable e,
			HttpServletRequest request) {
		return getFunctions().showException(e, request);
	}

	protected ActionForward showMessage(String msgkey,
			HttpServletRequest request, HttpServletResponse response) {
		return getFunctions().showMessage(msgkey, request, response);
	}


	private BaseFunctions getFunctions() {
		if (functions == null) {
			functions = new BaseFunctions();
		}
		return functions;
	}

	private BaseFunctions functions;
}
