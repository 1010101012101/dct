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

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * @date 2010-3-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class CurrentTimePlugin extends AbstractDPagePlugin {
	    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss zzzz";

	    public String execute(VWBContext context, Map<String,String> params) throws PluginException {
	    	
	    	String labelId="showtime"+String.valueOf((int)Math.rint(Math.random()*1000000));
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("<label id='").append(labelId).append("'></label>");
	    	sb.append("<script type=\"text/javascript\">");
	    	sb.append("	$(document).ready(function(){");
	    	sb.append("		changecutfunction();");
	    	sb.append("	});");
	    	sb.append("function changecutfunction(){");
	    	sb.append("	setInterval(showCutTime,1000);");
	    	sb.append("}");
	    	sb.append("function showCutTime(){");
	    	sb.append("	document.getElementById(\"").append(labelId).append("\").innerHTML=new Date().toLocaleString();");
	    	sb.append("}");
	    	sb.append("</script>");
	    	return sb.toString();
	    }


}
