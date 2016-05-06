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

package cn.vlabs.duckling.vwb.services.emailnotifier;

import java.util.ArrayList;

import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.services.TestService;

/**
 * Introduction Here.
 * @date Mar 16, 2010
 * @author xiejj@cnic.cn
 */
public class NotificationMailTest extends TestService {
//	@Test
//	public void testSend() {
//		VWBSite site = (VWBSite)this.manager.getFactory().getBean("site");
//		MailService mailservice = (MailService)this.manager.getFactory().getBean("sendemailservice");
//		NotificationMail mail = new NotificationMail(site, mailservice);
//		mail.setRecipient("sunp@cnic.cn");
//		mail.setChangeList(1, buildVersionList());
//		mail.send();
//	}
//	@Test
//	public void testSendTwice() {
//		VWBSite site = (VWBSite)this.manager.getFactory().getBean("site");
//		MailService mailservice = (MailService)this.manager.getFactory().getBean("sendemailservice");
//		NotificationMail mail = new NotificationMail(site, mailservice);
//
//		mail.setRecipient("sunp@cnic.cn");
//		mail.setChangeList(1, buildVersionList());
//		mail.send();
//		
//		mail.setRecipient("sunp@cnic.cn");
//		mail.setChangeList(2, buildAnotherVersionList());
//		mail.send();
//	}
	private ArrayList<LightDPage> buildAnotherVersionList(){
		ArrayList<LightDPage> versions = new ArrayList<LightDPage>();
		versions.add(createDPage(1, "sunp@cnic.cn", 1));
		versions.add(createDPage(2, "sunp@cnic.cn", 1));
		versions.add(createDPage(3, "sunp@cnic.cn", 1));
		return versions;
	}
	private ArrayList<LightDPage> buildVersionList(){
		ArrayList<LightDPage> versions = new ArrayList<LightDPage>();
		versions.add(createDPage(1, "sunp@cnic.cn", 1));
		versions.add(createDPage(2, "sunp@cnic.cn", 1));
		versions.add(createDPage(3, "sunp@cnic.cn", 1));
		return versions;
	}
	private DPage createDPage(int version, String author, int resourceId){
		DPage dpage= new DPage();
		dpage.setAuthor(author);
		dpage.setResourceId(resourceId);
		dpage.setVersion(version);
		return dpage;
	}
}
