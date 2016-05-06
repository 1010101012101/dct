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
package cn.vlabs.duckling.vwb.ui.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date May 6, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class SiteTemplateAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null || name.equals("")) {
			name = "init";
		}
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);

		if (context.hasAccess(response)) {
			return super.dispatchMethod(mapping, form, request, response, name);
		} else
			return null;
	}

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		request.setAttribute("templates", VWBContext.getContainer()
				.getTemplateService().getAllSiteTemplate());
		return doTemplateLayout(context);
	}

	public ActionForward templateDel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		String templateName = request.getParameter("templateName");
		if (templateName == null || templateName.equals("")) {
			request.setAttribute("templates", VWBContext.getContainer()
					.getTemplateService().getAllSiteTemplate());
			return doTemplateLayout(context);
		}

		VWBContext.getContainer().getTemplateService().deleteTemplate(templateName);

		request.setAttribute("templates", VWBContext.getContainer()
				.getTemplateService().getAllSiteTemplate());
		return doTemplateLayout(context);
	}

	private ActionForward doTemplateLayout(VWBContext context) {
		return layout(context, "/jsp/TemplateManage.jsp");
	}
}
