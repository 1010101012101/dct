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

import cn.vlabs.clb.api.CLBException;

/**
 * 皮肤未找到
 * 
 * @date 2013-3-29
 * @author xiejj@cstnet.cn
 */
public class SkinNotFoundException extends CLBException {
	private static final long serialVersionUID = 1L;

	private String name;

	private int siteId;

	public SkinNotFoundException(int siteId, String skinname) {
		super(String.format("Skin %s in site %d is not found.", siteId,
				skinname));
		this.siteId = siteId;
		this.name = skinname;
	}

	public String getName() {
		return name;
	}

	public int getSiteId() {
		return siteId;
	}
}