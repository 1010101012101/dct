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

package cn.vlabs.duckling.vwb.service.resource.config;

import java.io.InputStream;
import java.util.List;

import cn.vlabs.duckling.vwb.service.banner.provider.SystemXmlBanner;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;

import com.thoughtworks.xstream.XStream;

/**
 * Introduction Here.
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public class ConfigReader {
	public ConfigReader(){
		stream= new XStream();
		stream.autodetectAnnotations(true);
		stream.processAnnotations(new Class[]{Resources.class, DPageItem.class, FunctionItem.class, 
				PortalItem.class, PortletItem.class, SystemXmlBanner.class, MySpace.class});
	}
	
	public List<ConfigItem> fromXML(InputStream in){
		Resources res = (Resources)stream.fromXML(in); 
		List<ConfigItem> items = res.getItems();
		if (items!=null){
			for (ConfigItem item:items){
				item.init();
			}
		}
		return items;
	}
	
	public XStream getStream() {
		return stream;
	}
	
	private XStream stream;
}
