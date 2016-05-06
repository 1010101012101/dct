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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ecs.xhtml.a;
import org.apache.ecs.xhtml.b;
import org.apache.ecs.xhtml.img;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.tr;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.util.Utility;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.ui.UserNameUtil;

/**
 * Introduction Here.
 * 
 * @date 2010-3-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class RecentChangesPlugin extends AbstractDPagePlugin {
	/** How many days we show by default. */
	private static final int DEFAULT_DAYS = 100 * 365;
	private static Logger log = Logger.getLogger(RecentChangesPlugin.class);
	private static final String PARAM_DATE_FORMAT = "dateFormat";

	private static final String PARAM_FORMAT = "format";

	private static final String PARAM_TIME_FORMAT = "timeFormat";

	private String get(Map<String, String> params, String defaultValue,
			String paramName) {
		String value = (String) params.get(paramName);
		return null == value ? defaultValue : value;
	}

	private DateFormat getDateFormat(Map<String, String> params) {
		String formatString = get(params, "yyyy-MM-dd", PARAM_DATE_FORMAT);

		if ("".equals(formatString.trim()))
			return SimpleDateFormat.getDateInstance();

		return new SimpleDateFormat(formatString);

	}

	// TODO: Ideally the default behavior should be to return the default format
	// for the default
	// locale, but that is at odds with the 1st version of this plugin. We seek
	// to preserve the
	// behaviour of that first version, so to get the default format, the user
	// must explicitly do
	// something like: dateFormat='' timeformat='' which is a odd, but probably
	// okay.
	private DateFormat getTimeFormat(Map<String, String> params) {
		String formatString = get(params, "HH:mm:ss", PARAM_TIME_FORMAT);

		if ("".equals(formatString.trim()))
			return SimpleDateFormat.getTimeInstance();

		return new SimpleDateFormat(formatString);
	}

	private boolean isSameDay(Date a, Date b) {
		Calendar aa = Calendar.getInstance();
		aa.setTime(a);
		Calendar bb = Calendar.getInstance();
		bb.setTime(b);

		return aa.get(Calendar.YEAR) == bb.get(Calendar.YEAR)
				&& aa.get(Calendar.DAY_OF_YEAR) == bb.get(Calendar.DAY_OF_YEAR);
	}

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {
		int since = TextUtil.parseIntParameter((String) params.get("since"),
				DEFAULT_DAYS);
		int spacing = 4;
		boolean showAuthor = true;
		int tablewidth = 4;

		//
		// Which format we want to see?
		//
		if ("compact".equals(params.get(PARAM_FORMAT))) {
			spacing = 0;
			showAuthor = false;
			tablewidth = 2;
		}

		Calendar sincedate = new GregorianCalendar();
		sincedate.add(Calendar.DAY_OF_MONTH, -since);

		log.debug("Calculating recent changes from " + sincedate.getTime());

		Date end = Calendar.getInstance().getTime();
		List<LightDPage> changes = VWBContext.getContainer().getDpageService()
				.getDpagesSinceDate(getSite().getId(), sincedate.getTime());

		AttachmentService jdbcCLBService = VWBContext.getContainer()
				.getAttachmentService();
		int siteId = getSite().getId();
		List<CLBAttachment> attachments = jdbcCLBService
				.getCLBAttatchmentChanged(siteId, sincedate.getTime(), end);
		if (attachments != null)
			changes.addAll(attachments);
		Collections.sort(changes, new Comparator<LightDPage>() {
			public int compare(LightDPage o1, LightDPage o2) {
				if (o1 == null || o2 == null)
					return 0;

				LightDPage page1 = (LightDPage) o1;
				LightDPage page2 = (LightDPage) o2;

				return page1.getTime().before(page2.getTime()) ? 1 : -1;
			}
		});
		String contextPath = context.getHttpRequest().getContextPath();

		if (changes != null) {
			Date olddate = new Date(0);

			DateFormat fmt = getDateFormat(params);
			DateFormat tfmt = getTimeFormat(params);

			table rt = new table();
			rt.setWidth("90%");
			rt.setCellPadding(spacing).setClass("recentchanges");

			for (Iterator<LightDPage> i = changes.iterator(); i.hasNext();) {
				LightDPage page = i.next();

				Date lastmod = page.getTime();

				if (!isSameDay(lastmod, olddate)) {

					tr row = new tr();
					td col = new td();
					col.setColSpan(tablewidth).setClass("date");
					col.addElement(new b().addElement(fmt.format(lastmod)));
					rt.addElement(row);
					row.addElement(col);
					olddate = lastmod;
				}

				String link = context.getURL(VWBContext.VIEW,
						page.getResourceId());

				a linkel = null;
				if (page instanceof CLBAttachment) {
					CLBAttachment clbpage = (CLBAttachment) page;
					String str = clbpage.getFileName();
					String hashid = "";
					if (str.lastIndexOf(".") != -1)
						hashid = "clb:clb:"
								+ str.substring(str.lastIndexOf(".") + 1) + ":"
								+ clbpage.getClbId();
					else
						hashid = "clb:clb::" + clbpage.getClbId();

					String hashName = Utility.getBASE64(hashid);
					DPage parent = VWBContext
							.getContainer()
							.getDpageService()
							.getLatestDpageByResourceId(
									context.getSite().getId(),
									Integer.parseInt(clbpage.getParentName()));
					if (parent != null)
						linkel = new a(link, parent.getTitle() + "/"
								+ clbpage.getFileName()).setHref(contextPath
								+ "/attach/" + hashName);
					else {
						log.debug("CLB Attachment " + clbpage.getFileName()
								+ "" + clbpage.getClbId());
						linkel = new a(link, clbpage.getFileName())
								.setHref(contextPath + "/attach/" + hashName);
					}
				} else {
					linkel = new a(link, page.getTitle());
				}

				tr row = new tr();

				td col = new td().setWidth("50%").addElement(linkel);
				//
				// Add the direct link to the attachment info.
				//
				if (page instanceof CLBAttachment) {
					linkel = new a();
					linkel.addElement(new img().setSrc(contextPath
							+ "/images/attachment_small.png"));
					col.addElement(linkel);
				}

				row.addElement(col);
				rt.addElement(row);

				td infocol = (td) new td().setClass("lastchange");
				infocol.addElement(new a(
						page.getResourceId() + "?a=diff&r1=-1", tfmt
								.format(lastmod)));
				row.addElement(infocol);

				//
				// Display author information.
				//

				if (showAuthor) {
					String author = page.getAuthor();

					td authorinfo = new td();
					authorinfo.setClass("author");

					if (author != null) {

						authorinfo.addElement(UserNameUtil.getAuthorTip(
								context, page.getAuthor()));
					} else {
						authorinfo.addElement("unknown");
					}

					row.addElement(authorinfo);
				}
			}

			rt.setPrettyPrint(true);
			return rt.toString();
		}

		return "";
	}
}
