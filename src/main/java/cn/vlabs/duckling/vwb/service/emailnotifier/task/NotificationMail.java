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

package cn.vlabs.duckling.vwb.service.emailnotifier.task;

import java.util.List;

import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.mail.Mail;
import cn.vlabs.duckling.vwb.service.mail.MailService;
import cn.vlabs.duckling.vwb.service.url.UrlService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class NotificationMail {

	private static final String MARKER_CHANGED_PAGE_LIST = "CHANGED_PAGE_LIST";
	private static final String MARKER_SUBSCRIPTION_PAGE = "SUBSCRIPTION_PAGE";

	private static final String MARKER_SUBSCRIPTION_URL = "SUBSCRIPTION_URL";

	private static final String PROP_SUBSCRIPT_PAGE = "emailnotifier.subscriptionPage";

	private ChangeList changeList = null;
	private RenderingService htmlService;
	private MailService m_mailService;
	private Mail mail;
	private ISiteConfig siteconfig;
	private UrlService urlService;
	private ViewPortService viewport;
	public void setViewPortService(ViewPortService viewport){
		this.viewport = viewport;
	}
	
	public NotificationMail(int siteId,MailService mailservice,UrlService urlService) {
		this.urlService = urlService;
		this.m_mailService = mailservice;
		mail = new Mail();
		mail.setContentType("text/html");
		mail.setSubject(siteconfig.getProperty(siteId,"emailnotifier.subjectLine",
				"Notification from duckling"));
		String subPage = siteconfig.getProperty(siteId,PROP_SUBSCRIPT_PAGE);
		mail.putParam(MARKER_SUBSCRIPTION_PAGE, subPage);
		mail.putParam(MARKER_SUBSCRIPTION_URL,
				urlService.getViewURL(Integer.parseInt(subPage)));
	}
	private void loadMailTemplate(int siteId) {
		String page = siteconfig.getProperty(siteId,"emailnotifier.emailtextPage", "16");
		if (page != null) {
			String pageText = htmlService.getHTML(siteId,Integer.valueOf(page));
			pageText = pageText.replaceAll(
					"<img class=\"outlink\" [^>]*>[^<>]*</img>", "");
			pageText = pageText.replaceAll("<img class=\"outlink\" [^>]*/>",
					" ");
			pageText = pageText
					.replaceAll("<img class=\"outlink\" [^>]*>", " ");
			mail.setTemplate(pageText);
		} else {
			mail.setTemplate("");
		}
	}

	public void endChangeList() {
		mail.putParam(MARKER_CHANGED_PAGE_LIST, changeList.getHTML());
	}

	public void send(int siteId) {
		loadMailTemplate(siteId);
		m_mailService.sendMail(siteId,mail);
	}

	public void setChangeList(int siteid,int pageId, List<LightDPage> versions) {
		changeList.currentChangeList(pageId, versions);
		changeList.buildChangeList(siteid);
	}

	public void setRenderingService(RenderingService htmlService){
		this.htmlService = htmlService;
	}

	public void setRecipient(String recipient) {
		mail.setRecipient(recipient);
	}

	public void setSiteConfig(ISiteConfig siteConfig){
		this.siteconfig = siteConfig;
	}

	public void startChangeList() {
		mail.putParam(MARKER_CHANGED_PAGE_LIST, "");
		changeList = new ChangeList();
		changeList.setViewportService(viewport);
		changeList.setUrlService(urlService);
	}
}
