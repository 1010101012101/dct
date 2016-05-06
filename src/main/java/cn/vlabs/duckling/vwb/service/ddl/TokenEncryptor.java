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

package cn.vlabs.duckling.vwb.service.ddl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.common.util.Base64;
import cn.vlabs.duckling.common.util.Base64.DecodingException;

/**
 * 用来和DDL通信是加密用户凭证
 * @date 2016-1-15
 * @author xiejj@cstnet.cn
 */
public class TokenEncryptor {
	private static Logger log = Logger.getLogger(TokenEncryptor.class);
	private SecretKeySpec key;
	public TokenEncryptor(String keyString) throws DecodingException{
		byte[] bytes = Base64.decode(keyString.trim());
		key = new SecretKeySpec(bytes, "AES");
	}
	
	public String encrypt(String token){
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(token.getBytes("utf-8"));
			return Base64.encode(result);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			log.error("Encrypt token for access ddl failed.", e);
			return "";
		}
	}
}
