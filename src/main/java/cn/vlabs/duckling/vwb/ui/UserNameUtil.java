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

package cn.vlabs.duckling.vwb.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;

/**
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class UserNameUtil {
	public static String getAuthorTip(VWBContext context, String username) {
		String user = "";
		String[] names = parseUserName(username);
		if (names == null || names.length == 0)
			return "";
		MySpace main = VWBContainerImpl.findContainer().getMySpaceService().getMySpace(context.getSiteId(),
				names[0]);
		String link = null;
		if (main == null) {
			link = context.getURL(VWBContext.VIEW, "5020", "user=" + names[0]);
		} else {
			link = context.getViewURL(main.getResourceId());
		}

		user = "<span class=\"mobilepopup\" onmouseover=\"javascript:showPopup('";
		if (names.length > 1)
			user += link
					+ "', '"
					+ names[0]
					+ "', this)\" onmouseout=\"javascript:hidePopup()\"><a href=\"#\">"
					+ names[1] + "</a></span>";
		else
			user += link
					+ "', '"
					+ names[0]
					+ "', this)\" onmouseout=\"javascript:hidePopup()\"><a href=\"#\">"
					+ names[0] + "</a></span>";

		return user;
	}

	/**
	 * 从用户的名字中解析出TrueName和Name
	 */
	public static String[] parseUserName(String username) {
		String[] name = null;
		if (username != null) {
			if (username.indexOf("(") != -1) {
				name = new String[2];
				name[1] = username.substring(0, username.indexOf("("));
				name[0] = username.substring(username.indexOf("(") + 1,
						username.length() - 1);
			} else {
				name = new String[1];
				name[0] = username;
			}
		}
		return name;
	}

	public static String createMySpaceLink(String userName, String email,
			VWBContext context) {

		return "<a href=\""
				+ context.getURL(VWBContext.VIEW, "5020", "user=" + email)
				+ "\">" + userName + "</a>";// ;
	}

	public static String showMySpaceLink(String userName, int resourceId,
			VWBContext context) {
		return "<a href=\""
				+ context.getURL(VWBContext.VIEW, String.valueOf(resourceId),
						"") + "\">" + userName + "</a>";// ";
	}

	public static boolean verifyEmail(String emailaddress) {
		if (emailaddress == null)
			return false;
		Pattern pattern = Pattern
				.compile("\\w+(\\.[\\w-]+)*@(\\w+\\.)+[a-z]{2,3}");
		Matcher matcher = pattern.matcher(emailaddress);
		return matcher.matches();
	}
}
