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

package cn.vlabs.duckling.vwb.services.auth.acl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission;
import cn.vlabs.duckling.vwb.service.auth.policy.PolicyUtil;

/**
 * Introduction Here.
 * @date May 6, 2010
 * @author zzb
 */
public class AclTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAclToString() {
		AclEntry aclEntry = new AclEntry();
		Acl acl = new Acl();
		
		PagePermission edit = PagePermission.EDIT;
		PagePermission view = new PagePermission("*", PagePermission.VIEW_ACTION);
		assertTrue("edit imply view", edit.implies(view));
		assertFalse("edit imply view", view.implies(edit));
		
		PagePermission view1 = new PagePermission("1", PagePermission.VIEW_ACTION);
		assertTrue("edit imply view", edit.implies(view1));
		assertFalse("edit imply view", view1.implies(edit));
		
		PagePermission edit1 = new PagePermission("*",PagePermission.EDIT_ACTION);
		assertTrue("edit imply edit", edit.implies(edit1));
		assertTrue("edit imply edit", edit1.implies(edit));
		
		PagePermission edit2 = new PagePermission("1",PagePermission.EDIT_ACTION);
		assertTrue("edit imply specify edit", edit.implies(edit2));
		assertFalse("specify edit imply edit", edit2.implies(edit));
		
		Principal user = new UserPrincipal("zzb");
		aclEntry.addPermission(view);
		aclEntry.addPermission(edit);
		aclEntry.addPermission(view1);
		aclEntry.addPermission(edit1);
		aclEntry.addPermission(edit2);
		aclEntry.setPrincipal(user);
		acl.addEntry(aclEntry);
		// System.out.println(acl);
		System.out.println(PolicyUtil.acl2PolicyString(acl));
	}
	
	/*@Test
	public void testClassName() {
		System.out.println(new Object().getClass().getName());
	}*/

}