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

package cn.vlabs.duckling.vwb.service.init.provider;

import java.io.IOException;
import java.util.Map;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.service.skin.SkinService;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public class SkinInitProvider extends ContainerBaseDAO implements InitProvider {
	@Override
	public boolean init(int siteId, Map<String, String> params,
			String templatePath, String defaultDataPath) throws IOException {
		SkinService skinService = VWBContainerImpl.findContainer().getSkinService();
		String srcSkins = templatePath + "/skins";
		skinService.loadSiteSkins(siteId,params.get(KeyConstants.SITE_TEMPLATE_KEY),srcSkins);
		return true;
	}
}
