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

package cn.vlabs.duckling.vwb.ui.rsi.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @date 2012-11-22
 * @author xiejj
 */
public class RemoteSiteServiceTest {

	@Test
	public void testGetSiteDomains() throws DCTRsiServiceException {
		VWBAppConnection conn = new VWBAppConnection(
				"http://localhost/dct/ServiceServlet", null);
		RemoteSiteService service=VWBRemoteServiceFactory.getRemoteSiteService(conn);
//		String[] domains = service.getSiteDomains(41);
//		assertNotNull(domains);
//		assertEquals("xiejj13.csp2.escience.cn", domains[0]);
		
		String[] newDomains = new String[]{"xiejj3.csp2.escience.cn", "xiejj13.csp2.escience.cn", "xiejj14.csp2.escience.cn"};
		service.updateSiteDomains(41, newDomains );
		
		String[] nowInService = service.getSiteDomains(41);
		assertArrayEquals(newDomains, nowInService);
	}
}
