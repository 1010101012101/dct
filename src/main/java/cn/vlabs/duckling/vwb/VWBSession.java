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
package cn.vlabs.duckling.vwb;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.login.UnkownCredentialException;
import cn.vlabs.vwb.driver.internal.SiteSessionImpl;
import cn.vlabs.vwb.driver.services.container.SiteEnviromentServiceImpl;

public class VWBSession {
	private static Subject DEFAULT_SUBJECT;
	public static final String SITE_SESSION_KEY = "cn.vlabs.vwb.SiteSession";
	private static final int DOT = 46;

	private static final int NINE = 57;

	private static final int ONE = 48;

	private static final String SESSION_KEY = "vwbsession";
	/** An anonymous user's session status. */
	public static final String ANONYMOUS = "anonymous";
	/** An authenticated user's session status. */
	public static final String AUTHENTICATED = "authenticated";

	static {
		DEFAULT_SUBJECT = new Subject(UserPrincipal.GUEST);
		DEFAULT_SUBJECT.getPrincipals().add(UserPrincipal.GUEST);
		DEFAULT_SUBJECT.getPrincipals().add(Role.ALL);
		DEFAULT_SUBJECT.getPrincipals().add(Role.ANONYMOUS);
	}
	private static void initSiteSession(HttpServletRequest request,
			VWBSession vwbsession) {
		vwbsession.siteSession = SiteEnviromentServiceImpl
				.getSiteSession(request);
	}
	
	public void setSiteSession(SiteSessionImpl siteSession){
		this.siteSession=siteSession;
	}
	

	protected static final boolean isIPV4Address(String name) {
		if (name.charAt(0) == DOT || name.charAt(name.length() - 1) == DOT) {
			return false;
		}

		int[] addr = new int[] { 0, 0, 0, 0 };
		int currentOctet = 0;
		for (int i = 0; i < name.length(); i++) {
			int ch = name.charAt(i);
			boolean isDigit = ch >= ONE && ch <= NINE;
			boolean isDot = ch == DOT;
			if (!isDigit && !isDot) {
				return false;
			}
			if (isDigit) {
				addr[currentOctet] = 10 * addr[currentOctet] + (ch - ONE);
				if (addr[currentOctet] > 255) {
					return false;
				}
			} else if (name.charAt(i - 1) == DOT) {
				return false;
			} else {
				currentOctet++;
			}
		}
		return currentOctet == 3;
	}

	public static VWBSession findSession(HttpServletRequest request) {
		if (request == null)
			return null;
		HttpSession session = request.getSession();
		VWBSession vwbsession = (VWBSession) session.getAttribute(SESSION_KEY);
		if (vwbsession == null) {
			return refreshSession(request);
		}
		return vwbsession;
	}
	
	public static VWBSession refreshSession(HttpServletRequest request) {
		if (request == null)
			return null;
		HttpSession session = request.getSession();
		session.removeAttribute("currentUser");
		session.setAttribute(SITE_SESSION_KEY,null);
		VWBSession vwbsession = new VWBSession();
		initSiteSession(request, vwbsession);
		session.setAttribute(SESSION_KEY, vwbsession);
		return vwbsession;
	}

	/**
	 * 只是从当前已初始化的Session中获取信息
	 * 
	 * @param session
	 * @return
	 */
	public static VWBSession findSession(HttpSession session) {
		return (VWBSession) session.getAttribute(SESSION_KEY);
	}

	public static VWBSession findSession(PortletRequest request) {
		PortletSession psession = request.getPortletSession();
		VWBSession vwbsession = (VWBSession) psession.getAttribute(SESSION_KEY,
				PortletSession.APPLICATION_SCOPE);
		return vwbsession;
	}

	private boolean fullmode;

	private Subject m_subject;

	private SiteSessionImpl siteSession;

	private String status = AUTHENTICATED;

	public VWBSession() {
		m_subject = DEFAULT_SUBJECT;
		status = ANONYMOUS;
	}

	private Subject convertPrincipal(Collection<java.security.Principal> prins)
			throws UnkownCredentialException {
		if (prins != null && prins.size() == 0) {
			return null;
		}

		ArrayList<Principal> result = new ArrayList<Principal>();
		Principal newprin = null;
		Principal user = null;
		for (java.security.Principal prin : prins) {
			newprin = null;
			if (prin instanceof cn.vlabs.commons.principal.UserPrincipal) {
				cn.vlabs.commons.principal.UserPrincipal oldu = (cn.vlabs.commons.principal.UserPrincipal) prin;
				newprin = new UserPrincipal(oldu.getName(),
						oldu.getDisplayName(), UserPrincipal.LOGIN_NAME);
				user = newprin;
			}
			if (prin instanceof cn.vlabs.commons.principal.GroupPrincipal) {
				cn.vlabs.commons.principal.GroupPrincipal oldg = (cn.vlabs.commons.principal.GroupPrincipal) prin;
				newprin = new GroupPrincipal(oldg.getName());
			}
			if (prin instanceof cn.vlabs.commons.principal.RolePrincipal) {
				cn.vlabs.commons.principal.RolePrincipal oldr = (cn.vlabs.commons.principal.RolePrincipal) prin;
				String roleName = oldr.getGroupName() + "."
						+ oldr.getShortName();
				newprin = new Role(roleName);
			}
			if (newprin != null)
				result.add(newprin);
		}
		result.add(Role.AUTHENTICATED);
		result.add(Role.ALL);
		if (user == null)
			throw new UnkownCredentialException("User Principal not found");
		return new Subject(user, result);
	}
	public Principal getCurrentUser() {
		return m_subject.getCurrentUser();
	}

	public Principal[] getPrincipals() {
		return m_subject.getPrincipals().toArray(new Principal[0]);
	}

	public String getStatus() {
		return status;
	}

	public boolean hasPrincipal(Principal prin) {
		return m_subject.getPrincipals().contains(prin);
	}

	public void invalidate() {
		m_subject.getPrincipals().clear();
		m_subject.setCurrentUser(UserPrincipal.GUEST);
		m_subject.getPrincipals().clear();
		Set<Principal> principals = m_subject.getPrincipals();

		principals.clear();
		principals.add(UserPrincipal.GUEST);
		principals.add(Role.ALL);
		principals.add(Role.ANONYMOUS);
		principals.add(UserPrincipal.GUEST);

		status = ANONYMOUS;

		siteSession.setPrincipals(null);
		siteSession.setCurrentUser(null);
	}

	public boolean isAnonymous() {
		Set<Principal> principals = m_subject.getPrincipals();
		return principals.contains(Role.ANONYMOUS)
				|| principals.contains(UserPrincipal.GUEST)
				|| isIPV4Address(m_subject.getCurrentUser().getName());
	}

	public final boolean isAsserted() {
		return m_subject.getPrincipals().contains(Role.ASSERTED);
	}

	public boolean isAuthenticated() {
		// If Role.AUTHENTICATED is in principals set, always return true.
		if (m_subject.getPrincipals().contains(Role.AUTHENTICATED)) {
			return true;
		}

		// With non-JSPWiki LoginModules, the role may not be there, so
		// we need to add it if the user really is authenticated.
		if (!isAnonymous() && !isAsserted()) {
			// Inject AUTHENTICATED role
			m_subject.getPrincipals().add(Role.AUTHENTICATED);
			return true;
		}

		return false;
	}

	public boolean isFullMode() {
		return fullmode;
	}

	public void setFullMode(boolean fullmode) {
		this.fullmode = fullmode;
	}

	public void setPrincipals(Collection<Principal> principals) {
		Subject subject;
		try {
			subject = convertPrincipal(principals);
			setSubject(VWBSession.AUTHENTICATED, subject);
			Principal[] prins = principals.toArray(new Principal[principals
					.size()]);
			siteSession.setPrincipals(prins);
		} catch (UnkownCredentialException e) {
			// Do Nothing
		}
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubject(String status, Subject subject) {
		if (subject == null)
			throw new IllegalArgumentException("Subject can't be null");
		this.m_subject = subject;
		this.status = status;
	}
	
	public int getSiteId(){
		return this.siteSession.getSiteId();
	}

	public String getToken(){
		return m_subject.getToken();
	}
	public void setToken(String accessToken) {
		m_subject.setToken(accessToken);
	}
}
