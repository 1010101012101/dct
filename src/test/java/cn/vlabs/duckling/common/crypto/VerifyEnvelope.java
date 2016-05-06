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
package cn.vlabs.duckling.common.crypto;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.vlabs.duckling.common.crypto.impl.RSAKey;
import cn.vlabs.duckling.common.util.Base64;
import cn.vlabs.duckling.common.util.Base64.DecodingException;


public class VerifyEnvelope {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException, DecodingException{
		KeyFile keyFile = new KeyFile();
		RSAKey key = keyFile.load("C:\\keys\\localAppKeyPairDir\\keyPair.txt");
		
		byte[] decode = Base64.decode(
				"or0ckkXEfjEXxUTOgMNF+zPcXY5dzZl8uMKCDMDduPMyrwrxUvL7yQTT5nlG1YMTQxPQFiKiAqYm\n" +
				"9IcvU5e5z/vOZnDOpsqIQiu4lB7v0PeEnXCq/H2h0o6qHveZ6+K8HHeb3R3fFbaFKgAJbJ696CEl\n"+
				"Tx5glQi4oYI8+Yn8U+c=");
		System.out.println(key.verify("zengshan@ihep.ac.cn".getBytes("UTF-8"), decode));
	}
	private static void verifyDigest(String email) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest digest = MessageDigest.getInstance("SHA");
		byte[] result = digest.digest(email.getBytes("UTF-8"));
		System.out.println(Base64.encode(result));
	}
	private static void verifyEncrypt(RSAKey key){
		byte[] bytes = new byte[]{0,0, 119, 31, 66, -79, 20, -42, 119, 14, -48, -78, 112, -91, -113, -51, 79, 35, 49, 0};
		byte[] encrypted = key.encrypt(bytes);
		byte[] result = key.decrypt(encrypted);
		System.out.println(HexUtil.isEqual(bytes, result));
	}
	private static boolean verifyBase64(byte[] signed) throws DecodingException {
		String baseString=Base64.encode(signed);
		return HexUtil.isEqual(Base64.decode(baseString), signed);
	}
}
