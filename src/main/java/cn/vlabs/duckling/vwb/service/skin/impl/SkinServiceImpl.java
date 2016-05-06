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

package cn.vlabs.duckling.vwb.service.skin.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.skin.RemoteSkin;
import cn.vlabs.duckling.vwb.service.skin.Skin;
import cn.vlabs.duckling.vwb.service.skin.SkinNotFoundException;
import cn.vlabs.duckling.vwb.service.skin.SkinService;

/**
 * 皮肤管理的服务类
 * 
 * @date 2013-3-29
 * @author xiejj@cstnet.cn
 */
public class SkinServiceImpl implements SkinService {
	/**
	 * 
	 */
	private static Logger LOG = Logger.getLogger(SkinServiceImpl.class);
	private String clbStaticBaseUrl;
	private RemoteSkinRepository remote;

	private String sharedSkinPath;

	private ISiteConfig siteConfig;
	private SkinDAO skinDao;
	private int createSkin(int siteId,String template,String skinname, InputStream in,
			int length) {
		RemoteSkin skin = remote.createSkin(in, length);
		skin.setName(skinname);
		skin.setSiteId(siteId);
		skin.setTemplate(template);
		return skinDao.insert(skin);
	}
	private String getSkinName(String name){
		String result = name;
		result = result.substring(0, result.length()-".zip".length());
		return result;
	}
	private RemoteSkin readFromDB(int siteId, String name) {
		RemoteSkin skin = skinDao.getSkin(siteId, name);
		if (skin != null) {
			skin.setWebPath(clbStaticBaseUrl);
		}
		return skin;
	}
	private void updateSkin(int siteId, String skinname, InputStream in,
			int length) {
		RemoteSkin skin = readFromDB(siteId, skinname);
		if (skin != null) {
			remote.updateSkin(skin.getClbId(), skin.getSpace(), in, length);
		} else {
			throw new SkinNotFoundException(siteId, skinname);
		}
	}
	public void applySkin(int siteId,boolean global, String skinname) {
		Properties prop = new Properties();
		prop.setProperty(KeyConstants.SKIN_NAME, skinname);
		prop.setProperty(KeyConstants.SKIN_SHARED, Boolean.toString(global));
		siteConfig.setProperty(siteId, prop);
	}

	@Override
	public boolean exists(int siteId, String skinname) {
		return skinDao.getSkin(siteId, skinname) != null;
	}

	@Override
	public List<Skin> getAllSkin(int siteId) {
		if(siteId==Skin.ADMIN_SITE_ID){
			return getAdminSiteSkin();
		}
		
		String templateName = getSiteTemplate(siteId);
		List<Skin> all = getSharedTemplateSkin(templateName, siteId);
		List<RemoteSkin> siteSkins = getSiteTemplateSkins(siteId);
		all.addAll(siteSkins);
		return all;
	}
	
	public List<Skin> getAdminSiteSkin() {
		List<Skin> all = getAllSharedSkin(Skin.ADMIN_SITE_ID);
		List<RemoteSkin> siteSkins = getSiteTemplateSkins(Skin.ADMIN_SITE_ID);
		all.addAll(siteSkins);
		return all;
	}
	
	
	
	public List<Skin> getSharedTemplateSkin(String template, int siteId) {
		List<Skin> all = new ArrayList<Skin>();
		List<RemoteSkin> shareSkins = getSiteTemplateSkins(Skin.SHARE_SITE_ID,template);
		all.addAll(shareSkins);
		fillSkinExtendInfo(siteId,all);
		return all;
	}
	
	/**
	 * 传入调用该方法站点siteId 用于填充数据
	 * @param siteId 
	 * @return
	 */
	public List<Skin> getAllSharedSkin(int siteId){
		List<Skin> all = new ArrayList<Skin>();
		List<RemoteSkin> shareSkins = getAllSiteSkin(Skin.SHARE_SITE_ID);
		all.addAll(shareSkins);
		fillSkinExtendInfo(siteId,all);
		return all;
	}

	public Skin getCurrentSkin(int siteId) {
		String skinname = siteConfig.getProperty(siteId,KeyConstants.SKIN_NAME);
		boolean shared = siteConfig.getBool(siteId,KeyConstants.SKIN_SHARED, false);
		return getSkin(siteId, shared, skinname);
	}

	@Override
	public List<RemoteSkin> getSiteTemplateSkins(int siteId) {
		String templateName = getSiteTemplate(siteId);
		return getSiteTemplateSkins(siteId,templateName);
	}
	private String getSiteTemplate(int siteId) {
		String templateName=siteConfig.getProperty(siteId, KeyConstants.TEMPLATE_NAME);
		return templateName;
	}
	
	public List<RemoteSkin> getAllSiteSkin(int siteId) {
		List<RemoteSkin> result=new ArrayList<RemoteSkin>();
		List<RemoteSkin> temp=skinDao.getAllSkin(siteId);
		result.addAll(temp);
		return result;
	}
	
	@Override
	public List<RemoteSkin> getSiteTemplateSkins(int siteId,String forTemplate) {
		List<RemoteSkin> skins = skinDao.getAllTemplateSkin(siteId,forTemplate);
		fillSkinExtendInfo(siteId,skins);
		return skins;
	}
	
	private void fillSkinExtendInfo(int siteId,List<? extends Skin > skins){
		fillSkinClbBaseUrl(skins);
		fillSkinSiteTemplate(siteId, skins);
	}
	private void fillSkinSiteTemplate(int siteId, List<? extends Skin> skins) {
		if(skins.isEmpty()){
			return ;
		}
		String template=getSiteTemplate(siteId);
		for (Skin skin : skins) {
			skin.setCurrentSiteTemplate(template);
		}
	}
	private void fillSkinClbBaseUrl(List<? extends Skin> skins) {
		if(skins.isEmpty()){
			return ;
		}
		
		for (Skin skin : skins) {
			skin.setWebPath(clbStaticBaseUrl);
		}
	}

	@Override
	public Skin getSkin(int siteId, boolean shared, String name) {
		if (shared) {
			return readFromDB(Skin.SHARE_SITE_ID, name);
		} else {
			return readFromDB(siteId, name);
		}
	}

	@Override
	public void loadSharedSkin() {
		this.loadSiteSkins(Skin.SHARE_SITE_ID,"conference", sharedSkinPath);
	}

	@Override
	public void loadSiteSkins(int siteId, String template,String path) {
		File skinRootDir = new File(path);
		if (skinRootDir == null || !skinRootDir.exists()) {
			return;
		}
		File[] skins = skinRootDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".zip");
			}

		});
		for (int i = 0; i < skins.length; i++) {
			String name =getSkinName(skins[i].getName());
			try {
				FileInputStream in = new FileInputStream(skins[i]);
				try {
					createSkin(siteId, template,name, in, (int) skins[i].length());
				} finally {
					in.close();
				}
			} catch (IOException e) {
				LOG.error("cloud not't load skin "+name+" in "+path, e);
			}
		}
	}
	@Override
	public boolean remove(int siteId, String skinName) {
		RemoteSkin skin = skinDao.getSkin(siteId, skinName);
		if (skin != null) {
			remote.removeSkin(skin);
			skinDao.delete(siteId, skinName);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void removeAllSkins(int siteId) {
		List<RemoteSkin> skins = getSiteTemplateSkins(siteId);
		for (RemoteSkin skin : skins) {
			remote.removeSkin(skin);
		}
		skinDao.removeAll(siteId);
	}

	public void setClbStaticBaseUrl(String baseUrl) {
		this.clbStaticBaseUrl = baseUrl;
	}

	public void setRemoteRepository(RemoteSkinRepository repository) {
		this.remote = repository;
	}

	public void setSharedSkinPath(String path){
		this.sharedSkinPath = path;
	}
	public void setSiteConfig(ISiteConfig siteConfig){
		this.siteConfig = siteConfig;
	}
	public void setSkinDao(SkinDAO skinDao) {
		this.skinDao = skinDao;
	}
	
	@Override
	public void upload(int siteId, String template,String skinname, InputStream in, int length) {
		Skin skin = skinDao.getSkin(siteId, skinname);
		if (skin != null) {
			updateSkin(siteId, skinname, in, length);
		} else {
			createSkin(siteId,template, skinname, in, length);
		}
	}
}
