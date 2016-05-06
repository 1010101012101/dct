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

package cn.vlabs.duckling.license.impl;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.vlabs.duckling.license.InvalidLicenseException;
import cn.vlabs.duckling.license.License;

/**
 * 简单的授权证书生成工具
 * ┌------------------------------┐
 * |0x03|count|productId|productId|
 * | random   |      timestamp    |
 * |         hash                 |
 * └------------------------------┘
 * @date 2011-11-29
 * @author xiejj@cnic.cn
 */
public class SimpleLicenseAlogrithm implements LicenseAlogrithm {
	private static final byte SIMPLE_ALOGRITHM=0x03;
	
	private int digest(byte[] input, int offset, int len) throws InvalidLicenseException{
		try {
			MessageDigest digest=MessageDigest.getInstance("SHA-1");
			byte[] buff=new byte[len];
			ByteUtils.copy(buff, 0, input, 0, len);
			return digest.digest(buff, 0, len);
		} catch (NoSuchAlgorithmException e) {
			throw new InvalidLicenseException(e.getMessage());
		} catch (DigestException e) {
			throw new InvalidLicenseException(e.getMessage());
		}
	}
	
	public License verify(byte[] license) throws InvalidLicenseException {
		if (license==null||license.length<1)
			throw new InvalidLicenseException("Empty license is inputed.");
		if (license[0]!=SIMPLE_ALOGRITHM)
			throw new InvalidLicenseException("Invalid alogrithm.");
		//Product ID Count
		int count=ByteUtils.byte2Int(license, 1);
		
		//Product ids
		int[] products=new int[count];
		for (int i=0;i<count;i++){
			products[i]=ByteUtils.byte2Int(license,4*i+5);
		}
		//Random
//		int random=ByteUtils.byte2Int(license, 4*count+5);
		
		//Timestamp
		long timestamp=ByteUtils.byte2Long(license, 4*count+5+4);
		
		int hash = ByteUtils.byte2Int(ByteUtils.extract(license, 4*count+5+4+8, 4));
		int calHash=digest(license, 0, 4*count+5+4+8);
		if (hash==calHash){
			return new License(products, timestamp);
		}else{
			throw new InvalidLicenseException("License verify failed.");
		}
	}
	/**
	 * ┌------------------------------┐
	 * |0x03|count|productId|productId|
	 * | random   |      timestamp    |
	 * |         hash                 |
	 * └------------------------------┘
	 * @throws InvalidLicenseException 
	 */
	public byte[] genrate(License license) throws InvalidLicenseException {
		int productCount=license.getProducts().length;
		int currentPos=0;
		int len=5+4*productCount+4+8+4;
		byte[] result=new byte[len];
		
		result[0]=SIMPLE_ALOGRITHM;
		currentPos++;
		
		ByteUtils.copy(result, currentPos, ByteUtils.int2Bytes(productCount), 0, 4);
		currentPos+=4;
		
		for (int productId:license.getProducts()){
			ByteUtils.copy(result, currentPos, ByteUtils.int2Bytes(productId), 0, 4);
			currentPos+=4;
		}
		
		Random r = new Random();
		ByteUtils.copy(result, currentPos,ByteUtils.int2Bytes(r.nextInt()), 0, 4);
		currentPos+=4;
		
		ByteUtils.copy(result, currentPos, ByteUtils.long2Bytes(license.getGenerateTime().getTime()), 0, 8);
		currentPos+=8;
		
		ByteUtils.copy(result, currentPos, ByteUtils.int2Bytes(digest(result, 0, currentPos)), 0, 4);
		
		return result;
	}
	
}
