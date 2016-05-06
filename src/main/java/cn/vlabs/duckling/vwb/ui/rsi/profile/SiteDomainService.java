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

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.config.DomainNameService;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.DomainListRequest;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.DomainRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-5-17
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteDomainService extends AdminAccessService {
	public Object getSiteDomains(RestSession session, Object message) throws ServiceException{
		DomainRequestItem request = (DomainRequestItem) message;
		int siteId = request.getSiteId();
		VWBContainer container = VWBContainerImpl.findContainer();
		if (container.getSite(siteId)!=null){
			return container.getDomainService().getUsedDomain(siteId);
		}else{
			throw new ServiceException(DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
					"Site "+siteId+" not found.");
		}
	}
	
	public Object isDomainUsed(RestSession session, Object message)
			throws ServiceException {
		DomainRequestItem request = (DomainRequestItem) message;
		if (StringUtils.isEmpty(request.getDomain())) {
			return Boolean.FALSE;
		}
		VWBContainer container = VWBContainerImpl.findContainer();
		if (container.getDomainService().isDomainUsed(request.getDomain())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public Object updateAllDomains(RestSession session, Object message) throws ServiceException{
		DomainListRequest request = (DomainListRequest) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		DomainNameService domainNameService=container.getDomainService();
		if (container.getSite(request.getSiteId())==null){
			throw new ServiceException(DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
					"Site "+request.getSiteId()+" not found.");
		}
		
		String[] domains = request.getDomains();
		if (!domainNameService.updateSiteDomains(request.getSiteId(), domains)){
			throw new ServiceException(DCTRsiErrorCode.SITE_DOMAIN_CONFLICT,
					"Duplicated domain");
		}
		return null;
	}
	
	public Object updateSiteDomain(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		DomainRequestItem request = (DomainRequestItem) message;
		int siteId = request.getSiteId();
		String domain = request.getDomain();
		DomainNameService domainNameService = container.getDomainService();
		if (!domainNameService.updateSiteMainDomain(siteId, domain)){
			throw new ServiceException(DCTRsiErrorCode.SITE_DOMAIN_CONFLICT,
					"Duplicated domain");
		}
		return null;
	}
}
