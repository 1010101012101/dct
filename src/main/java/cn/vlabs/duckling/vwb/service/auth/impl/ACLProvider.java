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
 * 封装对资源的ACL存取。
 * @date Feb 26, 2010
 * @author xiejj@cnic.cn
 */
public interface ACLProvider{
	/**
	 * 查询资源的ACL
	 * @param id 资源的ID
	 * @return 资源上定义的ACL
	 */
	Acl getAcl(int siteId,int resourceId);
	/**
	 * 更新资源的ACL，如果资源的ACL没有定义，则创建ACL
	 * @param id 资源的ID号
	 * @param acl	资源对应的acl设置
	 */
	void updateAcl(int siteId,int resourceId, Acl acl);
	/**
	 * 删除资源上定义的ACL
	 * @param id 资源的ID
	 */
	void removeAcl(int siteId,int resourceId);
}
