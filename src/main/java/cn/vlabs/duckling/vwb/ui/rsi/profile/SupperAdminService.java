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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import cn.vlabs.duckling.vwb.ui.rsi.api.AbstractSiteAdminRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.ServiceWithInputStream;

/**
 * @date 2010-10-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public abstract class SupperAdminService extends ServiceWithInputStream {
	private Class<?>[] types = { RestSession.class, Object.class };

	public Object doAction(RestSession arg0, Object arg1)
			throws ServiceException {
		AbstractSiteAdminRequestItem item = (AbstractSiteAdminRequestItem) arg1;
		VWBContainer container = VWBContainerImpl.findContainer();
		if (VWBSession.findSession(getRequest()).isAuthenticated()) {
			SiteMetaInfo site = container.getAdminSite();
			VWBSession vsession = VWBSession.findSession(this.getRequest());
			String user = vsession.getCurrentUser().getName();
			if (StringUtils.isNotEmpty(user)) {
				List<java.security.Principal> prins = container
						.getUserService().getUserPrincipal(user, container.getSiteConfig().getVO(site.getId()));
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
		}
		if (StringUtils.isNotEmpty(item.getMethod())) {
			String method = item.getMethod();
			try {
				Method methodO = this.getClass().getMethod(method, types);
				return methodO.invoke(this, new Object[] { arg0, arg1 });
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException) {
					throw (ServiceException) ((InvocationTargetException) e)
							.getTargetException();
				}
				throw new ServiceException(
						DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
						"no such a service method :" + method);
			}
		}
		return this.doService(arg0, arg1);
	}

	public Object doService(RestSession arg0, Object arg1)
			throws ServiceException {
		return null;
	}

}
