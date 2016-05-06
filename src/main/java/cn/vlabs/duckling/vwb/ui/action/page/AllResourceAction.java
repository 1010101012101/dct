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

import cn.vlabs.duckling.util.JsonUtil;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;

import com.thoughtworks.xstream.XStream;

/**
 * Introduction Here.
 * 
 * @date 2010-3-7
 * @author Fred zhang (fred@cnic.cn)
 */
public class AllResourceAction extends BaseAction {
	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	Logger log = Logger.getLogger(AllResourceAction.class);

	private static String DPAGE = "DPage";
	private static String PORTAL = "Portal";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("query");
		String datatype = request.getParameter("datatype");
		List<ViewPort> pages = null;
		pages = readPages(VWBContainerImpl.findSite(request).getId(), query);// 只取类型为DPAGE或者PORTAL
		if ((pages != null) && (pages.size() > 0)) {

			if ("json".equals(datatype)) {
				String str = JsonUtil.list2json(pages);
				writejsonToResponse(response, str);
			} else {
				String xml = convertToXML(pages);
				writeToResponse(response, xml);
			}

		}
		return null;
	}

	/**
	 * @param response
	 * @param str
	 */
	private void writejsonToResponse(HttpServletResponse response, String str) {
		response.setContentType("text;charset=UTF-8");
		try {
			Writer wr = response.getWriter();
			wr.write(str);
			wr.close();
		} catch (IOException e) {
			log.debug("Write json to response error!", e);
		}

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

	private String convertToXML(List<ViewPort> pages) {
		XStream stream = new XStream();
		stream.alias("page", ViewPort.class);
		String xml = stream.toXML(pages);
		return xml;
	}

	// ADD BY DIYANLIANG
	private List<ViewPort> readPages(int siteId, String query) {
		List<ViewPort> lists = new ArrayList<ViewPort>();
		List<ViewPort> pages = VWBContainerImpl.findContainer().getViewPortService()
				.searchResourceByTitle(siteId, query);
		for (ViewPort page : pages) {
			if (DPAGE.equals(page.getType()) || PORTAL.equals(page.getType())) {
				ViewPort pagevo = new ViewPort();
				pagevo.setId(page.getId());
				pagevo.setTitle(page.getTitle());
				pagevo.setType(page.getType());
				lists.add(pagevo);
			}
		}
		return lists;
	}
}
