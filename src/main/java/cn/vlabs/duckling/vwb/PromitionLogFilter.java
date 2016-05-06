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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class PromitionLogFilter implements Filter {
	private static final Logger log = Logger.getLogger(PromitionLogFilter.class);
	
	private static String PROMOTION_HOST="promotionHost";
	private static String PROMOTION_HOST_EMPTY="empty";
	private static int ADMIN_SITE_ID=1;
	private static String PARAM_REF="ref";
	private String createUrl="";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		createUrl=filterConfig.getInitParameter("createUrl");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		
		if(!(request instanceof HttpServletRequest)){
			chain.doFilter(request, response);
			return;
		}
		
		
		HttpServletRequest httpRequest=(HttpServletRequest)request;
		String serverName=request.getServerName();
		if(!StringUtils.equals(serverName, VWBContext.getContainer().getDomainService().getSiteDefaultDomain(ADMIN_SITE_ID))){
			chain.doFilter(request, response);
			return;
		}
		
		
		HttpSession session =httpRequest.getSession();
		String promotionHost=(String) session.getAttribute(PROMOTION_HOST);
		
		String requestUrl=httpRequest.getRequestURI();
		if(StringUtils.equals(requestUrl, createUrl)){
			logCreateJson(promotionHost,requestUrl);
		}
		
		
		if(StringUtils.isNotBlank(promotionHost)){
			chain.doFilter(request, response);
			return;
		}
		
		String paramRef=request.getParameter(PARAM_REF);
		
		if(StringUtils.isNotBlank(paramRef)){
			session.setAttribute(PROMOTION_HOST, paramRef);
			logAccessJson("",paramRef,paramRef,requestUrl);
			chain.doFilter(request, response);
			return;
		}
		
		String referer=httpRequest.getHeader("Referer");
		String referHost=getRefererHost(referer);
		if(StringUtils.isNotBlank(referHost)){
			session.setAttribute(PROMOTION_HOST, getRefererNameByHost(referHost));
			logAccessJson(referer,referHost,getRefererNameByHost(referHost),requestUrl);
			chain.doFilter(request, response);
		}
		
	}
	
	private void logAccessJson(String referer,String referHost,String referHostName,String requestUrl){
		JSONObject obj=new JSONObject();
		obj.put("type", "access");
		obj.put("referHost", referHost);
		obj.put("referHostName", referHostName);
		obj.put("referer", referer);
		obj.put("requestUrl", requestUrl);
		obj.put("logDate", formatData2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
		log.info(obj.toJSONString());
	}
	
	private void logCreateJson(String referHostName,String requestUrl){
		JSONObject obj=new JSONObject();
		obj.put("type", "create");
		obj.put("referHost", "-");
		obj.put("referHostName", referHostName);
		obj.put("referer", "-");
		obj.put("requestUrl", requestUrl);
		obj.put("logDate", formatData2String(new Date(), "yyyy-MM-dd HH:mm:ss"));
		log.info(obj.toJSONString());
	}
	
	private  String formatData2String(Date date, String pattern) {
		if(date==null){
			return "";
		}
		SimpleDateFormat fm = new SimpleDateFormat(pattern);
		return fm.format(date);
	}
	
	private String getRefererNameByHost(String refererHost){
		if(StringUtils.isBlank(refererHost)){
			return PROMOTION_HOST_EMPTY;
		}
		
		switch(refererHost){
			case "www.dconference.cn":
				return "baidu";
			default:
				return refererHost;
		}
		
	}
	
	
	
	private String getRefererHost(String referer){
		URL refererUrl=null;
		try {
			refererUrl=new URL(referer);
		} catch (MalformedURLException e) {
			refererUrl=null;
		}
		
		
		if(refererUrl==null){
			return PROMOTION_HOST_EMPTY;
		}
		
		return refererUrl.getHost();
	}
	

	@Override
	public void destroy() {
		
	}

}
