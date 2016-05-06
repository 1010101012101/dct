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

package cn.vlabs.duckling.vwb.ui.rsi.api.dpage;

import java.util.List;

import cn.vlabs.duckling.vwb.ui.rsi.api.DCTRsiServiceException;
import cn.vlabs.duckling.vwb.ui.rsi.api.VWBAppConnection;


/**
 * Introduction Here.
 * @date 2010-3-30
 * @author Fred Zhang (fred@cnic.cn)
 */
public class RemoteDPageService implements DPageService {
	private VWBAppConnection con;

	public RemoteDPageService(VWBAppConnection con) {
		this.con = con;
	}
	public DPageInfo getContent(int siteId,int resourceId) throws DCTRsiServiceException { 
		return getContent(siteId,resourceId, -1);
	}

	public DPageInfo getContent(int siteId,int resourceId, int version)
			throws DCTRsiServiceException {
		DPageRequestItem item = new DPageRequestItem();
		item.setResourceId(resourceId);
		item.setVersion(version);
		item.setSiteId(siteId);
		DPageInfo info = (DPageInfo) con.sendService("getDpageService", item);
		return info;
	}
	
	public void updateContent(int siteId,int resourceId, String title, String content) throws DCTRsiServiceException{
		updateContent(siteId,resourceId, title, content, false);
	}
	
	public void updateContent(int siteId,int resourceId, String title, String content,boolean force)
			throws DCTRsiServiceException {
		DPageRequestItem info = new DPageRequestItem();
		info.setContent(content);
		info.setResourceId(resourceId);
		info.setTitle(title);
		info.setForce(force);
		info.setSiteId(siteId);
		con.sendService("updateDpageService", info);
	}

	public int createDPage(int siteId,String title, String content)
			throws DCTRsiServiceException {
		DPageRequestItem info = new DPageRequestItem();
		info.setContent(content);
		info.setTitle(title);
		info.setSiteId(siteId);
		Integer id = (Integer)con.sendService("createDpageService", info);
		return id.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<BasedDPage> getSubPages(int siteId, int pageId)
			throws DCTRsiServiceException {
		DPageRequestItem info = new DPageRequestItem();
		info.setResourceId(pageId);
		info.setSiteId(siteId);
		List<BasedDPage> dpages = (List<BasedDPage>)con.sendService("getSubDpageService", info);
		return dpages;
	}

}
