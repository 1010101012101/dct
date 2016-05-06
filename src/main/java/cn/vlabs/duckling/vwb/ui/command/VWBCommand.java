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

import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.VWBPermission;

/**
 * 全局命令
 * 
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public class VWBCommand extends AbstractCommand {
	public static final VWBCommand NONE = new VWBCommand("none", null, null,
			"/PortletContent.jsp", "%u/%n");
	public static final VWBCommand PLAIN = new VWBCommand("plain", null, null, "", "%u/%n");
	
	public static final VWBCommand FIND = new VWBCommand("find", null, 
			VWBPermission.SEARCH,  "/PortletContent.jsp", "%u/%v/%n");

	public static final VWBCommand ERROR = new VWBCommand("error", null, null,
			"/PortletContent.jsp", "%u/%v/%n");

	public static final VWBCommand ATTACH = new VWBCommand("attach", null,
			VWBPermission.CREATE_PAGES, "", "%u/%v/%n");

	public static final VWBCommand EDIT_PROFILE = new VWBCommand("editprofile", null,
			VWBPermission.EDIT_PROFILE, "/ConfigContent.jsp", "%u/%v/%n");
	
	public static final VWBCommand EDIT_PREFERENCE = new VWBCommand("editPreference", null,
			VWBPermission.EDIT_PREFERENCE, "/ConfigContent.jsp", "%u/%v/%n");
	
	public static final VWBCommand ADMIN = new VWBCommand("admin", null,
			AllPermission.ALL, "/ConfigContent.jsp", "%u/%v/%n");

	public static final VWBCommand LOGIN = new VWBCommand("login", null, 
			VWBPermission.LOGIN,"", "%U/login");
	
	public static final VWBCommand LOGOUT = new VWBCommand("logout", null, 
			VWBPermission.LOGOUT,"", "%U/logout");
	
	public static final VWBCommand CREATE_RESOURCE = new VWBCommand(
			"createresource", null, VWBPermission.CREATE_PAGES, "", "%u/%v/%n");

	/**
	 * @param action
	 * @param target
	 * @param permission
	 *            constructors
	 */
	public VWBCommand(String action, Object target, Permission permission,
			String contentJsp, String urlPattern) {
		super(action, target, permission, contentJsp, urlPattern);
	}

	public Command targetCommand(Object target) {
		return new VWBCommand(getAction(), target,
				this.getRequiredPermission(), getContentJSP(), getURLPattern());
	}
}
