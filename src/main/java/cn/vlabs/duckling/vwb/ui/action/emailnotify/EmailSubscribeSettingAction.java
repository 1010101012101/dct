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
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriberService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.form.SubscribeForm;

/**
 * 登录用户设置自己的订阅页面
 * 
 * @author y Sep 11, 2009
 */
public class EmailSubscribeSettingAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {
		if (name == null || name.equals("")) {
			name = "show";
		}
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.EDIT_INFO, res);
		if (context.hasAccess(response)) {
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
		// String resouceid =
		// String.valueOf(context.getResource().getResourceId());
		loadSubScribeList(context, p_request, context.getPage().getTitle());
		return this.layout(context, "/jsp/emailnotify/emailnotify_create.jsp");
	}
	private boolean isValidPageName(String pagename){
		if ("*".equals(pagename))
			return true;
		return StringUtils.isNumeric(pagename);
	}
	private boolean isInputValid(SubscribeForm form, HttpServletRequest request){
		boolean success = true;
		ActionMessages errors= new ActionMessages();
		if (StringUtils.isBlank(form.getPageName())|| !isValidPageName(form.getPageName())){
			errors.add("pageName", new ActionMessage("emailnotifier.pagenameerror"));
			success=false;
		}
		if (StringUtils.isBlank(form.getEmails())){
			errors.add("emails", new ActionMessage("emailnotifier.emailerror"));
			success=false;
		}
		if (StringUtils.isBlank(form.getHour()) || !StringUtils.isNumeric(form.getHour())){
			errors.add("rec_time", new ActionMessage("emailnotifier.timeerror"));
			success=false;
		}else{
			int iHour= Integer.parseInt(form.getHour());
			if (iHour<0 || iHour>23){
				errors.add("rec_time", new ActionMessage("emailnotifier.timeerror"));
				success=false;
			}
		}
			
		if (!success){
			saveErrors(request, errors);
		}
		return success;
	}
	private boolean verifyEmail(String emailaddress) {
		Pattern pattern = Pattern
				.compile("\\w+(\\.[\\w-]+)*@(\\w+\\.)+[a-z]{2,3}");
		Matcher matcher = pattern.matcher(emailaddress);

		if (!matcher.matches())
			log.error("Error parse emailaddress:" + emailaddress);

		return matcher.matches();
	}
	private ArrayList<String> parseMails(String mails){
		String emails = mails.trim().replaceAll("；", ";");
		StringTokenizer st = new StringTokenizer(emails.trim(), ";");
		ArrayList<String> mailArray= new ArrayList<String>();
		while (st.hasMoreTokens()){
			mailArray.add(st.nextToken());
		}
		return mailArray;
	}
	private void saveAnError(HttpServletRequest p_request,String fieldId, String messageKey){
		ActionMessages messages= new ActionMessages();
		messages.add(fieldId, new ActionMessage(messageKey));
		saveErrors(p_request, messages);
	}
	public ActionForward save(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		VWBContext context = VWBContext.getContext(p_request);
		EmailSubscriberService esm = VWBContext.getContainer().getEmailSubscriberService();
		
		
		SubscribeForm subscribeForm = (SubscribeForm)p_form;
		if (isInputValid(subscribeForm, p_request)){
			boolean mailvalid=true;
			ArrayList<String> mailArray= parseMails(subscribeForm.getEmails());
			if (mailArray.size()>0){
				ArrayList<EmailSubscriber> subscribes = new ArrayList<EmailSubscriber>();
				String creator = context.getCurrentUser().getName();
				
				for (String mail:mailArray){
					if (!verifyEmail(mail)){
						saveAnError(p_request, "emails", "emailnotifier.emailerror");
						mailvalid=false;
						break;
					}
					EmailSubscriber sub = new EmailSubscriber();
					sub.setNotify_creator(creator);
					sub.setPageTitle(subscribeForm.getPage());
					sub.setRec_time(Integer.parseInt(subscribeForm.getHour()));
					sub.setReceiver(mail);
					sub.setresourceId(subscribeForm.getPageName());
					subscribes.add(sub);
				}
				if (mailvalid){
					esm.createSubscribes(context.getSite().getId(),subscribes);
				}
			}else{
				saveAnError(p_request, "emails", "emailnotifier.emailerror");
				return layout(context, "/jsp/emailnotify/emailnotify_create.jsp");
			}
		}
	
		loadSubScribeList(context, p_request, subscribeForm.getPageName());
		return layout(context, "/jsp/emailnotify/emailnotify_create.jsp");
	}

	public ActionForward delete(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		VWBContext context = VWBContext.getContext(p_request);
		// 获取请求参数
		final String delESubId = p_request.getParameter("deleteESubId");
		try {
			EmailSubscriberService ess = VWBContext.getContainer()
					.getEmailSubscriberService();
			ess.removeSubscribe(context.getSite().getId(),Integer.parseInt(delESubId));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		loadSubScribeList(context, p_request, context.getPage().getTitle());
		return show(p_mapping, p_form, p_request, p_response);
	}

	private void loadSubScribeList(VWBContext context,
			HttpServletRequest p_request, String pageName) {
		EmailSubscriberService ess = VWBContext.getContainer()
				.getEmailSubscriberService();
	
		int resourceId = context.getResource().getResourceId();
		Collection<EmailSubscriber> eSubs = ess.findPageSubscribes(context.getSite().getId(),resourceId);
		p_request.setAttribute("eMailSubscribers", eSubs);

	}

}
