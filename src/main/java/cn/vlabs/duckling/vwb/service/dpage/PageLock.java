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

package cn.vlabs.duckling.vwb.service.dpage;

import java.io.Serializable;

/**
 * Introduction Here.
 * @date 2010-3-12
 * @author 狄
 */
public class PageLock implements Serializable {
	private static final long serialVersionUID = 1L;
	private int   pageid;
    private String pagelocker;
    private int pageVersion;
    private String sessionId;
    private String usrIp;
    
	public String getUsrIp() {
		return usrIp;
	}
	public void setUsrIp(String usrIp) {
		this.usrIp = usrIp;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getPageVersion() {
		return pageVersion;
	}
	public void setPageVersion(int pageVersion) {
		this.pageVersion = pageVersion;
	}
	public int getPageid() {
		return pageid;
	}
	public void setPageid(int pageid) {
		this.pageid = pageid;
	}
	public String getPagelocker() {
		return pagelocker;
	}
	public void setPagelocker(String pagelocker) {
		this.pagelocker = pagelocker;
	}

}
