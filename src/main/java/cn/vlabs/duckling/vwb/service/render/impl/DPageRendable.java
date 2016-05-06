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
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.render.Rendable;
import cn.vlabs.duckling.vwb.service.resource.IResourceService;
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Introduction Here.
 * 
 * @date Feb 26, 2010
 * @author Kevin Dong (kevin@cnic.ac.cn)
 */
public class DPageRendable implements Rendable {
	public DPageRendable(int viewportid) {
		this.viewportid = viewportid;
		this.version=-1;
	}
	public DPageRendable(int viewportid, int version) {
		this.viewportid = viewportid;
		this.version=version;
	}
	public void render(VWBContext context,PageContext pageContext)
			throws ServletException, IOException {
		IResourceService resourceService = VWBContext.getContainer().getResourceService();
		Resource resource;
		if (version!=-1){
			resource = resourceService.getResource(context.getSite().getId(),viewportid, version);
		}else{
			resource = resourceService.getResource(context.getSite().getId(),viewportid);
		}
		
 		if (resource!=null && resource instanceof DPage){
			DPage page = (DPage)resource;
			String html = context.getHTML(page);
			pageContext.getOut().print(html);
		}
	}

	public int getId() {
		return viewportid;
	}

	private int viewportid;
	private int version;
}
