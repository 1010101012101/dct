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

package cn.vlabs.duckling.vwb.service.idgen;

import cn.vlabs.duckling.vwb.service.cache.CacheService;

/**
 * @date 2013-5-23
 * @author liji@cstnet.cn
 */
public class LocalGeneratorService  implements IKeyGenerator {
	private IMaxIdReader reader;
	private CacheService cacheService;

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
		this.cacheService.setModulePrefix("idgen");
	}

	@Override
	public void setMaxIdReader(IMaxIdReader reader) {
		this.reader = reader;
	}

	@Override
	public synchronized int getNextID(int groupId) {
		Integer currentId = (Integer) cacheService.getFromCache(groupId,
				groupId);
		if (currentId == null) {
			currentId = reader.getMaxId(groupId);
		}
		int nextId = currentId + 1;
		cacheService.putInCache(groupId, groupId, nextId);
		return nextId;
	}
}