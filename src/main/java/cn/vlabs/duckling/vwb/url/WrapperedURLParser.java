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

package cn.vlabs.duckling.vwb.url;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 9, 2011 6:07:27 PM
 */
public abstract class WrapperedURLParser extends URLParser {
	public abstract void parse(String sitePrefix, HttpServletRequest request);

	protected String m_context;
	protected String m_pathinfo="";
	
	protected String m_servletName;
	protected String pageid = null;
	private String[] params =null;
	public String getContextPath() {
		return m_context;
	}
	public String getPage() {
		return pageid;
	}
	
	public String getPathInfo() {
		return m_pathinfo;
	}
	
	public String getServletName(){
		return m_servletName;
	}
	public boolean isPlainURL(){
		return false;
	}
	@Override
	public String getSiteContext() {
		return m_context;
	}
	
	public String[] getParams() {
		return params;
	}
	public boolean isSimpleURL(){
		return false;
	}	
	public boolean isSequenceSpecified(){
		return false;
	}
	public int getSequence(){
		return 0;
	}
	protected void copyParams(String[] ids, int start) {
		if (ids.length>start){
			params = new String[ids.length - start];
			for (int i = 0; i < params.length; i++) {
				params[i] = ids[i + start];
			}
		}
	}
}
