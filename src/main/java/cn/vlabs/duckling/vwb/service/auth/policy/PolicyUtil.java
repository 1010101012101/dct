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

package cn.vlabs.duckling.vwb.service.auth.policy;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.permissions.AllPermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.UserPermission;
import cn.vlabs.duckling.vwb.service.auth.permissions.VWBPermission;

/**
 * Introduction Here.
 * 
 * @date May 6, 2010
 * @author zzb
 */
public class PolicyUtil {

	private static Permission parsePermission(AuthorizationTokenStream ats)
			throws AuthorizationSyntaxParseException, IOException {
		String perm = ats.nextUsefulToken();
		if (perm == null) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", permission syntax error");
		} else if (!perm.toLowerCase().equals("permission")) {
			String rightBracket = perm;
			if (rightBracket == null || !rightBracket.contains("}")) {
				throw new AuthorizationSyntaxParseException("Line "
						+ ats.getLineNum() + ", no right bracket");
			} else if (!rightBracket.contains(";")) {
				throw new AuthorizationSyntaxParseException("Line "
						+ ats.getLineNum() + ", no \";\" sign finded");
			}
			return null;
		}
		String className = ats.nextUsefulToken();
		String isEnd = ats.nextUsefulToken();
		if (className == null) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", className is null");
		}
		if (isEnd == null) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", no operate object defined");
		} else {
			try {
				if (!isEnd.contains(";")) {
					String oper = isEnd;
					oper = oper.replace("\"", "");
					oper = oper.replace(",", "");
					isEnd = ats.nextUsefulToken();
					if (isEnd != null && isEnd.contains(";")) {
						String actions = isEnd.replace(";", "");
						actions = actions.replace("\"", "");
						Class<?> clazz = Class.forName(className, false,
								VWBPermission.class.getClassLoader());
						return ((Permission) clazz.getDeclaredConstructor(
								new Class[] { String.class, String.class })
								.newInstance(oper, actions));
					} else {
						throw new AuthorizationSyntaxParseException("Line "
								+ ats.getLineNum() + ", no \";\" sign finded");
					}
				} else {
					String oper = isEnd.replace(";", "");
					oper = oper.replace("\"", "");
					Class<?> clazz = Class.forName(className);
					return ((Permission) clazz.getDeclaredConstructor(
							String.class).newInstance(oper));
				}
			} catch (ClassNotFoundException e) {
				throw new AuthorizationSyntaxParseException("Line "
						+ ats.getLineNum() + ", ClassNotFoundException, "
						+ e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				throw new AuthorizationSyntaxParseException("Line "
						+ ats.getLineNum() + ", Exception happens, "
						+ e.getMessage());
			}
		}
	}

	private static Principal parsePrincipal(AuthorizationTokenStream ats)
			throws AuthorizationSyntaxParseException, IOException {
		String principal = ats.nextUsefulToken();
		String className = ats.nextUsefulToken();
		String roleName = ats.nextUsefulToken();
		if (principal == null || !principal.toLowerCase().equals("principal")) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", principal syntax error");
		}
		if (className == null) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", className is null");
		}
		if (roleName == null) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", roleName is null");
		} else {
			roleName = StringUtils.strip(roleName, "\"");
			roleName = roleName.replace("*", "All");
		}

		try {
			Class<?> clazz = Class.forName(className);
			return ((Principal) clazz.getDeclaredConstructor(String.class)
					.newInstance(roleName));
		} catch (ClassNotFoundException e) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", ClassNotFoundException, "
					+ e.getMessage());
		} catch (Exception e) {
			throw new AuthorizationSyntaxParseException("Line "
					+ ats.getLineNum() + ", Exception happens, "
					+ e.getMessage());
		}

	}

	private static String permiss2PolicyString(Permission permission) {
		StringBuffer sb = new StringBuffer();
		sb.append("permission ").append(permission.getClass().getName())
				.append(" \"");
		if (permission instanceof UserPermission) {
			sb.append(((UserPermission) permission).getWiki());
		} else if (permission instanceof VWBPermission) {
			sb.append("*");
		} else if (permission instanceof PagePermission) {
			sb.append(((PagePermission) permission).getPage());
		} else if (permission instanceof AllPermission) {
			sb.append("*");
		} else {
			sb.append(permission.getName());
		}
		sb.append("\"");
		String actions = permission.getActions();
		if (actions != null) {
			sb.append(" \"").append(actions).append("\"");
		}
		sb.append(";\n\r");
		return sb.toString();
	}

	public static List<PolicyData> acl2PolicyData(Acl acl) {
		List<PolicyData> pdl = new ArrayList<PolicyData>();

		Enumeration<AclEntry> entryEnum = acl.entries();
		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();
			Principal principal = entry.getPrincipal();
			String principalClass = principal.getClass().getName();
			String principalName = principal.getName();
			String objectname = null;
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.Role")
					&& principalName.equalsIgnoreCase("All")) {
				objectname = "所有人";
			}
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.GroupPrincipal")) {
				objectname = "社区用户";
			}
			if (objectname == null) {
				continue;
			}
			Enumeration<Permission> permissEnum = entry.permissions();
			while (permissEnum.hasMoreElements()) {
				Permission permission = permissEnum.nextElement();

				String permissionClass = permission.getClass().getName();
				String permissionType = null;
				if (permissionClass
						.equals("cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission")) {
					permissionType = "页面级";
				}
				if (permissionClass
						.equals("cn.vlabs.duckling.vwb.service.auth.permissions.VWBPermission")) {
					permissionType = "系统级";
				}
				if (permissionType == null) {
					continue;
				}

				String permissionActions = permission.getActions();
				String operations = permissionActions;

				String permissionName = permission.getName();

				String resource = null;
				if (permission instanceof UserPermission) {
					resource = ((UserPermission) permission).getWiki();
				} else if (permission instanceof VWBPermission) {
					resource = "*";
				} else if (permission instanceof PagePermission) {
					resource = ((PagePermission) permission).getPage();
				} else if (permission instanceof AllPermission) {
					resource = "*";
				} else {
					resource = permission.getName();
				}

				PolicyData pd = new PolicyData();

				pd.setId(pdl.size());
				pd.setPrincipalClass(principalClass);
				pd.setPrincipalName(principalName);

				pd.setPermissionClass(permissionClass);
				pd.setPermissionActions(permissionActions);
				pd.setPermissionName(permissionName);

				pd.setPrincipal(objectname);
				pd.setPermission(permissionType);
				pd.setOperation(operations);
				pd.setResource(resource);

				pdl.add(pd);
			}
		}

		return pdl;
	}

	public static String acl2PolicyString(Acl acl) {
		Enumeration<AclEntry> entryEnum = acl.entries();
		StringBuffer sb = new StringBuffer();
		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();
			sb.append("grant principal ");
			Principal principal = entry.getPrincipal();
			sb.append(principal.getClass().getName()).append(" \"");
			sb.append(principal.getName()).append("\" {\n\r");
			Enumeration<Permission> permissEnum = entry.permissions();
			while (permissEnum.hasMoreElements()) {
				Permission permission = permissEnum.nextElement();
				sb.append("\t").append(
						PolicyUtil.permiss2PolicyString(permission));
			}
			sb.append("};\n\r");
		}
		return sb.toString();
	}

	public static PolicyData add(String voGroup,Acl policy, String principalAlias,
			String permissionAlias, String operation, String resource) {

		Principal principal = null;
		if (principalAlias.equals("All")) {
			principal = new Role("All");
			principalAlias = "所有人";
		} else if (principalAlias.equals("VO")) {
			principal = new GroupPrincipal(voGroup);
			principalAlias = "社区用户";
		} else {
			return null;
		}

		AclEntry policyEntry = policy.getEntry(principal);
		Permission permission = null;
		if (permissionAlias.equals("Page")) {
			permission = new PagePermission(resource, operation);
			permissionAlias = "页面级";
		} else if (permissionAlias.equals("VWB")) {
			permission = new VWBPermission(resource, operation);
			permissionAlias = "系统级";
		} else {
			return null;
		}

		policyEntry.addPermission(permission);

		PolicyData pd = new PolicyData();
		pd.setPrincipalClass(principal.getClass().getName());
		pd.setPrincipalName(principal.getName());
		pd.setPrincipal(principalAlias);

		pd.setPermissionClass(permission.getClass().getName());
		pd.setPermissionName(permission.getName());
		pd.setPermissionActions(permission.getActions());
		pd.setPermission(permissionAlias);

		pd.setOperation(operation);
		pd.setResource(resource);

		return pd;
	}

	public static Acl parseEntry(BufferedReader reader)
			throws AuthorizationSyntaxParseException,
			AuthorizationLexicParseException {

		AuthorizationTokenStream ats = new AuthorizationTokenStream(reader);

		Acl acl = new Acl();
		try {
			while (ats.hasNextToken()) {
				String grant = ats.nextUsefulToken();
				if (grant != null && grant.toLowerCase().equals("grant")) {
					AclEntry aclEntry = new AclEntry();
					Principal principal = parsePrincipal(ats);
					aclEntry.setPrincipal(principal);
					String leftBracket = ats.nextUsefulToken();
					if (leftBracket == null || !leftBracket.equals("{")) {
						throw new AuthorizationSyntaxParseException("Line "
								+ ats.getLineNum() + ", no left bracket");
					}
					Permission permission = parsePermission(ats);
					while (permission != null) {
						aclEntry.addPermission(permission);
						permission = parsePermission(ats);
					}

					acl.addEntry(aclEntry);
				}
			}
		} catch (IOException e) {
			throw new AuthorizationLexicParseException("Lexic Parse Error, "
					+ e.getMessage());
		}
		return acl;

	}

	public static void remove(Acl policy, PolicyData pd) {
		Enumeration<AclEntry> entryEnum = policy.entries();

		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();
			Principal principal = entry.getPrincipal();

			String principalClass = principal.getClass().getName();
			String principalName = principal.getName();

			if (principalClass == null || principalName == null)
				continue;
			if (principalClass.equals(pd.getPrincipalClass())
					&& principalName.equals(pd.getPrincipalName())) {
				Enumeration<Permission> permissEnum = entry.permissions();
				while (permissEnum.hasMoreElements()) {
					Permission permission = permissEnum.nextElement();

					String permissionClass = permission.getClass().getName();
					String permissionName = permission.getName();
					String permissionActions = permission.getActions();

					if (permissionClass == null || permissionName == null
							|| permissionActions == null)
						continue;
					if (permissionClass.equals(pd.getPermissionClass())
							&& permissionName.equals(pd.getPermissionName())
							&& permissionActions.equals(pd
									.getPermissionActions())) {
						// 删除
						AclEntry policyEntry = policy.getEntry(principal);
						policyEntry.removePermission(permission);
					}
				}
			}
		}
	}

	public static Acl removeReserved(Acl policy) {
		Enumeration<AclEntry> entryEnum = policy.entries();
		// 删除被替换部分
		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();
			Principal principal = entry.getPrincipal();

			String principalClass = principal.getClass().getName();
			String principalName = principal.getName();

			if (principalClass == null || principalName == null)
				continue;
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.Role")
					&& principalName.equals("All")) {
				continue;
			}
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.GroupPrincipal")) {
				continue;
			}
			policy.removeEntry(entry);
		}

		return policy;
	}

	public static Acl replacePolicy(Acl policy, String vogroup) {
		Enumeration<AclEntry> entryEnum = policy.entries();

		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();

			Principal principal = entry.getPrincipal();

			String principalClass = principal.getClass().getName();
			String principalName = principal.getName();

			if (principalClass == null || principalName == null)
				continue;
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.GroupPrincipal")) {
				Principal principalNew = new GroupPrincipal(vogroup);
				entry.setPrincipal(principalNew);
			}
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.Role")) {
				int pos = principalName.indexOf('.');
				if (pos != -1) {
					String principalNameNew = vogroup
							+ principalName.substring(pos,
									principalName.length());
					Principal principalNew = new Role(principalNameNew);
					entry.setPrincipal(principalNew);
				}
			}
		}

		return policy;
	}

	public static List<PolicyData> replacePolicyData(List<PolicyData> pdl,
			String vogroup) {
		int size = pdl.size();
		for (int i = 0; i < size; i++) {
			pdl.get(i).setPrincipalName(vogroup);
		}
		return pdl;
	}

	public static Acl restoreReserved(Acl policy, Acl origin) {
		Enumeration<AclEntry> entryEnum = origin.entries();
		//
		while (entryEnum.hasMoreElements()) {
			AclEntry entry = entryEnum.nextElement();
			Principal principal = entry.getPrincipal();

			String principalClass = principal.getClass().getName();
			String principalName = principal.getName();

			if (principalClass == null || principalName == null)
				continue;
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.Role")
					&& principalName.equals("All")) {
				continue;
			}
			if (principalClass
					.equals("cn.vlabs.duckling.vwb.service.auth.GroupPrincipal")) {
				continue;
			}
			policy.addEntry(entry);
		}
		return policy;
	}

}
