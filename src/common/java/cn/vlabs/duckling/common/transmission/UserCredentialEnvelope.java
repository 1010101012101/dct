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

public class UserCredentialEnvelope {
	private String authAppId;
	private UserPrincipal user;
	private String validTime;
	/**
	 * @return the authAppId
	 */
	public String getAuthAppId() {
		return authAppId;
	}
	/**
	 * @param authAppId the authAppId to set
	 */
	public void setAuthAppId(String authAppId) {
		this.authAppId = authAppId;
	}
	/**
	 * @return the user
	 */
	public UserPrincipal getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(UserPrincipal user) {
		this.user = user;
	}
	/**
	 * @return the validTime
	 */
	public String getValidTime() {
		return validTime;
	}
	/**
	 * @param validTime the validTime to set
	 */
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	private static XStream getXStream() {
		XStream stream = new XStream();
		stream.alias("UserCredentialEnvelope", UserCredentialEnvelope.class);
		return stream;
	}

	public String toXML() {
		XStream stream = getXStream();
		return stream.toXML(this);
	}
	public static UserCredentialEnvelope valueOf(String xml){
		XStream stream = getXStream();
		return (UserCredentialEnvelope) stream.fromXML(xml);
	}
	

}
