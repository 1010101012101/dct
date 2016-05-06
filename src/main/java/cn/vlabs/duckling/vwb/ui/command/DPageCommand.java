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

package cn.vlabs.duckling.vwb.ui.command;

import java.security.Permission;

import cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 * Introduction Here.
 * 
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
public class DPageCommand extends AbstractCommand {
	public static final DPageCommand VIEW = new DPageCommand("view", null,
			PagePermission.VIEW, "/PageContent.jsp", "%u/%v/%n");

	public static final DPageCommand DIFF = new DPageCommand("diff", null,
			PagePermission.VIEW, "/InfoContent.jsp", "%u/%v/%n?a=diff");

	public static final DPageCommand INFO = new DPageCommand("info", null,
			PagePermission.VIEW, "/InfoContent.jsp", "%u/%v/%n?a=info");
	
	public static final DPageCommand EDIT_INFO = new DPageCommand("info", null,
			PagePermission.EDIT, "/InfoContent.jsp", "%u/%v/%n?a=info");
	
	public static final DPageCommand SHARE = new DPageCommand("share", null,
			PagePermission.EDIT, "/InfoContent.jsp", "%u/%v/%n?a=share");
	
	public static final DPageCommand SHARECONTENT = new DPageCommand("share", null,
			PagePermission.VIEW, "/PageContent.jsp", "%u/%v/%n?a=share");
	
	public static final DPageCommand PREVIEW = new DPageCommand("preview",
			null, PagePermission.EDIT, "/PortletContent.jsp",
			"%u/%v/%n?a=preview");

	public static final DPageCommand CONFLICT = new DPageCommand("conflict",
			null, PagePermission.EDIT, "/ConflictCotnent.jsp",
			"%u/%v/%n?a=conflict");

	public static final DPageCommand EDIT = new DPageCommand("edit", null,
			PagePermission.EDIT, "EditContent.jsp", "%u/%v/%n?a=edit");

	public static final DPageCommand DELETE = new DPageCommand("delete", null,
			PagePermission.DELETE, "InfoContent.jsp", "%u/%v/%n?a=del");

	public DPageCommand(String action, DPage target, Permission permission,
			String contentJsp, String urlPattern) {
		super(action, target, permission, contentJsp, urlPattern);
	}

	public Command targetCommand(Object target) {
		Permission permission;
		if (getRequiredPermission() != null) {
			DPage page = (DPage) target;
			permission = PermissionFactory.getPagePermission(page,
					getRequiredPermission().getActions());
		} else {
			permission = null;
		}
		return new DPageCommand(getAction(), (DPage) target, permission,
				getContentJSP(), getURLPattern());
	}
}
