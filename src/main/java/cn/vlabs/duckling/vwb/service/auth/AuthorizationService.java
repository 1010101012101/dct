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

package cn.vlabs.duckling.vwb.service.auth;

import java.security.Permission;

import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;

/**
 * 站点授权控制服务
 * 
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface AuthorizationService {
	/**
	 * 检查用户权限
	 * 
	 * @param session
	 * @param permission
	 * @return
	 */
	boolean checkPermission(int siteId, VWBSession session,
			Permission permission);

	void apply(int siteId, Acl policy);

	Acl getPolicy(int siteId);

	void updateViewPortAcl(int siteId, int resourceId, Acl acl)
			throws ViewPortNotExistException;
	Acl getViewPortAcl(int siteId, int resourceId);
}
