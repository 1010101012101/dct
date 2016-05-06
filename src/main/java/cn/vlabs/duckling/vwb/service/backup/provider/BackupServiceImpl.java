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
import java.util.List;

import cn.vlabs.duckling.vwb.service.backup.BackupService;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class BackupServiceImpl implements BackupService{
	private List<BackupProvider> providers;
	public void setBackupProviders(List<BackupProvider> providers){
		this.providers = providers;
	}
	public boolean backup(int siteId, String templatePath) {
		File templateDir = new File(templatePath);
		if (!templateDir.exists()){
			templateDir.mkdirs();
		}
		boolean succ=true;
		for (BackupProvider p:providers){
			succ = succ && p.backup(siteId, templatePath);
		}
		return succ;
	}
}