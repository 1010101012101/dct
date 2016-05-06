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
package cn.vlabs.duckling.common.transmission;


import cn.vlabs.commons.principal.UserPrincipal;

import com.thoughtworks.xstream.XStream;

public class Credential {
	public Credential(int appkeyid, int umtkeyid, UserPrincipal user){
		this.AppKeyId=appkeyid;
		this.UMTKeyId=umtkeyid;
		this.user=user;
	}
	
	public static Credential valueOf(String xml){
		return (Credential) stream.fromXML(xml);
	}
	
	public String toXML(){
		return stream.toXML(this);
	}
	
	public int getAppKeyId() {
		return AppKeyId;
	}
	public int getUMTKeyId() {
		return UMTKeyId;
	}
	public UserPrincipal getPrincipals() {
		return user;
	}
	private int AppKeyId;
	private int UMTKeyId;
	private UserPrincipal user;
	
	private static XStream stream;
	static{
		stream = new XStream();
		stream.alias("Credential", Credential.class);
	}
}
