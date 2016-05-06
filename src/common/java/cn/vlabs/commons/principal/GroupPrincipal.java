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

public class GroupPrincipal implements Principal,Serializable {
	private String name;
	private String voName = "";
	public GroupPrincipal(){};
	public GroupPrincipal(String name){
		int index = name.indexOf(".");
		if(index>0)
		{
			this.voName = name.substring(0, index);
			this.name = name.substring(index+1);
		}else
		{
			this.name=name;
		}
	}
	public GroupPrincipal(String name,String voName){
		this.name=name;
		this.voName = voName;
	}
	public String getName() {
		return name;
	}
	/**
	 * @return the voName
	 */
	public String getVoName() {
		return voName;
	}
	public int hashCode(){
		return ("Group".hashCode()+voName.hashCode()+name.hashCode());
	}
	public boolean equals(Object obj){
		if (obj==null)
			return false;
		if (obj==this)
			return true;
		if (obj instanceof GroupPrincipal){
			GroupPrincipal other = (GroupPrincipal)obj;
			return voName.equals(other.voName)&& name.equals(other.name);
		}
		return false;
	}
}
