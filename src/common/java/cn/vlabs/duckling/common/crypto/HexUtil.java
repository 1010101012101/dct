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

public class HexUtil {
	public static String toHexString(byte[] bytes){
		if (bytes==null||bytes.length==0){
			return "";
		}else{
			StringBuffer buffer =new StringBuffer();
			for (byte b:bytes){
				buffer.append(CHAR_ARRAY[(b>>4)&0xF]);//hi part
				buffer.append(CHAR_ARRAY[b&0xF]);	  //low part
			}
			return buffer.toString();
		}
	}
	
	public static byte[] toBytes(String hex){
		if (hex==null || hex.length()==0 || hex.length() %2 ==1)
			return null;
		
		int byteCount=hex.length()/2;
		byte[] bytes = new byte[byteCount];
		for (int i=0;i<byteCount;i++){
			int beginIndex = i*2;
			bytes[i]=(byte)Integer.parseInt(hex.substring(beginIndex, beginIndex+2), 16);
		}
		return bytes;
	}
	
	public static byte[] ltrim(byte[] a){
		if (a==null ||a.length==0){
			return a;
		}
		int index=0;
		while ( index<a.length && a[index]==0){
			index++;
		}
		if (index==0){
			return a;
		}else{
			byte[] b = new byte[a.length-index];
			System.arraycopy(a, index, b, 0, b.length);
			return b;
		}
	}
	public static boolean isEqual(byte[] a, byte[] b){
		if (a==b)
			return true;
		if (a==null || b==null || a.length!=b.length)
			return false;
		for (int i=0;i<a.length;i++){
			if (a[i]!=b[i])
				return false;
		}
		return true;
	}
	private static final char[] CHAR_ARRAY=new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8','9','A','B','C', 'D','E','F'};
}
