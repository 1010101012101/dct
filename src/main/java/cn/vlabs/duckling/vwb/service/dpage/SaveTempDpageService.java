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

package cn.vlabs.duckling.vwb.service.dpage;

import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;

/**
 * 临时保存服务
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface SaveTempDpageService {
	/**
	 * 保存一个临时页面
	 * @param page
	 * @param text
	 */
	void saveTempDpage(int siteId,DPage page, String text);
	/**
	 * 查询已保存的临时页面的信息
	 * @param siteId
	 * @param resourceId
	 * @param author
	 * @return
	 */
	TempPage getTempPage(int siteId, int resourceId, UserPrincipal author);
    /**
     * 删除临时页面
     * @param pageid
     * @param currentUser
     */
    void cleanTempPage(int siteId,int pageid, UserPrincipal currentUser);
}
