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

package cn.vlabs.duckling.vwb;

import java.util.HashSet;

/**
 * 欢迎页面配置管理
 * @date 2012-1-5
 * @author xiejj@cnic.cn
 */
public class WelcomePageIgnore {
	private HashSet<Integer> ignoreSet;
	public WelcomePageIgnore(String ignore){
		ignoreSet=new HashSet<Integer>();
		if (ignore!=null){
			String[] parts=ignore.split(":");
			if (parts!=null){
				for (String part:parts){
					part=part.trim();
					if (part.length()!=0){
						try{
							ignoreSet.add(Integer.parseInt(part));
						}catch (NumberFormatException e){
							//Do nothing
						}
					}
				}
			}
		}
	}
	public boolean isIgnored(int pageId){
		return ignoreSet.contains(pageId);
	}
}
