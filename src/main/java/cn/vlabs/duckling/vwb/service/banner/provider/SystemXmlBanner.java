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

package cn.vlabs.duckling.vwb.service.banner.provider;

import org.json.JSONObject;

import cn.vlabs.duckling.vwb.service.banner.Banner;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * @date 2013-5-27
 * @author xuzhijian
 */
@XStreamAlias("Banner")
public class SystemXmlBanner extends Banner {
private String leftName;
private String rightName;
private String middleName;
public String getLeftName() {
	return leftName;
}

public void setLeftName(String leftName) {
	this.leftName = leftName;
}

public String getRightName() {
	return rightName;
}

public void setRightName(String rightName) {
	this.rightName = rightName;
}

public String getMiddleName() {
	return middleName;
}

public void setMiddleName(String middleName) {
	this.middleName = middleName;
}

public void setBannerProfile(){
	JSONObject json = new JSONObject();
	json.put("dirName", getDirName());
	json.put("firstTitle", getFirstTitle());
	json.put("secondTitle", getSecondTitle());
	json.put("thirdTitle",getThirdTitle());
	json.put("leftName",this.getLeftName());
	json.put("rightName",this.getRightName());
	json.put("middleName",this.getMiddleName());
	super.setBannerProfile(json.toString());
}



}
