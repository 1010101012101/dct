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

package cn.vlabs.duckling.vwb.services.banner.provider.impl;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.impl.BannerProvider;
import cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl;
import cn.vlabs.duckling.vwb.services.TestService;

/**
 * @date 2011-11-3
 * @author y
 */
public class BannerProviderImplTest extends TestService{
	private  BannerProvider getBp(){
		BannerProviderImpl bp =  (BannerProviderImpl) this.manager.getFactory().getBean("bannerProvider");
		return bp;
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#createBanner(cn.vlabs.duckling.vwb.service.banner.Banner)}.
	 */
	@Test
	public void testCreateBanner() {
		int siteId=1;
		Banner banner = new Banner();
		banner.setBannerTitle(1);
		banner.setCreator("yhw@cnic.cn");
		banner.setFirstTitle("会议名称");
		banner.setSecondTitle("会议地点");
		banner.setThirdTitle("会议时间");
		banner.setName("TestBanner");
		banner.setOwnedtype("upload");
		banner.setCreatedTime(new Date());
		banner.setBannerProfile();
		getBp().createBanner(siteId,banner);
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#delete(int)}.
	 */
	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#getAllBanners()}.
	 */
	@Test
	public void testGetAllBanners() {
		List<Banner> bannerList = getBp().getAllBanners(1);
		System.out.println(bannerList.size());
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#getAllBannersByUser(java.lang.String)}.
	 */
	@Test
	public void testGetAllBannersByUser() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#getBannerById(int)}.
	 */
	@Test
	public void testGetBannerById() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#getDefaultBanner()}.
	 */
	@Test
	public void testGetDefaultBanner() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cn.vlabs.duckling.vwb.service.banner.provider.BannerProviderImpl#updateBanner(cn.vlabs.duckling.vwb.service.banner.Banner)}.
	 */
	@Test
	public void testUpdateBanner() {
		fail("Not yet implemented");
	}

}
