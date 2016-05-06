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
 * @date 2010-8-06
 * @author Dylan
 */
public class DEMapPlugin extends AbstractDPagePlugin {

	    public String execute(VWBContext context, Map<String,String> params)throws PluginException {
	    	 String restr="";
	    	 String width= (String)params.get("offsetWidth");
	    	 String height= (String)params.get("offsetHeight");
	    	 String scale= (String)params.get("scale");
	    	 String pointlist= (String)params.get("pointlist");
	    	 String containx=(String)params.get("containx");
	    	 String containy=(String)params.get("containy");
	    	 pointlist=pointlist.replace("@#@", "'");
	    	 pointlist=pointlist.replace("@!@", "=");
	    	 String mapid="map_canvas"+String.valueOf((int)Math.rint(Math.random()*1000000));
	    	 pointlist= pointlist.replace("PointList", "PointList"+mapid);
	    	 
	    	 
	    	 
	    	 String maptype=(String)params.get("maptype");
	    	 
	    	 
	    	 if(width==null||"".equals(width))width="500";
	    	 if(height==null||"".equals(height))height="500";
	    	 if(scale==null||"".equals(scale))scale="6";
	    	 if(containx==null||"".equals(containx))containx="40";
	    	 if(containy==null||"".equals(containy))containy="116";
	    	 String googlemapkey="";
	    	 if(context.getProperty("googlemapkey")!=null&&!"".equals(context.getProperty("googlemapkey")))
	    		 googlemapkey="&amp;key="+context.getProperty("googlemapkey");
	    	
	    	restr+="<script src=\"http://maps.google.com/maps?file=api&amp;v=2.x"+googlemapkey+"\" type=\"text/javascript\"></script>\n";
	    	restr+= "<table width=\"100%\"><tr><td><div id=\""+mapid+"\" class=\"map\" style=\"width:"+width+"px;height:"+height+"px\"></div></td></tr></table>\n";
	    	restr+= "<script  type=\"text/javascript\">\n";
	    	
	    	restr+=pointlist+";\n";
	    	restr+="</script>\n";
	    	restr+= "<script  type=\"text/javascript\">\n";
	    	
	    	restr+="$(document).ready(function() {";
	    	restr+=	"adddemap(PointList"+mapid+",\""+mapid+"\","+containx+","+containy+","+scale+","+maptype+")});\n";
	    	
	    	restr+="function adddemap(obj,mapid,containx,containy,scale,maptype){\n";
	    	restr+="	  var map= new GMap2(document.getElementById(mapid));    \n"; 
	    	restr+="	  map.setCenter(new GLatLng(containx, containy), scale);       \n";
	    	restr+="      map.enableScrollWheelZoom();        \n";
	    	
	    	
	    	
	    	restr+=" if(maptype&&maptype!='')map.setMapType(maptype);";
	    	
	    	
	    	
	    	restr+="      map.addControl(new GMapTypeControl());     \n";
	    	restr+="		  map.addControl(new GSmallMapControl());     \n";
		    restr+="	  function createMarker(point,info,imgurl){\n";
		    
		    restr+="   	if(imgurl&&imgurl!=\"\"){;";
		    
		    restr+="       var icon = new GIcon(); \n";
	    	restr+="       icon.image =imgurl; \n";
    		restr+="       icon.iconAnchor = new GPoint(point.y,point.x); \n";
    		
    		restr+="       icon.infoWindowAnchor = new GPoint(point.y,point.x-32); \n";
	    	restr+="       var marker = new GMarker(point,icon);    \n"; 
	    	
	    	restr+="		}else{var marker = new GMarker(point);}";
	    	
	    	restr+="       var html = '<div>'+ info +'</div>';     \n";
	    	restr+="     GEvent.addListener(marker, \"mouseover\", function() {   \n"; 
	    	restr+="        try{  marker.openInfoWindowHtml(info);  }catch(e){alert(e)}   \n";
	    	restr+="        });     \n";
	    	restr+="      GEvent.addListener(marker, \"mouseout\", function() {     \n";
	    	restr+="          marker.closeInfoWindow();     \n";
	    	restr+="       });     \n";
	    	restr+="         GEvent.addListener(marker, \"click\", function() {     \n";
	    	restr+="             map.setCenter(point, 12);    \n";  
	    	restr+="         });    \n";
	    	restr+="          return marker;     \n";
	    	restr+="     }     \n";

	    	restr+="	 for(i=0;i<obj.length;i++){\n";
	    	restr+="		var point = obj[i][0];\n";
	    	restr+="		var info=obj[i][1];\n";
	    	restr+="		var imgurl=obj[i][2];\n";
	    	restr+="		map.addOverlay(createMarker(point,info,imgurl));\n";
	    	restr+="	 }\n";
	  
	    	restr+="};\n";
	    	restr+="</script>\n";
	    	
	    	return restr;
	    }
	    
	  

}
