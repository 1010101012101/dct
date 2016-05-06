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

package cn.vlabs.duckling.vwb.ui.action.page;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.util.VOTreeUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.mail.Mail;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.share.AccessRecord;
import cn.vlabs.duckling.vwb.service.share.SharePageAccessService;
import cn.vlabs.duckling.vwb.service.share.SharePageMailService;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.form.SharePageForm;

/**
 * Introduction Here.
 * 
 * @date Mar 4, 2010
 * @author sunp(sunp@cnic.cn)
 */
public class SharePageAction extends BaseDispatchAction {
	private static Logger log = Logger.getLogger(SharePageAction.class);

	/**
	 * Method execute 处理共享请求
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */

	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null || name.equals("")) {
			name = "init";
		}

		boolean noAccessChk = false;
		String mode = null;
		String hash = request.getParameter("hash");
		String ID = request.getParameter("ID");
		if (hash != null && !hash.equals("") && ID != null && !ID.equals("")) {
			noAccessChk = true;
			mode = request.getParameter("mode");
		}

		if (noAccessChk) {
			VWBContext.createContext(request, DPageCommand.SHARECONTENT,
					getSavedViewPort(request));
			if (mode != null && mode.equals("mainpart")) {
				name = "showContent";
			} else {
				name = "showAllContent";
			}
			return super.dispatchMethod(mapping, form, request, response, name);
		} else {
			VWBContext context = VWBContext.createContext(request,
					DPageCommand.SHARE, getSavedViewPort(request));
			if (context.hasAccess(response)) {
				return super.dispatchMethod(mapping, form, request, response,
						name);
			} else {
				return null;
			}
		}
	}

	public ActionForward showContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		return mapping.findForward("content");
	}

	public ActionForward showAllContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		return doContentLayout(context);
	}

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		return doLayout(context);
	}

	public ActionForward showVO(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		return mapping.findForward("vo");
	}

	public ActionForward senMail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Resource res = this.getSavedViewPort(request);
		int resouceid = res.getResourceId();
		VWBContext context = VWBContext.getContext(request);
		String page = request.getParameter("page");
		request.setAttribute("page", page);
		SharePageForm sr = (SharePageForm) form;
		String users = sr.getEmailaddress();
		String content = sr.getEmailContent();
		String emailtitlecon = request.getParameter("emailTitle");
		if (users == null || content == null)
			return null;
		// 将邮件地址分为若干用户
		ArrayList<String> entities = new ArrayList<String>();
		int startIndex = users.indexOf(",");
		int index = 0;
		while (startIndex < users.length() && startIndex != -1) {
			String temp = users.substring(index, startIndex);
			if (!temp.trim().equals(""))
				entities.add(temp);
			index = startIndex + 1;
			startIndex = users.indexOf(",", startIndex + 1);
		}
		String temp = users.substring(index);
		if (!temp.trim().equals(""))
			entities.add(temp);

		entities = new ArrayList<String>();
		ArrayList<String> tmpUsers = entities;
		for (String tmp : tmpUsers) {
			boolean find = false;
			for (String tmp1 : entities) {
				if (tmp.equals(tmp1)) {
					find = true;
					break;
				}
			}
			if (!find)
				entities.add(tmp);
		}
		// 处理邮件发送
		try {
			String subjectLine = "";
			String currentUser = context.getCurrentUser().getName();
			subjectLine = emailtitlecon;

			SharePageMailService sm =VWBContext.getContainer().getSharePageMailService();

			SharePageAccessService spaf = VWBContext.getContainer().getSharePageAccessService();
			AccessRecord ar = spaf.WriteShareAcl(String.valueOf(resouceid));

			Mail mail = sm.parepareMail(context.getSite().getId(), subjectLine);

			request.setAttribute("EmailreturnUrl",
					context.getURL(VWBContext.VIEW, resouceid));

			// 发送给邮件列表
			if (entities != null) {
				String urlAdd = spaf.getUrl(context.getSite().getId(), resouceid,
						ar.getID(), ar.gethash());
				for (String entity : entities) {
					sm.sendNotifications(context.getSite().getId(), mail,
							currentUser, entity, subjectLine, urlAdd, page,
							content);
				}
			}

			// 发给组织内部人员
			if ((sr.getObjectIds() != null)
					&& (!sr.getObjectIds().trim().equals(""))) {
				String selected = sr.getObjectIds();// ojbectIds == emailids
				// 处理选择的用户
				VOTreeUtil util = new VOTreeUtil(context);
				ArrayList<String> selectEmails = util
						.getSelectedEmail(selected);

				String urlAdd = context.getURL(VWBContext.VIEW, resouceid);
				for (String entity : selectEmails) {
					sm.sendNotifications(context.getSite().getId(), mail,
							currentUser, entity, subjectLine, urlAdd, page,
							content);
				}
			}
		} catch (Exception e) {
			log.error("发送邮件失败！");
			request.setAttribute("error", "error.share.email");
			request.setAttribute("destination", "javascript:history.go(-1)");
			return mapping.findForward("failure");
		}

		request.setAttribute("info", "notify");
		return doSuccessLayout(context);
	}

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "/jsp/sharePage.jsp");//
	}

	private ActionForward doContentLayout(VWBContext context) {
		return layoutShare(context, "/jsp/shareContent.jsp");
	}

	private ActionForward doSuccessLayout(VWBContext context) {
		return layout(context, "jsp/success.jsp");
	}

}
