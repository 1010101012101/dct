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

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author euniverse
 */
public interface DPageService {
	/**
	 * 新创建dpage
	 * 
	 */
	DPage createDpage(DPage dpage) throws InvalidDPageDtoException;

	/**
	 * 
	 * 更新已经存在的dpage
	 * 
	 */
	void updateDpage(DPage dpage) throws InvalidDPageDtoException;

	// search
	/**
	 * 根据资源id，获取最新版本的dpage
	 */
	DPage getLatestDpageByResourceId(int siteId,int resourceId);

	/**
	 * 
	 * 根据资源ids，获取最新版本的dpages
	 */
	List<DPage> getLatestDpagesByResourceIds(int siteId,int[] resourceIds);

	/**
	 * 
	 * 根据资源id，获取dpage的历史版本信息
	 */
	List<DPage> getDpageVersionsByResourceId(int siteId,int resourceId);

	/**
	 * 
	 * 根据资源id，获取dpage的历史版本信息.获取到的结果按版本号升序排列。
	 */
	List<DPage> getDpageVersionsByResourceIdA(int siteId, int resourceId);

	/**
	 * 
	 * 根据资源id，起始行和每页显示数量，获取分页后的dpage的历史版本信息。
	 */
	List<DPage> getDpageVersionsByResourceId(int siteId,int resourceId, int offset,
			int pageSize);

	/**
	 * 
	 * 根据资源id，起始行和每页显示数量，获取分页后的dpage的历史版本信息。获取到的结果按版本号降序排列。
	 */
	List<DPage> getDpageVersionsByResourceIdD(int siteId, int resourceId, int offset,
			int pageSize);

	/**
	 * 根据版本号，获取相应的dpage版本内容
	 */
	DPage getDpageVersionContent(int siteId, int resourceId, int version);

	// delete
	/**
	 * 删除与资源id对应的dpage，
	 */
	void deleteDpageByResourceId(int siteId, int resourceId);

	/**
	 * 删除与资源ids对应的dpages
	 */

	List<LightDPage> getDpagesSinceDate(int siteId, Date date);

	CurrentPageResultSet searchSubDpages(int siteId, int resourceId,
			Map<String, Object> searchedConditions);

	boolean isDpageExist(int siteId, int resourceId);

	List<DPageNodeInfo> listSubPage(int siteId, int resourceId);

	String getParentTitle(int siteId, int resourceId);

	void moveDpageNode(int siteId, int resourceId, int parent);

	List<DPage> searchPages(int siteId, Map<String,Object> searchedConditions);

	List<LightDPage> searchDpageByTitle(int siteId,String title);

	List<LightDPage> getAllPage(int siteId);

	List<DPage> getAllWeightPage(int siteId);

	/**
	 * 页面上锁 di
	 */
	PageLock lockPage(int siteId,int pageid, String pagelocker, int pageVersion,
			String sessionid, String usrIp);

	/**
	 * 得到当前页面锁 di
	 */
	PageLock getCurrentLock(int siteId,int pageid);

	/**
	 * 关闭当前页面锁 di
	 */
	void unlockPage(int siteId,int pageid);
}
