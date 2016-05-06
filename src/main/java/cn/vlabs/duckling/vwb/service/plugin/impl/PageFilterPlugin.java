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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.vwb.VWBContext;
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
public class PageFilterPlugin  extends AbstractDPagePlugin {
	private static final String CURRENT_USER = "current";
	private static final DateFormat dateFormate = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	private DPageService dpageService;
	
	public String execute(VWBContext context, Map<String,String> params) throws PluginException{
		Map<String, Object> searchedConditions = new HashMap<String, Object>();
		searchedConditions.put("prefix", params.get("prefix"));
		searchedConditions.put("suffix", params.get("suffix"));
		String beginDateStr = (String) params.get("begindate");
		String endDateStr = (String) params.get("enddate");
		String recentDays = (String) params.get("recentDays");
		String classtype = (String) params.get("class"); // ul的lass类型，默认为news
		if (classtype == null)
			classtype = "news";
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
			throw new PluginException("The format of plugin date is incorrect!",e);
		}
		// 如果当前的user设置为current ，默认为当前查询用户
		if (CURRENT_USER.equals(params.get("user"))) {
			searchedConditions.put("user", context.getCurrentUser().getName());
		} else {
			searchedConditions.put("user", params.get("user"));
		}
		if (params.get("count") != null) {
			searchedConditions.put("count", Integer.valueOf((String) params
					.get("count")));
		}
		searchedConditions.put("beginDate", beginDate);
		searchedConditions.put("endDate", endDate);
	    dpageService = VWBContext.getContainer().getDpageService();			
		List<DPage> pages = dpageService.searchPages(context.getSite().getId(),searchedConditions);  /**获取查询的页面**/
		StringBuffer html = new StringBuffer();
		if (pages != null && pages.size() > 0) {

			html.append("<ul class=").append(classtype).append(">");

			for (Iterator<DPage> i = pages.iterator(); i.hasNext();) {
				DPage page =  i.next();
				String pageUrl = context.getURL(VWBContext.VIEW, page.getResourceId());		
				html.append("<li>");
				html.append("<a href =").append(pageUrl).append(">");
				html.append(page.getTitle());
				html.append("</a>");
				html.append("</li>");
			}
			html.append("</ul>");
		}
		return html.toString();
	}

	public static Date getLastDate(long day) {
		Date currentDate = new Date();
		long lastDate = currentDate.getTime() - 3600000 * 24 * day;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(lastDate);
		return calendar.getTime();
	}

}
