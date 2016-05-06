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

package cn.vlabs.duckling.vwb.ui.map;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.vwb.driver.PortalDriver;

/**
 * Introduction Here.
 * @date Mar 4, 2010
 * @author xiejj@cnic.cn
 */
public class PortalMapper extends AbstractRequestMapper {
	private Logger log = Logger.getLogger(PortalMapper.class);
	private PortalDriver m_portalDriver;
	@Override
	protected void doMap(Resource vp, String[] params,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	String url =null;
    		if (isAction(params)){
    			log.debug("portlet's config action is requested.");
    			if (!isReadonly()){
    				url = "/"+params[1]+"Portlet.do";
    				forward(request, response, url);
    			}else{
    				showReadonlyPage(request, response);
    			}
    		}else{
    			log.debug("portlet content is requested");
    			m_portalDriver.service(request, response);
    		}
	}

	@Override
	protected String getType() {
		return Resource.TYPE_PORTAL;
	}
	
	public void init(ServletContext context){
		m_portalDriver = new PortalDriver();
		m_portalDriver.init(context);
	}
	private boolean isAction(String[] params){
		return (params!=null && params.length==2 && params[0].equals("a"));
	}

}
