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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * 请求转发接口
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public interface IRequestMapper {
	/**
	 * 映射请求
	 * @param
	 * @throws IOException 
	 * @throws ServletException 
	 */
	void map(Resource vp, String[] params, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	/**
	 * @param
	 */
	void init(ServletContext serlvetContext);
}
