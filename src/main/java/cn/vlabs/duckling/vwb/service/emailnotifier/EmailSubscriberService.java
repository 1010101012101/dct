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

package cn.vlabs.duckling.vwb.service.emailnotifier;

import java.util.Collection;
import java.util.List;

/**
 * Introduction Here.
 * 
 * @date Mar 4, 2010
 * @author key
 */
public interface EmailSubscriberService {
	/**
	 * 查找所有的订阅信息
	 * 
	 * @return
	 */
	Collection<EmailSubscriber> getAllSubscribes(int siteId);

	/**
	 * 搜索订阅信息
	 * 
	 * @param receiver
	 *            订阅者邮件
	 * @param pageTitle
	 *            订阅的页面名称
	 * @param rec_time
	 *            订阅通知的发送时间
	 * @return 所有符合条件的订阅者
	 */
	Collection<EmailSubscriber> findSubscribe(int siteId, String receiver,
			String pageTitle, int rec_time);

	/**
	 * 删除订阅
	 * 
	 * @param eSubscriberId
	 *            订阅信息ID
	 * @return
	 */
	void removeSubscribe(int siteId, int eSubscriberId);

	/**
	 * 查询需要在某个时间发送的更新通知
	 * 
	 * @param hTime
	 *            订阅的发送时间
	 * @return
	 */
	Collection<EmailSubscriber> findReceiveAt(int siteId, int hTime);

	/**
	 * 查询所有关于该页面的订阅
	 * 
	 * @param resourceId
	 * @return
	 */
	Collection<EmailSubscriber> findPageSubscribes(int siteId, int resourceId);

	/**
	 * 创建邮件通知
	 * 
	 * @param subscribes
	 */
	void createSubscribes(int siteId, Collection<EmailSubscriber> subscribes);

	/**
	 * 查找用户的订阅信息
	 * 
	 * @param name
	 * @return
	 */
	List<EmailSubscriber> findUserSubscribes(int siteId, String name);
}