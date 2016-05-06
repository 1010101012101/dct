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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import cn.vlabs.duckling.util.StringUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.CurrentPageResultSet;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * 
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SubDpageFilterPlugin extends AbstractDPagePlugin {
	private static final DateFormat dateFormate = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	private DateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {

		Map<String, Object> searchedConditions = new HashMap<String, Object>();
		searchedConditions.put("prefix", params.get("prefix"));
		searchedConditions.put("suffix", params.get("suffix"));
		String beginDateStr = (String) params.get("begindate");
		String endDateStr = (String) params.get("enddate");
		String recentDays = (String) params.get("recentdays");
		String showChars = (String) params.get("showchars");
		// added by kevin
		String hasTitle = (String) params.get("hastitle");
		boolean bHasTitle = hasTitle != null && "true".equals(hasTitle) ? true
				: false;
		String styleClass = (String) params.get("styleclass");
		String nextPage = (String) params.get("nextpage");
		boolean bNextPage = nextPage != null && "true".equals(nextPage) ? true
				: false;
		String dataFormat = (String) params.get("dataformat");
		if ((dataFormat != null) && (!"".endsWith(dataFormat.trim()))) {
			onlyDateFormat = new SimpleDateFormat(dataFormat);
		}
		// ended
		int iShowChars = showChars != null ? Integer.parseInt(showChars) : 40;
		String simpleTable = (String) params.get("simple");
		boolean bsimpleTable = simpleTable != null
				&& "true".equals(simpleTable) ? true : false;
		Date beginDate, endDate;
		// 设置查询的开始和截止时间
		try {
			if (recentDays != null) {
				beginDate = getLastDate(Long.parseLong(recentDays));
				endDate = new Date();
			} else {
				if (beginDateStr != null) {

					beginDate = dateFormate.parse(beginDateStr);
				} else {
					beginDate = new Date(0);
				}
				if (endDateStr != null) {
					endDate = dateFormate.parse(endDateStr);
				} else {
					endDate = new Date();
				}
			}

		} catch (ParseException e) {
			throw new PluginException(
					"The format of plugin date is incorrect!", e);
		}
		String resourceid = (String) params.get("resourceid");
		int iResourceId = 0;
		if ((resourceid == null) || ("".equals(resourceid))) {
			iResourceId = context.getResource().getResourceId();
		} else {
			iResourceId = Integer.parseInt(resourceid);
		}
		searchedConditions.put("user", params.get("user"));
		searchedConditions.put("beginDate", beginDate);
		searchedConditions.put("endDate", endDate);
		searchedConditions.put("pageSize",
				!bsimpleTable ? params.get("pagesize") : "10");
		searchedConditions.put("currentPage",
				context.getHttpRequest() != null ? context.getHttpRequest()
						.getParameter("cur_page") : null);

		DPageService dPageService = VWBContext.getContainer().getDpageService();
		CurrentPageResultSet pageSet = dPageService.searchSubDpages(getSite()
				.getId(), iResourceId, searchedConditions);
		StringBuffer html = new StringBuffer();
		String contextPath = context.getHttpRequest().getContextPath();
		if (!bsimpleTable) {
			html.append("<div>");
			// title
			if ((styleClass == null) || ("".endsWith(styleClass.trim()))) {
				html.append("<table width=100%>");
			} else {
				html.append("<table class=\"" + styleClass + "\">");
			}
			if (bHasTitle) {
				html.append("<tr><td width=3%><img hspace=\"2\" border=\"0\" src=\""
						+ contextPath + "/images/dot_files.gif\"/></td>");
				html.append(" <td align=\"left\"><b><font color=\"#2292dd\">"
						+ StringUtil.normalizeString(dPageService
								.getLatestDpageByResourceId(getSite().getId(),
										iResourceId).getTitle())
						+ "</font></b></td>");
				html.append("<td align=right><a href=\"");
				html.append(context.getViewURL(iResourceId));
				html.append("\">"
						+ context.getBundle(
								AbstractDPagePlugin.PLUGINS_RESOURCEBUNDLE)
								.getString("subdpagefilterplugin.more")
						+ "</a></td>");
				html.append("</tr>");
				html.append("<tr> <td></td><td colspan=\"2\" align=\"right\"> <hr></td>  </tr>");
			}
		} else {
			html.append("<div class=\"DCT_liebiao\"><b><font color=\"#2292dd\">&nbsp;"
					+ getKey(context, "plugin.simple.subpage")
					+ "</font></b><br><div  class=\"DCT_bar\">");

		}
		// end title
		// content
		List<DPage> dpages = (List<DPage>) pageSet.getData();
		if (!bsimpleTable) {
			html.append("<tr>");
			html.append("<td colspan=3><ul class=\"pop_ul\">");
			for (int i = 0; i < dpages.size(); i++) {
				DPage dpage = dpages.get(i);
				// TODO 资源连续拼写
				String href = context.getViewURL(dpage.getResourceId());
				String title = StringUtil.normalizeString(dpage.getTitle());
				String tip = title != null && title.length() > iShowChars ? title
						.substring(0, iShowChars) + "..."
						: title;
				html.append("<li><a href=" + href + " title=" + title + ">"
						+ tip + "</a>");
				html.append("&nbsp;&nbsp;(")
						.append(onlyDateFormat.format(dpage.getTime()))
						.append(")");
				html.append("</li>");

			}
			if (bNextPage) {
				html.append("<tr> <td></td><td colspan=\"3\" align=\"right\"> <hr></td>  </tr>");
				html.append(
						"<tr> <td colspan=\"3\" align=\"right\" width=\"100%\">")
						.append(pageSet.getToolBar(context.getViewURL(iResourceId))
								+ " </td>  </tr>");
			}
			html.append("</table>");
			html.append("</div>");
		} else {
			for (int i = 0; i < dpages.size(); i++) {
				DPage dpage = dpages.get(i);
				html.append("<ul><li>");
				String href = context.getURL(VWBContext.VIEW,
						dpage.getResourceId());
				String title = StringUtil.normalizeString(dpage.getTitle());
				html.append("<a class='wikipage' href='" + href + "'>" + title
						+ "</a>");
				html.append("</li></ul>");
			}
			html.append("</div>");
			html.append("</div>");
		}

		return html.toString();
	}

	private String getKey(VWBContext context, String key) {
		ResourceBundle rb = context
				.getBundle(AbstractDPagePlugin.PLUGINS_RESOURCEBUNDLE);
		return rb.getString(key);
	}

	public static Date getLastDate(long day) {
		Date currentDate = new Date();
		long lastDate = currentDate.getTime() - 3600000 * 24 * day;
		// Date date_3_hm_date = new Date(lastDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(lastDate);
		return calendar.getTime();
	}

}
