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

package cn.vlabs.duckling.vwb.service.skin;



/**
 * 远程的存放于CLB中的Skin
 * 
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class RemoteSkin extends Skin {
	
	private int clbId;

	private int id;

	private String space;
	public int getClbId() {
		return clbId;
	}


	public int getId() {
		return id;
	}

	public String getSpace() {
		return space;
	}

	public boolean isShared() {
		return getSiteId()==SHARE_SITE_ID;
	}

	public boolean isInFileSystem() {
		return false;
	}

	public void setClbId(int clbId) {
		this.clbId = clbId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSpace(String space) {
		this.space = space;
	}
	
	public void setWebPath(String baseUrl) {
		if (!baseUrl.endsWith("/")) {
			this.webPath = baseUrl + "/";
		} else {
			this.webPath = baseUrl;
		}
		this.webPath = this.webPath + "trivial/" + space;
	}

}
