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

import java.security.ProviderException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.util.AttachmentURL;
import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.attach.Attachment;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.resource.Resource;

public class LinkTag extends VWBLinkTag implements ParamHandler, BodyTag {
	static final long serialVersionUID = 0L;

	private String m_version = null;
	private String m_class = null;
	private String m_style = null;
	private String m_title = null;
	private String m_target = null;
	private String m_compareToVersion = null;
	private String m_rel = null;
	private String m_jsp = null;
	private String m_docid = null;
	private boolean m_cachable = false;
	private String m_context = null;
	private String m_accesskey = null;
	private boolean m_absolute = false;
	private boolean m_overrideAbsolute = false;

	private Map<String, String> m_containedParams;

	private BodyContent m_bodyContent;

	public void initTag() {
		super.initTag();
		m_version = m_class = m_style = m_title = m_target = m_compareToVersion = m_rel = m_jsp = m_docid = m_accesskey = null;
		m_containedParams = null;
		m_absolute = m_cachable = false;
	}

	public void setAccessKey(String key) {
		m_accesskey = key;
	}

	public void setAbsolute(String arg) {
		m_overrideAbsolute = true;
		m_absolute = TextUtil.isPositive(arg);
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String arg) {
		m_version = arg;
	}

	public void setClass(String arg) {
		m_class = arg;
	}

	public void setStyle(String style) {
		m_style = style;
	}

	public void setCachable(boolean cachable) {
		m_cachable = cachable;
	}

	public void setDocId(String docId) {
		m_docid = docId;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public void setTarget(String target) {
		m_target = target;
	}

	public void setCompareToVersion(String ver) {
		m_compareToVersion = ver;
	}

	public void setRel(String rel) {
		m_rel = rel;
	}

	public void setJsp(String jsp) {
		m_jsp = jsp;
	}

	public void setContext(String context) {
		m_context = context;
	}

	/**
	 * Support for ParamTag supplied parameters in body.
	 */
	public void setContainedParameter(String name, String value) {
		if (name != null) {
			if (m_containedParams == null) {
				m_containedParams = new HashMap<String, String>();
			}
			m_containedParams.put(name, value);
		}
	}

	/**
	 * This method figures out what kind of an URL should be output. It mirrors
	 * heavily on JSPWikiMarkupParser.handleHyperlinks();
	 * 
	 * @return
	 * @throws ProviderException
	 */
	private String figureOutURL() throws ProviderException {
		String url = null;

		if (StringUtils.isEmpty(m_pageName)) {
			Resource resource = vwbcontext.getResource();

			if (resource != null) {
				m_pageName = Integer.toString(resource.getResourceId());
				m_pageId = resource.getResourceId();
			}
		}
		if (m_jsp != null) {
			String params = addParamsForRecipient(null, m_containedParams);
			if (m_context != null) {
				url = vwbcontext.getURL(m_context, m_jsp, params, m_absolute);
			} else
				url = vwbcontext.getURL(VWBContext.NONE, m_jsp, params,
						m_absolute);
		} else if (m_docid != null) {
			AttachmentURL attachUrl = new AttachmentURL(m_cachable,
					Integer.parseInt(m_docid));
			url = attachUrl.buildURL(vwbcontext.getBaseURL());
		} else if (!StringUtils.isEmpty(m_pageName)) {
			m_context = VWBContext.VIEW;
			Resource p = VWBContext.getContainer().getResourceService()
					.getResource(vwbcontext.getSiteId(), m_pageId);
			if (p != null) {

				String params = (m_version != null) ? "version=" + getVersion()
						: null;

				params = addParamsForRecipient(params, m_containedParams);

				if (p instanceof Attachment) {
					String ctx = m_context;
					// Switch context appropriately when attempting to view an
					// attachment, but don't override the context setting
					// otherwise
					if (m_context == null || m_context.equals(VWBContext.VIEW)) {
						ctx = VWBContext.ATTACH;
					}
					url = vwbcontext.getURL(ctx, m_pageName, params, m_absolute);
				} else {
					url = makeBasicURL(m_context, m_pageId, params, m_absolute);
				}
			} else {
				String params = addParamsForRecipient(null, m_containedParams);
				url = vwbcontext.getURL(m_context, m_pageName, params, m_absolute);
			}
		} else {
			url = makeBasicURL(m_context, Constant.DEFAULT_FRONT_PAGE, null,
					m_absolute);
		}

		return url;
	}

	private String addParamsForRecipient(String addTo,
			Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return addTo;
		}
		StringBuffer buf = new StringBuffer();
		Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> e = it.next();
			String n = (String) e.getKey();
			String v = (String) e.getValue();
			buf.append(n);
			buf.append("=");
			buf.append(v);
			if (it.hasNext()) {
				buf.append("&");
			}
		}
		if (addTo == null) {
			return buf.toString();
		}
		if (!addTo.endsWith("&")) {
			return addTo + "&" + buf.toString();
		}
		return addTo + buf.toString();
	}

	private String makeBasicURL(String context, int page, String parms,
			boolean absolute) {
		String url;
		if (context.equals(VWBContext.DIFF)) {
			int r1 = 0;
			int r2 = 0;

			if (DiffLinkTag.VER_LATEST.equals(getVersion())) {
				DPage latest = VWBContext.getContainer().getDpageService()
						.getLatestDpageByResourceId(vwbcontext.getSiteId(), page);

				r1 = latest.getVersion();
			} else if (DiffLinkTag.VER_PREVIOUS.equals(getVersion())) {
				r1 = vwbcontext.getPage().getVersion() - 1;
				r1 = (r1 < 1) ? 1 : r1;
			} else if (DiffLinkTag.VER_CURRENT.equals(getVersion())) {
				r1 = vwbcontext.getPage().getVersion();
			} else {
				r1 = Integer.parseInt(getVersion());
			}

			if (DiffLinkTag.VER_LATEST.equals(m_compareToVersion)) {
				DPage latest = VWBContext.getContainer().getDpageService()
						.getLatestDpageByResourceId(vwbcontext.getSiteId(), page);

				r2 = latest.getVersion();
			} else if (DiffLinkTag.VER_PREVIOUS.equals(m_compareToVersion)) {
				r2 = vwbcontext.getPage().getVersion() - 1;
				r2 = (r2 < 1) ? 1 : r2;
			} else if (DiffLinkTag.VER_CURRENT.equals(m_compareToVersion)) {
				r2 = vwbcontext.getPage().getVersion();
			} else {
				r2 = Integer.parseInt(m_compareToVersion);
			}

			parms = "r1=" + r1 + "&amp;r2=" + r2;
		}

		url = vwbcontext.getURL(m_context, m_pageName, parms, m_absolute);

		return url;
	}

	public int doVWBStart() throws Exception {
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() {
		try {
			if (!m_overrideAbsolute) {
				m_absolute = false;
			}

			JspWriter out = pageContext.getOut();
			String url = figureOutURL();

			switch (m_format) {
			case URL:
				out.print(url);
				break;
			default:
			case ANCHOR:
				StringBuffer sb = new StringBuffer(20);

				sb.append((m_class != null) ? "class=\"" + m_class + "\" " : "");
				sb.append((m_style != null) ? "style=\"" + m_style + "\" " : "");
				sb.append((m_target != null) ? "target=\"" + m_target + "\" "
						: "");
				sb.append((m_title != null) ? "title=\"" + m_title + "\" " : "");
				sb.append((m_rel != null) ? "rel=\"" + m_rel + "\" " : "");
				sb.append((m_accesskey != null) ? "accesskey=\"" + m_accesskey
						+ "\" " : "");
				out.print("<a " + sb.toString() + " href=\"" + url + "\">");
				break;
			}

			// Add any explicit body content. This is not the intended use
			// of LinkTag, but happens to be the way it has worked previously.
			if (m_bodyContent != null) {
				String linktext = m_bodyContent.getString().trim();
				out.write(linktext);
			}

			// Finish off by closing opened anchor
			if (m_format == ANCHOR)
				out.print("</a>");
		} catch (Exception e) {
			// Yes, we want to catch all exceptions here, including
			// RuntimeExceptions
			log.error("Tag failed", e);
		}

		return EVAL_PAGE;
	}

	public void setBodyContent(BodyContent bc) {
		m_bodyContent = bc;
	}

	public void doInitBody() throws JspException {
	}
}
