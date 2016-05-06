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

package cn.vlabs.duckling.vwb.service.emailnotifier.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.url.UrlService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * @date 2013-5-28
 * @author xiejj
 */
public class ChangeList {
	private static final String EMAIL_SUBSCRIBER_LIST = "<table border=1 width=600 bgcolor=#ffe4e4>"
			+ "<tr><th width='50%'>页面名(标题)</th><th width='35%'>更新人</th><th width='15%'>版本变化</th></tr>"
			+ "CHANGED_PAGE_LIST</table>";

	private static final String m_pageListItemTemplate = "<tr><td><a href='CHANGED_PAGE_URL' target='_blank'>"
			+ "<font size=2>CHANGED_PAGE_TITLE</font></a></td>"
			+ "<td width=100><font size=2>CHANGED_PAGE_AUTHORS</font></a></td>"
			+ "<td width=200><a href='PAGE_DIFF_URL' target='_blank'>"
			+ "<font size=2>查看版本变化</font></a></td></tr>";

	private static final String MARKER_CHANGED_PAGE_AUTHOR = "CHANGED_PAGE_AUTHORS";

	private static final String MARKER_CHANGED_PAGE_LIST = "CHANGED_PAGE_LIST";

	private static final String MARKER_CHANGED_PAGE_NAME = "CHANGED_PAGE_NAME";

	private static final String MARKER_CHANGED_PAGE_NAME_TITLE = "CHANGED_PAGE_TITLE";

	private static final String MARKER_CHANGED_PAGE_URL = "CHANGED_PAGE_URL";
	private static final String MARKER_PAGE_DIFF_URL = "PAGE_DIFF_URL";

	private int m_pageId;
	private List<LightDPage> m_versionList;

	private UrlService urlConstructor;
	private ViewPortService viewport;
	String m_subscribedPagesList = null;

	public ChangeList() {
		m_subscribedPagesList = "";
	}

	private String buildChangeInfo(List<LightDPage> pageVersions) {
		StringBuffer buffer = new StringBuffer();
		HashMap<String, String> authors = new HashMap<String, String>();

		for (Iterator<LightDPage> iter = pageVersions.iterator(); iter
				.hasNext();) {
			LightDPage page = iter.next();
			String author = page.getAuthor();

			if (author == null) // ignore null authors...
				continue;

			if (authors.get(author) == null)
				authors.put(author, author);
		}
		for (String value : authors.values()) {
			if (buffer.length() > 0)
				buffer.append(", ");
			buffer.append(value);
		}
		return buffer.toString();
	}

	private String buildDiffURL(List<Integer> vlist, int siteId, int pageid) {

		Integer max = (Integer) vlist.get(0);
		Integer min = (Integer) vlist.get(0);
		for (int i = 0; i < vlist.size(); i++) {
			if (max < (Integer) vlist.get(i))
				max = (Integer) vlist.get(i);
			if (min > (Integer) vlist.get(i))
				min = (Integer) vlist.get(i);
		}
		String baseurl = urlConstructor.getBaseURL();
		if (!baseurl.endsWith("/")) {
			baseurl = baseurl + "/";
		}
		return urlConstructor.getURL(VWBContext.DIFF, Integer.toString(pageid), "r1="
				+ max + "&r2=" + (min == 1 ? min : min - 1));
	}

	public void buildChangeList(int siteId) {

		String pageTitle = viewport.getViewPort(siteId, m_pageId).getTitle();

		String listItem = "";

		List<Integer> vlist = new ArrayList<Integer>();
		for (Iterator<LightDPage> iter = m_versionList.iterator(); iter
				.hasNext();) {
			LightDPage page = iter.next();
			vlist.add(page.getVersion());
		}

		if ((vlist == null) || (vlist.size() == 0))
			return;
		listItem = m_pageListItemTemplate.replaceAll(
				MARKER_CHANGED_PAGE_NAME_TITLE, pageTitle + "(" + pageTitle
						+ ")");
		listItem = listItem.replaceAll(MARKER_CHANGED_PAGE_NAME,
				Integer.toString(m_pageId));
		listItem = listItem.replaceAll(MARKER_CHANGED_PAGE_URL,
				urlConstructor.getViewURL(m_pageId));

		listItem = listItem.replaceAll(MARKER_CHANGED_PAGE_AUTHOR,
				buildChangeInfo(m_versionList));

		listItem = listItem.replaceAll(MARKER_PAGE_DIFF_URL,
				buildDiffURL(vlist, siteId, m_pageId));
		m_subscribedPagesList += listItem;

	}

	public void currentChangeList(int pageId, List<LightDPage> versionList) {
		this.m_pageId = pageId;
		this.m_versionList = versionList;
	}

	public String getHTML() {
		String changePageList = EMAIL_SUBSCRIBER_LIST.replace(
				MARKER_CHANGED_PAGE_LIST, m_subscribedPagesList);
		return changePageList;
	}

	public void setUrlService(UrlService urlService) {
		this.urlConstructor = urlService;
	}

	public void setViewportService(ViewPortService viewport) {
		this.viewport = viewport;
	}
}
