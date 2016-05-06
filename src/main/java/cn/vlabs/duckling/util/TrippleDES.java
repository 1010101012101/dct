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

package cn.vlabs.duckling.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import cn.vlabs.duckling.common.util.Base64;

/**
 * TrippleDES 加密算法的操作类
 * @date 2011-11-21
 * @author xiejj@cnic.cn
 */
public abstract class TrippleDES {
	public static String encrypt(String key, String msg){
		try {
			SecretKey skey = generateKey(key);
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			byte[] bytes = cipher.doFinal(msg.getBytes("UTF-8"));
			
			return Base64.encode(bytes);
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (InvalidKeySpecException e) {
		}
		return "";
	}
	public static String decrypt(String key, String encrypted){
		try {
			SecretKey skey = generateKey(key);
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			byte[] chars= cipher.doFinal(Base64.decode(encrypted));
			return new String(chars, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
		} catch (NoSuchPaddingException e) {
		} catch (InvalidKeyException e) {
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (InvalidKeySpecException e) {
		} catch (IOException e) {
		}
		return "";
	}
	
	private static SecretKey generateKey(String key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException{
		DESedeKeySpec keySpec=new DESedeKeySpec(key.getBytes());
		SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
		return factory.generateSecret(keySpec);
	}
}
