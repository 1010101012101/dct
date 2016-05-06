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

package cn.vlabs.duckling.vwb.service.dml;

import cn.vlabs.duckling.vwb.VWBContext;

/**
 * 页面渲染服务
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface RenderingService{
	/**
	 * 渲染页面
	 * @param context 当前访问的Context
	 * @param pageContent 页面DML数据
	 * @return 渲染以后的HTML
	 */
	String getHTML(VWBContext context, String pageContent);
	/**
	 * 渲染页面
	 * @param siteId 页面所属的SiteId
	 * @param pageId 页面ID号
	 * @return 渲染完成的页面
	 */
	String getHTML(int siteId, int pageId) ;
}
