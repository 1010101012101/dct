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

package cn.vlabs.duckling.vwb;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cn.vlabs.duckling.vwb.spi.VWBContainer;

public class VWBSessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		VWBContainer container = VWBContainerImpl.findContainer();
		container.getFlexSessionService().remove(session.getId());
		container.getLoginSessionService().remove(session.getId());
		container.getAuthenticationService().userOffline(session.getId());
	}

	public static boolean contains(String sessionid) {
		return VWBContainerImpl.findContainer().getFlexSessionService().contains(sessionid);
	}

}
