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

/**
 * Bytes <--> Integer 转化工具
 * 
 * @date 2011-11-29
 * @author xiejj@cnic.cn
 */
public abstract class ByteUtils {
	public static int byte2Int(byte[] bytes) {
		return byte2Int(bytes, 0);
	}

	public static int byte2Int(byte[] bytes, int offset) {
		int val = 0;
		for (int i = 0; i < 4; i++) {
			byte b = bytes[i+offset];
			int orBin = b & 0x7f;
			if (b < 0) {
				orBin = orBin | 0x80;
			}
			val = (val << 8) | orBin;
		}
		return val;
	}

	public static long byte2Long(byte[] bytes, int offset) {
		long val = 0;
		for (int i = 0; i < 8; i++) {
			byte b = bytes[i+offset];
			long orBin = b & 0x7f;
			if (b < 0) {
				orBin = orBin | 0x80;
			}
			val = (val << 8) | orBin;
		}
		return val;
	}

	public static void copy(byte[] dest, int destOffset, byte[] src,
			int srcOffset, int len) {
		for (int i = 0; i < len; i++) {
			dest[destOffset + i] = src[srcOffset + i];
		}
	}

	public static boolean equals(byte[] a, byte[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	public static byte[] extract(byte[] input, int offset, int len) {
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = input[i + offset];
		}
		return result;
	}

	public static byte[] long2Bytes(long val) {
		byte[] bytes = new byte[8];
		for (int i = 7; i >= 0; i--) {
			bytes[i] = (byte) (val & 0xFF);
			val = val >>> 8;
		}
		return bytes;
	}

	public static byte[] int2Bytes(int val) {
		byte[] bytes = new byte[4];
		for (int i = 3; i >= 0; i--) {
			bytes[i] = (byte) (val & 0xFF);
			val = val >>> 8;
		}
		return bytes;
	}
}
