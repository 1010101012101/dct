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

package cn.vlabs.duckling.vwb.service.banner;

import java.io.InputStream;
import java.util.List;

/**
 * Introduction Here.
 * 
 * @date 2010-2-26
 * @author Fred Zhang (fred@cnic.cn)
 */
public interface BannerService {
	List<Banner> getAvailableBanners(String user, int siteId);

	Banner createBanner(int siteId, Banner banner);

	void updateBanner(int siteId, Banner banner);

	void setDefaultBanner(int siteId, int bannerId);

	void deleteBanner(int siteId,int bannerId);

	Banner getBannerById(int siteId, int id);

	Banner getDefaultBanner(int siteId);

	List<Banner> getAllBanners(int siteId);

	int savePicture(String fileName, long size, InputStream stream);

	int updatePicture(int clbId, String filename, long length, InputStream in);

	void fillBannerFileUrl(Banner banner);

	void fillBannersFileUrl(List<Banner> bannerList);

	Banner getFirstBanner(int siteId);
}
