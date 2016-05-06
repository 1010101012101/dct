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

package cn.vlabs.duckling.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import cn.vlabs.duckling.common.util.Base64.DecodingException;

/**
 * @date 2012-4-10
 * @author xiejj
 */
public class Base64Test {

	@Test
	public void testDecodeString() throws DecodingException {
		String base = "QL0AFWMIX8NRZTKeof9cXsvbvu8=";
		byte[] decoded =Base64.decode(base);
		String result = Base64.encode(decoded, 0, decoded.length, 0, "\n");
		assertEquals(base, result);
	}
}
