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

package cn.vlabs.duckling.vwb.service.mail.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.mail.Mail;
import cn.vlabs.duckling.vwb.service.mail.MailService;

/**
 * @date Mar 15, 2010
 * @author xiejj@cnic.cn
 */
public class MailServiceImpl implements MailService {

	private static Logger log = Logger.getLogger(MailService.class);
	private static final String PROP_EMAIL_FROMADDRESS = "email.fromAddress";

	private static final String PROP_EMAIL_PASSWORD = "email.password";

	private static final String PROP_EMAIL_USERID = "email.username";

	private ISiteConfig siteConfig;

	private void cheat(MimeMessage mimeMessage, String serverDomain)
			throws MessagingException {
		mimeMessage.saveChanges();
		mimeMessage.setHeader("User-Agent",
				"Thunderbird 2.0.0.16 (Windows/20080708)");
		String messageid = mimeMessage.getHeader("Message-ID", null);
		messageid = messageid.replaceAll("\\.JavaMail.*", "@" + serverDomain
				+ ">");
		mimeMessage.setHeader("Message-ID", messageid);
	}

	private String getCharset(int siteId) {
		return siteConfig.getProperty(siteId, KeyConstants.ENCODING, "UTF-8");
	}

	private InternetAddress getFromAddress(int siteId) {
		String mailFrom = siteConfig
				.getProperty(siteId, PROP_EMAIL_FROMADDRESS);
		try {
			return new InternetAddress(mailFrom);
		} catch (AddressException e) {
			log.error(e);
			return null;
		}
	}

	private Authenticator readAuthenticator(int siteId) {
		String userId = siteConfig.getProperty(siteId, PROP_EMAIL_USERID, "");
		String password = siteConfig.getProperty(siteId, PROP_EMAIL_PASSWORD,
				"");
		return new EmailAuthenticator(userId, password);

	}

	private Properties readMailProperties(int siteId) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host",
				siteConfig.getProperty(siteId, "email.mail.smtp.host"));
		properties.put("mail.smtp.auth",
				siteConfig.getProperty(siteId, "email.mail.smtp.auth"));
		properties.put("mail.pop3.host",
				siteConfig.getProperty(siteId, "email.mail.pop3.host"));
		return properties;
	}

	public void configChanged() {
		log.info("Email properties changed. reloading...");
	}

	public void sendMail(int siteId, Mail mail) {
		log.debug("sendEmail() to: " + mail.getRecipient());
		try {
			InternetAddress fromAddress = getFromAddress(siteId);
			Session session = Session.getInstance(readMailProperties(siteId),
					readAuthenticator(siteId));
			session.setDebug(false);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(getFromAddress(siteId));
			msg.setRecipient(Message.RecipientType.TO,
					new InternetAddress(mail.getRecipient()));
			msg.setSubject(mail.getSubject());
			msg.setSentDate(new Date());

			Multipart mp = new MimeMultipart();

			MimeBodyPart txtmbp = new MimeBodyPart();
			String contentType = mail.getContentType() + ";charset="
					+ getCharset(siteId);
			txtmbp.setContent(mail.getMessage(), contentType);
			mp.addBodyPart(txtmbp);

			List<String> attachments = mail.getAttachments();
			for (Iterator<String> it = attachments.iterator(); it.hasNext();) {
				MimeBodyPart mbp = new MimeBodyPart();
				String filename = it.next();
				FileDataSource fds = new FileDataSource(filename);
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(MimeUtility.encodeText(fds.getName()));
				mp.addBodyPart(mbp);
			}

			msg.setContent(mp);

			if ((fromAddress != null) && (fromAddress.getAddress() != null)
					&& (fromAddress.getAddress().indexOf("@") != -1))
				cheat(msg,
						fromAddress.getAddress().substring(
								fromAddress.getAddress().indexOf("@")));

			Transport.send(msg);
			log.info("Successfully send the mail to " + mail.getRecipient());

		} catch (Throwable e) {
			e.printStackTrace();
			log.debug(
					"Exception occured while trying to send notification to: "
							+ mail.getRecipient(), e);
		}
	}

	public void setSiteConfig(ISiteConfig siteConfig) {
		this.siteConfig = siteConfig;
	}
}
