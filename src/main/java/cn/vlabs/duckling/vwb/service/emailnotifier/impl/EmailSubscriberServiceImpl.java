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

package cn.vlabs.duckling.vwb.service.emailnotifier.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriberService;

/**
 * Introduction Here.
 * 
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class EmailSubscriberServiceImpl implements EmailSubscriberService {
	private EmailSubscriberProvider provider;

	private boolean isSubScribeExist(int siteId, EmailSubscriber sub) {
		return provider.isSubscribeExist(siteId, sub);
	}

	@Override
	public void createSubscribes(int siteId,
			Collection<EmailSubscriber> subscribes) {
		ArrayList<EmailSubscriber> nonExists = new ArrayList<EmailSubscriber>();
		for (EmailSubscriber sub : subscribes) {
			if (!isSubScribeExist(siteId, sub)) {
				nonExists.add(sub);
			}
		}
		if (nonExists.size() > 0) {
			provider.createEmailSubscriber(siteId, nonExists);
		}
	}

	@Override
	public Collection<EmailSubscriber> getAllSubscribes(int siteId) {
		return provider.getAllEmailSubscribers(siteId);
	}

	@Override
	public Collection<EmailSubscriber> findSubscribe(int siteId,
			String receiver, String pageName, int rec_time) {
		return provider.findEmailSubscribers(siteId, receiver, pageName,
				rec_time);
	}

	@Override
	public void removeSubscribe(int siteId, int subscribeId) {
		provider.removeEmailSubscribers(subscribeId);
	}

	public void setProvider(EmailSubscriberProvider provider) {
		this.provider = provider;
	}

	@Override
	public Collection<EmailSubscriber> findReceiveAt(int siteId, int receiveTime) {
		return provider.findReceiveAt(siteId, receiveTime);
	}

	@Override
	public List<EmailSubscriber> findPageSubscribes(int siteId, int resourceId) {
		return provider.findPageSubscribes(siteId, resourceId);
	}

	@Override
	public List<EmailSubscriber> findUserSubscribes(int siteId, String username) {
		return provider.findUserSubScribe(siteId, username);
	}
}
