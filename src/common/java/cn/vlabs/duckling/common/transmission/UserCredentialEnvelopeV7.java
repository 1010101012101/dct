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

import java.util.Random;

import cn.vlabs.commons.principal.UserPrincipalV7;

import com.thoughtworks.xstream.XStream;

/**
 * @date 2013-3-12
 * @author cerc
 */
public class UserCredentialEnvelopeV7 {
	private String authAppId;
	private UserPrincipalV7 user;
	private String validTime;
	public String getAuthAppId() {
		return authAppId;
	}
	public void setAuthAppId(String authAppId) {
		this.authAppId = authAppId;
	}
	public UserPrincipalV7 getUser() {
		return user;
	}
	public void setUser(UserPrincipalV7 user) {
		this.user = user;
	}
	public String getValidTime() {
		return validTime;
	}
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	private static XStream getXStream() {
		XStream stream = new XStream();
		stream.alias("UserCredentialEnvelopeV7", UserCredentialEnvelopeV7.class);
		return stream;
	}

	public String toXML() {
		XStream stream = getXStream();
		return stream.toXML(this);
	}
	public static UserCredentialEnvelopeV7 valueOf(String xml){
		XStream stream = getXStream();
		return (UserCredentialEnvelopeV7) stream.fromXML(xml);
	}
	
}
