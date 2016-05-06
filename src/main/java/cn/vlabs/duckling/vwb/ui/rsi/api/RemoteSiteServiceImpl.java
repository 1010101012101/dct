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

package cn.vlabs.duckling.vwb.ui.rsi.api;

import java.util.List;

import cn.vlabs.duckling.vwb.ui.rsi.api.banner.BannerItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.banner.BannerRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.DomainListRequest;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.DomainRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.SiteLefyCycleRequest;
import cn.vlabs.duckling.vwb.ui.rsi.api.domain.SiteStatus;
import cn.vlabs.duckling.vwb.ui.rsi.api.language.LanguageRequest;
import cn.vlabs.duckling.vwb.ui.rsi.api.mail.MailRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.mail.MailSettingItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.sitestatus.SiteStatusRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.skins.SkinRequestItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.template.TemplateItem;
import cn.vlabs.duckling.vwb.ui.rsi.api.template.TemplateRequestItem;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class RemoteSiteServiceImpl implements RemoteSiteService {
	private VWBAppConnection con;

	public RemoteSiteServiceImpl(VWBAppConnection con) {
		this.con = con;
	}

	public SiteResponseItem createSite(SiteRequestItem params)
			throws DCTRsiServiceException {
		return (SiteResponseItem) con.sendService("createSiteService", params);
	}

	public void updateSiteName(String siteName, int siteId)
			throws DCTRsiServiceException {
		SiteNameRequestItem item = new SiteNameRequestItem();
		item.setSiteId(siteId);
		item.setSiteName(siteName);
		con.sendService("updateSiteNameService", item);
	}

	public void updateRootBanner(int siteId, String bannerName, StreamInfo in)
			throws DCTRsiServiceException {
		this.updateBanner(siteId, 101, bannerName, in);
	}

	public void updateBanner(int siteId, int resourceId, String bannerName,
			StreamInfo in) throws DCTRsiServiceException {
		BannerRequestItem item = new BannerRequestItem();
		item.setBannerName(bannerName);
		item.setSiteId(siteId);
		item.setResourceId(resourceId);
		item.setFileName(in.getFilename());
		item.setMethod("updateBanner");
		con.sendService("bannerService", item, in);
	}

	public void cspCreateBanner(int siteId, BannerItem banner)
			throws DCTRsiServiceException {
		BannerRequestItem item = new BannerRequestItem();
		item.setBannerName(banner.getName());
		item.setSiteId(siteId);
		item.setResourceId(101);
		item.setFirstTitle(banner.getFirstTitle());
		item.setSecondTitle(banner.getSecondTitle());
		item.setThirdTitle(banner.getThirdTitle());
		item.setMethod("cspCreateBanner");

		con.sendService("bannerService", item);
	}

	public void updateBannerWithImg(int siteId, BannerItem banner, StreamInfo in)
			throws DCTRsiServiceException {
		BannerRequestItem item = new BannerRequestItem();
		item.setBannerName(banner.getName());
		item.setSiteId(siteId);
		item.setResourceId(101);
		item.setFileName(in.getFilename());
		item.setFirstTitle(banner.getFirstTitle());
		item.setSecondTitle(banner.getSecondTitle());
		item.setThirdTitle(banner.getThirdTitle());
		item.setMethod("updateBannerWithImg");
		con.sendService("bannerService", item, in);

	}

	public void updateBannerNoImg(int siteId, BannerItem banner)
			throws DCTRsiServiceException {
		BannerRequestItem item = new BannerRequestItem();
		item.setBannerName(banner.getName());
		item.setSiteId(siteId);
		item.setResourceId(101);
		item.setFirstTitle(banner.getFirstTitle());
		item.setSecondTitle(banner.getSecondTitle());
		item.setThirdTitle(banner.getThirdTitle());
		item.setMethod("updateBannerNoImg");

		con.sendService("bannerService", item);
	}

	public boolean isDomainUsed(String domain) throws DCTRsiServiceException {
		DomainRequestItem item = new DomainRequestItem();
		item.setDomain(domain);
		item.setSiteId(1);
		item.setMethod("isDomainUsed");
		return ((Boolean) con.sendService("domainService", item))
				.booleanValue();
	}

	public void updateDomain(int siteId, String domain)
			throws DCTRsiServiceException {
		DomainRequestItem item = new DomainRequestItem();
		item.setDomain(domain);
		item.setSiteId(siteId);
		item.setMethod("updateSiteDomain");
		con.sendService("domainService", item);
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> getMenu(int siteId, int resourceId)
			throws DCTRsiServiceException {
		MenuRequestItem request = new MenuRequestItem();
		request.setResourceId(resourceId);
		request.setVersion(-1);
		request.setSiteId(siteId);
		request.setMethod("getLeftMenu");
		return (List<MenuItem>) con.sendService("leftMenuService", request);
	}

	public void updateMenu(int siteId, int resourceId, List<MenuItem> items)
			throws DCTRsiServiceException {
		MenuRequestItem request = new MenuRequestItem();
		request.setResourceId(resourceId);
		request.setSiteId(siteId);
		request.setMethod("updateLeftMenu");
		request.setItems(items);
		con.sendService("leftMenuService", request);

	}

	public MailSettingItem getMailSettingItem(int siteId)
			throws DCTRsiServiceException {
		MailRequestItem request = new MailRequestItem();
		request.setSiteId(siteId);
		request.setMethod("getEmailSetting");
		return (MailSettingItem) con.sendService("mailService", request);
	}

	public void updateMainlSettingItem(int siteId, MailSettingItem mailItem)
			throws DCTRsiServiceException {
		MailRequestItem request = new MailRequestItem();
		request.setSiteId(siteId);
		request.setMethod("updateMailSetting");
		request.setMailSetting(mailItem);
		con.sendService("mailService", request);
	}

	@SuppressWarnings("unchecked")
	public List<SkinItem> getAllSkins(int siteId) throws DCTRsiServiceException {
		SkinRequestItem request = new SkinRequestItem();
		request.setSiteId(siteId);
		request.setMethod("getAllSkins");
		return (List<SkinItem>) con.sendService("skinsService", request);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TemplateItem> getAllTemplate() throws DCTRsiServiceException{
		TemplateRequestItem request = new TemplateRequestItem();
		request.setSiteId(1);
		request.setMethod("getAllTemplate");
		return (List<TemplateItem>) con.sendService("templateService", request);
	}

	public void setSkin(int siteId, String skinName)
			throws DCTRsiServiceException {
		SkinRequestItem request = new SkinRequestItem();
		request.setSiteId(siteId);
		request.setSkinName(skinName);
		request.setMethod("setSkin");
		con.sendService("skinsService", request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.vlabs.duckling.vwb.ui.rsi.api.RemoteSiteService#getDefaultBanner(int)
	 */
	public BannerItem getDefaultBanner(int siteId)
			throws DCTRsiServiceException {
		return this.getBanner(siteId, 101);
	}

	public BannerItem getBanner(int siteId, int resourceId)
			throws DCTRsiServiceException {
		BannerRequestItem request = new BannerRequestItem();
		request.setSiteId(siteId);
		request.setResourceId(resourceId);
		request.setMethod("getBanner");
		return (BannerItem) con.sendService("bannerService", request);
	}

	public String getSiteDefaultLanguage(int siteId)
			throws DCTRsiServiceException {
		LanguageRequest request = new LanguageRequest();
		request.setSiteId(siteId);
		request.setMethod("getLanguage");
		return (String) con.sendService("siteLanguageService", request);
	}

	public void setSiteDefaultLanguage(int siteId, String language)
			throws DCTRsiServiceException {
		LanguageRequest request = new LanguageRequest();
		request.setSiteId(siteId);
		request.setLanguage(language);
		request.setMethod("setLanguage");
		con.sendService("siteLanguageService", request);
	}

	public void destroySite(int siteId) throws DCTRsiServiceException {
		con.sendService("destroySiteService", Integer.valueOf(siteId));
	}

	public void activateSite(int siteId) throws DCTRsiServiceException {
		SiteLefyCycleRequest request = new SiteLefyCycleRequest();
		request.setMethod("activateSite");
		request.setSiteId(siteId);
		con.sendService("siteLifeCycleService", request);
	}

	public void deactivateSite(int siteId) throws DCTRsiServiceException {
		SiteLefyCycleRequest request = new SiteLefyCycleRequest();
		request.setMethod("deactivateSite");
		request.setSiteId(siteId);
		con.sendService("siteLifeCycleService", request);
	}

	public SiteStatus checkSiteStatus(int siteId) throws DCTRsiServiceException {
		SiteLefyCycleRequest request = new SiteLefyCycleRequest();
		request.setMethod("checkSiteStatus");
		request.setSiteId(siteId);
		return (SiteStatus) con.sendService("siteLifeCycleService", request);
	}

	public boolean publishSite(int siteId) throws DCTRsiServiceException {
		SiteStatusRequestItem ssri = new SiteStatusRequestItem();
		ssri.setMethod("publishedSite");
		ssri.setSiteId(siteId);
		return (Boolean) con.sendService("sitePublishService", ssri);
	}

	public boolean isPublishedSite(int siteId) throws DCTRsiServiceException {
		SiteStatusRequestItem ssri = new SiteStatusRequestItem();
		ssri.setMethod("isPublishedSite");
		ssri.setSiteId(siteId);
		return (Boolean) con.sendService("sitePublishService", ssri);
	}

	public String[] getSiteDomains(int siteId) throws DCTRsiServiceException {
		DomainRequestItem item = new DomainRequestItem();
		item.setSiteId(siteId);
		item.setMethod("getSiteDomains");
		return (String[])con.sendService("domainService", item);
	}

	public void updateSiteDomains(int siteId, String[] domains)
			throws DCTRsiServiceException {
		DomainListRequest item = new DomainListRequest();
		item.setSiteId(siteId);
		item.setMethod("updateAllDomains");
		item.setDomains(domains);
		con.sendService("domainService", item);
	}
	
	
}
