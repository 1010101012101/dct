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

package cn.vlabs.duckling.vwb.service.resource.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Introduction Here.
 * 
 * @date Mar 5, 2010
 * @author xiejj@cnic.cn
 */
@XStreamAlias("portlet")
public class PortletItem {
	@XStreamAsAttribute
	private String context;
	
	@XStreamAsAttribute
	private String name;

	public String getContext() {
		return context;
	}

	public String getName() {
		return name;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setName(String name) {
		this.name = name;
	}
}
