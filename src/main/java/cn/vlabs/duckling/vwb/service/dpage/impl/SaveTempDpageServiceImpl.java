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

package cn.vlabs.duckling.vwb.service.dpage.impl;

import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.SaveTempDpageService;
import cn.vlabs.duckling.vwb.service.dpage.TempPage;
import cn.vlabs.duckling.vwb.service.dpage.provider.TempPageProvider;

/**
 * Introduction Here.
 * @date 2010-3-9
 * @author 狄
 */
public class SaveTempDpageServiceImpl implements SaveTempDpageService {
	private TempPageProvider provider;
	public void setTempPageProvider(TempPageProvider provider){
		this.provider = provider;
	}
	//保存临时页面
	public void saveTempDpage(int siteId,DPage page, String content){
		if (provider.exists(siteId,  page.getResourceId(), page.getAuthor())){
			provider.update(siteId,  page.getResourceId(), page.getAuthor(),content);
		}else{
			provider.save(siteId, page.getResourceId(), page.getAuthor(), content);
		}
    }
	private String getAuthor(UserPrincipal p){
		return p.getFullName()+"("+p.getName()+")";
	}
    public void cleanTempPage(int siteId,int pageid, UserPrincipal currentUser){
    	provider.remove(siteId, pageid, getAuthor(currentUser));
    }
    
	@Override
	public TempPage getTempPage(int siteId, int resourceId, UserPrincipal author) {
		return provider.getTempPage(siteId, resourceId, getAuthor(author));
	}
}
