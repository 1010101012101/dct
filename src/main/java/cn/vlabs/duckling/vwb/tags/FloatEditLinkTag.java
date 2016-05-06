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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import cn.vlabs.duckling.vwb.VWBContext;

/**
 * Introduction Here.
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
public class FloatEditLinkTag extends VWBBaseTag {
	private static final long serialVersionUID = 1L;
	@Override
	public int doVWBStart() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String contextPath =request.getContextPath();
		String image = "<img src=\""+contextPath+"/images/edit.png\"/>";
		String html = "<a href=\'"+buildEditLink(contextPath)+"\'>"+image+"</a>";
		try {
			pageContext.getOut().println(html);
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}
	public void setViewPort(int id){
		m_viewportid=id;
	}
	public void setEditId(int id){
		this.m_editid=id;
	}
	private String buildEditLink(String contextPath){
		int parentid = VWBContext.getContainer().getViewPortService().getViewPort(vwbcontext.getSite().getId(),m_editid).getParent();
		return vwbcontext.getURL(VWBContext.EDIT, Integer.toString(m_editid),"parentPage="+parentid+"&returnPage="+m_viewportid);
	}
	private int m_viewportid;
	private int m_editid;
}
