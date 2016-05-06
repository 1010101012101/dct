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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * 
 * @date 2010-7-28
 * @author Dylan
 */
public class ImageScrollPlugin extends AbstractDPagePlugin {

	static final String[] c_externalImg = { "jpg", "jpeg", "gif", "png", "bmp" };

	private String buildScript(List<String> images){
		StringBuilder scriptBuilder = new StringBuilder();
		scriptBuilder.append("[");
		boolean first =true;
		for (String url:images){
			if (!first){
				scriptBuilder.append(",");
			}else{
				first=false;
			}
			scriptBuilder.append("'"+url+"'");
		}
		scriptBuilder.append("]");
		return scriptBuilder.toString();
	}

//	private List<String> getImageUrls(String imgpath, VWBContext context){
//		String[] imgpatharr = imgpath.split("/");
//		String clbpath = imgpatharr[0];
//		String filter = imgpatharr[1];
//		FolderInfo[] files = readImages(context, clbpath);
//		ArrayList<String> urls = new ArrayList<String>();
//		String baseurl = context.getSite().getBaseURL()+"/cachable/";
//		for (FolderInfo f : files) {
//			if (accept(f, filter)){
//				urls.add( AccessCLBHelper.getHashId(f.name,Integer.toString(f.docid), baseurl));
//			}
//		}
//		return urls;
//	}

//	private boolean accept(FolderInfo f, String strfilter) {
//		strfilter = strfilter.toLowerCase();
//		strfilter = strfilter.replace("*", "");
//		try {
//			String tmp = f.name.toLowerCase();
//			if ("".equals(strfilter) || ".".equals(strfilter)) {
//				if (f.isFile) {
//					for (int i = 0; i < c_externalImg.length; i++) {
//						if (tmp.endsWith(c_externalImg[i]))
//							return true;
//					}
//				}
//			} else if (tmp.endsWith(strfilter)) {
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			return false;
//
//		}
//	}

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {
		String restr = "";
		String width = (String) params.get("offsetWidth");
		String height = (String) params.get("offsetHeight");
		String path = (String) params.get("path");
		String time = (String) params.get("time");
		String classname = (String) params.get("classname");

		if (time == null || "".equals(time))
			time = "2000";
		String id = "imgscr" + (int) Math.rint(Math.random() * 10000000);
//		List<String> urls = getImageUrls(path, context);
		//CLB 6.0.1升级中暂时去除ImgSrcoll Plugin
		List<String> urls = new ArrayList<String>();
		String arrayScript = buildScript(urls);
		
		String src;
		if (urls.size()>=0){
			src=urls.get(0);
		}else{
			src="";
		}
		if (classname == null || "".equals(classname)) {
			restr = "<table  border=\"0\" style=\"background:black;width:"
					+ width + "px;height:" + height
					+ "px;\"><tr><td valign=\"middle\" align=\"center\">";

		} else {
			restr = "<table  border=\"0\" style=\"width:" + width
					+ "px;height:" + height
					+ "px;\"><tr><td valign=\"middle\" align=\"center\">";
		}
		restr += "<img maxWidth=\"" + width + "\" maxHeight=\"" + height
				+ "\" src=\"" + src + "\" path=\"" + path
				+ "\" class=\"srollImage\" id=\"" + id + "\">";
		restr += "</td></tr></table>";
		restr += String.format(SCRIPT_TEMPLATE, id, id, arrayScript, time);
		return restr;
	}
	private static final String SCRIPT_TEMPLATE="<script language='javascript'>" +
			"var scroller%s=new ImageScroller('%s',%s,%s);" +	//id, images, time
			"</script>";
}
