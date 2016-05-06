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
package cn.vlabs.duckling.vwb.ui.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.common.properties.Config;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.Oauth;
import cn.vlabs.umt.oauth.UMTOauthConnectException;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;
public abstract class AbstractLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Properties properties;
	private String principalKey;

	private Config loadConfig(String realPath) throws ServletException {
		Config config = new Config();
		FileInputStream in = null;
		try {
			in = new FileInputStream(realPath);
			config.load(in);
		} catch (IOException e) {
			throw new ServletException("Load SSO configuration failed.", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Do noting
				}
			}
		}
		return config;
	}

	private void copyToLocal(String prefix, Config config) {
		if (prefix==null){
			prefix="";
		}else if (!prefix.endsWith(".")){
			prefix = prefix+".";
		}
		properties = new Properties();
		String[] keys = new String[]{"client_id","client_secret", "redirect_uri","access_token_URL","authorize_URL","scope","theme"};
		for (String key:keys){
			properties.setProperty(key, config.getProperty(prefix+key));
		}
	}

	@Override
	public void destroy() {
		properties = null;
	}

	protected abstract String preLogin(HttpServletRequest request,HttpServletResponse response);

	protected abstract void hasLogin(HttpServletRequest request,HttpServletResponse response) throws IOException;

	protected abstract void postLogin(HttpServletRequest request, HttpServletResponse response, AccessToken token, String viewUrl)throws IOException;
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("code") == null) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpSession session = httpRequest.getSession(false);
			if (requireAuthenticate(session)) {
				// 需要重定向
				String viewUrl = preLogin(request, response);
				redirectToUmt(request, response, viewUrl);
			} else {
				// 已经登录
				hasLogin(request, response);
			}
		} else {
			if (request.getParameter("sub")!=null){
				// 获取了Code换成AccessToken
				Oauth oauth = new Oauth(properties);
				try {
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					AccessToken token = oauth.getAccessTokenByRequest(request);
					postLogin(request, response, token,
							httpRequest.getParameter("state"));
				} catch (UMTOauthConnectException e) {
					throw new ServletException(e);
				} catch ( OAuthProblemException e){
					throw new ServletException(e);
				}
			}else{
				response.sendRedirect(request.getParameter("state")+"&code="+request.getParameter("code"));
			}
		}
	}

	private boolean requireAuthenticate(HttpSession session) {
		return session == null || session.getAttribute(principalKey) == null;
	}

	private void redirectToUmt(HttpServletRequest request,
			HttpServletResponse response, String viewUrl) throws IOException,
			ServletException {
		Oauth oauth = new Oauth(properties);
		int siteId=getVwbcontext(request).getSiteId();
		VWBContainer container = VWBContainerImpl.findContainer();
		Locale locale =request.getLocale();
		String language =locale.getLanguage() + "_" + locale.getCountry();
		if(!locale.equals(Locale.CHINA)){
			language = Locale.US.getLanguage() + "_" + Locale.US.getCountry();
		}
		if (StringUtils.isEmpty(language)) {
			language = container.getSiteConfig().getProperty(siteId,KeyConstants.SITE_LANGUAGE);
		}
		
		try {
			String redirectUrl = oauth.getAuthorizeURL(request) + "&state="
					+ URLEncoder.encode(viewUrl, "UTF-8")+"&locale="+language;
			response.sendRedirect(redirectUrl);
		} catch (UMTOauthConnectException e) {
			throw new ServletException(e);
		}
	}
	
	

	@Override
	public void init(ServletConfig config) throws ServletException {
		String file = config.getInitParameter("configFile");
		if (file == null) {
			throw new ServletException(
					"Init parameter configFile  which contains sso configuration is required.");
		}
		principalKey = config.getInitParameter("principalKey");
		if (principalKey == null) {
			principalKey = "umt.principal";
		}
		String prefix = config.getInitParameter("prefix");
		String realPath = config.getServletContext().getRealPath(file);
		Config config1 = loadConfig(realPath);
		copyToLocal(prefix, config1);
	}
	
	private VWBContext getVwbcontext(HttpServletRequest request) {
		return VWBContext.createContext(request, VWBCommand.LOGIN, null);
	}
	
}