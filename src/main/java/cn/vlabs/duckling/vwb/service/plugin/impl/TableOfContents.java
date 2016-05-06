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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dml.dml2html.Dml2Html;
import cn.vlabs.duckling.vwb.service.dml.dml2html.Dml2HtmlEngine;
import cn.vlabs.duckling.vwb.service.dml.impl.DmlContextBridge;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * 
 * @date Feb 25, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class TableOfContents extends AbstractDPagePlugin {
	private DPage page = null;

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {

		DPageService dpageService = VWBContext.getContainer().getDpageService();
		String resourceid = (String) params.get("resourceid");
		int iResourceId = 0;
		if (resourceid == null) {
			iResourceId = context.getResource().getResourceId();
		} else {
			iResourceId = Integer.parseInt(resourceid);
		}
		StringBuffer sb = new StringBuffer();
		String flag = "TableOfContent-" + iResourceId;
		if (context.getVariable(flag) == null) {
			context.setVariable(flag, "");
			page = dpageService.getLatestDpageByResourceId(getSite().getId(),
					iResourceId);
			ResourceBundle rb = context
					.getBundle(AbstractDPagePlugin.PLUGINS_RESOURCEBUNDLE);

			if (context.getVariable(VAR_ALREADY_PROCESSING) != null)
				return rb.getString("tableofcontents.title");

			sb.append("<div class=\"toc\">\n");
			sb.append("<div class=\"collapsebox\">\n");

			String title = (String) params.get(PARAM_TITLE);
			if (title != null) {
				sb.append("<h4><div>" + TextUtil.replaceEntities(title)
						+ "</div></h4>\n");
			} else {
				sb.append("<h4><div>" + rb.getString("tableofcontents.title")
						+ "</div></h4>\n");
			}

			// should we use an ordered list?
			m_usingNumberedList = false;
			if (params.containsKey(PARAM_NUMBERED)) {
				String numbered = (String) params.get(PARAM_NUMBERED);
				if (numbered.equalsIgnoreCase("true")) {
					m_usingNumberedList = true;
				} else if (numbered.equalsIgnoreCase("yes")) {
					m_usingNumberedList = true;
				}
			}

			// if we are using a numbered list, get the rest of the parameters
			// (if any) ...
			if (m_usingNumberedList) {
				int start = 0;
				String startStr = (String) params.get(PARAM_START);
				if ((startStr != null) && (startStr.matches("^\\d+$"))) {
					start = Integer.parseInt(startStr);
				}
				if (start < 0)
					start = 0;

				m_starting = start;
				m_level1Index = start - 1;
				if (m_level1Index < 0)
					m_level1Index = 0;
				m_level2Index = 0;
				m_level3Index = 0;
				m_prefix = (String) params.get(PARAM_PREFIX);
				if (m_prefix == null)
					m_prefix = "";
				m_lastLevel = Heading.HEADING_LARGE;
			}

			getHeading(context, page.getContent());

			sb.append("<ul>\n" + m_buf.toString() + "</ul>\n");
			sb.append("</div>\n</div>\n");
		}
		// System.out.println("sb.toString()="+sb.toString());
		return sb.toString();

	}

	private static Logger log = Logger.getLogger(TableOfContents.class);

	public static final String PARAM_TITLE = "title";
	public static final String PARAM_NUMBERED = "numbered";
	public static final String PARAM_START = "start";
	public static final String PARAM_PREFIX = "prefix";

	private static final String VAR_ALREADY_PROCESSING = "__TableOfContents.processing";

	StringBuffer m_buf = new StringBuffer();
	private boolean m_usingNumberedList = false;
	private String m_prefix = "";
	private int m_starting = 0;
	private int m_level1Index = 0;
	private int m_level2Index = 0;
	private int m_level3Index = 0;
	private int m_lastLevel = 0;

	public void headingAdded(VWBContext context, Heading hd)
			throws PluginException {
		log.debug("HD: " + hd.m_level + ", " + hd.m_titleText + ", "
				+ hd.m_titleAnchor);

		switch (hd.m_level) {
		case Heading.HEADING_SMALL:
			m_buf.append("<li class=\"toclevel-3\">");
			m_level3Index++;
			break;
		case Heading.HEADING_MEDIUM:
			m_buf.append("<li class=\"toclevel-2\">");
			m_level2Index++;
			break;
		case Heading.HEADING_LARGE:
			m_buf.append("<li class=\"toclevel-1\">");
			m_level1Index++;
			break;
		default:
			throw new PluginException(
					"Unknown depth in toc! (Please submit a bug report.)");
		}

		if (m_level1Index < m_starting) {
			// in case we never had a large heading ...
			m_level1Index++;
		}
		if ((m_lastLevel == Heading.HEADING_SMALL)
				&& (hd.m_level != Heading.HEADING_SMALL)) {
			m_level3Index = 0;
		}
		if (((m_lastLevel == Heading.HEADING_SMALL) || (m_lastLevel == Heading.HEADING_MEDIUM))
				&& (hd.m_level == Heading.HEADING_LARGE)) {
			m_level3Index = 0;
			m_level2Index = 0;
		}

		String titleSection = hd.m_titleSection.replace('%', '_');
		String pageName = page.getTitle();// .encodeName(context.getPage().getName()).replace(
											// '%', '_' );
		String url = context.getBaseURL();// context.getURL( WikiContext.VIEW,
											// context.getPage().getName() );
		String sectref = "#section-" + pageName + "-" + titleSection;

		m_buf.append("<a class=\"wikipage\" href=\"" + url + sectref + "\">");
		if (m_usingNumberedList) {
			switch (hd.m_level) {
			case Heading.HEADING_SMALL:
				m_buf.append(m_prefix + m_level1Index + "." + m_level2Index
						+ "." + m_level3Index + " ");
				break;
			case Heading.HEADING_MEDIUM:
				m_buf.append(m_prefix + m_level1Index + "." + m_level2Index
						+ " ");
				break;
			case Heading.HEADING_LARGE:
				m_buf.append(m_prefix + m_level1Index + " ");
				break;
			default:
				throw new PluginException(
						"Unknown depth in toc! (Please submit a bug report.)");
			}
		}
		m_buf.append(TextUtil.replaceEntities(hd.m_titleText) + "</a></li>\n");

		m_lastLevel = hd.m_level;
	}

	public String getHeading(VWBContext context, String pagedata) {
		String reStr = "";
		String viewMode = (String) context.getHttpRequest().getSession()
				.getAttribute("mode");
		Dml2HtmlEngine d2lengine = new Dml2HtmlEngine();
		d2lengine.setM_wysiwygEditorMode(false);// 是否是fck
		d2lengine.setPageName(page.getTitle());
		d2lengine.setView_Mode(viewMode);// 编辑模式还是浏览模式

		DmlContextBridge dmlcontextbridge = new DmlContextBridge();
		dmlcontextbridge.setContext(context);

		d2lengine.setDmlcontext(dmlcontextbridge);
		try {
			Dml2Html d2h = new Dml2Html(pagedata, d2lengine);
			Element parentelement = d2h.getHTMLDom();
			printChildren(parentelement, context);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reStr;

	}

	private void printChildren(Element base, VWBContext context) {
		for (Iterator<?> i = base.getContent().iterator(); i.hasNext();) {
			Object c = i.next();
			if (c instanceof Element) {
				Element e = (Element) c;
				String n = e.getName().toLowerCase();
				if ("h1".equals(n)) {
					m_buf.append("<li>");
					m_level1Index++;
					printHeading(context, e);
				} else if ("h2".equals(n)) {
					m_buf.append("<li class=\"toclevel-1\">");

					m_level2Index++;
					printHeading(context, e);
				} else if ("h3".equals(n)) {
					m_buf.append("<li class=\"toclevel-2\">");

					m_level3Index++;
					printHeading(context, e);
				} else if ("h4".equals(n)) {
					m_buf.append("<li class=\"toclevel-3\">");

					m_lastLevel++;
					printHeading(context, e);
				}
				printChildren(e, context);
			}
		}
	}

	private void printHeading(VWBContext context, Element e) {
		String url = context.getURL(VWBContext.VIEW, page.getResourceId());
		String sectref = "";

		if ("h1".equals(e.getName().toLowerCase())) {
			sectref = "#h1_" + m_level1Index;

		} else if ("h2".equals(e.getName().toLowerCase())) {
			sectref = "#h2_" + m_level2Index;

		} else if ("h3".equals(e.getName().toLowerCase())) {
			sectref = "#h3_" + m_level3Index;

		} else if ("h4".equals(e.getName().toLowerCase())) {
			sectref = "#h4_" + m_lastLevel;

		}
		m_buf.append("<a class=\"wikipage\" href=\"" + url + sectref + "\">");

		m_buf.append(TextUtil.replaceEntities(e.getValue()) + "</a></li>\n");
	}
}
