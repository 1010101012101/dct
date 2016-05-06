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
package cn.vlabs.commons.credential;
/**
 * 完成信任证的验证和结构分离
 * 
 * 身份信息的消息格式：
<?xml version="1.0" encoding="UTF-8"?>
<credinfo>
    <timestamp></timestamp>
    <inteval></inteval>
    <WebServerURL></WebServerURL>
    <principals>
        <principal type="group">
            <name>groupname</name>
        </principal>
        <principal type="role">
            <id>1</id>
            <name>rolename</name>
        </principal>
        <principal type="user">
            <name>user</name>
            <truename>truename</truename>
            <email>emails@some.site</email>
        </principal>
    </principals>
</credinfo>
Credential凭证的格式为：
<?xml version="1.0" encoding="UTF-8"?>
<credential>
    <data>
        <![CDATA[ 这里是上边的身份信息 ]]>
    </data>
    <sign>对身份信息的签名</sign>
</credential>
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import cn.vlabs.commons.xml.XMLReader;


public class CredentialUtil<P> {
	public CredentialUtil(PrincipalFactory<P> factory) {
		this.factory = factory;
	}

	public boolean VerifyCredential() {
		return false;
	}

	public Collection<P> getPrincipal() {
		return principals;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public void setSigCredential(String sigCredential) {
		this.sigCredential = sigCredential;
	}

	public void setUmtCertificate(String umtCertificate) {
		this.umtCertificate = umtCertificate;
	}
	public Collection<P> getPrincipals() {
		return this.principals;
	}

	public String getWebServerURL() {
		return this.webServerURL;
	}
	public Collection<Pair> getAttributes(){
		return this.pairs;
	}
	public boolean setCredentialXML(String xml) {
		ByteArrayInputStream sbis = null;
		try {
			sbis = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			XMLReader reader = XMLReader.newReader((InputStream) sbis,
					"credential");

			if (!(readCredential(reader) && readSignature(reader)))
				return false;

			//验证签名        
			VerifySig vs = new VerifySig();
			if (!vs.veriSig(credential, sigCredential, umtCertificate,"SHA1withRSA"))
				return false;

			//获取Credential
			if (!getCredential(credential))
				return false;
			return true;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				sbis.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private boolean readCredential(XMLReader reader) {
		credential = reader.getString("data");
		return (credential != null);
	}

	private boolean readSignature(XMLReader reader) {
		sigCredential = reader.getString("sign");
		return (sigCredential != null);
	}

	private boolean getCredential(String credData) {
		ByteArrayInputStream sbis = null;
		try {
			sbis = new ByteArrayInputStream(credData.getBytes("UTF-8"));
			XMLReader reader = XMLReader.newReader((InputStream) sbis,
					"credinfo");

			//解析XML文档
			timestamp = reader.getString("timestamp");
			interval = reader.getString("interval");
			webServerURL = reader.getString("WebServerURL");
			if (interval == null || timestamp == null || webServerURL == null)
				return false;

			if (overTime())
				return false;
			pairs=readPairs(reader);
			principals = readPrincipals(reader);
			if (principals.size() > 0)
				return true;
			else
				return false;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				sbis.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	private ArrayList<Pair> readPairs(XMLReader reader){
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		XMLReader attributes =reader.getSubElement("attributes");
		if (attributes!=null){
			Iterator attr = attributes.iterator("attribute");
			while (attr.hasNext()){
				XMLReader attribute=(XMLReader) attr.next();
				Pair p= new Pair();
				p.setName(attribute.getString("name"));
				p.setValue(attribute.getString("value"));
				pairs.add(p);
			}
		}
		return pairs;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<P> readPrincipals(XMLReader reader) {
		XMLReader principalReader = (XMLReader) reader
				.getSubElement("principals");
		Iterator pri = principalReader.iterator("principal");
		ArrayList<P> xmlPrincipals = new ArrayList<P>();
		while (pri.hasNext()) {
			XMLReader nextReader = (XMLReader) pri.next();
			String type = nextReader.getString("type");
			if (type == null)
				continue;

			if (type.equals("user")) {
				String name = nextReader.getString("name");
				String truename = nextReader.getString("truename");
				String email = nextReader.getString("email");
				if (name != null) {
					P p = (P) factory.createUser(name, truename, email);
					xmlPrincipals.add(p);
				}
			} else if (type.equals("role")) {
				String rolename = nextReader.getString("name");
				if (rolename != null)
					xmlPrincipals.add((P) factory.createRole(rolename));
			} else if (type.equals("group")) {
				String name = nextReader.getString("name");
				if (name != null)
					xmlPrincipals.add((P) factory.createGroup(name));
			}
		}
		return xmlPrincipals;
	}

	private boolean overTime() throws NumberFormatException {
		//判断时间戳的有效性
		Date date1 = new Date();
		Date date2 = new Date(Long.parseLong(timestamp));
		long templong = new Long(interval).longValue();
		if ((date1.getTime() - date2.getTime()) > templong * 60 * 60 * 1000) {
			return true;
		} else
			return false;
	}

	private PrincipalFactory factory;

	private String credential;

	private String sigCredential;

	private String umtCertificate;

	//Credential Parameter
	private String timestamp;

	private String interval;
	private ArrayList<Pair> pairs;
	private String webServerURL;

	private ArrayList<P> principals;
}