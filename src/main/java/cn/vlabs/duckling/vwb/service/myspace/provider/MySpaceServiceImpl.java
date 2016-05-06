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

package cn.vlabs.duckling.vwb.service.myspace.provider;

import java.util.Calendar;
import java.util.Date;

import cn.vlabs.duckling.vwb.VWBException;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.event.DPageEvent;
import cn.vlabs.duckling.vwb.service.event.DPageEventType;
import cn.vlabs.duckling.vwb.service.event.VWBEvent;
import cn.vlabs.duckling.vwb.service.event.VWBEventListener;
import cn.vlabs.duckling.vwb.service.event.VWBEventService;
import cn.vlabs.duckling.vwb.service.myspace.MySpace;
import cn.vlabs.duckling.vwb.service.myspace.MySpaceService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date 2010-3-25
 * @author Fred Zhang (fred@cnic.cn)
 */
public class MySpaceServiceImpl implements MySpaceService, VWBEventListener {
	private VWBCacheService cache;
	private MySpaceProvider mySpaceProvider;
	private ViewPortService viewportService;

	public void setCacheService(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("myspace");
	}

	public void setViewportService(ViewPortService viewportService) {
		this.viewportService = viewportService;
	}

	public void init() throws VWBException {
		VWBEventService.addVWBEventListener(DPageService.class, this);
	}

	public void setMySpaceProvider(MySpaceProvider mySpaceProvider) {
		this.mySpaceProvider = mySpaceProvider;
	}

	@Override
	public MySpace createMySpace(MySpace main) {
		MySpace m = mySpaceProvider
				.getMySpace(main.getSiteId(), main.getUser());
		if (m != null) {
			return m;
		}
		if (main.getResourceId() <= 0) {
			ViewPort vp = new ViewPort();
			vp.setTitle(main.getUser());
			vp.setCreator(main.getCreator());
			vp.setType(Resource.TYPE_DPAGE);
			Date time = Calendar.getInstance().getTime();
			vp.setCreateTime(time);
			int resourceId = viewportService.createViewPort(main.getSiteId(),
					vp);
			main.setResourceId(resourceId);
		}
		mySpaceProvider.createMySpace(main);
		cache.putInCache(main.getSiteId(), main.getUser(), main);
		return main;
	}

	@Override
	public MySpace getMySpace(int siteId, String user) {
		MySpace space = (MySpace) cache.getFromCache(siteId, user);
		if (space == null) {
			space = mySpaceProvider.getMySpace(siteId, user);
			if (space != null) {
				cache.putInCache(siteId, user, space);
			}
		}
		return space;
	}

	@Override
	public void actionPerformed(VWBEvent event) {
		if (event instanceof DPageEvent) {
			if (((DPageEvent) event).getType() == DPageEventType.DPAGE_DELETE) {
				DPage page = (DPage) ((DPageEvent) event).getSource();
				MySpace myspace = mySpaceProvider.getMySpace(page.getSiteId(),
						page.getResourceId());
				if (myspace != null) {
					cache.removeEntry(page.getSiteId(), myspace.getUser());
					mySpaceProvider.deleteMySpace(page.getSiteId(),
							page.getResourceId());
				}
			}
		}
	}

}
