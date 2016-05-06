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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.SiteResponseItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.ServiceWithInputStream;

/**
 * @date 2010-9-6
 * @author Fred Zhang (fred@cnic.cn)
 */
public class DestroySiteService extends ServiceWithInputStream {
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
			Integer siteId = (Integer) message;
			container.destroySite(siteId);
		} else {
			throw new ServiceException(DCTRsiErrorCode.LOGIN_ERROR,
					"failed!may be your session has been invalid!");
		}
		return ssi;
	}
}
