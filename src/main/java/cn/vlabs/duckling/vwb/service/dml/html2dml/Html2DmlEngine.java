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

package cn.vlabs.duckling.vwb.service.dml.html2dml;

import java.io.PrintWriter;
import java.io.Writer;

import cn.vlabs.duckling.common.util.Base64;
import cn.vlabs.duckling.vwb.VWBContext;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
public class Html2DmlEngine {
	private Writer m_outTimmer = new WhitespaceTrimWriter();
	private PrintWriter m_out = new PrintWriter(m_outTimmer);
	private int preType = 0;// 所有父级元素中是否存在pre或者per样式的span >0是存在
	private String baseURL = "";
	private VWBContext vwbcontext;

	public VWBContext getVwbcontext() {
		return vwbcontext;
	}

	public void setVwbcontext(VWBContext vwbcontext) {
		this.vwbcontext = vwbcontext;
	}

	public void setM_out(PrintWriter m_out) {
		this.m_out = m_out;
	}

	public int getPreType() {
		return preType;
	}

	public void setPreType(int preType) {
		this.preType = preType;
	}

	public Writer getM_outTrimmer() {
		return m_outTimmer;
	}

	public PrintWriter getM_out() {
		return m_out;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String findAttachment(String link, Html2DmlEngine html2dmlengine) {
		String baseurl = html2dmlengine.getBaseURL();
		if (link != null) {
			if (link.startsWith(baseurl)) {
				int index = link.lastIndexOf("/");
				link = link.substring(index + 1);
				String strhash = getFromBASE64(link);
				if (strhash.indexOf("clb") != -1)
					return link;
			}
		}
		return null;
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		try {
			byte[] b = Base64.decode(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
}
