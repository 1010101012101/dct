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

package cn.vlabs.duckling.vwb.service.render.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.render.Rendable;
import cn.vlabs.duckling.vwb.service.resource.JSPFunction;

/**
 * Introduction Here.
 * @date Feb 5, 2010
 * @author xiejj@cnic.cn
 */
public class JSPRendable implements Rendable{
	
	public JSPRendable(String jsp,int viewportid){
		this.viewportid = viewportid;
		this.jsp = jsp;
	}
	public JSPRendable(int viewportid){
		this.viewportid = viewportid;
	}
	
	public void render(VWBContext context, PageContext pageContext) throws ServletException, IOException {
		if (jsp!=null){
			pageContext.include(jsp);
		}else{
			JSPFunction jf= VWBContext.getContainer().getFunctionService().getFunction(context.getSite().getId(),viewportid);
			pageContext.include(jf.getURL());
		}
	}
	public int getId() {
		return viewportid;
	}
	public void setId(int id){
		this.viewportid=id;
	}
	private int viewportid;
	private String jsp;
}
