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

package cn.vlabs.duckling.vwb.service.attach;

import cn.vlabs.duckling.util.Utility;

public class CLBAttachment extends Attachment {
	private int clbId;
	private int length;
	private String parentName;
	private int siteId;
	private String title;
	private String updateby;

	private String version;

	public CLBAttachment(String parentPage, String fileID, String version) {
		super(parentPage, fileID);
		parentName = parentPage;
		this.version = version;
		super.setResourceId(Integer.parseInt(parentPage));
	}

	public int getClbId() {

		return clbId;
	}

	public String getCLBVersion() {
		return version;
	}

	public String getHashID() {
		String hashid = "clb:clb::" + getClbId();
		return Utility.getBASE64(hashid);
	}

	public String getLastModifiedCustom(String fmt) {
		return Utility.getDateCustom(this.getTime(), fmt);
	}

	public int getLength() {
		return length;
	}

	public String getParentName() {
		return parentName;
	}

	public int getSiteId() {
		return this.siteId;
	}

	public String getTitle() {
		return title;
	}

	public String getUpdateby() {
		return updateby;
	}

	public void setClbId(int id) {
		this.clbId = id;
	}

	public void setCLBVersion(String param) {
		this.version = param;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUpdateby(String by) {
		this.updateby = by;
	}

	public String toString() {
		return "CLBAttachment [" + this.getFileName() + ";mod="
				+ this.getTime() + "]";
	}
}
