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
package cn.vlabs.commons.principal;

import java.security.Principal;

import cn.vlabs.commons.credential.PrincipalFactory;

public class DefaultPrinFactory implements PrincipalFactory<Principal> {

	public Principal createGroup(String groupname) {
		return new GroupPrincipal(groupname);
	}

	public Principal createRole(String roleName) {
		return new RolePrincipal(roleName);
	}

	public Principal createUser(String name, String displayName, String email) {
		UserPrincipal p = new UserPrincipal(name,displayName, email);
		return p;
	}

}
