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

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.ui.UserNameUtil;

/**
 * Introduction Here.
 * 
 * @date 2010-3-16
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SessionsPlugin extends AbstractDPagePlugin {
	public static final String PARAM_PROP = "property";

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {

		String prop = (String) params.get(PARAM_PROP);

		Collection<Principal> principals = VWBContext.getContainer().getAuthenticationService().getOnlineUsers(context.getSiteId());
		if ("users".equals(prop)) {
			return displayUser(context, principals);
		} else if ("allusers".equals(prop)) {
			StringBuffer s = new StringBuffer();
			for (Principal prin:principals) {
				UserPrincipal p = (UserPrincipal)prin;
				if (!UserPrincipal.GUEST.equals(p)) {
					s.append(UserNameUtil.getAuthorTip(context,
							p.getFullName() + "(" + p.getName() + ")")
							+ ", ");
				}

			}
			// remove the last comma and blank :
			return s.substring(0, s.length() - (s.length() > 2 ? 2 : 0));
		}

		return null;
	}

	private String displayUser(VWBContext context, Collection<Principal> principals) {

		Set<String> distinctPrincipals = new HashSet<String>();
		for (Principal prin:principals) {
			if (prin instanceof UserPrincipal) {
				UserPrincipal p = (UserPrincipal)prin;
				String userfullname = p.getFullName() + "(" + p.getName() + ")";
				if (!distinctPrincipals.contains(userfullname)) {
					distinctPrincipals.add(userfullname);
				}
			}
		}
		//
		//
		StringBuffer s = new StringBuffer();
		String title = getKey(context, "sessionsPlugin.title");
		s.append(title + "<br>");
		for (String author : distinctPrincipals) {
			s.append(UserNameUtil.getAuthorTip(context,author) + ", ");
		}
		// remove the last comma and blank :
		return s.substring(0, s.length() - 2);
	}

	private String getKey(VWBContext context, String key) {
		ResourceBundle rb = context
				.getBundle(AbstractDPagePlugin.PLUGINS_RESOURCEBUNDLE);
		return rb.getString(key);
	}
}
