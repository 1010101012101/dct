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
package cn.vlabs.duckling.vwb.ui.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * 登录用户设置有关自身的所有邮件订阅
 * 
 * @author y Sep 11, 2009
 */
public class MyPageAction extends BaseDispatchAction {
	public ActionForward execute(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.EDIT_PROFILE, res);
		if (context.hasAccess(p_response)) {

			DPageService dps = VWBContext.getContainer().getDpageService();

			Map<String, Object> searchedConditions = new HashMap<String, Object>();
			initializeSearchedConditions(context, searchedConditions);

			// search created page, and put the result in request scope.
			searchedConditions.put("operation", "CREATE");

			List<DPage> pagesCreated = dps.searchPages(context.getSite().getId(),searchedConditions);
			// the current url in jsp is wrote in a rigid way, the generation of
			// URL, which will be use in jsp, in the following codes is more
			// flexible and controllable.
			// for(WikiPage page:pagesCreated){
			// page.setAttribute("url", generateURL(page.getName(),p_request));
			// }
			p_request.setAttribute("createdPage", pagesCreated);

			// search edited page, and put the result in request scope.
			searchedConditions.put("operation", "EDIT");
			List<DPage> pagesEdited = dps.searchPages(context.getSite().getId(),searchedConditions);
			p_request.setAttribute("editedPage", pagesEdited);

			// search created or edited page, and put the result in request
			// scope.
			/*
			 * searchedConditions.put("operation", "CREATEANDEDIT"); List<WikiPage>
			 * pagesCreatedAndEdited =
			 * engine.getPageManager().searchPagesByVesion(searchedConditions);
			 * p_request.setAttribute("createdOrEditedPage",
			 * pagesCreatedAndEdited);
			 */

			return doLayout(context);
		}
		return null;
	}

	// initialize searchContiditons
	private void initializeSearchedConditions(VWBContext context,
			Map<String, Object> searchedConditions) {
		searchedConditions.put("prefix", null);
		searchedConditions.put("suffix", null);
		searchedConditions.put("user", null);
		searchedConditions.put("user", context.getCurrentUser().getName());
		searchedConditions.put("count", new Integer(10));
		Date beginDate, endDate;
		beginDate = new Date(0);
		endDate = new Date();
		searchedConditions.put("beginDate", beginDate);
		searchedConditions.put("endDate", endDate);

	}

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "/jsp/personalization/my_page.jsp");
	}
}
