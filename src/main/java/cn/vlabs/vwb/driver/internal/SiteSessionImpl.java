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

package cn.vlabs.vwb.driver.internal;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.vwb.SiteContext;
import cn.vlabs.vwb.SiteSession;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 11, 2010 8:27:31 PM
 */
public class SiteSessionImpl implements SiteSession, Serializable {
	private static final long serialVersionUID = 1L;

	private Principal user;

	private Principal[] principals;

	private SiteContext siteContext;

	private HashMap<String, Object> variables=new HashMap<String, Object>(); 
	
	public void setCurrentUser(Principal user) {
		this.user = user;
	}

	public void setPrincipals(Principal[] prins) {
		this.principals = prins;
		if (principals != null) {
			for (Principal prin : principals) {
				if (prin instanceof UserPrincipal)
					setCurrentUser(prin);
			}
		}
	}

	public void setSiteContext(SiteContext siteContext) {
		this.siteContext = siteContext;
	}

	public Principal getCurrentUser() {
		return user;
	}

	public Principal[] getPrincipals() {
		return principals;
	}

	public SiteContext getSiteContext() {
		return siteContext;
	}

	public int getSiteId() {
		return siteContext.getSiteId();
	}

	public String getVO() {
		return siteContext.getVO();
	}

	public SiteContext getSiteContext(int siteid) {
		if (siteid==siteContext.getSiteId()){
			return siteContext;
		}else{
			return new SiteContextImpl(siteid);
		}
	}
	
	public Object getAttribute(String key){
		return variables.get(key);
	}
	
	public void setAttribute(String key, Object value){
		if (value==null){
			variables.remove(key);
		}else{
			variables.put(key, value);
		}
	}
	public void clear(){
		this.variables.clear();
	}
}
