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

package cn.vlabs.duckling.vwb.tags;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;

/**
 * @date 2011-1-12
 * @author diyanliang@cnic.cn <vwb:SubPage pageid="1(7,6,5,4,3);2"/> ;隔开父页面
 *         （）里面是子页面
 */
public class SubPageTag extends VWBBaseTag {

	private static final long serialVersionUID = 0L;
	private String m_pageid;

	public int doVWBStart() throws IOException {
		DPageService dPageService = VWBContext.getContainer().getDpageService();
		String ids[] = m_pageid.split(";");
		for (int i = 0; i < ids.length; i++) {
			String arrayString = "";
			String strPageid = "";
			String strSubPage = "";
			int iPageid = 1;
			if (ids[i] != null) {
				arrayString = ids[i];
			}
			int startindex;
			startindex = arrayString.indexOf("(");
			if (startindex != -1) {
				strPageid = arrayString.substring(0, startindex);
				try {
					iPageid = Integer.valueOf(strPageid);
				} catch (Exception e) {
					e.printStackTrace();
					iPageid = 1;
				}
				strSubPage = arrayString.substring(startindex + 1,
						arrayString.length() - 1);

				DPage page = dPageService.getLatestDpageByResourceId(
						vwbcontext.getSiteId(), iPageid);
				if (getSubPageCount(dPageService, iPageid) != 0) {
					pageContext.getOut().print(
							"<div class=\"DCT_ptitle\">" + page.getTitle()
									+ "</div>");
					pageContext.getOut().print(
							getInnerHTML(dPageService, iPageid, strSubPage));
				}
			} else {

				strPageid = arrayString;
				try {
					iPageid = Integer.valueOf(strPageid);
				} catch (Exception e) {
					e.printStackTrace();
					iPageid = 1;
				}
				DPage page = dPageService.getLatestDpageByResourceId(
						vwbcontext.getSiteId(), iPageid);
				if (getSubPageCount(dPageService, iPageid) != 0) {
					pageContext.getOut().print(
							"<div class=\"DCT_ptitle\">" + page.getTitle()
									+ "</div>");
					pageContext.getOut().print(
							getInnerHTML(dPageService, iPageid));
				}

			}
		}
		return SKIP_BODY;
	}

	public int getSubPageCount(DPageService dpageService, int m_id) {
		int count = 0;

		List<DPageNodeInfo> list = dpageService.listSubPage(vwbcontext.getSiteId(), m_id);
		for (DPageNodeInfo entity : list) {
			DPage page = VWBContext
					.getContainer()
					.getDpageService()
					.getLatestDpageByResourceId(vwbcontext.getSiteId(),
							entity.getResourceId());
			AuthorizationService authorizationService = VWBContext
					.getContainer().getAuthorizationService();
			if (authorizationService.checkPermission(vwbcontext.getSiteId(),
					VWBSession.findSession(vwbcontext.getHttpRequest()),
					PermissionFactory.getPagePermission(page, "view")))
				count++;
		}
		return count;
	}

	public String getInnerHTML(DPageService dpageService, int m_id) {
		String innerHTML = "<ul>";
		List<DPageNodeInfo> list = dpageService.listSubPage(vwbcontext.getSiteId(), m_id);
		Collections.sort(list, new Comparator<DPageNodeInfo>() {
			public int compare(DPageNodeInfo arg0, DPageNodeInfo arg1) {
				if (arg0.getPageorder() == null)
					arg0.setPageorder(0);
				if (arg1.getPageorder() == null)
					arg1.setPageorder(0);
				return arg1.getPageorder().compareTo(arg0.getPageorder());
			}
		});
		for (DPageNodeInfo entity : list) {
			DPage page = VWBContext
					.getContainer()
					.getDpageService()
					.getLatestDpageByResourceId(vwbcontext.getSiteId(),
							entity.getResourceId());
			AuthorizationService authorizationService = VWBContext
					.getContainer().getAuthorizationService();
			if (authorizationService.checkPermission(vwbcontext.getSiteId(),
					VWBSession.findSession(vwbcontext.getHttpRequest()),
					PermissionFactory.getPagePermission(page, "view")))
				innerHTML += "<li><a href=\""
						+ vwbcontext.getViewURL(entity.getResourceId()) + "\">"
						+ entity.getTitle() + "</a></li>";
		}
		return innerHTML + "</ul>";
	}

	public String getInnerHTML(DPageService dpageService, int m_id,
			String strSubPage) {
		String innerHTML = "<ul>";
		List<DPageNodeInfo> list = dpageService.listSubPage(vwbcontext.getSiteId(), m_id);

		String strSubPages[] = strSubPage.split(",");
		for (int i = 0; i < strSubPages.length; i++) {
			String strsubpageid = strSubPages[i];
			int isubpageid = 1;
			try {
				isubpageid = Integer.valueOf(strsubpageid);
			} catch (Exception e) {
				e.printStackTrace();
				isubpageid = 1;
			}

			for (DPageNodeInfo entity : list) {
				if (entity.getResourceId() == isubpageid) {
					entity.setPageorder(9999 - i);
				}

			}
		}

		Collections.sort(list, new Comparator<DPageNodeInfo>() {
			public int compare(DPageNodeInfo arg0, DPageNodeInfo arg1) {
				if (arg0.getPageorder() == null)
					arg0.setPageorder(0);
				if (arg1.getPageorder() == null)
					arg1.setPageorder(0);
				return arg1.getPageorder().compareTo(arg0.getPageorder());
			}
		});
		for (DPageNodeInfo entity : list) {
			DPage page = VWBContext
					.getContainer()
					.getDpageService()
					.getLatestDpageByResourceId(vwbcontext.getSiteId(),
							entity.getResourceId());
			AuthorizationService authorizationService = VWBContext
					.getContainer().getAuthorizationService();
			if (authorizationService.checkPermission(vwbcontext.getSiteId(),
					VWBSession.findSession(vwbcontext.getHttpRequest()),
					PermissionFactory.getPagePermission(page, "view")))
				innerHTML += "<li><a href=\""
						+ vwbcontext.getViewURL(entity.getResourceId()) + "\">"
						+ entity.getTitle() + "</a></li>";
		}
		return innerHTML + "</ul>";
	}

	public String getPageid() {
		return m_pageid;
	}

	public void setPageid(String m_pageid) {
		this.m_pageid = m_pageid;
	}

}
