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

package cn.vlabs.duckling.vwb.service.resource.impl;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.resource.FunctionService;
import cn.vlabs.duckling.vwb.service.resource.JSPFunction;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigReader;
import cn.vlabs.duckling.vwb.service.resource.config.FunctionItem;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public class VWBFunctionService implements FunctionService {
	private static Logger log = Logger.getLogger(VWBFunctionService.class);

	private HashMap<Integer, JSPFunction> functions;

	// String funcXml = site.findPrivate("conf/functions.xml");
	private String funcXml;

	private ViewPortService viewportService;

	public JSPFunction getFunction(int siteId, int resourceId) {
		ViewPort viewport = viewportService.getViewPort(siteId, resourceId);
		if (viewport!=null&& Resource.TYPE_FUNCTION.equals(viewport.getType())){
			return functions.get(resourceId);
		}else{
			return null;
		}
	}

	public void init() {
		functions = new HashMap<Integer, JSPFunction>();
		try {

			FileInputStream in = new FileInputStream(funcXml);
			ConfigReader reader = new ConfigReader();
			List<ConfigItem> items = reader.fromXML(in);
			if (items != null) {
				for (ConfigItem item : items) {
					if (item instanceof FunctionItem) {
						FunctionItem fi = (FunctionItem) item;
						JSPFunction func = new JSPFunction();
						func.setResourceId(fi.getId());
						func.setURL(fi.getUrl());
						functions.put(func.getResourceId(), func);
					}
				}
			}
			if (log.isInfoEnabled())
				log.info("Total " + functions.size() + " functions been found");
		} catch (Throwable e) {
			log.error("Init function map error:" + e.getMessage());
		}
	}

	public void setFuncXml(String path) {
		this.funcXml = path;
	}
	public void setViewportService(ViewPortService viewportService) {
		this.viewportService = viewportService;
	}
}
