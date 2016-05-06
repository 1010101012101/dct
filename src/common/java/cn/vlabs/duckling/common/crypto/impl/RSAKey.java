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

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import javax.crypto.Cipher;

import cn.vlabs.duckling.common.crypto.EncryptException;
import cn.vlabs.duckling.common.crypto.HexUtil;
import cn.vlabs.duckling.common.crypto.IAsymetricKey;


public class RSAKey implements IAsymetricKey {
	public RSAKey(){
		this.privatekey=null;
		this.publickey=null;
	}
	
	public RSAKey(RSAPublicKey publickey, RSAPrivateKey privatekey){
		this.privatekey=privatekey;
		this.publickey=publickey;
	}
	public RSAKey(RSAPublicKey publickey){
		this.publickey=publickey;
	}
	
	public RSAKey(RSAPrivateKey privatekey){
		this.privatekey=privatekey;
	}
	
	public byte[] sign(byte[] bytes) {
		if (privatekey==null)
			throw new EncryptException("需要私钥才能进行签名操作。");
		return encrypt(digest(bytes), privatekey);
	}
	
	public boolean verify(byte[] bytes, byte[] signature) {
		if (publickey==null){
			throw new EncryptException("需要公钥才能进行验证签名。");
		};
		byte[] trimed =HexUtil.ltrim(digest(bytes));
		return HexUtil.isEqual(decrypt(signature, publickey),trimed);
	}

	public byte[] decrypt(byte[] raw) {
		if (privatekey==null)
			throw new EncryptException("需要私钥才能解密。");
		return decrypt(raw, privatekey);
	}

	public byte[] encrypt(byte[] data) {
		if (publickey==null)
			throw new EncryptException("需要公钥才能加密。");
		return encrypt(data, publickey);
	}
    
	public void generate() {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",provider);
			keyPairGen.initialize(KEY_SIZE, new SecureRandom());
			KeyPair keypair = keyPairGen.genKeyPair();
			privatekey=(RSAPrivateKey) keypair.getPrivate();
			publickey=(RSAPublicKey) keypair.getPublic();
			createTime = new Date();
		} catch (Exception e) {
			throw new EncryptException(e.getMessage());
		}
	}

	public Date getCreateTime() {
		return createTime;
	}
	
	private byte[] digest(byte[] data){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptException(e.getMessage());
		}
	}
	private static Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
	private byte[] encrypt(byte[] data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA",provider);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
			// 加密块大小为127
			// byte,加密后为128个byte;因此共有2个加密块，第一个127
			// byte第二个为1个byte
			int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
			int leavedSize = data.length % blockSize;
			int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
					: data.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (data.length - i * blockSize > 0) {
				if (data.length - i * blockSize > blockSize)
					cipher.doFinal(data, i * blockSize, blockSize, raw, i
							* outputSize);
				else
					cipher.doFinal(data, i * blockSize, data.length - i
							* blockSize, raw, i * outputSize);
				i++;
			}
			return raw;
		} catch (Exception e) {
			throw new EncryptException(e.getMessage());
		}
	}
	
	private byte[] decrypt(byte[] raw, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA",provider);
			cipher.init(Cipher.DECRYPT_MODE, key);
			int blockSize = cipher.getBlockSize();
			ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
			int j = 0;
			while (raw.length - j * blockSize > 0) {
				bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
				j++;
			}
			return bout.toByteArray();
		} catch (Exception e) {
			throw new EncryptException(e.getMessage());
		}
	}
	private static final int KEY_SIZE = 1024;

	private Date createTime;

	private RSAPublicKey publickey;
	private RSAPrivateKey privatekey;
	
	public RSAPrivateKey getRSAPrivate() {
		return privatekey;
	}
	public RSAPublicKey getRSAPublic() {
		return publickey;
	}
}
