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

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.attach.Attachment;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 * Introduction Here.
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class LinkToTag extends VWBLinkTag {

	private static final long serialVersionUID = 0L;

	private String m_version = null;

	public String m_title = "";

	public String m_accesskey = "";

	public void initTag() {
		super.initTag();
		m_version = null;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String arg) {
		m_version = arg;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public void setAccesskey(String access) {
		m_accesskey = access;
	}

	public int doVWBStart() throws IOException {
		String pageName = m_pageName;
		boolean isattachment = false;

		if (m_pageName == null) {
			DPage p = vwbcontext.getPage();

			if (p != null) {
				pageName = Integer.toString(p.getResourceId());

				isattachment = p instanceof Attachment;
			} else {
				return SKIP_BODY;
			}
		}

		JspWriter out = pageContext.getOut();
		String url;
		String linkclass;

		if (isattachment) {
			url = vwbcontext.getURL(VWBContext.ATTACH, pageName,
					(getVersion() != null) ? "version=" + getVersion() : null);
			linkclass = "attachment";
		} else {
			StringBuffer params = new StringBuffer();
			if (getVersion() != null)
				params.append("version=" + getVersion());

			url = vwbcontext.getURL(VWBContext.VIEW, pageName, params
					.toString());
			linkclass = "wikipage";
		}

		switch (m_format) {
		case ANCHOR:
			out.print("<a class=\"" + linkclass + "\" href=\"" + url
					+ "\" accesskey=\"" + m_accesskey + "\" title=\"" + m_title
					+ "\">");
			break;
		case URL:
			out.print(url);
			break;
		}

		return EVAL_BODY_INCLUDE;
	}
}
