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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.permissions.VWBPermission;

/**
 * Introduction Here.
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public class AuthorizationFileParser {
	private static final Logger log = Logger
			.getLogger(AuthorizationFileParser.class);
	private AuthorizationTokenStream ats;

	public AuthorizationFileParser(BufferedReader reader) {
		this.ats = new AuthorizationTokenStream(reader);
	}

	public Acl parseEntry() throws AuthorizationSyntaxParseException,
			AuthorizationLexicParseException {
		Acl acl = new Acl();
		try {
			while (ats.hasNextToken()) {
				String grant = ats.nextUsefulToken();
				if (grant != null && grant.toLowerCase().equals("grant")) {
					AclEntry aclEntry = new AclEntry();
					Principal principal = parsePrincipal();
					aclEntry.setPrincipal(principal);
					String leftBracket = ats.nextUsefulToken();
					if (leftBracket == null || !leftBracket.equals("{")) {
						throw new AuthorizationSyntaxParseException("Line "
								+ ats.getLineNum() + ", no left bracket");
					}
					Permission permission = parsePermission();
					while (permission != null) {
						aclEntry.addPermission(permission);
						permission = parsePermission();
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

	private Principal parsePrincipal()
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

	private Permission parsePermission()
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

	public void close() {
		try {
			ats.close();
		} catch (IOException e) {
			log.error("Cloase policy file failed", e);
		}
	}
}
