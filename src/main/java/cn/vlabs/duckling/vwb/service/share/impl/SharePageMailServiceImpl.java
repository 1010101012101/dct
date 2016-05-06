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

package cn.vlabs.duckling.vwb.service.share.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.mail.Mail;
import cn.vlabs.duckling.vwb.service.mail.MailService;
import cn.vlabs.duckling.vwb.service.share.SharePageMailService;

/**
 * @date Mar 4, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class SharePageMailServiceImpl implements SharePageMailService {
	private static final String SHARE_PAGE_CONTENT = "SHARE_PAGE_CONTENT";
	private static final String SHARE_PAGE_NAME = "SHARE_PAGE_NAME";
	
	private static final String SHARE_PAGE_SUBSCRIBER = "SHARE_PAGE_SUBSCRIBER";
	private static final String DEFAULT_TEMPLATE = "<p>亲爱的用户：</p> +SHARE_PAGE_SUBSCRIBER+将页面"
			+ SHARE_PAGE_NAME
			+ "共享给您。请您及时访问该页面。<p>"
			+ SHARE_PAGE_CONTENT
			+ "</p> --管理员(wiki@escience.cn)";

	private static final Logger log = Logger
			.getLogger(SharePageMailServiceImpl.class);

	private MailService mailService;
	private RenderingService renderService;

	private ISiteConfig siteConfig;

	public Mail parepareMail(int siteId, String subjectLine) {
		Mail m_mail = new Mail();
		m_mail.setContentType("text/html");
		m_mail.setSubject(subjectLine);
		// 创建邮件内容
		String message = "";
		String m_sharePage = siteConfig.getProperty(siteId,
				"emailtooutside.template");
		if (m_sharePage != null) {
			message = renderService.getHTML(siteId,
					Integer.valueOf(m_sharePage));
		}
		if (StringUtils.isEmpty(message)) {
			message = DEFAULT_TEMPLATE;
		}
		m_mail.setTemplate(message);
		return m_mail;
	}

	public void sendNotifications(int siteId, Mail mail, String subscriber,
			String recipient, String subjectLine, String url, String title,
			String content) {
		log.debug("sendNotifications");
		mail.setRecipient(recipient);
		if ((subscriber == null)) {
			log.debug("No entities, nothing to do.");
			return;
		}
		mail.putParam(SHARE_PAGE_SUBSCRIBER, subscriber);
		String pageName = "<a href='" + url + "'>" + title + "</a>";
		mail.putParam(SHARE_PAGE_NAME, pageName);
		if (content != null)
			mail.putParam(SHARE_PAGE_CONTENT, content);
		else
			mail.putParam(SHARE_PAGE_CONTENT, "");

		mailService.sendMail(siteId, mail);
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public void setRenderService(RenderingService renderService) {
		this.renderService = renderService;
	}

	public void setSiteConfig(ISiteConfig siteConfig) {
		this.siteConfig = siteConfig;
	}
}
