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

package cn.vlabs.duckling.vwb.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.config.DomainNameService;
import cn.vlabs.duckling.vwb.service.config.IContainerConfig;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.config.SiteItem;
import cn.vlabs.duckling.vwb.service.site.SiteManageService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;

/**
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public class DomainServiceImpl implements DomainNameService {
	private static final int GLOBAL_SITE_ID = 0;
	private static final Logger log = Logger.getLogger(DomainServiceImpl.class);
	private VWBCacheService cache;
	private IContainerConfig containerConfig;
	private IDomainProvider m_provider;
	private ISiteConfig siteConfig;
	private SiteManageService siteManagerService;

	private Properties buildProperties(String[] domains) {
		boolean first = true;
		Properties prop = new Properties();
		int i = 0;
		if (domains != null) {
			for (String domain : domains) {
				if (!prop.containsValue(domain)){
					if (first) {
						first = false;
						prop.put(KeyConstants.SITE_DOMAIN_KEY, domain);
					} else {
						prop.put(String.format("%s.%d",
								KeyConstants.SITE_DOMAIN_KEY, i), domain);
					}
					i++;
				}
			}
		}
		return prop;
	}

	private Properties compact(Map<String, String> domains){
		Properties prop = new Properties();
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(domains.keySet());
		Collections.sort(keys);
		int id = 0;
		HashSet<String> valueSet = new HashSet<String>();
		for (String key : keys) {
			String value = domains.get(key);
			if (!valueSet.contains(value) && isValidDomain(value)) {
				if (id == 0) {
					prop.put(KeyConstants.SITE_DOMAIN_KEY, value);
				} else {
					prop.put(KeyConstants.SITE_DOMAIN_KEY + "." + id, value);
				}
				id++;
				valueSet.add(value);
			}
		}
		return prop;
	}

	private String convertDate2String(Date date) {
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		StringBuilder dateStr = new StringBuilder(dateformat.format(date));

		return dateStr.toString();
	}

	private Set<String> getUsedDomainSet(int siteId) {
		String[] oldDomains = getUsedDomain(siteId);
		Set<String> oldSet = new HashSet<String>();
		if (oldDomains != null) {
			for (String oldDomain : oldDomains) {
				oldSet.add(oldDomain);
			}
		}
		return oldSet;
	}

	private boolean isDomainConflict(int siteId, String[] domains) {
		if (domains != null) {
			Set<String> oldSet = getUsedDomainSet(siteId);
			for (String domain : domains) {
				if ( !oldSet.contains(domain) && isDomainUsed(domain)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private boolean isValidDomain(String domain) {
		return !StringUtils.isBlank(domain)
				&& !StringUtils.equals(getDefaultDomain(), domain);
	}

	private void refineDomain(int siteId) {
		Map<String, String> domains = siteConfig.getPropertyStartWith(siteId,
				KeyConstants.SITE_DOMAIN_KEY);
		Properties prop = compact(domains);
		if (prop.size()!=domains.size()){
			log.info("Domain name's config is inconsist. it will be refined...");
			for (String key : domains.keySet()) {
				if (!prop.containsKey(key)) {
					siteConfig.removeProperty(siteId, key);
					cache.removeEntry(GLOBAL_SITE_ID, domains.get(key));
				}
			}
			siteConfig.setProperty(siteId, prop);
		}
	}

	@Override
	public int findSite(String domain) {
		Integer siteId = (Integer) cache.getFromCache(GLOBAL_SITE_ID, domain);
		if (siteId == null) {
			siteId = m_provider.getSiteId(domain);
			if (siteId!=-1){//Site is not found
				cache.putInCache(GLOBAL_SITE_ID, domain, siteId);
			}
		}
		return siteId;
	}

	@Override
	public Collection<SiteItem> getAllSiteItems() {
		Map<Integer, SiteItem> siteItems = m_provider.getAllSiteItems();
		List<SiteMetaInfo> sitesMeta = siteManagerService.getAllSites();
		List<SiteItem> list = new ArrayList<SiteItem>();
		for (SiteMetaInfo site : sitesMeta) {
			SiteItem item = siteItems.get(Integer.valueOf(site.getId()));
			if (item != null) {
				item.setState(site.getState());
				item.setCreateTime(convertDate2String(site.getCreateTime()));
				list.add(item);
			} else {
				SiteItem si = new SiteItem();
				si.setSiteName("");
				si.setSiteId(site.getId());
				si.setState(site.getState());
				si.setCreateTime(convertDate2String(site.getCreateTime()));
				siteItems.put(Integer.valueOf(site.getId()), si);
				list.add(si);
			}
		}
		return list;
	}

	@Override
	public String getDefaultDomain() {
		return containerConfig.getProperty(KeyConstants.SITE_DOMAIN_KEY);
	}
	
	@Override
	public String getSiteDefaultDomain(int siteId) {
		return siteConfig.getInternalProperty(siteId, KeyConstants.SITE_DOMAIN_KEY);
	}
	@Override
	public String[] getUsedDomain(int siteId) {
		Map<String, String> keys = siteConfig.getInteranlPeopertyStartWith(
				siteId, KeyConstants.SITE_DOMAIN_KEY);
		LinkedList<String> keyList = new LinkedList<String>();
		keyList.addAll(keys.keySet());
		Collections.sort(keyList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		String[] domains = new String[keys.size()];
		int index = 0;
		for (String key : keyList) {
			domains[index] = keys.get(key);
			index++;
		}
		return domains;
	}

	@Override
	public boolean isDomainUsed(String domain) {
		return StringUtils.equals(getDefaultDomain(), domain)
				||findSite(domain)!= -1;
	}

	public void setCacheService(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("domain");
	}

	public void setContainerConfig(IContainerConfig containerConfig) {
		this.containerConfig = containerConfig;
	}

	public void setDomainProvider(IDomainProvider provider) {
		this.m_provider = provider;
	}

	public void setSiteConfig(ISiteConfig siteConfig) {
		this.siteConfig = siteConfig;
	}


	public void setSiteManagerService(SiteManageService siteManagerService) {
		this.siteManagerService = siteManagerService;
	}

	@Override
	public boolean updateSiteDomains(int siteId, String[] domains) {
		if (!isDomainConflict(siteId,domains)){
			Map<String, String> domainMap = siteConfig.getPropertyStartWith(siteId,
					KeyConstants.SITE_DOMAIN_KEY);
			for (String key : domainMap.keySet()) {
				siteConfig.removeProperty(siteId, key);
				cache.removeEntry(GLOBAL_SITE_ID, domainMap.get(key));
			}
			siteConfig.setProperty(siteId, buildProperties(domains));
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean removeSiteDomains(int siteId) {
		Map<String, String> domainMap = siteConfig.getPropertyStartWith(siteId,
				KeyConstants.SITE_DOMAIN_KEY);
		if(domainMap==null){
			return true;
		}
		
		for (String key : domainMap.keySet()) {
			siteConfig.removeProperty(siteId, key);
			cache.removeEntry(GLOBAL_SITE_ID, domainMap.get(key));
		}
		return true;
	}
	
	@Override
	public boolean updateSiteMainDomain(int siteId, String domain) {
		if (!isDomainConflict(siteId, new String[]{domain})){
			cache.removeEntry(GLOBAL_SITE_ID,getSiteDefaultDomain(siteId));
			siteConfig.setProperty(siteId, KeyConstants.SITE_DOMAIN_KEY,domain);
			refineDomain(siteId);
			return true;
		}else{
			return false;
		}
	}
}
