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

package cn.vlabs.duckling.vwb.service.dml.impl;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.dml.dml2html.Dml2Html;
import cn.vlabs.duckling.vwb.service.dml.dml2html.Dml2HtmlEngine;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.ui.action.HtmlValidateUtil;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;

/**
 * @date 2010-2-24
 * @author diyanliang@cnic.cn
 */
public class RenderingServiceImpl implements RenderingService {
	private static final int DPAGE_LATEST_VERSION = -1;
	private DPageService dpageService;

	@Override
	public String getHTML(int siteId, int pageId) {
		SiteMetaInfo site= VWBContainerImpl.findContainer().getSite(siteId);
		DPage dpage = dpageService.getDpageVersionContent(siteId, pageId,
				DPAGE_LATEST_VERSION);
		VWBContext context = new VWBContext(null, site, DPageCommand.VIEW,
				dpage);
		String res = getHTML(context, dpage.getContent());
		return res;
	}

	public String getHTML(VWBContext context, String pageContent) {
		if(!HtmlValidateUtil.checkHtmlTextValidate(pageContent)){
			return HtmlValidateUtil.disabledSubmit(pageContent);
		}
		
		String res = "";
		// 第一步 定义参数
		String viewMode = "0";
		boolean m_wysiwygEditorMode = false;
		// 第二步 取是command
		String strModeType = context.getWysiwygEditorMode();
		boolean modetype = context.isFullMode();
		if (modetype) {
			viewMode = "1";
		}
		if ((VWBContext.EDITOR_MODE).equals(strModeType)) {
			m_wysiwygEditorMode = true;
		}
		// 建立解析参数容器
		Dml2HtmlEngine d2lengine = new Dml2HtmlEngine();
		d2lengine.setM_wysiwygEditorMode(m_wysiwygEditorMode);// 是否是fck
		String pagename = String.valueOf(Constant.DEFAULT_FRONT_PAGE);
		d2lengine.setPageName(pagename);
		d2lengine.setView_Mode(viewMode);// 编辑模式还是浏览模式
		// d2lengine.setBaseurl(context.getBaseURL());
		DmlContextBridge dmlcontextbridge = new DmlContextBridge();
		dmlcontextbridge.setContext(context);
		d2lengine.setDmlcontext(dmlcontextbridge);
		// 建立解析器并解析
		try {
			Dml2Html d2h = new Dml2Html(pageContent, d2lengine);
			res = d2h.getHTMLString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void setDpageService(DPageService dpageService) {
		this.dpageService = dpageService;
	}
}
