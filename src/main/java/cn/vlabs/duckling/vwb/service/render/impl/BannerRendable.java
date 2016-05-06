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

package cn.vlabs.duckling.vwb.service.render.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.BannerService;
import cn.vlabs.duckling.vwb.service.render.Rendable;

/**
 * Introduction Here.
 * 
 * @date 2010-3-1
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerRendable implements Rendable {
	private int id;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void render(VWBContext context, PageContext pageContext)
			throws ServletException, IOException {
		BannerService bannerService = VWBContext.getContainer().getBannerService();
		Banner banner = null;
		if (id > 0) {
			banner = bannerService.getBannerById(context.getSite().getId(),id);
		}
		if (banner == null || banner.getId() < 1) {
			banner = bannerService.getDefaultBanner(context.getSite().getId());
		}
		String jsp = "";
		if (id == -2) {
			jsp = "/WEB-INF/banner/skinbanner/skinbanner.jsp";
		}
		if (banner == null || banner.getId() < 1) {
			List<Banner> banners = bannerService.getAllBanners(context.getSite().getId());
			for (Banner bn : banners) {
				if (bn.getType() == Banner.WITH3TITLETYPE
						&& bn.getFirstTitle().length() > 1) {
					banner = bn;
					break;
				}
			}
		}

		String bannerTitleContent = "";
		bannerTitleContent = context.getProperty("duckling.site.name");
		context.getHttpRequest().setAttribute("realBannerTitleContent",
				bannerTitleContent);

		if (banner != null) {
			if (id != -2) {
				jsp = "/WEB-INF/banner/jsp/type" + banner.getType() + ".jsp";
			}
			context.getHttpRequest().setAttribute("realBanner", banner);
		} else {
			banner = new Banner();
			banner.setFirstTitle(bannerTitleContent);
		}

		if (id == -2) {
			
			if (banner.getFirstTitle() == null
					|| banner.getFirstTitle().trim().length() < 1)
				banner.setFirstTitle(bannerTitleContent);
		}
		context.getHttpRequest().setAttribute("realBanner", banner);
		pageContext.include(jsp);
	}

}
