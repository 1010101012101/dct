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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * @date 2015年4月14日
 * @author xzj
 */
public class CPSFilter implements Filter{

	private static String CPS_ADV="cps_adv";
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		int advSiteId=Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("as"), "-1"));
		
		if(advSiteId<=0){
			chain.doFilter(request, response);
			return;
		}
		
		
		
		
		
		
		HttpServletResponse rep = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		request.setAttribute("cps", advSiteId);
		
		Cookie[] cookies=req.getCookies();
		
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if(StringUtils.equals(CPS_ADV, cookie.getName())){
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(0);
				}
			}
		}
		
		
		Cookie myCookie=new Cookie(CPS_ADV,advSiteId+"");
		myCookie.setMaxAge(60*60*24);//一天
		myCookie.setPath(req.getContextPath());
		rep.addCookie(myCookie);
		chain.doFilter(request, response);
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	

}
