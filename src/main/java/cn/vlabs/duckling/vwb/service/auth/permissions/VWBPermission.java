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

package cn.vlabs.duckling.vwb.service.auth.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Arrays;

/**
 * Introduction Here.
 * @date Mar 10, 2010
 * @author xiejj@cnic.cn
 */
public class VWBPermission extends Permission {
	private static final long serialVersionUID = 1L;

	public static final String CREATE_PAGES_ACTION = "createPages";

	public static final String LOGIN_ACTION = "login";
	
	public static final String LOGOUT_ACTION = "logout";

	public static final String UPLOAD_ACTION = "upload";

	public static final String PORTLET_ACTION = "portlet";

	public static final String EDIT_PREFERENCE_ACTION = "editPreference";
	
	public static final String EDIT_PROFILE_ACTION = "editProfile";

	public static final String SEARCH_ACTION = "search";

	protected static final int CREATE_PAGES_MASK = 0x1;

	protected static final int LOGIN_MASK = 0x2;

	protected static final int EDIT_PREFERENCE_MASK = 0x4;
	
	protected static final int EDIT_PROFILE_MASK = 0x8;
	
	protected static final int PORTLET_MASK = 0x10;

	protected static final int SEARCH_MASK = 0x20;

	protected static final int UPLOAD_MASK = 0x40;
	
	protected static final int LOGOUT_MASK = 0x80;

	private static final String WILDCARD = "*";

	public static final VWBPermission CREATE_PAGES = new VWBPermission(
			WILDCARD, CREATE_PAGES_ACTION);

	public static final VWBPermission LOGIN = new VWBPermission(WILDCARD,
			LOGIN_ACTION);
	
	public static final VWBPermission LOGOUT = new VWBPermission(WILDCARD,
			LOGOUT_ACTION);
	
	public static final VWBPermission PORTLET = new VWBPermission(WILDCARD,
			PORTLET_ACTION);

	public static final Permission EDIT_PREFERENCE = new VWBPermission(
			WILDCARD, EDIT_PREFERENCE_ACTION);

	public static final Permission SEARCH = new VWBPermission(WILDCARD,
			SEARCH_ACTION);
	
	public static final Permission EDIT_PROFILE= new VWBPermission(WILDCARD, EDIT_PROFILE_ACTION);

	private final String m_actionString;

	private final int m_mask;

	public VWBPermission(String actions) {
		this(WILDCARD, actions);
	}

	public static boolean isVWBPermission(String action) {
		return (CREATE_PAGES_ACTION.equals(action)
				|| LOGIN_ACTION.equals(action)
				|| UPLOAD_ACTION.equals(action)
				|| PORTLET_ACTION.equals(action)
				|| EDIT_PREFERENCE_ACTION.equals(action)
				|| SEARCH_ACTION.equals(action)
				|| EDIT_PROFILE_ACTION.equals(action)
				|| LOGOUT_ACTION.equals(action));
	}

	public VWBPermission(String target, String actions) {
		super(target);
		String[] pageActions = actions.toLowerCase().split(",");
		Arrays.sort(pageActions, String.CASE_INSENSITIVE_ORDER);
		m_mask = createMask(actions);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < pageActions.length; i++) {
			buffer.append(pageActions[i]);
			if (i < (pageActions.length - 1)) {
				buffer.append(",");
			}
		}
		m_actionString = buffer.toString();
	}

	public final boolean equals(Object obj) {
		if (!(obj instanceof VWBPermission)) {
			return false;
		}
		VWBPermission p = (VWBPermission) obj;
		return p.m_mask == m_mask && m_actionString.equals(p.m_actionString);
	}

	public final String getActions() {
		return m_actionString;
	}

	public final int hashCode() {
		return m_mask
				+ ((13 * m_actionString.hashCode()) * 23 * m_actionString
						.hashCode());
	}

	public final boolean implies(Permission permission) {
		// Permission must be a WikiPermission
		if (!(permission instanceof VWBPermission)) {
			return false;
		}
		VWBPermission p = (VWBPermission) permission;

		// Build up an "implied mask" for actions
		int impliedMask = impliedMask(m_mask);

		// If actions aren't a proper subset, return false
		return (impliedMask & p.m_mask) == p.m_mask;
	}

	public PermissionCollection newPermissionCollection() {
		return new AllPermissionCollection();
	}

	public final String toString() {
		return "(\"" + this.getClass().getName() + "\",\"" + getActions()
				+ "\")";
	}

	protected static final int impliedMask(int mask) {
		return mask;
	}

	protected static final int createMask(String actions) {
		if (actions == null || actions.length() == 0) {
			throw new IllegalArgumentException(
					"Actions cannot be blank or null");
		}
		int mask = 0;
		String[] actionList = actions.split(",");
		for (int i = 0; i < actionList.length; i++) {
			String action = actionList[i];
			if (action.equalsIgnoreCase(CREATE_PAGES_ACTION)) {
				mask |= CREATE_PAGES_MASK;
			} else if (action.equalsIgnoreCase(SEARCH_ACTION)) {
				mask |= SEARCH_MASK;
			} else if (action.equalsIgnoreCase(LOGIN_ACTION)) {
				mask |= LOGIN_MASK;
			} else if (action.equalsIgnoreCase(EDIT_PREFERENCE_ACTION)) {
				mask |= EDIT_PREFERENCE_MASK;
			} else if (action.equalsIgnoreCase(PORTLET_ACTION)) {
				mask |= PORTLET_MASK;
			} else if (action.equals(UPLOAD_ACTION)) {
				mask |= UPLOAD_MASK;
			}else if (action.equalsIgnoreCase(EDIT_PROFILE_ACTION)){
				mask |= EDIT_PROFILE_MASK;
			}else if (action.equals(LOGOUT_ACTION)){
				mask |= LOGOUT_MASK;
			}
			else {
				throw new IllegalArgumentException("Unrecognized action: "
						+ action);
			}
		}
		return mask;
	}
}
