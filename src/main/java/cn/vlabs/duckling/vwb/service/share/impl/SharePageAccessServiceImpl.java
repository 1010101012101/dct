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
package cn.vlabs.duckling.vwb.service.share.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.share.AccessRecord;
import cn.vlabs.duckling.vwb.service.share.SharePageAccessService;
import cn.vlabs.duckling.vwb.service.url.impl.UrlServiceImpl;

public class SharePageAccessServiceImpl implements SharePageAccessService {
	private static final Logger log = Logger
			.getLogger(SharePageAccessServiceImpl.class);
	private int expirePeriod = 0;
	private ISiteConfig siteConfig;
	
	public void setSiteConfig(ISiteConfig siteConfig){
		this.siteConfig=siteConfig;
	}
	private SharePageAccessProvider provider = null;
	private String genHash() {
		// 随机生成hash
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("Blowfish");
		} catch (NoSuchAlgorithmException e) {
			log.error("Generate hash error!");
			return null;
		}

		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		return cn.vlabs.duckling.util.Utility.getBASE64(new String(key
				.getEncoded()));
	}

	private String readAccessRecord(int ID, String hash) {

		AccessRecord ar = provider.get(ID);
		if (ar == null) {
			return null;
		}
		log.debug("Access original get=" + ar.gethash().trim()
				+ " original hash=" + hash);
		if (!ar.gethash().trim().equals(hash)) {
			log.error("Fake access!");
			return null;
		}
		if (System.currentTimeMillis() > (ar.getShareTime() + expirePeriod * 3600 * 1000)) {
			log.error("Access URL expired.");
			return null;
		}

		return ar.getURL().trim();

	}

	private void updateAccessRecord(int ID, String hash) {
		AccessRecord ar = provider.get(ID);

		if (!ar.gethash().equals(hash)) {
			log.error("Fake access!");
			return;
		}

		if (ar.getAccessTime() != 0)
			return;

		ar.setAccessTime(System.currentTimeMillis());
		provider.update(ar);
	}

	private AccessRecord writeAccessRecord(String page) {
		String hash = genHash();
		if (hash != null) {
			AccessRecord ar = new AccessRecord();
			ar.setURL(page);
			ar.setShareTime(System.currentTimeMillis());
			ar.setAccessTime(0);
			ar.sethash(hash);
			ar.setID(provider.add(ar));
			return ar;
		} else {
			return null;
		}
	}

	public String getMainAreaUrl(int siteId, int resouceid, int ID, String hash) {
		UrlServiceImpl urlConstructor = new UrlServiceImpl(siteId,siteConfig);
		String pageId = readAccessRecord(ID, hash);
		if (pageId == null)
			return null;
		try {
			
			return urlConstructor.getURL(VWBContext.SHARE, pageId, "mode=mainpart&hash="
					+ java.net.URLEncoder.encode(hash, "UTF-8") + "&ID=" + ID);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getPageID(int ID, String hash) {
		String pageId = readAccessRecord(ID, hash);
		if (pageId == null)
			return null;
		return pageId;
	}

	public String getUrl(int siteId, int resouceid, int ID, String hash) {
		UrlServiceImpl urlConstructor = new UrlServiceImpl(siteId,siteConfig);
		String pageId = readAccessRecord(ID, hash);
		if (pageId == null)
			return null;
		try {
			return urlConstructor.getURL(VWBContext.SHARE, pageId, "&hash="
					+ java.net.URLEncoder.encode(hash, "UTF-8") + "&ID=" + ID);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setExpiredPeriod(int expirePeriod) {
		this.expirePeriod = expirePeriod;
	}

	public void setProvider(SharePageAccessProvider provider) {
		this.provider = provider;
	}

	public void UpdateShareAcl(int ID, String hash) {
		updateAccessRecord(ID, hash);
	}

	public AccessRecord WriteShareAcl(String page) {
		return writeAccessRecord(page);
	}

}
