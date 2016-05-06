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

package cn.vlabs.duckling.vwb.ui.rsi.dpage;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dml.html2dml.HtmlStringToDMLTranslator;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.PageLock;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.dpage.DPageRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.ServiceWithInputStream;

/**
 * Introduction Here.
 * 
 * @date 2010-3-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class UpdateDPageContentService extends ServiceWithInputStream {

	public Object doAction(RestSession session, Object message)
			throws ServiceException {
		DPageRequestItem info = (DPageRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(info.getSiteId());
		DPage page = container.getDpageService().getLatestDpageByResourceId(
				site.getId(), info.getResourceId());
		if (page == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR,
					"update error:The page(" + info.getResourceId()
							+ ") is not existent");
		}
		if (!VWBSession.findSession(getRequest()).isAuthenticated()) {
			throw new ServiceException(DCTRsiErrorCode.LOGIN_ERROR,
					"failed!may be your session has been invalid!");
		}
		VWBContext vwbcontext = VWBContext.createContext(info.getSiteId(),
				getRequest(), DPageCommand.EDIT, page);
		String user = vwbcontext.getCurrentUser().getName();
		if (StringUtils.isNotEmpty(user)) {
			List<java.security.Principal> prins = container.getUserService()
					.getUserPrincipal(user, vwbcontext.getVO());
			try {
				Subject subject = LoginAction.convertPrincipal(prins);
				VWBSession vwbsession = VWBSession.findSession(getRequest());
				vwbsession.setSubject(VWBSession.AUTHENTICATED, subject);
			} catch (VWBException e) {
				e.printStackTrace();
			}
		}
		if (!vwbcontext.hasAccess(getResponse())) {
			throw new ServiceException(DCTRsiErrorCode.FORBIDDEN_ERROR,
					"has no access");
		}
		PageLock pagelock = (PageLock) (container.getDpageService())
				.getCurrentLock(site.getId(), info.getResourceId());
		if (pagelock != null && !info.isForce()) {
			throw new ServiceException(DCTRsiErrorCode.CONFLICT_ERROR,
					"save conflict");
		}
		try {
			UserPrincipal p = (UserPrincipal) vwbcontext.getCurrentUser();
			page.setAuthor(p.getFullName() + "(" + p.getName() + ")");
			page.setContent(new HtmlStringToDMLTranslator().translate(
					info.getContent(), vwbcontext));
			page.setTitle(info.getTitle());
			container.getDpageService().updateDpage(page);
		} catch (Throwable e) {
			throw new ServiceException(0, "update the dpage(resourceid:"
					+ page.getResourceId() + ") error:");
		}
		return null;
	}

}
