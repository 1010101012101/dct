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
package cn.vlabs.duckling.vwb.ui.action.emailnotify;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriberService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * 登录用户设置有关自身的所有邮件订阅
 * 
 * @author y Sep 11, 2009
 */
public class EmailSubscribeManageAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			name = "show";
		}
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.EDIT_PROFILE, res);
		if (context.getVWBSession().isAuthenticated()
				&& context.hasAccess(response)) {
			return super.dispatchMethod(mapping, form, request, response, name);
		} else
			return null;
	}

	/**
	 * show create jsp
	 * 
	 * @param p_mapping
	 * @param p_form
	 * @param p_request
	 * @param p_response
	 * @return
	 * @throws IOException
	 */
	public ActionForward show(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {

		VWBContext context = VWBContext.getContext(p_request);
		loadMySubscribes(context, p_request);
		return doLayout(context);

	}

	public ActionForward delete(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		VWBContext context = VWBContext.getContext(p_request);
		if (context.hasAccess(p_response)) {

			// 获取请求参数
			final DynaActionForm form = (DynaActionForm) p_form;

			final String delESubId = (String) form.get("deleteESubId");
			EmailSubscriberService ess = VWBContext.getContainer()
					.getEmailSubscriberService();
			ess.removeSubscribe(context.getSite().getId(),Integer.parseInt(delESubId));
			log.info("usser:" + context.getCurrentUser().getName()
					+ "  delete subscribe record:" + delESubId + " success!");

			loadMySubscribes(context, p_request);

		}

		return doLayout(context);

	}

	private void loadMySubscribes(VWBContext context,
			HttpServletRequest p_request) {
		EmailSubscriberService ess = VWBContext.getContainer()
				.getEmailSubscriberService();
		List<EmailSubscriber> eSubs = ess.findUserSubscribes(context.getSite().getId(),context
				.getCurrentUser().getName());
		p_request.setAttribute("eMailSubscribers", eSubs);

	}

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "/jsp/emailnotify/emailnotify_list.jsp");
	}

}
