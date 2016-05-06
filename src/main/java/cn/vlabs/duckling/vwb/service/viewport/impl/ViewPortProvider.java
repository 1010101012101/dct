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

package cn.vlabs.duckling.vwb.service.viewport.impl;

import java.util.List;

import cn.vlabs.duckling.vwb.service.idgen.IMaxIdReader;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;

/**
 * Introduction Here.
 * 
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public interface ViewPortProvider extends IMaxIdReader {
	/**
	 * 更新ViewPort
	 * 
	 * @param 新的设置
	 * @return 返回修改之前的ViewPort
	 */
	void update(ViewPort vp);

	void updateSon(int siteId, int parentId, int newParent);

	int create(ViewPort vp);

	void remove(int siteId, int vid);

	ViewPort getViewPort(int siteId, int id);

	List<ViewPort> searchResourceByTitle(int siteId, String title);

	List<ViewPort> getAllViewPort(int siteId);
}