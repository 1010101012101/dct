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

package cn.vlabs.duckling.vwb.ui.action;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class CreatePageAction extends BaseAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String title=request.getParameter("title");
        try {
        	int parent=Integer.valueOf(request.getParameter("ResourceId"));
        	VWBContext context = VWBContext.createContext(request,VWBContext.CREATE_RESOURCE);
        	
        	ViewPort vp = new ViewPort();
        	vp.setParent(parent);
        	vp.setTitle(title);
        	vp.setCreateTime(new Date());
        	vp.setType(Resource.TYPE_DPAGE);
        	UserPrincipal  p = (UserPrincipal)context.getCurrentUser();
    	    String strcreator=p.getFullName()+"("+p.getName()+")";
    	    vp.setCreator(strcreator);
    	    
    		int newid=VWBContext.getContainer().getViewPortService().createViewPort(context.getSite().getId(),vp);

    		response.setContentType("text/xml;charset=utf-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter(); 
			out.println(newid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
