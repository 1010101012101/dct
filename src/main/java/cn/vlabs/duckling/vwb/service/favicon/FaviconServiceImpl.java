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

package cn.vlabs.duckling.vwb.service.favicon;

import java.io.InputStream;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * @date 2013-5-29
 * @author xiejj
 */
public class FaviconServiceImpl implements FaviconService {
	private static final String FAVICON="duckling.favicon";
	private VWBCacheService cache;

	private CLBConnection conn;

	private ISiteConfig siteConfig;

	private int createDocument(InputStream in, long size){
		DocumentService docService = CLBServiceFactory.getDocumentService(conn);
		CreateInfo createInfo = new CreateInfo();
		StreamInfo streamInfo = new StreamInfo();
		streamInfo.setFilename("favicon.ico");
		streamInfo.setInputStream(in);
		streamInfo.setLength(size);
		
		createInfo.setTitle(streamInfo.getFilename());
		createInfo.setIsPub(1);

		UpdateInfo ui= docService.createDocument(createInfo, streamInfo);
		return ui.getDocid();
	}
	
	private String getDirectUrl(int docid){
		DocumentService docService=  CLBServiceFactory.getDocumentService(conn);
		return docService.getContentURL(docid, "latest");
	}
	@Override
	public String getFavicon(int siteId) {
		String url = (String) cache.getFromCache(siteId, FAVICON);
		if (url==null){
			String docId=siteConfig.getProperty(siteId, FAVICON);
			if (docId!=null){
				url = getDirectUrl(Integer.valueOf(docId));
				cache.putInCache(siteId, FAVICON, url);
			}
		}
		return url;
	}
	@Override
	public void save(int siteId, InputStream in, long size) {
		int docid=createDocument(in, size);
		siteConfig.setProperty(siteId, FAVICON,Integer.toString(docid));
		cache.removeEntry(siteId, FAVICON);
	}
	
	public void setCacheService(VWBCacheService cache){
		this.cache = cache;
		this.cache.setModulePrefix("fav");
	}

	public void setClbConnection(CLBConnection conn) {
		this.conn = conn;
	}
	
	public void setSiteConfig(ISiteConfig config) {
		this.siteConfig = config;
	}
}
