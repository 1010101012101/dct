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

package cn.vlabs.duckling.vwb.ui.rsi.api.banner;

import java.util.Date;

/**
 * @date 2010-7-2
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerItem {
	private int id;
	private String url;
	private Date createdTime;
	private String name;
	private String firstTitle;
	private String secondTitle;
	private String thirdTitle;
	/**
	 * banner图片的url
	 * @return the uri
	 */
	public String getURL() {
		return url;
	}
	/**
	 * 设置站点图片的url
	 * @param uri the uri to set
	 */
	public void setURL(String url) {
		this.url = url;
	}
	/**
	 * 获取banner创建时间
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}
	/**
	 * 设置banner创建时间
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * 获取banner的名字
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置banner的名字
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取banner的id
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * 设置banner的id
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstTitle() {
		return firstTitle;
	}
	public void setFirstTitle(String firstTitle) {
		this.firstTitle = firstTitle;
	}
	public String getSecondTitle() {
		return secondTitle;
	}
	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}
	public String getThirdTitle() {
		return thirdTitle;
	}
	public void setThirdTitle(String thirdTitle) {
		this.thirdTitle = thirdTitle;
	}
	
	

}
