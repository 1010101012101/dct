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

import cn.vlabs.duckling.vwb.ui.rsi.api.AbstractSiteAdminRequestItem;

/**
 * @date 2010-5-14
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerRequestItem extends AbstractSiteAdminRequestItem{
	private int resourceId;
    private String bannerName;
    private String fileName;
    private String firstTitle;
    private String secondTitle;
    private String thirdTitle;

	/**
	 * @return the resourceId
	 */
	public int getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * @return the bannerName
	 */
	public String getBannerName() {
		return bannerName;
	}
	/**
	 * @param bannerName the bannerName to set
	 */
	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
