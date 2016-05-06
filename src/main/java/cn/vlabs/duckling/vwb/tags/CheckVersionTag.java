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

import java.io.IOException;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;

/**
 * Introduction Here.
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class CheckVersionTag extends VWBBaseTag {
	private static final long serialVersionUID = 0L;

	public static final int LATEST = 0;

	public static final int NOTLATEST = 1;

	public static final int FIRST = 2;

	public static final int NOTFIRST = 3;

	private int m_mode;
	private DPage m_page;
	public void setPage(DPage page){
		this.m_page=page;
	}
	public void initTag() {
		super.initTag();
		m_mode = 0;
	}

	public void setMode(String arg) {
		if ("latest".equals(arg)) {
			m_mode = LATEST;
		} else if ("notfirst".equals(arg)) {
			m_mode = NOTFIRST;
		} else if ("first".equals(arg)) {
			m_mode = FIRST;
		} else {
			m_mode = NOTLATEST;
		}
	}

	public final int doVWBStart() throws IOException {
		SiteMetaInfo site = vwbcontext.getSite();
		DPage page = m_page;
		if (page==null)
			page=(DPage) vwbcontext.getResource();

		if (page != null && vwbcontext.resourceExists(page.getResourceId())) {
			int version = page.getVersion();
			boolean include = false;

			DPage latest = VWBContext.getContainer().getDpageService().getLatestDpageByResourceId(site.getId(),page.getResourceId());

			log.debug("Doing version check: this=" + page.getVersion()
					+ ", latest=" + latest.getVersion());

			switch (m_mode) {
			case LATEST:
				include = (version < 0) || (latest.getVersion() == version);
				break;

			case NOTLATEST:
				include = (version > 0) && (latest.getVersion() != version);
				break;

			case FIRST:
				include = (version == 1)
						|| (version < 0 && latest.getVersion() == 1);
				break;

			case NOTFIRST:
				include = version > 1;
				break;
			}

			if (include) {
				return EVAL_BODY_INCLUDE;
			}
		}

		return SKIP_BODY;
	}
}
