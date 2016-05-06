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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import cn.vlabs.duckling.common.crypto.impl.RSAKey;

/**
 * 密钥文件保存工具
 * 
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 4, 2009 3:31:44 PM
 */
/**
 * ----Begin---- public/publickey/private/privatekey -----End-----
 */
public class KeyFile {
	public InputStream saveAsStream(RSAKey rsa){
		StringBuffer buffer = new StringBuffer();
		encodePublic(rsa, buffer);
		buffer.append("/");
		encodePrivate(rsa, buffer);
		String result;
		try {
			result = saveAsString(buffer.toString());
		} catch (IOException e) {
			throw new EncryptException(e.getMessage());
		}
		if(result!=null&&!"".equals(result.trim())){
			return new ByteArrayInputStream(result.getBytes());
		}
		return null;
		
	}
	public void saveKey(String filename, RSAKey rsa) {
		StringBuffer buffer = new StringBuffer();

		encodePublic(rsa, buffer);
		buffer.append("/");
		encodePrivate(rsa, buffer);
		try {
			save(filename, buffer.toString());
		} catch (IOException e) {
			throw new EncryptException(e.getMessage());
		}
	}

	public RSAKey loadFromPublicKeyContent(String publicKeyContent) {
		RSAKey key = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
					HexUtil.toBytes(publicKeyContent));
			RSAPublicKey newPublicKey = (RSAPublicKey) keyFactory
					.generatePublic(keySpec);
			key = new RSAKey(newPublicKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;

	}

	public RSAKey loadFromString(String keyContent) {
		try {
			return parseContent(readFromString(keyContent).toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public RSAKey load(String filename) {
		try {
			return parseContent(readFile(filename).toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void savePublic(Writer writer, RSAKey key) {
		if (key.getRSAPublic() == null)
			throw new NullPointerException("Public key is null.");
		StringBuffer buffer = new StringBuffer();
		try {
			encodePublic(key, buffer);
			save(writer, buffer.toString());
		} catch (IOException e) {
			throw new EncryptException(e.getMessage());
		}
	}

	public void savePublic(String file, RSAKey key) {
		if (key.getRSAPublic() == null)
			throw new NullPointerException("Public key is null.");
		StringBuffer buffer = new StringBuffer();
		try {
			encodePublic(key, buffer);
			save(file, buffer.toString());
		} catch (IOException e) {
			throw new EncryptException(e.getMessage());
		}
	}

	public void savePrivate(String file, RSAKey key) {
		if (key.getRSAPrivate() == null)
			throw new NullPointerException("Public key is null.");
		StringBuffer buffer = new StringBuffer();
		try {
			encodePrivate(key, buffer);
			save(file, buffer.toString());
		} catch (IOException e) {
			throw new EncryptException(e.getMessage());
		}
	}

	private RSAKey parseContent(String keyString) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKey newPublicKey = null;
			RSAPrivateKey newPrivatekey = null;
			if (keyString == null || keyString.length() == 0)
				return null;
			String[] parts = keyString.toString().split("/");
			if (parts != null && (parts.length == 2 || parts.length == 4)) {
				for (int i = 0; i < parts.length / 2; i++) {
					String keyPart = new String(HexUtil.toBytes(parts[2 * i]));
					byte[] raw = HexUtil.toBytes(parts[2 * i + 1]);
					if ("public".equals(keyPart)) {
						X509EncodedKeySpec keySpec = new X509EncodedKeySpec(raw);
						newPublicKey = (RSAPublicKey) keyFactory
								.generatePublic(keySpec);
					}
					if ("private".equals(keyPart)) {
						PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
								raw);
						newPrivatekey = (RSAPrivateKey) keyFactory
								.generatePrivate(keySpec);
					}
				}
			}
			return new RSAKey(newPublicKey, newPrivatekey);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void encodePrivate(RSAKey rsa, StringBuffer buffer) {
		if (rsa.getRSAPrivate() != null) {
			buffer.append(HexUtil.toHexString("private".getBytes()));
			buffer.append("/");
			buffer.append(HexUtil.toHexString(rsa.getRSAPrivate().getEncoded()));
		}
	}

	private void encodePublic(RSAKey rsa, StringBuffer buffer) {
		if (rsa.getRSAPublic() != null) {
			buffer.append(HexUtil.toHexString("public".getBytes()));
			buffer.append("/");
			buffer.append(HexUtil.toHexString(rsa.getRSAPublic().getEncoded()));
		}
	}

	private StringBuffer readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));
		return unwrap(reader);
	}

	private StringBuffer readFromString(String content) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(content));
		return unwrap(reader);
	}

	private StringBuffer unwrap(BufferedReader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("--")) {
					buffer.append(line);
				}
			}
		} finally {
			if (reader != null)
				reader.close();
		}
		return buffer;
	}

	private void save(Writer writer, String encoded) throws IOException {
		writer.write(BEGIN);
		String line = null;
		int current = 0;
		int next = 64;
		do {
			if (next < encoded.length())
				line = encoded.substring(current, next);
			else
				line = encoded.substring(current);
			writer.write(line);
			writer.write("\n");
			current = next;
			next += 64;
		} while (current < encoded.length());
		writer.write(END);
	}

	private void save(String file, String encoded) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			save(writer, encoded);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
	private String saveAsString(String encoded)throws IOException{
		StringBuffer sb=new StringBuffer();
		sb.append(BEGIN);
		String line = null;
		int current = 0;
		int next = 64;
		do {
			if (next < encoded.length())
				line = encoded.substring(current, next);
			else
				line = encoded.substring(current);
			sb.append(line);
			sb.append("\n");
			current = next;
			next += 64;
		} while (current < encoded.length());
		sb.append(END);
		return sb.toString();
	}

	private static final String BEGIN = "-----BEGIN CERTIFICATE-----\n";

	private static final String END = "-----END CERTIFICATE-----";
}

