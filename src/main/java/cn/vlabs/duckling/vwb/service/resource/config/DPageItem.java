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


import cn.vlabs.duckling.vwb.service.resource.Resource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Introduction Here.
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */

@XStreamAlias("DPage")
public class DPageItem extends AbstractConfigItem {
	private String file;

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}
	
	@Override
	public String getType() {
		return Resource.TYPE_DPAGE;
	}
}
