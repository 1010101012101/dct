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

public class RolePrincipal implements Principal,Serializable{
	private static final long serialVersionUID = 1L;
	private String groupName = "";
	private String voName = "";
	private String name;
	public RolePrincipal(){};
	public RolePrincipal(String name){
		int index = name.lastIndexOf(".");
		this.name = name.substring(index+1);
		name = name.substring(0, index);
		index = name.indexOf(".");
		if(index>0)
		{
			this.voName = name.substring(0, index);
			this.groupName = name.substring(index+1);
		}else
		{
			this.groupName = name;
		}
	}
	public RolePrincipal(String name,String groupName, String voName){
		this.name=name;
		this.groupName = groupName;
		this.voName = voName;
	}
	public String getName() {
		return groupName+"."+name;
	}
	public String getShortName()
	{
		return name;
	}
	
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @return the voName
	 */
	public String getVoName() {
		return voName;
	}

	public boolean equals(Object obj){
		if (obj==null)
			return false;
		if (obj==this)
			return true;
		if (obj instanceof RolePrincipal){
			RolePrincipal other = (RolePrincipal)obj;
			return name.equals(other.name)&&groupName.equals(other.groupName)&&voName.equals(other.voName);
		}
		return false;
	}
	public int hashCode(){
		return ("Role"+name+voName+groupName).hashCode();
	}
}
