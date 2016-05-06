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

package cn.vlabs.duckling.vwb.service.dml.impl;

import java.util.Map;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.dml.DmlContext;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
public class DmlContextBridge implements DmlContext {
	private VWBContext context;

	public VWBContext getContext() {
		return context;
	}

	public void setContext(VWBContext context) {
		this.context = context;
	}

	public boolean isDpageExists(int resourceId) {
		return VWBContext.getContainer().getDpageService().isDpageExist(context.getSite().getId(),resourceId);

	}

	public boolean isDpagaeType(int resourceId) {
		return VWBContext.getContainer().getViewPortService().isDpagaeType(context.getSite().getId(),resourceId);
	}

	public String pluginexecute(String pluginname, Map<String,String> parsedParams) {
		if (VWBContext.getContainer().getPluginService() == null)
			return "";
		String str = "";
		try {
			str = VWBContext.getContainer().getPluginService()
					.execute(context, pluginname, parsedParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public String getBaseUrl() {
		String basrurl = context.getBaseURL();
		if (basrurl.charAt(basrurl.length() - 1) == '/') {
			return basrurl;
		} else {
			return basrurl + "/";
		}
	}

	public String getCLBBaseUrl() {
		String basrurl = context.getCLBBaseURL();
		if (basrurl.charAt(basrurl.length() - 1) == '/') {
			return basrurl;
		} else {
			return basrurl + "/";
		}
	}

	public String getViewURL(int resourceid) {
		return context.getViewURL(resourceid);
	}

	public String getEditURL(int resourceid) {
		return context.getEditURL(resourceid);
	}

	public String getDdataSiteTableName(String tablename) {
		String sitename = "site" + context.getSite().getId() + "_";
		return sitename + tablename;
	}

	public String removeDdataSiteTableName(String tablename) {
		String sitename = "site" + context.getSite().getId() + "_";
		if (tablename.startsWith(sitename)) {
			tablename = tablename.substring(sitename.length());
		}
		return tablename;

	}
	
	public String getDirectUrl(int docid){
		AttachmentService service=VWBContext.getContainer().getAttachmentService();
		return service.getDirectUrl(docid);
	}
	public String getDirectUrl(int docid, String version) {
		AttachmentService service=VWBContext.getContainer().getAttachmentService();
		return service.getDirectUrl(docid, version);
	}
}
