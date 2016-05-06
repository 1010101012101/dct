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

package cn.vlabs.duckling.vwb.service.ddl;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.upload.FormFile;
import org.json.simple.JSONObject;

import cn.vlabs.duckling.vwb.VWBContext;

/**
 * @date 2015-12-21
 * @author xiejj@cstnet.cn
 */
public interface DDLService {
	/**
	 * 上传文件并分享
	 * @param siteId	站点的ID	
	 * @param token		用户身份Token
	 * @param file		上传的文件对象
	 * @return			上传成功后返回上传结果
	 */
	JSONObject upload(VWBContext context, FormFile file);
	/**
	 * 创建前台访问DDL的身份凭证
	 * @param currentUser	当前用户的用户名
	 * @return	身份凭证
	 */
	String createToken(VWBContext context);
	/**
	 * 获取站点对应的TeamCode
	 * @return
	 */
	String getTeamCode(HttpServletRequest request);
	/**
	 * @param request
	 * @return
	 */
	String getTeamCodeAndInit(HttpServletRequest request);
}
