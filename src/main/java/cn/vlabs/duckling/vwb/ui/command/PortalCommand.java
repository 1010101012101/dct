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
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class PortalCommand extends AbstractCommand {
	public static final PortalCommand VIEW=new PortalCommand("portlet", null, PagePermission.VIEW, "/PortletContent.jsp", "%u/%v/%n");
	public static final PortalCommand CONFIG=new PortalCommand("portlet_config", null, PagePermission.DELETE, "/PortletContent.jsp", "%u/%v/%n");
	public static final PortalCommand ADMIN=new PortalCommand("portlet_admin", null, PagePermission.DELETE, "/ConfigContent.jsp", "%u/%v/%n");
	public static final PortalCommand SIMPLE=new PortalCommand("simple", null, PagePermission.VIEW, "/PortletContent.jsp", "%U/portal/%n");
	public PortalCommand(String action, Object target, Permission permission,
			String contentJsp, String urlpattern) {
		super(action, target, permission, contentJsp, urlpattern);
	}
	
	public Command targetCommand(Object target) {
		Permission permission = null;
		if (getRequiredPermission() != null) {
			Resource resource = (Resource) target;
			permission = PermissionFactory.getPagePermission(resource,
					getRequiredPermission().getActions());
		}
		return new PortalCommand(getAction(), target, permission,getContentJSP(), getURLPattern());
	}

}
