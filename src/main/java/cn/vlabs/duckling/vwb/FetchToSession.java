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

package cn.vlabs.duckling.vwb;

import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.service.ddl.DDLService;
import cn.vlabs.vwb.driver.internal.SiteSessionImpl;

/**
 * @date 2011-9-6
 * @author xiejj@cnic.cn
 */
public class FetchToSession {
	private final HashSet<String> paramSet;
	private static final String FETCH_PARAMS="fetch_to_portal";
	private DDLService ddlService;
	public FetchToSession(String paramStr){
		if (StringUtils.isNotEmpty(paramStr)){
			paramStr=paramStr.trim();
			paramSet = new HashSet<String>();
			String[] paramArray = paramStr.split(":");
			for (String param:paramArray){
				if (StringUtils.isNotEmpty(param)){
					paramSet.add(param);
				}
			}
		}else{
			paramSet = null;
		}
	}
	public void setDdlService(DDLService ddlService) {
		this.ddlService = ddlService;
	}
	public void fetchToPortal(HttpSession session, SiteSessionImpl siteSession){
		HashMap<String, String> params = getSavedParams(session);
		for (String param:params.keySet()){
			siteSession.setAttribute(param, params.get(param));
		}
	}
	public void saveToSession(HttpServletRequest request){
		if (paramSet!=null){
			HashMap<String, String> savedParams = getSavedParams(request.getSession());
			for (String param:paramSet){
				String paramValue = request.getParameter(param);
				//如果冲参数中为获得 则从cookie中查找
				if(StringUtils.isBlank(paramValue)){
					paramValue=this.getCookieParameter(request, param);
				}
				
				if (paramValue!=null){
					savedParams.put(param, paramValue);
				}
			}
			String teamCode = ddlService.getTeamCode(request);
			VWBSession session = VWBSession.findSession(request);
			savedParams.put("teamCode", teamCode);
			savedParams.put("token", session.getToken());
		}
	}
	
	
	private String getCookieParameter(HttpServletRequest request,String name){
		Cookie[] cookies=request.getCookies();
		if(cookies==null||cookies.length<=0||StringUtils.isBlank(name)){
			return "";
		}
		
		for(Cookie cookie:cookies){
			if(StringUtils.equals(name, cookie.getName())){
				return cookie.getValue();
			}
		}
		
		return "";
	}
	
	
	@SuppressWarnings("unchecked")
	private HashMap<String, String> getSavedParams(HttpSession session){
		HashMap<String, String> savedParams = (HashMap<String, String>)session.getAttribute(FETCH_PARAMS);
		if (savedParams==null){
			savedParams= new HashMap<String, String>();
			session.setAttribute(FETCH_PARAMS, savedParams);
		}
		return savedParams;
	}
}
