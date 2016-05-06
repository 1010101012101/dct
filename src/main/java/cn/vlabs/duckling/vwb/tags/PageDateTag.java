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
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class PageDateTag extends VWBBaseTag {
	private static final long serialVersionUID = 0L;

	public static final String DEFAULT_FORMAT = "dd-MMM-yyyy HH:mm:ss zzz";

	private String m_format = null;

	private DPage m_page = null;

	public void initTag() {
		super.initTag();
		m_format = null;
	}

	public String getFormat() {
		if (m_format == null)
			return vwbcontext.getProperty(KeyConstants.SITE_DATE_FORMAT,
					DEFAULT_FORMAT);

		return m_format;
	}

	public void setPage(DPage page) {
		this.m_page = page;
	}

	public void setFormat(String arg) {
		m_format = arg;
	}

	public final int doVWBStart() throws IOException {
		DPage page = m_page;
		Date d = null;
		if (page == null) {
			page = vwbcontext.getPage();
		}
		d = page.getTime();
		if (d != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(getFormat());
			pageContext.getOut().write(fmt.format(d));
		} else {
			pageContext.getOut().write("&lt;never&gt;");
		}

		return SKIP_BODY;
	}
}
