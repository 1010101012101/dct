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

import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * Introduction Here.
 * 
 * @date 2010-2-26
 * @author Fred Zhang (fred@cnic.cn)
 */
public class Banner extends BaseBanner {
	/*public static final int FIRSTBANNER = 1;*/

	public static final int INHERITSKIN = -2;

	public static final int WITH3TITLETYPE = 3;

	@XStreamOmitField
	private String leftPictureUrl;
	@XStreamOmitField
	private int leftPictureClbId;
	@XStreamOmitField
	private String rightPictureUrl;
	@XStreamOmitField
	private int rightPictureClbId;
	@XStreamOmitField
	private String middlePictureUrl;
	@XStreamOmitField
	private int middlePictureClbId;
	@XStreamOmitField
	private int cssClbId;
	@XStreamOmitField
	private String cssUrl;

	private String firstTitle="";
	private String secondTitle="";
	private String thirdTitle="";

	private String dirName;
	
	




	public int getCssClbId() {
		return cssClbId;
	}

	public void setCssClbId(int cssClbId) {
		this.cssClbId = cssClbId;
	}

	public String getCssUrl() {
		return cssUrl;
	}

	public void setCssUrl(String cssUrl) {
		this.cssUrl = cssUrl;
	}

	public String getLeftPictureUrl() {
		return leftPictureUrl;
	}

	public void setLeftPictureUrl(String leftPictureUrl) {
		this.leftPictureUrl = leftPictureUrl;
	}

	public String getRightPictureUrl() {
		return rightPictureUrl;
	}

	public void setRightPictureUrl(String rightPictureUrl) {
		this.rightPictureUrl = rightPictureUrl;
	}

	public String getMiddlePictureUrl() {
		return middlePictureUrl;
	}

	public void setMiddlePictureUrl(String middlePictureUrl) {
		this.middlePictureUrl = middlePictureUrl;
	}

	public int getLeftPictureClbId() {
		return leftPictureClbId;
	}

	public void setLeftPictureClbId(int leftPictureClbId) {
		this.leftPictureClbId = leftPictureClbId;
	}


	public int getRightPictureClbId() {
		return rightPictureClbId;
	}

	public void setRightPictureClbId(int rightPictureClbId) {
		this.rightPictureClbId = rightPictureClbId;
	}

	public int getMiddlePictureClbId() {
		return middlePictureClbId;
	}

	public void setMiddlePictureClbId(int middlePictureClbId) {
		this.middlePictureClbId = middlePictureClbId;
	}

	public String getFirstTitle() {
		return this.firstTitle==null?"":firstTitle;
	}

	public void setFirstTitle(String firstTitle) {
		this.firstTitle = firstTitle;
	}

	public String getSecondTitle() {
		return this.secondTitle==null?"":secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public String getThirdTitle() {
		return this.thirdTitle==null?"":thirdTitle;
	}

	public void setThirdTitle(String thirdTitle) {
		this.thirdTitle = thirdTitle;
	}
	
	public void setBannerProfile(){
		JSONObject json = new JSONObject();
		json.put("dirName", getDirName());
		json.put("firstTitle", getFirstTitle());
		json.put("secondTitle", getSecondTitle());
		json.put("thirdTitle",getThirdTitle());
		super.setBannerProfile(json.toString());
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
}
