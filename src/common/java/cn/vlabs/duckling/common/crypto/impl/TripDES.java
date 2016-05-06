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
package cn.vlabs.duckling.common.crypto.impl;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import cn.vlabs.duckling.common.crypto.IKey;


public class TripDES implements IKey, Serializable {
	private static final long serialVersionUID = 1L;

	private String Algorithm = "DESede"; // 定义 加密算法,可用 DES,DESede,Blowfish

	private SecretKey deskey;
	
	private Date createTime;

	public Date getCreateTime(){
		if (createTime==null || deskey==null)
			throw new NotCreated();
		return createTime;
	}
	public void generate(){
		KeyGenerator keygen;
		try {
			keygen = KeyGenerator.getInstance(Algorithm);
			this.deskey=keygen.generateKey();
			this.createTime=new Date();
		} catch (NoSuchAlgorithmException e) {
		}
	}
	public byte[] encrypt(byte[] bytes) {
		try {
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(bytes);
		} catch (Exception e) {
		}
		return null;
	}

	public byte[] decrypt(byte[] cipherByte) {
		try {
			Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.DECRYPT_MODE, deskey);
			return c.doFinal(cipherByte);
		} catch (Exception e) {
		}
		return null;
	}
}
