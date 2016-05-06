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

package cn.vlabs.duckling.vwb.ui.rsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.common.transmission.SignedEnvelope;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.login.LoginAction;
import cn.vlabs.duckling.vwb.service.login.Subject;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.api.VMT2AuthInfo;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;
import cn.vlabs.rest.ServiceWithInputStream;

import com.thoughtworks.xstream.XStream;

/**
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VWBAppLoginService extends ServiceWithInputStream {
	private static Set<String> ips = null;
	private static Logger log = Logger.getLogger(VWBAppLoginService.class);

	public Object doAction(RestSession session, Object message)
			throws ServiceException {
		log.debug("Login using new umt");
		return newLogin(message);
	}
	private String getVO(SiteMetaInfo site){
		VWBContainer container = VWBContainerImpl.findContainer();
		return container.getSiteConfig().getVO(site.getId());
	}
	/**
	 * @param message
	 * @return
	 */
	private Object newLogin(Object message) {
		VMT2AuthInfo auth = (VMT2AuthInfo) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SignedEnvelope envelope = auth.getAppSignedEnvelope();
		if (ips == null) {
			init();
		}
		if (ips.contains(getRequest().getRemoteAddr())
				|| container.getUserService().verifyByAppId(envelope)) {
			SiteMetaInfo site = VWBContainerImpl.findSite(this.getRequest());
			if (site == null) {
				site = container.getAdminSite();
			}
			List<java.security.Principal> prins = null;
			if (auth.getUser() == null || auth.getUser().equals("")) {
				log.debug("parsing using credential");
				java.security.Principal[] ps = (java.security.Principal[]) new XStream()
						.fromXML(auth.getAppSignedEnvelope().getContent());
				prins = Arrays.asList(ps);
				log.debug("Parsed principals is " + prins);
			} else {
				log.debug("read principals from user service");
				prins = container.getUserService().getUserPrincipal(
						auth.getUser(), getVO(site));
			}
			if (prins != null) {
				log.info("save principals to session");
				try {
					savePrincipals(site.getId(),prins);
				} catch (VWBException e) {
					log.error("App login failed", e);
				}
			} else {
				log.error(String
						.format("No user principals find in vmt( current user=%s, current vo=%s)",
								auth.getUser() + getVO(site)));
				log.error("Please check your vo specified by configuration existed");
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private void init() {
		ips = new HashSet<String>();
		String fileName = getRequest().getSession(true).getServletContext()
				.getRealPath("/WEB-INF/conf/accessIPs.txt");
		try {
			if (new File(fileName).exists()) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(fileName),
								"UTF-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.length() > 0 && !line.startsWith("#")) {// A
																		// pattern
																		// line
						ips.add(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void savePrincipals(int siteId,Collection<java.security.Principal> prins)
			throws VWBException {
		Subject subject = LoginAction.convertPrincipal(prins);
		VWBSession vwbsession = VWBSession.findSession(getRequest());
		vwbsession.setSubject(VWBSession.AUTHENTICATED, subject);
		VWBContainer container= VWBContainerImpl.findContainer();
		container.getAuthenticationService().login(siteId,getRequest());
	}
}
