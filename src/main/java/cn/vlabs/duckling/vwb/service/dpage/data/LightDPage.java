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

package cn.vlabs.duckling.vwb.service.dpage.data;

import java.util.Date;

import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * 
 * @date 2010-2-10
 * @author euniverse
 */
public class LightDPage extends Resource{
	private int resourceId;
	private int version;
	private Date time;
	private String author;
    private String title;
    private int pageorder;
    
	/**
	 * @return the pageorder
	 */
	public int getPageorder() {
		return pageorder;
	}

	/**
	 * @param pageorder the pageorder to set
	 */
	public void setPageorder(int pageorder) {
		this.pageorder = pageorder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String creator) {
		this.author = creator;
	}
	public String getType() {
		return Resource.TYPE_DPAGE;
	}

	

}
