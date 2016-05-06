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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.InvalidDPageDtoException;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.tags.InsertDiffTag;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;

/**
 * MyEclipse Struts Creation date: 08-06-2009 XDoclet definition:
 * 
 * @struts.action path="/difference" name="diffForm"
 *                input="/jsp/InfoContent.jsp" scope="request" validate="true"
 * @date Mar 3, 2010
 * @author Yong Ke(keyong@cnic.cn)
 */
public class PageDifferenceAction extends BaseDispatchAction {

	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String method) throws Exception {
		if (method == null || method.equals("")) {
			method = "info";
		}
		return super.dispatchMethod(mapping, form, request, response, method);
	}

	public ActionForward rename(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, InvalidDPageDtoException {
		VWBContext context = VWBContext.getContext(request);
		String sNewTitle = request.getParameter("renameto");
		DPage page = (DPage) context.getResource();

		if (sNewTitle != null && sNewTitle.trim().length() > 0) {
			page.setTitle(sNewTitle);
			VWBContext.getContainer().getDpageService().updateDpage(page);
		}

		return doLayout(context);
	}

	public ActionForward diff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, InvalidDPageDtoException {
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.DIFF, this.getSavedViewPort(request));
		if (context.hasAccess(response)) {
			String sVersion = request.getParameter("r1");
			String sCompareTo = request.getParameter("r2");
			int iVersion = 0, iCompareTo = 0;
			if (sVersion != null) {
				iVersion = Integer.parseInt(sVersion);
			}

			if (sCompareTo != null) {
				iCompareTo = Integer.parseInt(sCompareTo);
			} else {
				int iLastVersion = VWBContext
						.getContainer()
						.getDpageService()
						.getLatestDpageByResourceId(context.getSite().getId(),
								context.getResource().getResourceId())
						.getVersion();
				if (iLastVersion > 1) {
					iCompareTo = iLastVersion - 1;
				}
			}
			request.setAttribute(InsertDiffTag.ATTR_OLDVERSION, iVersion);
			request.setAttribute(InsertDiffTag.ATTR_NEWVERSION, iCompareTo);
			request.setAttribute(VWBContext.CONTEXT_KEY, context);
			return doLayout(context);
		} else
			return null;
	}

	public ActionForward info(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, InvalidDPageDtoException {
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.INFO, this.getSavedViewPort(request));
		if (context.hasAccess(response)) {
			request.setAttribute(VWBContext.CONTEXT_KEY, context);
			return doLayout(context);
		} else
			return null;
	}

	public ActionForward del(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, InvalidDPageDtoException {
		Resource resource = getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.DELETE, resource);
		if (context.hasAccess(response)) {
			DPageService service = VWBContext.getContainer().getDpageService();
			service.deleteDpageByResourceId(context.getSiteId(),
					resource.getResourceId());

			response.sendRedirect(context.getFrontPage());
			return null;
		} else
			return null;
	}

	private ActionForward doLayout(VWBContext context) {
		return layout(context, "/jsp/InfoContent.jsp");
	}

}
