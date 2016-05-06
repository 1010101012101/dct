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

import java.io.Serializable;
import java.security.Principal;

public class UserPrincipal implements Principal,Serializable {
	private static final long serialVersionUID = 1L;
	public UserPrincipal(){};
	public UserPrincipal(String name, String displayName, String email){
		this.name=name;
		this.displayName=displayName;
		this.email=email;
	}
	public UserPrincipal(String name, String displayName, String email,String authBy){
		this.name=name;
		this.displayName=displayName;
		this.email=email;
		this.authBy = authBy;
	}
	public String getAuthBy()
	{
		return authBy;
	}
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getEmail() {
		return email;
	}
	
	public int hashCode(){
		return ("User".hashCode()+name.hashCode());
	}
	public boolean equals(Object obj){
		if (obj==null)
			return false;
		if (obj==this)
			return true;
		if (obj instanceof UserPrincipal){
			UserPrincipal other = (UserPrincipal)obj;
			return name.equals(other.name);
		}
		return false;
	}
	private String displayName;
	private String email;
	private String name;
	private String authBy="umt";
}
