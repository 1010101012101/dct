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

import org.apache.commons.lang.StringUtils;


/**
 * 皮肤类
 * 
 * @date 2013-3-28
 * @author xiejj@cstnet.cn
 */
public abstract class Skin {
	public static final int SHARE_SITE_ID = 0;
	public static final int ADMIN_SITE_ID = 1;
	private String name;

	private int siteId;

	protected String webPath;
	
	private String template;
	
	private String currentSiteTemplate;

	public abstract void setWebPath(String baseUrl);

	public String getName() {
		return name;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
	public abstract boolean isShared();

	public abstract boolean isInFileSystem();

	public String getThumb() {
		return webPath + "/thumb.jpg";
	}

	public String getWebPath() {
		return webPath;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setCurrentSiteTemplate(String currentSiteTemplate){
		this.currentSiteTemplate=currentSiteTemplate;
	}
	
	public String getCurrentSiteTemplate(){
		return currentSiteTemplate;
	}
	
	public boolean isAvailable(){
		if(StringUtils.isBlank(getTemplate())||StringUtils.isBlank(getCurrentSiteTemplate())){
			return false;
		}
		return StringUtils.equals(getTemplate(), getCurrentSiteTemplate());
	}
	
	
}
