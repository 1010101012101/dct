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

import java.util.Date;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.action.BannerAction;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.banner.BannerItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.banner.BannerRequestItem;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-5-14
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteBannerService extends AdminAccessService {

	public Object updateBanner(RestSession session, Object message)
			throws ServiceException {
		BannerRequestItem item = (BannerRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(item.getSiteId());

		Banner banner = new Banner();
		updateBanner(item, site, banner);
		return null;
	}

	public Object updateBannerWithImg(RestSession session, Object message)
			throws ServiceException {
		BannerRequestItem item = (BannerRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		SiteMetaInfo site = container.getSite(item.getSiteId());

		Banner banner = container.getBannerService().getDefaultBanner(
				site.getId());
		updateBanner(item, site, banner);
		return null;
	}

	public Object cspCreateBanner(RestSession session, Object message)
			throws ServiceException {
		BannerRequestItem item = (BannerRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();

		int siteId =item.getSiteId();
		Banner banner = container.getBannerService().getFirstBanner(
				siteId);
		banner.setCreatedTime(new Date());
		banner.setStatus(1);
		if (banner.getId() < 1) {
			banner.setOwnedtype("system");
			banner.setName(item.getBannerName());
		}
		banner.setCreator(VWBSession.findSession(this.getRequest())
				.getCurrentUser().getName());

		banner.setFirstTitle(item.getFirstTitle());
		banner.setSecondTitle(item.getSecondTitle());
		banner.setThirdTitle(item.getThirdTitle());
		// end
		if (banner.getId() < 1) {
			banner = container.getBannerService().createBanner(siteId,banner);
		} else {
			container.getBannerService().updateBanner(siteId,banner);
		}
		ViewPort viewport = container.getViewPortService().getViewPort(
				siteId, item.getResourceId());
		viewport.setBanner(Banner.INHERITSKIN);
		try {
			container.getViewPortService().updateViewPort(siteId,
					viewport);
		} catch (ViewPortNotExistException e) {

		} catch (Exception e) {

		}
		VWBContainerImpl.findContainer().getBannerService().setDefaultBanner(siteId,Banner.INHERITSKIN);
		return null;
	}

	// 未看明白 待查 为什么把banner status 设为1？
	private void updateBanner(BannerRequestItem item, SiteMetaInfo site, Banner banner) {
		VWBContainer container = VWBContainerImpl.findContainer();
		banner.setCreatedTime(new Date());
		banner.setStatus(1);
		if (banner.getId() < 1) {
			banner.setOwnedtype("system");
			banner.setName(item.getBannerName());
		}
		banner.setCreator(VWBSession.findSession(this.getRequest())
				.getCurrentUser().getName());

		String fileName = item.getFileName();
		banner.setFirstTitle(item.getFirstTitle());
		banner.setSecondTitle(item.getSecondTitle());
		banner.setThirdTitle(item.getThirdTitle());
		int clbId = container.getBannerService().savePicture(fileName,
				getStream().getLength(), getStream().getInputStream());
		BannerAction.copyCssByType(banner, getRequest());
		banner.setLeftPictureClbId(clbId);
		// end
		if (banner.getId() < 1) {
			banner = container.getBannerService().createBanner(site.getId(),banner);
		} else {
			container.getBannerService().updateBanner(site.getId(),banner);
		}

		ViewPort viewport = container.getViewPortService().getViewPort(
				site.getId(), item.getResourceId());
		viewport.setBanner(banner.getId());
		try {
			container.getViewPortService().updateViewPort(site.getId(),
					viewport);
		} catch (ViewPortNotExistException e) {
			e.printStackTrace();
		}

		// begin:yhw@cnic.cn add for three title of banner
		// @2011-11-9

	}

	public Object updateBannerNoImg(RestSession session, Object message)
			throws ServiceException {
		BannerRequestItem item = (BannerRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		int siteId = item.getSiteId();
		Banner banner = container.getBannerService().getFirstBanner(
				siteId);

		if (banner.getId() < 1) {
			banner.setCreatedTime(new Date());
			banner.setType(Banner.WITH3TITLETYPE);
			banner.setStatus(1);
			banner.setOwnedtype("system");
		}
		banner.setCreator(VWBSession.findSession(this.getRequest())
				.getCurrentUser().getName());
		BannerAction.copyCssByType(banner, getRequest());
		// begin:yhw@cnic.cn add for three title of banner
		// @2011-11-9
		banner.setFirstTitle(item.getFirstTitle());
		banner.setSecondTitle(item.getSecondTitle());
		banner.setThirdTitle(item.getThirdTitle());
		container.getBannerService().updateBanner(siteId,banner);
		// end
		ViewPort viewport = container.getViewPortService().getViewPort(
				siteId, item.getResourceId());
		viewport.setBanner(banner.getId());
		try {
			container.getViewPortService().updateViewPort(siteId,
					viewport);
		} catch (ViewPortNotExistException e) {

		}
		return null;
	}

	public Object getBanner(RestSession session, Object message)
			throws ServiceException {
		BannerRequestItem item = (BannerRequestItem) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		ViewPort viewPort = container.getViewPortService().getViewPort(item.getSiteId(),item.getResourceId());
		Banner banner =container.getBannerService().getBannerById(item.getSiteId(),viewPort.getBanner());
		BannerItem bitem = null;
		if (banner != null) {
			bitem = new BannerItem();
			bitem.setCreatedTime(banner.getCreatedTime());
			bitem.setId(banner.getId());
			bitem.setName(bitem.getName());
			bitem.setURL(banner.getLeftPictureUrl());
			bitem.setFirstTitle(banner.getFirstTitle());
			bitem.setSecondTitle(banner.getSecondTitle());
			bitem.setThirdTitle(banner.getThirdTitle());
		}
		return bitem;
	}

}
