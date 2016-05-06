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

package cn.vlabs.duckling.vwb.service.backup.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.skin.RemoteSkin;
import cn.vlabs.duckling.vwb.service.skin.SkinService;
import cn.vlabs.rest.IFileSaver;

/**
 * Introduction Here.
 * 
 * @date May 7, 2010
 * @author zzb
 */
public class SkinProvider extends BaseTemplateProvider {

	private static final String SKIN_PATH = "/skins/";

	private static Logger log = Logger.getLogger(SkinProvider.class);
	private static class BackupSaver implements IFileSaver{
		private String basePath;
		public BackupSaver(String basePath){
			this.basePath = basePath;
		}
		private String name;
		public void setSkinName(String skinname){
			this.name = skinname;
		}
		
		private File getFullPath(){
			return new File(basePath+name+".zip");
		}
		@Override
		public void save(String filename, InputStream in) {
			File targetFile = getFullPath();
			try {
				FileUtil.copyInputStreamToFile(in, targetFile);
			} catch (IOException e) {
				log.error("Error on backup skin files.",e);
			}
		}
	}
	@Override
	public boolean backup(int siteId,String templatePath) {
		if (siteId == 1) {
			// if the site is manager site, don't backup skin
			return true;
		}
		SkinService skinService = VWBContainerImpl.findContainer().getSkinService();
		AttachmentService attachService = VWBContainerImpl.findContainer().getAttachmentService();
		
		List<RemoteSkin> skins = skinService.getSiteTemplateSkins(siteId);
		BackupSaver saver = new BackupSaver(templatePath + SKIN_PATH);
		for (RemoteSkin skin : skins) {
			saver.setSkinName(skin.getName());
			attachService.getContent(skin.getClbId(), saver);
		}
		return true;
	}

}
