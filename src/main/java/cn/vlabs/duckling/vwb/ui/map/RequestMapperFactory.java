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

package cn.vlabs.duckling.vwb.ui.map;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletContext;

/**
 * 请求转发工厂类
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public class RequestMapperFactory implements Registable {
	public void init(ServletContext serlvetContext){
		Collection<IRequestMapper> mappers=maps.values();
		for (IRequestMapper map:mappers){
			map.init(serlvetContext);
		}
	}
	public RequestMapperFactory(){
		maps= new HashMap<String, IRequestMapper>();
	}
	public IRequestMapper getRequestMapper(String type){
		return maps.get(type);
	}
	
	public IRequestMapper regist(String type, IRequestMapper mapper){
		return maps.put(type, mapper);
	}
	private HashMap<String, IRequestMapper> maps;
}
