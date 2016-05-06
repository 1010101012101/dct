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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @date 2013-3-14
 * @author xiejj
 */
public class HexUtilTest {

	/**
	 * Test method for {@link cn.vlabs.duckling.common.crypto.HexUtil#ltrim(byte[])}.
	 */
	@Test
	public void testLtrim() {
		byte[] result = HexUtil.ltrim(new byte[]{});
		assertEquals(0, result.length);
		
		result = HexUtil.ltrim(new byte[]{0});
		assertEquals(0, result.length);
		
		
		result = HexUtil.ltrim(new byte[]{0,0});
		assertEquals(0, result.length);
		
		result = HexUtil.ltrim(new byte[]{0,0,1});
		assertEquals(1, result.length);
		
		result = HexUtil.ltrim(new byte[]{0, 1,0});
		assertEquals(2, result.length);
	}

}
