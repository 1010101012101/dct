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

import com.thoughtworks.xstream.XStream;

public class PublicKeyEnvelope {
	private String appId;
	private String publicKey;
	private String validTime;

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the validTime
	 */
	public String getValidTime() {
		return validTime;
	}

	/**
	 * @param validTime
	 *            the validTime to set
	 */
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	public static PublicKeyEnvelope valueOf(String xml){
		XStream stream = getXStream();
		return (PublicKeyEnvelope) stream.fromXML(xml);
	}
	private static XStream getXStream() {
		XStream stream = new XStream();
		stream.alias("PublicKeyEnvelope", PublicKeyEnvelope.class);
		return stream;
	}

	public String toXML() {
		XStream stream = getXStream();
		return stream.toXML(this);
	}
}
