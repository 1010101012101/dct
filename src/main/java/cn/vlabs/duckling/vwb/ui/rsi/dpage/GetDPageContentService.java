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
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.dpage.DPageInfo;
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
public class GetDPageContentService extends ServiceWithInputStream {

	public Object doAction(RestSession session, Object message)
			throws ServiceException {
		DPageRequestItem item = (DPageRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(item.getSiteId());
		if (site == null) {
			throw new ServiceException(DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
					"The site(" + item.getSiteId() + ") is not existent");
		}
		DPage page = container.getDpageService().getLatestDpageByResourceId(
				site.getId(), item.getResourceId());
		if (page == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR, "The page("
					+ item.getResourceId() + ") is not existent");
		}

		if (!VWBSession.findSession(getRequest()).isAuthenticated()) {
			throw new ServiceException(DCTRsiErrorCode.LOGIN_ERROR,
					"failed!may be your session has been invalid!");
		}
		
		VWBSession.findSession(getRequest()).getCurrentUser().getName();
		
		VWBContext vwbcontext = VWBContext.createContext(item.getSiteId(),
				getRequest(), DPageCommand.VIEW, page);
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
		DPageInfo result = new DPageInfo();
		result.setResourceId(item.getResourceId());
		result.setContent(container.getRenderingService().getHTML(vwbcontext,
				page.getContent()));
		result.setTitle(page.getTitle());
		result.setAuthor(page.getAuthor());
		result.setLastModified(page.getTime());
		return result;
	}

}
