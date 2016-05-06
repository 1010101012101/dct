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

package cn.vlabs.duckling.vwb.service.banner;

import java.io.Serializable;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * add a complex title ( has 3 components ) field to banner
 * 
 * @date 2011-11-3
 * @author y
 */
@XStreamAlias("BannerWith3Title")
public class BaseBanner implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;


	private int status = 1; //0未发布；1已发布；2默认banner

	private int type = 3;

	private String creator;

	private Date createdTime;

	@XStreamAlias("pageId")
	private int bannerTitle;

	@XStreamOmitField
	private int tempBannerTitle;

	private String ownedtype;

	@XStreamOmitField
	private boolean selected;

	// complex field (dirName,leftName,middelName,rightName,firstTitle,secondeTitle,thirdTitle)
	private String bannerProfile;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSystem() {
		boolean flag = false;
		if ("system".equals(ownedtype)) {
			return true;
		}
		return flag;
	}
	
	public boolean isPublic() {
		boolean flag = false;
		if ("public".equals(ownedtype)) {
			return true;
		}
		return flag;
	}

	public String getOwnedtype() {
		return ownedtype;
	}

	public void setOwnedtype(String ownedtype) {
		this.ownedtype = ownedtype;
	}

	public int getTempBannerTitle() {
		return tempBannerTitle;
	}

	public void setTempBannerTitle(int tempBannerTitle) {
		this.tempBannerTitle = tempBannerTitle;
	}

	public int getBannerTitle() {
		return bannerTitle;
	}

	public void setBannerTitle(int bannerTitle) {
		this.bannerTitle = bannerTitle;
		this.tempBannerTitle = bannerTitle;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

	public String getBannerProfile() {
		return bannerProfile;
	}

	public void setBannerProfile(String bannerProfile) {
		this.bannerProfile = bannerProfile;
	}

}
