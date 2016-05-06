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

package cn.vlabs.duckling.vwb.service.auth.impl;

import cn.vlabs.duckling.vwb.service.auth.acl.Acl;


/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public interface PolicyProvider {
	
	public static final String POLICY_FILE="duckling.site.policy";

	/**
	 * Brief Intro Here
	 * 从站点读取policy文件内容
	 * @return 返回文件内容
	 */
	Acl getAcl(int siteId);
	
	
	/**
	 * Brief Intro Here
	 * 把policy内容写入到站点的相应位置
	 * @param content 需要写入的文件内容
	 * @return 写文件是否成功
	 */
	void setAcl(int siteId, Acl acl);
}