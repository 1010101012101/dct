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

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import cn.vlabs.duckling.util.TrippleDES;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * @date 2010-3-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class WebClipping extends AbstractDPagePlugin {
	private static final Logger log = Logger.getLogger(WebClipping.class);


    /** The complete HTML and JavaScript code for the clipping */
    private String clipping;
	public String execute(VWBContext context, Map<String,String> params)
			throws PluginException {        
        String URL           = (String) params.get("URL");
        //add by diyanliang 09-5-7
        if(URL==null)URL=(String) params.get("url");
        String width         = (String) params.get("width");
        String height        = (String) params.get("height");
        String name          = (String) params.get("name");
        String longdesc      = (String) params.get("longdesc");
        String frameborder   = (String) params.get("frameborder");
        String marginwidht   = (String) params.get("marginwidht");
        String marginheight  = (String) params.get("marginheight");
        String scrolling     = (String) params.get("scrolling");
        String align         = (String) params.get("align");
        String vspace        = (String) params.get("vspace");
        String hspace        = (String) params.get("hspace");
        String key			 = (String) params.get("key");

        log.debug("URL =" + URL );
        
        clipping = "\n";
        if (URL == null){
            clipping +="WebClipping plugin"; clipping +="Mandatory parameter \"URL\" is missing";
        }
        else{
        	if (key!=null && key.length()>=24){
        		String sig = genSignature(context, key);
        		URL=composeUrl(URL, "sig", sig);
        	};
            clipping += "<IFRAME";
            clipping += " SRC=\""    +URL+"\"";
            if (width        != null) clipping += " WIDTH=\""        +width+"\"";
            if (height       != null) clipping += " HEIGHT=\""       +height+"\"";
            if (frameborder  != null) clipping += " FRAMEBORDER=\""  +frameborder+"\"";
            if (marginwidht  != null) clipping += " MARGINWIDTH=\""  +marginwidht+"\"";
            if (marginheight != null) clipping += " MARGINHEIGHT=\"" +marginheight+"\"";
            if (scrolling    != null) clipping += " SCROLLING=\""    +scrolling+"\"";
            if (align        != null) clipping += " ALIGN=\""        +align+"\"";
            /* these two a not standard HTML */ 
            if (vspace       != null) clipping += " VSPACE=\""       +vspace+"\"";
            if (hspace       != null) clipping += " HSPACE=\""       +hspace+"\"";
            /* these two actually make no sense in the context of this pluging */ 
            if (name         != null) clipping += " NAME=\""         +name+"\"";
            if (longdesc     != null) clipping += " LONGDESC=\""     +longdesc+"\"";
            clipping += ">\n";
            clipping += "Your browser doesn't understand IFRAME. \n";
            clipping += "Click following link to open the content in a new window: \n";
            clipping += "link </IFRAME>\n";
            
        }
        clipping += "\n";
        
        return clipping;

	}
	
	private String composeUrl(String url, String param, String value){
		String seperator;
		if (url.indexOf("?")!=-1){
			seperator="&";
		}else{
			seperator="?";
		}
		return url+seperator+param+"="+value;
	}
	private String genSignature(VWBContext context, String key){
		UserPrincipal current=(UserPrincipal)context.getCurrentUser();
		if (!UserPrincipal.GUEST.equals(current)){
			JSONObject json=new JSONObject();
			json.put("email", current.getName());
			json.put("name", current.getFullName());
			json.put("timestamp", System.currentTimeMillis());
			return TrippleDES.encrypt(key, json.toString());
		}else{
			return "";
		}
	}
}
