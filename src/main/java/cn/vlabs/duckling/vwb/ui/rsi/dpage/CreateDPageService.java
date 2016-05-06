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

import java.util.Calendar;
import java.util.Date;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dml.html2dml.HtmlStringToDMLTranslator;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
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
public class CreateDPageService extends ServiceWithInputStream {

	public Object doAction(RestSession session, Object message)
			throws ServiceException {
		DPageRequestItem info = (DPageRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		int siteId = info.getSiteId();
		Integer pageId = null;
		if (VWBSession.findSession(getRequest()).isAuthenticated()) {
			UserPrincipal p = (UserPrincipal) VWBSession.findSession(
					this.getRequest()).getCurrentUser();
			ViewPort vp = new ViewPort();
			vp.setTitle(info.getTitle());
			vp.setCreator(p.getFullName() + "(" + p.getName() + ")");
			vp.setType(Resource.TYPE_DPAGE);
			Date time = Calendar.getInstance().getTime();
			vp.setCreateTime(time);
			ViewPortService vs = container.getViewPortService();
			int resourceid = vs.createViewPort(siteId, vp);
			DPage page = new DPage();
			page.setTitle(info.getTitle());
			page.setResourceId(resourceid);
			page.setAuthor(p.getFullName() + "(" + p.getName() + ")");
			page.setTime(time);
			page.setSiteId(siteId);
			try {
				VWBContext context = VWBContext.createContext(siteId,
						getRequest(), DPageCommand.VIEW, page);
				page.setContent(new HtmlStringToDMLTranslator().translate(
						info.getContent(), context));
				page = VWBContext.getContainer().getDpageService()
						.createDpage(page);
				pageId = Integer.valueOf(page.getResourceId());
			} catch (Throwable e) {
				throw new ServiceException(0, "create the dpage(resourceid:"
						+ page.getResourceId() + ") error:");
			}
		} else {
			throw new ServiceException(DCTRsiErrorCode.LOGIN_ERROR,
					"failed!may be your session has been invalid!");
		}
		return pageId;
	}

}
