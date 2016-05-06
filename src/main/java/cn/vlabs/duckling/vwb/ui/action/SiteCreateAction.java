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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.user.NoPermissionException;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date May 6, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class SiteCreateAction extends BaseDispatchAction {

	private ActionForward doCreateLayout(VWBContext context) {
		return layout(context, "/jsp/SiteCreate.jsp");
	}

	private boolean setParamsAndValidateForCreateSiste(
			HttpServletRequest request, Map<String, String> params) {
		String sitename = request.getParameter("sitename");
		String domain = request.getParameter("domain");
		String policy = request.getParameter("policy");
		if (StringUtils.isEmpty(sitename) || StringUtils.isEmpty(domain)) {
			return false;
		}
		params.put(KeyConstants.SITE_NAME_KEY, sitename);
		params.put(KeyConstants.SITE_DOMAIN_KEY, domain);

		params.put(KeyConstants.SITE_ACCESS_OPTION_KEY, policy);

		String mailservice = request.getParameter("mailservice");
		if ("2".equals(mailservice)) {
			String smtp = request.getParameter("smtp");
			params.put(KeyConstants.SITE_SMTP_HOST_KEY, smtp);

			String certificate = request.getParameter("certificate");
			params.put(KeyConstants.SITE_MAIL_AUTH_KEY, certificate);
			String fromaddress = request.getParameter("replyaddress");
			if (StringUtils.isNotEmpty(fromaddress)) {
				params.put(KeyConstants.SITE_MAIL_FORMADDRESS, fromaddress);
			}
			String sendaddress = request.getParameter("sendaddress");
			if (StringUtils.isNotEmpty(sendaddress)) {
				params.put(KeyConstants.SITE_MAIL_KEY, sendaddress);
			}
			String smtpuser = request.getParameter("smtpuser");
			params.put(KeyConstants.SITE_MAIL_USERNAME, smtpuser);

			String smtppassword = request.getParameter("smtppassword");
			String smtppassword2 = request.getParameter("smtppassword2");
			if (!StringUtils.equals(smtppassword, smtppassword2)) {
				return false;
			}
			params.put(KeyConstants.SITE_MAIL_PASSWORD, smtppassword);
		}
		String template = request.getParameter("template");
		params.put(KeyConstants.SITE_TEMPLATE_KEY, template);
		return true;
	}

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

	public ActionForward createSite(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		Map<String, String> params = new HashMap<String, String>();
		boolean success = false;
		int newSiteId = -1;
		SiteMetaInfo smi = null;
		if (setParamsAndValidateForCreateSiste(request, params)) {
			try {
				smi = VWBContext.getContainer().createSite(context.getCurrentUser().getName(), params);
				success = true;
				newSiteId = smi.getId();
			} catch (NoPermissionException e) {
				request.setAttribute("errorTip", "2");
			}
		}
		if (success) {
			request.getSession().setAttribute("siteManageNotify",
					"新站点" + String.valueOf(newSiteId) + "已经创建.");
			response.sendRedirect("5024");
			return null;
		}

		return doCreateLayout(context);
	}

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		request.setAttribute("templates", VWBContext.getContainer()
				.getTemplateService().getAllSiteTemplate());
		return doCreateLayout(context);
	}

	public ActionForward isDomainValid(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String domain = request.getParameter("domain");

		if (domain == null || domain.equals("")) {
			response.getWriter().print("false");
			return null;
		}

		if (VWBContext.getContainer().getDomainService().isDomainUsed(domain)) {
			response.getWriter().print("false");
		} else {
			response.getWriter().print("true");
		}
		return null;

	}
}
