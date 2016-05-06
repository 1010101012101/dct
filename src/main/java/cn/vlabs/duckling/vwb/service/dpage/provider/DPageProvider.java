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

package cn.vlabs.duckling.vwb.service.dpage.provider;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.dpage.data.SearchResult;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author euniverse
 */
public interface DPageProvider {
	DPagePo create(DPagePo dpage);

	void update(DPagePo dpage);

	void deleteByResourceId(int siteId, int resourceId);

	void deleteByResourceId(int siteId, int[] resourceIds);

	List<DPagePo> getHistoryByResourceId(int siteId, int resourceId);

	List<DPagePo> getHistoryByResourceId(int siteid, int resourceId,
			int offset, int pageSize);

	DPagePo getLatestByResourceId(int siteId, int resourceId);

	List<DPagePo> getLatestByResourceIds(int siteId, int[] resourceIds);

	int getDpageCount(int siteId);

	List<LightDPage> getDpagesSinceDate(int siteId, Date date);

	DPagePo getVersionContent(int siteId, int resourceId, int version);

	boolean isDpageExist(int siteid, int resourceId);

	List<DPageNodeInfo> getSubPages(int siteId, int resourceId);

	List<DPageNodeInfo> getRootPages(int siteId);

	SearchResult searchSubDpages(int siteId, int resourceId,
			Map<String, Object> searchedConditions);

	List<DPagePo> searchPages(int siteId, Map<String,Object> searchedConditions);

	List<LightDPage> searchDpageByTitle(int siteId, String title);

	List<LightDPage> getAllPages(int siteId);

	List<DPagePo> getAllWeightPages(int siteId);
}
