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

package cn.vlabs.duckling.vwb.service.banner.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.BannerService;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * Introduction Here.
 * 
 * @date 2010-2-26
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerServiceImpl implements BannerService {
	protected static final Logger log = Logger
			.getLogger(BannerServiceImpl.class);
	public static final int SKINBANNERID = -2;
	public static final int WITH3TITLEBANNERTYPE = 3;
	private BannerProvider bannerProvider;

	private VWBCacheService cache;

	private ISimpleClbClient simpleClbClient;

	private void clearBannerClbPicture(Banner banner) {
		int leftPictureClbId = banner.getLeftPictureClbId();
		if (leftPictureClbId > 0) {
			this.simpleClbClient.delete(leftPictureClbId);
		}
		int rightPictureClbId = banner.getRightPictureClbId();
		if (rightPictureClbId > 0) {
			this.simpleClbClient.delete(leftPictureClbId);
		}
		int middlePictureClbId = banner.getMiddlePictureClbId();
		if (middlePictureClbId > 0) {
			this.simpleClbClient.delete(leftPictureClbId);
		}
		int cssClbId = banner.getCssClbId();
		if (cssClbId > 0) {
			this.simpleClbClient.delete(cssClbId);
		}
	}

	public Banner createBanner(int siteId, Banner banner) {
		banner.setCreatedTime(new Date());
		banner = bannerProvider.createBanner(siteId, banner);
		this.fillBannerFileUrl(banner);
		return banner;
	}

	public void deleteBanner(int siteId,int bannerId) {
		Banner banner = bannerProvider.getBannerById(bannerId);
		cache.removeEntry(siteId, bannerId);
		if (banner.getStatus() != 2) {
			bannerProvider.delete(bannerId);
			clearBannerClbPicture(banner);
		}
	}

	public void fillBannerFileUrl(Banner banner) {
		if (banner == null) {
			return;
		}
		int leftPictureClbId = banner.getLeftPictureClbId();
		if (leftPictureClbId > 0) {
			banner.setLeftPictureUrl(this.simpleClbClient
					.getDirectURL(leftPictureClbId));
		}
		int rightPictureClbId = banner.getLeftPictureClbId();
		if (rightPictureClbId > 0) {
			banner.setRightPictureUrl(this.simpleClbClient
					.getDirectURL(rightPictureClbId));
		}
		int middlePictureClbId = banner.getLeftPictureClbId();
		if (middlePictureClbId > 0) {
			banner.setMiddlePictureUrl(this.simpleClbClient
					.getDirectURL(middlePictureClbId));
		}
		int cssClbId = banner.getCssClbId();
		if (cssClbId > 0) {
			banner.setCssUrl(this.simpleClbClient.getDirectURL(cssClbId));
		}
	}

	public void fillBannersFileUrl(List<Banner> bannerList) {
		if (bannerList == null || bannerList.size() <= 0) {
			return;
		}
		for (Banner banner : bannerList) {
			fillBannerFileUrl(banner);
		}
	}

	public List<Banner> getAllBanners(int siteId) {
		List<Banner> bannerList = bannerProvider.getAllBanners(siteId);
		fillBannersFileUrl(bannerList);
		return bannerList;
	}
	public List<Banner> getAvailableBanners(String user, int siteId) {
		List<Banner> bannerList = bannerProvider.getAllBannersByUser(user,
				siteId);
		fillBannersFileUrl(bannerList);
		return bannerList;
	}

	public Banner getBannerById(int siteId, int bannerId) {
		Banner banner = null;
		if (bannerId == SKINBANNERID) {
			banner = this.getFirstBanner(siteId);
		} else {
			banner = (Banner) cache.getFromCache(siteId, bannerId);
			if (banner == null) {
				banner = bannerProvider.getBannerById(bannerId);
				cache.putInCache(siteId, bannerId, banner);
			}
		}

		if (banner != null
				&& (banner.getFirstTitle() == null || banner.getFirstTitle()
						.trim().length() < 1)) {
			Banner banner1 = this.getFirstBanner(siteId);
			banner.setFirstTitle(banner1.getFirstTitle());
		}

		if (banner != null) {
			fillBannerFileUrl(banner);
		}
		return banner;
	}

	public Banner getDefaultBanner(int siteId) {
		Banner banner= (Banner)cache.getFromCache(siteId, "default"+siteId);
		if (banner==null){
			banner = bannerProvider.getDefaultBanner(siteId);
			cache.putInCache(siteId, "default"+siteId, banner);
		}
		if (banner != null) {
			fillBannerFileUrl(banner);
		}
		return banner;
	}

	public Banner getFirstBanner(int siteId) {
		Banner banner = (Banner) cache.getFromCache(siteId, "firt"
				+ siteId);
		if (banner == null) {
			banner = bannerProvider.getFirstBanner(siteId);
			if (banner != null) {
				fillBannerFileUrl(banner);
			}
		}
		return banner;
	}

	@Override
	public int savePicture(String fileName, long length, InputStream in) {
		return simpleClbClient.createFile(fileName, length, in);
	}

	public void setBannerProvider(BannerProvider bannerProvider) {
		this.bannerProvider = bannerProvider;
	}

	public void setCacheService(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("banner");
	}

	public synchronized void setDefaultBanner(int siteId, int bannerId) {
		Banner willBeDefault = getBannerById(siteId, bannerId);
		Banner defaultBanner = this.getDefaultBanner(siteId);
		if (willBeDefault != null) {
			if (willBeDefault.getId() != defaultBanner.getId()) {
				willBeDefault.setStatus(2);
				updateBanner(siteId, willBeDefault);
				Banner banner = defaultBanner;
				defaultBanner = willBeDefault;
				banner.setStatus(1);
				cache.removeEntry(siteId, "default"+siteId);
				updateBanner(siteId, banner);
			}
		}
	}

	public void setSimpleClbClient(ISimpleClbClient simpleClbClient) {
		this.simpleClbClient = simpleClbClient;
	}

	public void updateBanner(int siteId, Banner banner) {
		cache.removeEntry(siteId, banner.getId());
		bannerProvider.updateBanner(siteId, banner);
	}

	@Override
	public int updatePicture(int clbId, String filename, long length,
			InputStream in) {
		return simpleClbClient.updateFile(clbId, filename, length, in);
	}

}