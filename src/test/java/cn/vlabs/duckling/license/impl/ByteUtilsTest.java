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

import org.junit.Assert;
import org.junit.Test;

/**
 * @date 2011-11-29
 * @author xiejj
 */
public class ByteUtilsTest {

	/**
	 * Test method for {@link cn.vlabs.duckling.license.impl.ByteUtils#byte2Int(byte[])}.
	 */
	@Test
	public void testByte2Int() {
		byte[] bytes = ByteUtils.int2Bytes(65535);
		byte[] required=new byte[]{0,0,(byte)255,(byte)255};
		assertEquals(bytes, required);
		
		int[] tests=new int[]{0xFFFFFFFF, 0x80000000,0xffff};
		for (int t:tests){
			Assert.assertEquals(t, ByteUtils.byte2Int(ByteUtils.int2Bytes(t)));
		}
	}
	
	private void assertEquals(byte[]a, byte[] b){
		for(int i=0;i<4;i++){
			Assert.assertEquals(a[i], b[i]);
		}
	}
}
