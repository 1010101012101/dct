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
import java.util.Collection;
import java.util.ResourceBundle;

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
import cn.vlabs.duckling.vwb.ui.base.BaseAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * 管理员配置所有的邮件订阅
 * 
 * @author y Sep 11, 2009
 */
public class EmailSubscribeAdminAction extends BaseAction {
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
	public ActionForward execute(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			ResourceBundle mr = context.getBundle("templates/default");
			final DynaActionForm form = (DynaActionForm) p_form;
			String pageName = (String) form.get("pageName");
			if (pageName == null)
				pageName = "";
			else {
				p_request.setAttribute("pageName", pageName);
			}
			String creator = (String) form.get("creator");
			if (creator == null)
				creator = "";
			else {
				p_request.setAttribute("creator", creator);
			}
			String emails = (String) form.get("emails");
			if (emails == null)
				emails = "";
			else {
				p_request.setAttribute("emails", emails);
			}
			String shour = (String) form.get("hour");
			int hour=-1;
			if (StringUtils.isBlank(shour))
				shour = "";
			else {
				if (StringUtils.isNumeric(shour)){
					hour = Integer.parseInt(shour);
					p_request.setAttribute("hour", shour);
				}
			}

			EmailSubscriberService ess = VWBContext.getContainer().getEmailSubscriberService();
		
			String errorinfo = "";
			String delESubId = (String) form.get("deleteESubId");
			if (!StringUtils.isBlank(delESubId)){
				if (StringUtils.isNumeric(delESubId)){
					ess.removeSubscribe(context.getSite().getId(),Integer.parseInt(delESubId));
				}else{
					errorinfo += " " + mr.getString("emailnotifier.deldberror");
					p_request.setAttribute("errorInfo", errorinfo);
				}
			}

			Collection<EmailSubscriber> eSubs = ess.findSubscribe(context.getSite().getId(),emails, pageName, hour);
	        p_request.setAttribute("eMailSubscribers", eSubs);
	        return doLayout(context);
		}else
			return null;
	}

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "/jsp/emailnotify/emailnotify_all.jsp");
	}

}
