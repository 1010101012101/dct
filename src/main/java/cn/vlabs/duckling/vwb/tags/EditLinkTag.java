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

import javax.servlet.jsp.JspWriter;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class EditLinkTag extends VWBLinkTag {
	private static final long serialVersionUID = 0L;

	public String m_version = null;
	public String m_title = "";
	public String m_accesskey = "";

	public void initTag() {
		super.initTag();
		m_version = null;
	}

	public void setVersion(String vers) {
		m_version = vers;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public void setAccesskey(String access) {
		m_accesskey = access;
	}

	public final int doVWBStart() throws IOException {
		DPage page = null;
		String versionString = "";
		int pageId = -1;

		//
		// Determine the page and the link.
		//
		if (m_pageName == null) {
			page = vwbcontext.getPage();
			if (page == null) {
				// You can't call this on the page itself anyways.
				return SKIP_BODY;
			}

			pageId = page.getResourceId();
		} else {
			pageId = m_pageId;
		}

		//
		// Determine the latest version, if the version attribute is "this".
		//
		if (m_version != null) {
			if ("this".equalsIgnoreCase(m_version)) {
				if (page == null) {
					// No page, so go fetch according to page name.
					page = (DPage)VWBContainerImpl.findContainer().getResourceService()
							.getResource(vwbcontext.getSiteId(), m_pageId);
				}

				if (page != null) {
					versionString = "version=" + page.getVersion();
				}
			} else {
				versionString = "version=" + m_version;
			}
		}

		//
		// Finally, print out the correct link, according to what
		// user commanded.
		//
		JspWriter out = pageContext.getOut();

		switch (m_format) {
		case ANCHOR:
			out.print("<a href=\""
					+ vwbcontext.getURL(VWBContext.EDIT,
							Integer.toString(pageId), versionString)
					+ "\" accesskey=\"" + m_accesskey + "\" title=\"" + m_title
					+ "\">");
			break;

		case URL:
			out.print(vwbcontext.getURL(VWBContext.EDIT,
					Integer.toString(pageId), versionString));
			break;
		}

		return EVAL_BODY_INCLUDE;
	}
}
