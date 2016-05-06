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
import java.security.Permission;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.auth.permissions.VWBPermission;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * 
 * @date Mar 1, 2010
 * @author xiejj@cnic.cn
 */
public class PermissionTag extends VWBBaseTag {
	private static final String ALL_PERMISSION = "allPermission";
	private static final String EDIT = "edit";

	private static final long serialVersionUID = 3761412993048982325L;

	private String[] m_permissionList;

	/**
	 * Initializes the tag.
	 */
	public void initTag() {
		super.initTag();
		m_permissionList = null;
	}

	/**
	 * Sets the permissions to look for (case sensitive). See above for the
	 * format.
	 * 
	 * @param permission
	 *            A list of permissions
	 */
	public void setPermission(String permission) {
		m_permissionList = StringUtils.split(permission, '|');
	}

	/**
	 * Checks a single permission.
	 * 
	 * @param permission
	 * @return
	 */
	private boolean checkPermission(String permission) {
		Resource resource = vwbcontext.getResource();
		boolean gotPermission = false;
		if (VWBPermission.isVWBPermission(permission)) {
			gotPermission = vwbcontext.checkPermission(new VWBPermission(
					permission));
		} else if (ALL_PERMISSION.equals(permission)) {
			gotPermission = vwbcontext.checkPermission(new AllPermission("*"));
		} else if (resource != null) {
			if (EDIT.equals(permission)) {
				DPage latest = (DPage) VWBContext.getContainer()
						.getResourceService()
						.getResource(vwbcontext.getSiteId(), resource.getResourceId());
				DPage page = (DPage) resource;

				if (page.getVersion() != Constant.DPAGE_LATEST_VERSION
						&& latest.getVersion() != page.getVersion()) {
					return false;
				}
			}

			Permission p = PermissionFactory.getPagePermission(resource,
					permission);
			gotPermission = vwbcontext.checkPermission(p);
		}

		return gotPermission;
	}

	/**
	 * Initializes the tag.
	 * 
	 * @return the result of the tag: SKIP_BODY or EVAL_BODY_CONTINUE
	 * @throws IOException
	 *             this exception will never be thrown
	 */
	public final int doVWBStart() throws IOException {
		for (int i = 0; i < m_permissionList.length; i++) {
			String perm = m_permissionList[i];

			boolean hasPermission = false;

			if (perm.charAt(0) == '!') {
				hasPermission = !checkPermission(perm.substring(1));
			} else {
				hasPermission = checkPermission(perm);
			}

			if (hasPermission)
				return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}
}
