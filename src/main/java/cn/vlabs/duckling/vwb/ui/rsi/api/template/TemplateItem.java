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

package cn.vlabs.duckling.vwb.ui.rsi.api.template;

/**
 * @date 2010-5-30
 * @author Fred Zhang (fred@cnic.cn)
 */
public class TemplateItem {
	private String name;
	private String defaultSkin;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultSkin() {
		return defaultSkin;
	}
	public void setDefaultSkin(String defaultSkin) {
		this.defaultSkin = defaultSkin;
	}
	
	
}