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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.user.NoPermissionException;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.SiteRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.SiteResponseItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.ServiceWithInputStream;

/**
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class CreateSiteService extends ServiceWithInputStream {

	public Object doAction(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteResponseItem ssi = new SiteResponseItem();
		if (VWBSession.findSession(getRequest()).isAuthenticated()) {
			SiteMetaInfo site = container.getAdminSite();
			VWBSession vsession = VWBSession.findSession(this.getRequest());
			String user = vsession.getCurrentUser().getName();
			if (StringUtils.isNotEmpty(user)) {
				List<java.security.Principal> prins = container
						.getUserService().getUserPrincipal(user,
								container.getSiteConfig().getVO(site.getId()));
				try {
					Subject subject = LoginAction.convertPrincipal(prins);
					VWBSession vwbsession = VWBSession
							.findSession(getRequest());
					vwbsession.setSubject(VWBSession.AUTHENTICATED, subject);
				} catch (VWBException e) {
					e.printStackTrace();
				}
			}
			if (!VWBContext.checkPermission(getRequest(), AllPermission.ALL)) {
				throw new ServiceException(DCTRsiErrorCode.FORBIDDEN_ERROR,
						"has no access");
			}
			SiteRequestItem item = (SiteRequestItem) message;
			Map<String, String> siteparams = new HashMap<String, String>();
			if (setParamsAndValidateForCreateSiste(item, siteparams)) {
				try {
					SiteMetaInfo smi = container.createSite(
							item.getSiteAdmin(), siteparams);
					ssi.setVoId(smi.getUmtVo());
					ssi.setSiteName(smi.getSiteName());
					ssi.setSiteId(smi.getId());
				} catch (NoPermissionException e) {
					throw new ServiceException(DCTRsiErrorCode.FORBIDDEN_ERROR,
							"has no access");
				}
			} else {
				throw new ServiceException(
						DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
						"sitename and domainname is required");
			}
		} else {
			throw new ServiceException(DCTRsiErrorCode.LOGIN_ERROR,
					"failed!may be your session has been invalid!");
		}
		return ssi;
	}

	private boolean setParamsAndValidateForCreateSiste(SiteRequestItem item,
			Map<String, String> params) {
		String sitename = item.getSiteName();
		String[] domains = item.getDomainNames();
		String policy = item.getPolicy() != null ? item.getPolicy()
				: "teamwork";
		if (StringUtils.isEmpty(sitename)) {
			return false;
		}
		if (domains != null && domains.length > 0
				&& StringUtils.isNotEmpty(domains[0])) {
			params.put(KeyConstants.SITE_DOMAIN_KEY, domains[0]);
			for (int i = 1; i < domains.length; i++) {
				params.put(KeyConstants.SITE_DOMAIN_KEY + "." + i, domains[i]);
			}
		} else {
			return false;
		}

		params.put(KeyConstants.SITE_NAME_KEY, sitename);

		params.put(KeyConstants.SITE_ACCESS_OPTION_KEY, policy);

		String mail = item.getEmail();
		if (StringUtils.isNotEmpty(mail)) {
			params.put(KeyConstants.SITE_MAIL_KEY, mail);
		}
		String smtp = item.getSmtp();
		if (StringUtils.isNotEmpty(smtp)) {
			params.put(KeyConstants.SITE_SMTP_HOST_KEY, smtp);
		}
		boolean certificate = item.isSmtpCertificate();
		if (certificate) {
			params.put(KeyConstants.SITE_MAIL_AUTH_KEY, "true");
		} else {
			params.put(KeyConstants.SITE_MAIL_AUTH_KEY, "false");
		}
		String smtpuser = item.getSmtpUser();
		if (StringUtils.isNotEmpty(smtpuser)) {
			params.put(KeyConstants.SITE_MAIL_USERNAME, smtpuser);
		}
		String smtppassword = item.getSmtpPassword();
		if (StringUtils.isNotEmpty(smtppassword)) {
			params.put(KeyConstants.SITE_MAIL_PASSWORD, smtppassword);
		}

		String template = item.getTemplate();
		if (StringUtils.isNotEmpty(template)) {
			params.put(KeyConstants.SITE_TEMPLATE_KEY, template);
		} else {
			params.put(KeyConstants.SITE_TEMPLATE_KEY, "default");
		}
		
		String skin =item.getSkin();
		if (StringUtils.isNotEmpty(skin)) {
			params.put(KeyConstants.SITE_SKIN_KEY, skin);
		} 

		if (item.getPublisheState() != null) {
			params.put(KeyConstants.SITE_PUBLISHED, item.getPublisheState());
		}
		return true;
	}

}
