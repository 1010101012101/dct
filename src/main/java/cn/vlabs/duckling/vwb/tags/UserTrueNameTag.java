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
import java.security.Principal;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;
import cn.vlabs.duckling.vwb.service.myspace.MySpaceService;
import cn.vlabs.duckling.vwb.ui.UserNameUtil;

/**
 * Introduction Here.
 * 
 * @date Mar 6, 2010
 * @author xiejj@cnic.cn
 */
public class UserTrueNameTag extends VWBBaseTag {
	private static final long serialVersionUID = 0L;

	public final int doVWBStart() throws IOException {
		Principal user = vwbcontext.getVWBSession().getCurrentUser();

		if (user != null) {
			String name = "";
			if (user instanceof UserPrincipal) {
				name = ((UserPrincipal) user).getFullName();
				if ((name == null) || (name.equals("")))
					name = user.getName();
			} else
				name = user.getName();
			MySpaceService sm =VWBContext.getContainer().getMySpaceService();
			MySpace mySpace = sm.getMySpace(vwbcontext.getSiteId(),user.getName());
			String link = "";
			if (mySpace != null) {
				link = UserNameUtil.showMySpaceLink(
						TextUtil.replaceEntities(name),
						mySpace.getResourceId(), vwbcontext);
			} else {
				link = UserNameUtil.createMySpaceLink(
						TextUtil.replaceEntities(name), user.getName(),
						vwbcontext);
			}
			pageContext.getOut().print(link);
		}

		return SKIP_BODY;
	}
}
