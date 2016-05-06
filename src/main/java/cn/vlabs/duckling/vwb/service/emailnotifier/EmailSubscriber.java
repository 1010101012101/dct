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

import java.util.Calendar;
import java.util.Date;

/**
 * Introduction Here.
 * @date Mar 1, 2010
 * @author y wkm(wkm@cnic.cn)
 */
public class EmailSubscriber {
	private int id;
	private String m_notify_creator;
	private String m_receiver;
	private int m_rec_time;
	private String m_pagetitle;
	private String m_resourceId;

	public String getresourceId() {
		return m_resourceId;
	}

	public void setresourceId(String resourceid) {
		this.m_resourceId = resourceid;
	}

	public String getNotify_creator() {
		return m_notify_creator;
	}

	public void setNotify_creator(String notify_creator) {
		this.m_notify_creator = notify_creator;
	}

	public String getReceiver() {
		return m_receiver;
	}

	public void setReceiver(String receiver) {
		this.m_receiver = receiver;
	}

	public int getRec_time() {
		return m_rec_time;
	}

	public void setRec_time(int rec_time) {
		this.m_rec_time = rec_time;
	}

	public String getPageTitle() {
		return m_pagetitle;
	}

	public void setPageTitle(String page_name) {
		this.m_pagetitle = page_name;
	}

	private int hourOffsetFrom(int offset) {
		offset = (offset % 24); // -23 to 23
		offset = (offset + 24); // 0 to 46
		offset = (offset % 24); // 0 to 23
		return offset;
	}

	public boolean shouldSendAt(Date scheduledDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(scheduledDate);
		return (cal.get(Calendar.HOUR_OF_DAY) == hourOffsetFrom(m_rec_time));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean subscribeTo(String resourceId) {
		if (m_resourceId.equals("*"))
			return true;
		return m_resourceId.equals(resourceId);
	}

}
