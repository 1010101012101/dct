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

package cn.vlabs.duckling.vwb.service.variable;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.ddl.DDLService;
import cn.vlabs.duckling.vwb.service.favicon.FaviconService;
import cn.vlabs.duckling.vwb.service.skin.SkinService;

/**
 * Introduction Here.
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
public class VariableServiceImpl implements VariableService {
	private FaviconService faviconService;
	private ISiteConfig siteconfig;
	private SkinService skinService;
	private DDLService ddlSerivce;
	public Object getValue(VWBContext context,int siteId, String varName) throws NoSuchVariableException{
		Object value =  getValue(context,siteId,varName, null);
		if (value==null){
			throw new NoSuchVariableException(varName);
		}
		return value;
	}
	public Object getValue(VWBContext context, int siteId,String varName, String defValue){
		if (StringUtils.isEmpty(varName)){
			throw new IllegalArgumentException("");
		}
		if (varName.equals("pagename")){
			return Integer.toString(context.getResource().getResourceId());
		}
		if (varName.equals("pagetitle")){
			return context.getResource().getTitle();
		}
		if (varName.equals("applicationName")){
			return siteconfig.getProperty(siteId, KeyConstants.SITE_NAME_KEY);
		}
		if (varName.equals("encoding")){
			return siteconfig.getProperty(siteId,KeyConstants.ENCODING);
		}
		if (varName.equals("skin")){
			return skinService.getCurrentSkin(siteId);
		}
		
		if (varName.equals("favicon")){
			return faviconService.getFavicon(siteId);
		}
		
		if (varName.equals("ddlteam")){
			return ddlSerivce.getTeamCode(context.getHttpRequest());
		}
		
		if (varName.equals("ddltoken")){
			return ddlSerivce.createToken(context);
		}
		
		String value = siteconfig.getProperty(siteId,varName);
		if (value!=null){
			return value;
		}else
			return defValue;
	}
	public void setFaviconService(FaviconService faviconService){
		this.faviconService = faviconService;
	}
	public void setSiteConfig(ISiteConfig siteConfig){
		this.siteconfig = siteConfig;
	}
	public void setSkinService(SkinService service){
		this.skinService = service;
	}
	public void setDDLService(DDLService service) {
		this.ddlSerivce = service;
	}
}
