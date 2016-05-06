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
import java.util.ArrayList;
import java.util.List;

import cn.vlabs.duckling.vwb.service.skin.LocalSkin;
import cn.vlabs.duckling.vwb.service.skin.Skin;

/**
 * 本地的皮肤管理，在系统初始化时使用。
 * 
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class LocalSkinRepository {
	private File skinRootDir;
	private String contextPath;
	public void setSkinRoot(String path) {
		this.skinRootDir = new File(path);
	}

	public void setContextPath(String contextPath){
		this.contextPath = contextPath;
	}
	public List<Skin> scan() {
		List<Skin> result = new ArrayList<Skin>();
		if (skinRootDir == null || !skinRootDir.exists()) {
			return result;
		}

		File[] skins = skinRootDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}

		});
		for (int i = 0; i < skins.length; i++) {
			String name = skins[i].getName();
			LocalSkin skin = new LocalSkin();
			skin.setName(name);
			skin.setWebPath(contextPath);
			result.add(skin);
		}
		return result;
	}

	public LocalSkin getSkin(String name) {
		File skinPath = new File(skinRootDir, name);
		if (skinPath.exists()){
			LocalSkin skin = new LocalSkin();
			skin.setName(name);
			skin.setWebPath(contextPath);
			return skin;
		}
		return null;
	}
}
