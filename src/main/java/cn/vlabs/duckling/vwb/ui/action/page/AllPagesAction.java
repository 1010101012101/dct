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

package cn.vlabs.duckling.vwb.ui.action.page;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;

import com.thoughtworks.xstream.XStream;

/**
 * Introduction Here.
 * 
 * @date 2010-3-7
 * @author Fred zhang (fred@cnic.cn)
 */
public class AllPagesAction extends BaseAction {
	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	Logger log = Logger.getLogger(AllPagesAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("query");
		List<DPageVo> pages = null;
		String containId = request.getParameter("containId");
		SiteMetaInfo site = VWBContainerImpl.findSite(request);
		if (containId == null) {
			pages = readPages(site.getId(), query);
		} else {
			pages = readPagesContainIds(site.getId(), query);
		}
		if ((pages != null) && (pages.size() > 0)) {
			String xml = convertToXML(pages);
			writeToResponse(response, xml);
		}
		return null;
	}

	private void writeToResponse(HttpServletResponse response, String xml) {
		response.setContentType("text/xml;charset=UTF-8");
		try {
			Writer wr = response.getWriter();
			wr.write(xml);
			wr.close();
		} catch (IOException e) {
			log.debug("Write xml to response error!", e);
		}
	}

	private String convertToXML(List<DPageVo> pages) {
		XStream stream = new XStream();
		stream.alias("page", DPageVo.class);
		String xml = stream.toXML(pages);
		return xml;
	}

	private List<DPageVo> readPagesContainIds(int siteId, String query) {
		List<DPageVo> lists = new ArrayList<DPageVo>();
		List<LightDPage> pages = VWBContainerImpl.findContainer().getDpageService().searchDpageByTitle(siteId,
				query);
		for (LightDPage page : pages) {
			DPageVo pagevo = new DPageVo();
			pagevo.setId(page.getResourceId());
			pagevo.setTitle(page.getTitle() + "(" + page.getResourceId() + ")");
			lists.add(pagevo);
		}
		return lists;
	}

	private List<DPageVo> readPages(int siteId, String query) {
		List<DPageVo> lists = new ArrayList<DPageVo>();
		List<LightDPage> pages = VWBContainerImpl.findContainer().getDpageService().searchDpageByTitle(siteId,
				query);
		for (LightDPage page : pages) {
			DPageVo pagevo = new DPageVo();
			pagevo.setId(page.getResourceId());
			pagevo.setTitle(page.getTitle());
			lists.add(pagevo);
		}
		return lists;
	}
}
