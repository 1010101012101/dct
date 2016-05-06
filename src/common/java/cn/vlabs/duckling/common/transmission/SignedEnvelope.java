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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.vlabs.duckling.common.crypto.impl.RSAKey;
import cn.vlabs.duckling.common.util.Base64;

import com.thoughtworks.xstream.XStream;

public class SignedEnvelope {
	private String content;
	private String signature;
	private String appId;

	public SignedEnvelope() {
	};

	public SignedEnvelope(String content) {
		this.content = content;
		this.appId = content;
	}

	public SignedEnvelope(String content, String appId) {
		this.content = content;
		this.appId = appId;
	}

	public SignedEnvelope(String content, String signature, String appId) {
		this.content = content;
		this.signature = signature;
		this.appId = appId;
	}

	public String getContent() {
		return content;
	}

	public String getSignature() {
		return signature;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void genSignature(RSAKey key) {
		if (content != null) {
			try {
				byte[] bytes = content.getBytes("UTF-8");
				byte[] signedbytes = key.sign(bytes);
				signature = Base64.encode(signedbytes);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	public boolean verify(RSAKey key) {
		if (content != null && signature != null) {
			try {
				byte[] sigbytes = Base64.decode(signature);
				return key.verify(content.getBytes("UTF-8"), sigbytes);
			} catch (UnsupportedEncodingException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	public String toXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<SignedEnvelope><content><![CDATA[");
		buffer.append(content);
		buffer.append("]]></content><signature>");
		buffer.append(signature);
		buffer.append("</signature>");
		buffer.append("<appId>");
		buffer.append(appId);
		buffer.append("</appId>");
		buffer.append("</SignedEnvelope>");
		return buffer.toString();
	}

	public static SignedEnvelope valueOf(String xml) {
		XStream stream = getXStream();
		return (SignedEnvelope) stream.fromXML(xml);
	}

	private static XStream getXStream() {
		XStream stream = new XStream();
		stream.alias("SignedEnvelope", SignedEnvelope.class);
		return stream;
	}

}
