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

package cn.vlabs.duckling.vwb.service.viewport;

import java.util.List;

/**
 * 资源查询接口
 * 
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface ViewPortService {
	int createViewPort(int siteId, ViewPort vp);

	int getMappedAclId(int siteId, int id);

	boolean isDpagaeType(int siteId, int resourceId);

	ViewPort getMappedView(int siteId, int id);

	ViewPort getViewPort(int siteId, int id);

	void removeViewPort(int siteId, int vid);

	void removeMetaViewPort(int siteId, int vid);

	void updateBaseInfo(int siteId, ViewPort vp);

	void updateViewPort(int siteId, ViewPort vp)
			throws ViewPortNotExistException;

	List<ViewPort> searchResourceByTitle(int siteId, String title);
}
