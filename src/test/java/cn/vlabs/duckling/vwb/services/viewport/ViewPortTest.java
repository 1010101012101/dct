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

package cn.vlabs.duckling.vwb.services.viewport;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.viewport.ViewPort;

/**
 * Introduction Here.
 * @date Feb 3, 2010
 * @author IBM
 */
public class ViewPortTest {
	private ViewPort vp ;
	/**
	 * Brief Intro Here
	 * @param
	 */
	@Before
	public void setUp() throws Exception {
		vp = new ViewPort();
	}

	/**
	 * Brief Intro Here
	 * @param
	 */
	@After
	public void tearDown() throws Exception {
		vp = null;
	}

	@Test
	public void testLeftMenu() {
		vp.setLeftMenu(ViewPort.INHERIT);
		assertTrue(vp.isShowLeftMenu());
		vp.setLeftMenu(ViewPort.DISABLED);
		assertFalse(vp.isShowLeftMenu());
	}
	@Test
	public void testTrail() {
		vp.setTrail(ViewPort.INHERIT);
		assertTrue(vp.isShowTrail());
		vp.setTrail(ViewPort.DISABLED);
		assertFalse(vp.isShowTrail());
	}
	
	@Test
	public void testTopMenu() {
		vp.setTopMenu(ViewPort.DISABLED);
		assertTrue(vp.isShowTopMenu());
		vp.setTopMenu(ViewPort.DISABLED);
		assertFalse(vp.isShowTopMenu());
	}
}
