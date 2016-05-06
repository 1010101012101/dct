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

package cn.vlabs.duckling.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @date 2011-11-29
 * @author xiejj
 */
public class LicenseUtilTest {

	@Test
	public void test() throws InvalidLicenseException {
		License testLicense = new License(new int[] {1,2,3,4,5,6},
				System.currentTimeMillis());
		String licenseTxt = LicenseUtil.generate(testLicense);
		System.out.println(licenseTxt);
		License license = LicenseUtil.verify(licenseTxt);
		assertNotNull(license);
		assertNotNull(license.getProducts());
		assertEquals(license.getGenerateTime(), testLicense.getGenerateTime());
	}
	
	@Test
	public void generate() throws InvalidLicenseException {
		for (int i=1;i<=6;i++){
			License license = new License(new int[] {i},System.currentTimeMillis());
			String licenseTxt = LicenseUtil.generate(license);
			System.out.println(i+":"+licenseTxt);
		}
	}
}
