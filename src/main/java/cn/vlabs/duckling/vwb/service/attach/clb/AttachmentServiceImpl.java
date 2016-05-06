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
package cn.vlabs.duckling.vwb.service.attach.clb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.rest.IFileSaver;
import cn.vlabs.rest.ServiceContext;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * 访问clb服务的工具类
 * 
 * @author yhw May 14, 2009
 */
public class AttachmentServiceImpl implements AttachmentService {
	private static Logger log = Logger.getLogger(AttachmentServiceImpl.class);
	//全局的缓存
	private static final int SITE_ID=0;
	private VWBCacheService cache;
	private CLBConnection conn;
	private DocumentService dService;
	private JDBCCLBProvider provider = null;

	private UpdateInfo createDocument(String author, boolean isPub,StreamInfo stream) {
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		CreateInfo createInfo = new CreateInfo();
		createInfo.setTitle(stream.getFilename());
		if (isPub) {
			createInfo.setIsPub(1);
		} else {
			createInfo.setIsPub(0);
		}

		return dService.createDocument(createInfo, stream);
	}
	private DocumentService getDocumentService() {
		if (dService == null) {
			if (conn == null) {
				log.error("CLB connection is not available.");
				return null;
			}
			dService = CLBServiceFactory.getDocumentService(conn);
		}
		return dService;
	}

	private CachedPair getLatestVersion(int docid){
		String key = String.format("docpair_%d", docid);
		CachedPair pair = (CachedPair) cache.getFromCache(SITE_ID, key);
		if (pair==null){
			MetaInfo meta = getDocumentService().getMeta(docid);
			if (meta!=null){
				pair = new CachedPair();
				pair.setDocid(meta.getDocid());
				pair.setVersion(meta.getVersion());
				cache.putInCache(SITE_ID, key, pair);
			}
		}
		return pair;
	}

	private void saveAttachInfo(int siteId, String author, String parentid, UpdateInfo ui, StreamInfo stream) {
		CLBAttachment att = new CLBAttachment(parentid, stream.getFilename(), ui.getVersion());
		att.setSiteId(siteId);
		att.setClbId(ui.getDocid());
		att.setTime(new Date());
		att.setAuthor(author);
		att.setLength((int) stream.getLength());
		att.setVersion(Integer.parseInt(ui.getVersion()));
		provider.putCLBAttachmentData(att);
	}


	@Override
	public int createDocument(int siteId, String parentId, String author, boolean isPub,StreamInfo stream) {
		UpdateInfo updateInfo = createDocument(author, isPub, stream);
		saveAttachInfo(siteId, author, parentId, updateInfo, stream);
		return updateInfo.getDocid();
	}
	@Override
	public List<CLBAttachment> getCLBAttatchmentChanged(int siteId, Date begin, Date end) {
		return provider.listAllChanged(siteId, begin, end);
	}
	@Override
	public void getContent(int docid, IFileSaver fs) {
		getDocumentService().getContent(docid, fs);
	}
	@Override
	public void getContent(int docid, String version, IFileSaver fs) {
		getDocumentService().getContent(docid, version, fs);
	}
	@Override
	public String getDirectUrl(int docid) {
		CachedPair pair= this.getLatestVersion(docid);
		if (pair!=null){
			return getDirectUrl(pair.getDocid(), pair.getVersion());
		}else{
			log.error("Can't find attachement "+docid);
			return "";
		}
	}

	@Override
	public String getDirectUrl(int docid, String version) {
		if ("latest".equals(version)){
			return getDirectUrl(docid);
		}
		
		String key = String.format("directUrl_%d_%s",docid, version);
		String url =(String)cache.getFromCache(SITE_ID, key);
		if (url==null){
			url = getDocumentService().getContentURL(docid,version);
			cache.putInCache(SITE_ID, key, url);
		}
		
		return url;
	}
	@Override
	public MetaInfo getMetaInfo(int docid) {
		return getDocumentService().getMeta(docid);
	}
	public void init() {
		ServiceContext.setMaxConnection(20, 20);
	}
	@Override
	public List<CLBAttachment> recentUploaded(int siteId, int num) {
		return provider.findRecentUploaded(siteId, num);
	}
	@Override
	public List<CLBAttachment> search(int siteId, String filename) {
		if (filename==null){
			return new ArrayList<CLBAttachment>();
		}
		return provider.search(siteId, filename);
	}
	
	public void setCacheService(VWBCacheService cache){
		this.cache = cache;
		this.cache.setModulePrefix("attach");
	}
	
	public void setClbConnection(CLBConnection conn){
		this.conn = conn;
	}
	
	public void setProvider(JDBCCLBProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public UpdateInfo updateDocument(int siteId, String parentId,String author, int docid,StreamInfo stream) {
		UpdateInfo ui = getDocumentService().update(docid, stream.getFilename(), stream);
		saveAttachInfo(siteId, author, parentId, ui, stream);
		return ui;
	}
}
