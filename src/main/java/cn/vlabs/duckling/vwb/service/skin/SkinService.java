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

package cn.vlabs.duckling.vwb.service.skin;

import java.io.InputStream;
import java.util.List;
/**
 * 皮肤管理服务
 * @date 2013-3-28
 * @author xiejj@cstnet.cn
 */
public interface SkinService {
	/**
	 * 删除皮肤
	 * @param skinName	皮肤的名称
	 * @return	如果删除成功返回true
	 */
	boolean remove(int siteId,String skinName);
	
	/**
	 * 判断皮肤是否存在。
	 * @param skinname	皮肤的名称
	 * @return	如果皮肤存在返回true
	 */
	boolean exists(int siteId, String skinname);
	
	/**
	 * 获得站点下的所有可用皮肤
     * @param siteId	当前站点的ID
	 * @return	皮肤的列表
	 */
	 List<RemoteSkin> getSiteTemplateSkins(int siteId);
	 /**
	  * 获得所有的可用皮肤列表
	  * @param siteId	当前站点的ID
	  * @return	可用的皮肤列表
	  */
	 List<Skin> getAllSkin(int siteId);

	/**
	 * 获取当前使用的皮肤
	 * @param skinname
	 * @return
	 */
	Skin getSkin(int siteId, boolean shared, String skinname);
	/**
	 * 上传皮肤
	 * @param siteId	皮肤隶属的站点ID
	 * @param skinname	皮肤的名称
	 * @param in		皮肤Zip包的输入流
	 * @param length	皮肤ZIP包的长度
	 */
	void upload(int siteId,String template, String skinname, InputStream in, int length);

	/**
	 * 删除站点所有的皮肤
	 * @param siteId 要删除的皮肤所属的站点ID
	 */
	void removeAllSkins(int siteId);

	/**
	 * 装载站点的皮肤
	 * @param siteId		站点ID
	 * @param template	站点模板名称
	 * @param skinRootPath	站点的皮肤根路径
	 */
	void loadSiteSkins(int siteId, String template,String skinRootPath);

	/**
	 * 装载共享的皮肤
	 */
	void loadSharedSkin();
	/**
	 * 获取站点当前使用的皮肤
	 * @param siteId
	 * @return
	 */
	Skin getCurrentSkin(int siteId);
	/**
	 * 给站点应用皮肤
	 * @param siteId
	 * @param global
	 * @param skinname
	 */
	void applySkin(int siteId,boolean global, String skinname);

	/**
	 * @param siteId
	 * @param forTemplate
	 * @return
	 */
	List<RemoteSkin> getSiteTemplateSkins(int siteId, String forTemplate);
}