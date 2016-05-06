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

package cn.vlabs.duckling.vwb.service.auth.impl;

import java.security.Permission;
import java.security.Principal;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public class AuthorizationServiceImpl implements AuthorizationService {

	private static Logger log = Logger
			.getLogger(AuthorizationServiceImpl.class);
	private static final String POLICY_KEY = "p";
	private VWBCacheService cache;
	private ACLProvider m_acls;
	private ViewPortService m_viewports;

	private PolicyProvider policys;

	public Acl getViewPortAcl(int siteId, int resourceId) {
		return m_acls.getAcl(siteId, resourceId);
	}

	public void setAclProvider(ACLProvider acls) {
		this.m_acls = acls;
	}

	public void updateViewPortAcl(int siteId, int resourceId, Acl acl)
			throws ViewPortNotExistException {
		ViewPort vp = m_viewports.getViewPort(siteId, resourceId);
		if (acl == null || acl.isEmpty()) {
			m_acls.removeAcl(siteId, resourceId);
			if (!vp.isAclInherit()) {
				vp.setAclPolicy(ViewPort.INHERIT);
				m_viewports.updateViewPort(siteId, vp);
			}
		} else {
			m_acls.updateAcl(siteId, resourceId, acl);
			if (vp.isAclInherit()) {
				vp.setAclPolicy(ViewPort.ENABLED);
				m_viewports.updateViewPort(siteId, vp);
			}
		}
	}

	private boolean checkAcl(Acl acl, VWBSession session, Permission permission) {
		Principal[] principals = acl.findPrincipals(permission);
		for (Principal principal : principals) {
			if (session.hasPrincipal(principal))
				return true;
		}
		return false;
	}

	private boolean checkPageAcl(int siteId, VWBSession session,
			Permission permission) {
		if (permission.getName() == null)
			return true;

		Acl pageacl = null;
		try {
			int resourceId = Integer.parseInt(permission.getName());
			int mappedid = m_viewports.getMappedAclId(siteId, resourceId);
			pageacl = getViewPortAcl(siteId, mappedid);
			if (pageacl == null) {
				return true;
			}
			Permission upcast = PermissionFactory.createPermission(mappedid,
					Resource.TYPE_DPAGE, permission.getActions());
			if (checkAcl(pageacl, session, upcast)) {
				return true;
			} else {
				log.debug("User " + session.getCurrentUser().getName()
						+ " has no access. " + permission.toString());
				log.debug("Page ACL is " + pageacl.toString());
				log.debug("Current User Principals:\n"
						+ toString(session.getPrincipals()));
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}

	}

	private boolean checkPolicy(int siteId, VWBSession session,
			Permission permission) {
		Acl policy = getPolicy(siteId);
		if (policy == null)
			return false;
		return checkAcl(policy, session, permission);
	}

	private String toString(Principal[] prins) {
		if (prins != null) {
			StringBuffer prinString = new StringBuffer();
			for (Principal principal : prins) {
				prinString.append(principal.toString() + "\n");
			}
			return prinString.toString();
		}
		return "";
	}

	public void apply(int siteId, Acl policy) {
		policys.setAcl(siteId, policy);
		cache.putInCache(siteId, POLICY_KEY, policy);
	}

	public boolean checkPermission(int siteId, VWBSession session,
			Permission permission) {
		if (permission == null)// No require Permission
			return true;

		if (session == null)// No Session created
			return false;

		if (checkPolicy(siteId, session, AllPermission.ALL)) {
			return true;
		}

		if (checkPolicy(siteId, session, permission)) {
			if (permission instanceof PagePermission) {
				return checkPageAcl(siteId, session, permission);
			} else
				return true;
		} else {
			log.debug("User " + session.getCurrentUser().getName()
					+ " has no access. " + permission.toString());
			log.debug("Current User Principals:\n"
					+ toString(session.getPrincipals()));
			return false;
		}
	}

	@Override
	public Acl getPolicy(int siteId) {
		Acl acl = (Acl) cache.getFromCache(siteId, POLICY_KEY);
		if (acl == null) {
			acl = policys.getAcl(siteId);
		}
		return acl;
	}

	public void setCacheService(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("athor");
	}

	public void setPolicyProvider(PolicyProvider policyProvider) {
		this.policys = policyProvider;
	}

	public void setViewPortService(ViewPortService viewportService) {
		this.m_viewports = viewportService;
	}
}
