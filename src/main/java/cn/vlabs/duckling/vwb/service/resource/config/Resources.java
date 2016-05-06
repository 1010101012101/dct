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

import java.util.ArrayList;
import java.util.List;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
/**
 * Introduction Here.
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
@XStreamAlias("Resources")
public class Resources {
	
	public Resources() {
		items = new ArrayList<ConfigItem>();
	}
	
	public void addItem(ConfigItem item) {
		items.add(item);
	}
	
	public void addItems(List<ConfigItem> items) {
		items.addAll(items);
	}
	
	public List<ConfigItem> getItems(){
		return items;
	}
	
	@XStreamImplicit
	private List<ConfigItem> items;
}
