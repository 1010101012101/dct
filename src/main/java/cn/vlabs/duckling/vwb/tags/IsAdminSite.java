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

package cn.vlabs.duckling.vwb.tags;

import cn.vlabs.duckling.vwb.spi.VWBContainer;

/**
 * @date May 15, 2010
 * @author xiejj@cnic.cn
 */
public class IsAdminSite extends VWBBaseTag {
	private static final long serialVersionUID = 1L;

	@Override
	public int doVWBStart() throws Exception {
		if (vwbcontext.getSiteId()==VWBContainer.ADMIN_SITE_ID)
			return EVAL_BODY_INCLUDE;
		else
			return SKIP_BODY;
	}

}