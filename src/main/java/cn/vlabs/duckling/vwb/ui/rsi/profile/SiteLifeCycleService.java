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
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.SiteLefyCycleRequest;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.SiteStatus;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-10-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteLifeCycleService extends SupperAdminService {
	
	public void activateSite(RestSession session, Object message) throws ServiceException {
		SiteLefyCycleRequest request = (SiteLefyCycleRequest)message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo  smi = container.getSiteManagerService().getSiteInfo(request.getSiteId());
		smi.setState(SiteState.WORK);
		container.getSiteManagerService().updateSiteMeta(smi);
	}

	public void deactivateSite(RestSession session, Object message) throws ServiceException {
		SiteLefyCycleRequest request = (SiteLefyCycleRequest)message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo  smi = container.getSiteManagerService().getSiteInfo(request.getSiteId());
		smi.setState(SiteState.HANGUP);
    	container.getSiteManagerService().updateSiteMeta(smi);
	}
    public SiteStatus checkSiteStatus(RestSession session, Object message) throws ServiceException {
    	SiteLefyCycleRequest request = (SiteLefyCycleRequest)message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo info = container.getSiteManagerService().getSiteInfo(request.getSiteId());
		SiteStatus status = new SiteStatus();
		if(info != null)
		{
			status.setExist(true);
			status.setActive(info.isWorking()?true:false);
			if(status.isActive())
			{
				status.setStatusMessage("The site (id="+request.getSiteId()+",name="+info.getSiteName()+") is active!");
			}else
			{
				status.setStatusMessage("The site (id="+request.getSiteId()+",name="+info.getSiteName()+") is deactive!");
			}
			
		}else
		{
			status.setExist(false);
			status.setActive(false);
			status.setStatusMessage("The site (id="+request.getSiteId()+") is not existent!");
		}
    	return status;
	}
}
