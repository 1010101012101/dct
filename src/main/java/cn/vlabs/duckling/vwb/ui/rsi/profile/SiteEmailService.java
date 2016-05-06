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

package cn.vlabs.duckling.vwb.ui.rsi.profile;

import java.util.Properties;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.mail.MailRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.mail.MailSettingItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-5-18
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteEmailService extends AdminAccessService {
	public Object getEmailSetting(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		MailRequestItem item = (MailRequestItem) message;
		ISiteConfig siteConfig = container.getSiteConfig();
		MailSettingItem mailsetting = new MailSettingItem();
		int siteId =item.getSiteId();
		mailsetting.setPassWord(siteConfig.getProperty(siteId,KeyConstants.SITE_MAIL_PASSWORD));
		mailsetting.setReplyAddress(siteConfig.getProperty(siteId,KeyConstants.SITE_MAIL_FORMADDRESS));
		mailsetting.setSendAddress(siteConfig.getProperty(siteId,KeyConstants.SITE_MAIL_KEY));
		mailsetting.setSmtpAuth(Boolean.valueOf(siteConfig.getProperty(siteId,KeyConstants.SITE_MAIL_AUTH_KEY)));
		mailsetting.setSmtpServer(siteConfig.getProperty(siteId,KeyConstants.SITE_SMTP_HOST_KEY));
		mailsetting.setUserName(siteConfig.getProperty(siteId,KeyConstants.SITE_MAIL_USERNAME));
		return mailsetting;
	}

	public Object updateMailSetting(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		MailRequestItem item = (MailRequestItem) message;
		MailSettingItem setting = item.getMailSetting();
		Properties properteis = new Properties();
		properteis.setProperty(KeyConstants.SITE_MAIL_PASSWORD, setting.getPassWord());
		properteis.setProperty(KeyConstants.SITE_MAIL_FORMADDRESS, setting.getReplyAddress());
		properteis.setProperty(KeyConstants.SITE_MAIL_KEY, setting.getSendAddress());
		properteis.setProperty(KeyConstants.SITE_MAIL_AUTH_KEY, Boolean.toString(setting.isSmtpAuth()));
		properteis.setProperty(KeyConstants.SITE_SMTP_HOST_KEY, setting.getSmtpServer());
		properteis.setProperty(KeyConstants.SITE_MAIL_USERNAME, setting.getUserName());
		container.getSiteConfig().setProperty(item.getSiteId(),properteis);
		return null;
	}
}
