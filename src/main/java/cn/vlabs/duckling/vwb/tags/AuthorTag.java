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

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.ui.UserNameUtil;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class AuthorTag extends VWBBaseTag {
	private static final long serialVersionUID = 1L;

	@Override
	public int doVWBStart() throws Exception {
		DPage page = vwbcontext.getPage();
		String author = page.getAuthor();

		if (StringUtils.isEmpty(author))
			author = "unknown";

		if (!StringUtils.isEmpty(author)) {
			pageContext.getOut().print(UserNameUtil.getAuthorTip(vwbcontext, author));
		} else {
			pageContext.getOut().print("unknown");
		}

		return SKIP_BODY;
	}
}
