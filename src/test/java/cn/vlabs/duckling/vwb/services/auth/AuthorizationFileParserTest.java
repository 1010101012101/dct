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
package cn.vlabs.duckling.vwb.services.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.Permission;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.policy.AuthorizationFileParser;

public class AuthorizationFileParserTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testParseEntry() throws Exception {
		String fileName = "WebRoot/WEB-INF/conf/duckling.policy";
		//System.out.println(file.getCanonicalPath());
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
		Assert.assertNotNull(br);
		
		AuthorizationFileParser afp = new AuthorizationFileParser(br);
		Acl acl = afp.parseEntry();
		Enumeration<AclEntry> entrys = acl.entries();
		
		int entryNum = 0;
		int permNum = 0;
		while(entrys.hasMoreElements()) {
			entryNum ++;
			AclEntry entry = entrys.nextElement();
			Enumeration<Permission> enumer = entry.permissions();
			while(enumer.hasMoreElements()) {
				enumer.nextElement();
				permNum ++;
			}
		}
		
		Assert.assertEquals(permNum, 4);
		Assert.assertEquals(entryNum, 2);
	}

}