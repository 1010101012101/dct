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

/**
 * Introduction Here.
 * @date May 8, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class PolicyData {
	int id;
	String principalClass;
	String principalName;
	String permissionClass;
	String permissionName;
	String permissionActions;
	
	String principal;
	String permission;
	String operation;
	String resource;
	
	//---------------------------------------------------
	
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPrincipalClass() {
		return principalClass;
	}
	public void setPrincipalClass(String principalClass) {
		this.principalClass = principalClass;
	}
	public String getPrincipalName() {
		return principalName;
	}
	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}
	public String getPermissionClass() {
		return permissionClass;
	}
	public void setPermissionClass(String permissionClass) {
		this.permissionClass = permissionClass;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public String getPermissionActions() {
		return permissionActions;
	}
	public void setPermissionActions(String permissionActions) {
		this.permissionActions = permissionActions;
	}
}
