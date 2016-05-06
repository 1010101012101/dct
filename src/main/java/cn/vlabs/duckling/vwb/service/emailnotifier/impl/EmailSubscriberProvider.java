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

import java.util.List;

import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;

/**
 * Introduction Here.
 * 
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public interface EmailSubscriberProvider {
	/**
	 * 创建页面更新订阅
	 * 
	 * @param eSubscriber
	 */
	void createEmailSubscriber(int siteId,List<EmailSubscriber> eSubscriber);

	/**
	 * 查询所有页面更新订阅
	 * 
	 * @return
	 */
	List<EmailSubscriber> getAllEmailSubscribers(int siteId);

	/**
	 * 查询页面更新订阅信息
	 * 
	 * @param id
	 * @return
	 */
	EmailSubscriber getEmailSubscriberById(int siteId,int id);

	/**
	 * 更新页面更新订阅
	 * 
	 * @param banner
	 */
	void updateEmailSubscriber(int siteId,EmailSubscriber banner);

	/**
	 * 删除页面更新通知
	 * 
	 * @param id
	 */
	void removeEmailSubscribers(int id);

	/**
	 * 查找邮件订阅
	 * 
	 * @param subscriber
	 * @param receiver
	 * @param pageName
	 * @param rev_time
	 * @param resourceId
	 * @return
	 */
	List<EmailSubscriber> findEmailSubscribers(int siteId,String receiver,
			String pageName, int rev_time);

	/**
	 * 删除邮件订阅
	 * 
	 * @param id
	 */
	void delete(int id);

	/**
	 * 判断订阅是否已经存在
	 * 
	 * @param sub
	 * @return
	 */
	boolean isSubscribeExist(int siteId,EmailSubscriber sub);

	/**
	 * @param receiveTime
	 * @return
	 */
	List<EmailSubscriber> findReceiveAt(int siteId,int receiveTime);

	/**
	 * @param resourceId
	 * @return
	 */
	List<EmailSubscriber> findPageSubscribes(int siteId,int resourceId);

	/**
	 * @param username
	 * @return
	 */
	List<EmailSubscriber> findUserSubScribe(int siteId,String username);
}