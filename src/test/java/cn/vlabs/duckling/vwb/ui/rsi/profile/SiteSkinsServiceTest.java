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

package cn.vlabs.duckling.vwb.ui.rsi.profile;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.ui.rsi.api.DCTConnection;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiServiceException;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinRequestItem;

/**
 * @date 2011-12-5
 * @author y
 */
public class SiteSkinsServiceTest {
	DCTConnection conn;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		 conn = new DCTConnection(
				"http://localhost/dct/ServiceServlet", 
				"admin@root.umt", "");
		
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.ui.rsi.profile.SiteSkinsService#getAllSkins(cn.vlabs.rest.RestSession, java.lang.Object)}.
	 * @throws DCTRsiServiceException 
	 */
	@Test
	public void testGetAllSkins() throws DCTRsiServiceException {
		SkinRequestItem message = new SkinRequestItem();
		message.setSiteId(1);
		message.setMethod("getAllSkins");
		List<SkinItem> skinitems = (List<SkinItem>) conn.sendService("skinsService", message);
		System.out.println(skinitems.size());
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.ui.rsi.profile.SiteSkinsService#setSkin(cn.vlabs.rest.RestSession, java.lang.Object)}.
	 */
	@Test
	public void testSetSkin() {
		fail("Not yet implemented");
	}

}
