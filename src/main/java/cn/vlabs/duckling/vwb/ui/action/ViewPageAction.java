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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.render.impl.DPageRendable;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class ViewPageAction extends BaseAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		DPage page = (DPage)getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request, DPageCommand.VIEW, page);
		if (context.hasAccess(response)){
			int version = getRequestVersion(request);
			ActionForward forward = null;
			if (VWBContext.LATEST_VERSION!=version && page.getVersion()!=version ){
				DPageService dpageService = VWBContext.getContainer().getDpageService();
				DPage versionPage = dpageService.getDpageVersionContent(context.getSite().getId(),page.getResourceId(), version);
				if (versionPage!=null){
					context.targetCommand(versionPage);
					forward = layout(context, new DPageRendable(versionPage.getResourceId(), versionPage.getVersion()));
				}else{
					log.warn(String.format("Page's conent id=%d, version=%d is not found.", page.getResourceId(), version));
					try {
						response.sendError(HttpStatus.SC_NOT_FOUND);
					} catch (IOException e) {
						log.error("forward to not found page failed.",e);
					}
					return null;
				}
			}else{
				forward = layout(context);
			}
			return forward;
		}
		return null;
	}
	private int getRequestVersion(HttpServletRequest request){
		String version = request.getParameter("version");
		if (version!=null){
			try{
				return Integer.parseInt(version);
			}catch(NumberFormatException e){
				log.warn(e.getMessage());
			}
		}
		return VWBContext.LATEST_VERSION;
	}
}