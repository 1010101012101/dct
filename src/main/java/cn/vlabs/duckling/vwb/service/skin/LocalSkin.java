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


/**
 * 存放于本地的Skin
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class LocalSkin extends Skin {
	@Override
	public void setWebPath(String baseUrl) {
		if (baseUrl.endsWith("/")) {
			this.webPath = baseUrl.substring(0, baseUrl.length()-1);
		} else {
			this.webPath = baseUrl;
		}
		this.webPath = this.webPath+"/skins/"+this.getName();
	}

	@Override
	public boolean isInFileSystem() {
		return true;
	}
	
	@Override
	public boolean isShared(){
		return true;
	}
}