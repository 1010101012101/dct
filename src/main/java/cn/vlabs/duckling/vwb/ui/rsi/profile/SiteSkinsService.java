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

package cn.vlabs.duckling.vwb.ui.rsi.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.skin.Skin;
import cn.vlabs.duckling.vwb.service.skin.SkinService;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiErrorCode;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-5-25
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteSkinsService extends AdminAccessService {

	public Object getAllSkins(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		SkinRequestItem item = (SkinRequestItem) message;
		SkinService skinService = container.getSkinService();
		int siteId = item.getSiteId();
		List<Skin> skins = skinService.getAllSkin(siteId);
		Skin currentSkin = skinService.getCurrentSkin(siteId);
		List<SkinItem> skinitems = new ArrayList<SkinItem>();
		for (Skin skin : skins) {
			SkinItem skinItem = new SkinItem();
			skinItem.setGlobal(skin.isShared());
			skinItem.setName(skin.getName());
			skinItem.setThumb(skin.getThumb());
			skinItem.setWebPath(skin.getWebPath());
			skinItem.setTemplate(skin.getTemplate());
			skinItem.setAvailable(skin.isAvailable());
			if (skin.getName().equals(currentSkin.getName()))
				skinItem.setCurrentSkin(true);
			else
				skinItem.setCurrentSkin(false);
			skinitems.add(skinItem);
		}
		return skinitems;
	}

	public Object setSkin(RestSession session, Object message)
			throws ServiceException {
		VWBContainer container = VWBContainerImpl.findContainer();
		SkinRequestItem item = (SkinRequestItem) message;
		List<Skin> skins = container.getSkinService().getAllSkin(item.getSiteId());
		Skin set = null;
		for (Skin skin : skins) {
			if (skin.getName().equals(item.getSkinName())) {
				set = skin;
			}
		}
		if (set == null) {
			throw new ServiceException(DCTRsiErrorCode.PARAMETER_INVALID_ERROR,
					"skinName is not existent");
		} else {
			Properties properteis = new Properties();
			properteis.setProperty(KeyConstants.SKIN_NAME, set.getName());
			properteis.setProperty(KeyConstants.SKIN_SHARED,
					Boolean.toString(set.isShared()));
			container.getSiteConfig().setProperty(item.getSiteId(),properteis);
		}
		return null;
	}
}
