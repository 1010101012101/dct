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

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.site.PublishState;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.sitestatus.SiteStatusRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * 
 * @date 2010-10-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SitePublishService extends SupperAdminService {
	public Object publishedSite(RestSession session, Object arg)
			throws ServiceException {
		SiteStatusRequestItem request = (SiteStatusRequestItem) arg;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(request.getSiteId());
		if (site == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR,
					"Site not found");
		}
		if (!VWBContext.checkPermission(getRequest(), AllPermission.ALL)) {
			throw new ServiceException(DCTRsiErrorCode.FORBIDDEN_ERROR,
					"has no access");
		}
		if (!site.isPublished()) {
			container.getSiteManagerService().changePublishState(
					request.getSiteId(), PublishState.PUBLISHED);
			return true;
		}

		return false;
	}

	public Object isPublishedSite(RestSession session, Object arg)
			throws ServiceException {
		SiteStatusRequestItem request = (SiteStatusRequestItem) arg;
		SiteMetaInfo site = VWBContainerImpl.findContainer().getSite(
				request.getSiteId());
		if (site == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR,
					"Site not found");
		}
		if (!VWBContext.checkPermission(getRequest(), AllPermission.ALL)) {
			throw new ServiceException(DCTRsiErrorCode.FORBIDDEN_ERROR,
					"has no access");
		}
		if (site.isPublished()) {
			return true;
		}

		return false;
	}

}
