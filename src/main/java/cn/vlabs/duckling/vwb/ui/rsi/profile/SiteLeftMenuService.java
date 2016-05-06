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

import java.io.IOException;

import org.jdom.JDOMException;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dml.html2dml.HtmlStringToDMLTranslator;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-5-17
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteLeftMenuService extends AdminAccessService {

	public Object getLeftMenu(RestSession arg0, Object arg1)
			throws ServiceException {
		MenuRequestItem request = (MenuRequestItem) arg1;
		VWBContainer container = VWBContainerImpl.findContainer();
		int siteId = request.getSiteId();
		DPage page = container.getDpageService().getLatestDpageByResourceId(
				siteId, request.getResourceId());

		if (page == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR, "The page("
					+ request.getResourceId() + ") is not existent");
		}
		VWBContext context = VWBContext.createContext(siteId, getRequest(),
				DPageCommand.VIEW, page);
		context.setWysiwygEditorMode(VWBContext.EDITOR_MODE);
		return LeftMenuUtil.extractLeftMenuItemFromContent(container
				.getRenderingService().getHTML(context, page.getContent()));
	}

	public Object updateLeftMenu(RestSession arg0, Object arg1)
			throws ServiceException {
		MenuRequestItem request = (MenuRequestItem) arg1;
		VWBContainer container = VWBContainerImpl.findContainer();
		int siteId = request.getSiteId();
		DPage page = container.getDpageService().getLatestDpageByResourceId(
				siteId, request.getResourceId());
		if (page == null) {
			throw new ServiceException(DCTRsiErrorCode.EMPTY_ERROR, "The page("
					+ request.getResourceId() + ") is not existent");
		}
		UserPrincipal p = (UserPrincipal) VWBSession.findSession(
				this.getRequest()).getCurrentUser();
		page.setAuthor(p.getFullName() + "(" + p.getName() + ")");
		VWBContext context = VWBContext.createContext(siteId, getRequest(),
				DPageCommand.VIEW, page);
		context.setUseDData(true);
		String textWithoutMetaData = "";
		try {
			textWithoutMetaData = new HtmlStringToDMLTranslator().translate(
					LeftMenuUtil.generateContent(request.getItems()), context);
		} catch (JDOMException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		page.setContent(textWithoutMetaData);
		try {
			container.getDpageService().updateDpage(page);
		} catch (Throwable e) {
			throw new ServiceException(0, "update the dpage(resourceid:"
					+ page.getResourceId() + ") error:");
		}
		return null;
	}

}
