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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.service.site.ISiteManageService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;


/**
 * Introduction Here.
 * @date 2010-05-13
 * @author Kevin (kevin@cnic.cn)
 */
public class SiteListPlugin extends AbstractDPagePlugin {
    private DateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static int MAX_SITE_COUNT = 1000; //maxSite;
	public String execute(VWBContext context, Map<String,String> params)
			throws PluginException {
		String styleClass = (String)params.get("styleclass");
        String dataFormat = (String)params.get("dataformat");
        String countStr = (String)params.get("count");
        String all = (String)params.get("all");

        boolean bIsAll = all!=null&&"true".equals(all)?true:false;
        int count = MAX_SITE_COUNT;  //maxSite;
        if ((countStr!=null) && !("".endsWith(countStr.trim()))) {
	        try {
	        	count = Integer.parseInt(countStr);
	        	if (count == 0) {
	        		count = MAX_SITE_COUNT;
	        	}
	        } catch (NumberFormatException ex) {
	        	ex.printStackTrace();
	        }
        }
        if ((dataFormat!=null) && (!"".endsWith(dataFormat.trim()))) {
        	onlyDateFormat = new SimpleDateFormat(dataFormat); 
        }
        ISiteManageService sms = VWBContext.getContainer().getSiteManagerService();
        Collection<SiteMetaInfo> sites =sms.getAllSites();
    	
    	StringBuffer html = new StringBuffer();
        if ((styleClass==null) || ("".endsWith(styleClass.trim()))) {
        	html.append("<div>");
        } else {
        	html.append("<div class=\"").append(styleClass).append("\"><ul>");
        }
        int flag=0;
    	for(SiteMetaInfo site:sites){
    		if (!bIsAll) {
	    		if (SiteState.WORK.equals(SiteState.valueOf(site.getState().getValue())) && flag<count) {
		    		html.append("<li><a href=\"").append(context.getBaseURL());
		    		html.append("\" target=\"_blank\">");
		    		html.append(site.getSiteName());
		    		html.append("</a>&nbsp;&nbsp;&nbsp;(");
		    		html.append(onlyDateFormat.format(sms.getSiteInfo(site.getId()).getCreateTime())).append(")");
		    		flag++;
	    		}
    		} else {
    			if (flag<count) {
		    		html.append("<li><a href=\"").append(context.getBaseURL());
		    		html.append("\" target=\"_blank\">");
		    		html.append(site.getSiteName());
		    		html.append("</a>&nbsp;&nbsp;&nbsp;(");
		    		html.append(onlyDateFormat.format(sms.getSiteInfo(site.getId()).getCreateTime())).append(")");
		    		flag++;
    			}
    		}
		}
    	
    	html.append("</ul></div>"); 	   
        return html.toString();
	}
}
