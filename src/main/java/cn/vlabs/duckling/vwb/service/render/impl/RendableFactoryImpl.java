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

import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.render.DView;
import cn.vlabs.duckling.vwb.service.render.Rendable;
import cn.vlabs.duckling.vwb.service.render.RendableFactory;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public class RendableFactoryImpl implements RendableFactory {
	private ViewPortService m_viewports;
	private ISiteConfig m_config;
	
	public void setSiteConfig(ISiteConfig siteConfig){
		this.m_config = siteConfig;
	}
	public void setViewportService(ViewPortService viewportService){
		this.m_viewports = viewportService;
	}

	public DView createView(ViewPort vp) {
		DView view = new DView();

		// Banner
		if (vp.isShowBanner()) {
			BannerRendable br = new BannerRendable();
			br.setId(vp.getBanner());
			view.setBanner(br);
		}
		// LeftMenu
		if (vp.isShowLeftMenu())
			view.setLeftMenu(createRendable(vp.getSiteId(), vp.getLeftMenu()));

		// TopMenu
		if (vp.isShowTopMenu())
			view.setTopMenu(createRendable(vp.getSiteId(), vp.getTopMenu()));

		// Content
		view.setContent(createRendable(vp));

		// Footer
		view.setFooter(createRendable(vp.getSiteId(), vp.getFooter()));

		// Trail
		view.setShowTrail(vp.isShowTrail());

		// UserBox
		view.setShowUserbox(Boolean.valueOf(m_config
				.getProperty(vp.getSiteId(),"duckling.userbox")));
		// SearchBox
		view.setShowSearchbox(Boolean.valueOf(m_config
				.getProperty(vp.getSiteId(),"duckling.searchbox")));

		return view;
	}

	private Rendable createRendable(int siteId, int vid) {
		ViewPort vp = m_viewports.getViewPort(siteId, vid);
		if (vp != null)
			return createRendable(vp);
		else
			return null;
	}

	private Rendable createRendable(ViewPort vp) {
		if (Resource.TYPE_FUNCTION.equals(vp.getType())) {
			return new JSPRendable(vp.getId());
		}
		if (Resource.TYPE_DPAGE.equals(vp.getType())) {
			return new DPageRendable(vp.getId());
		}
		if (Resource.TYPE_PORTAL.equals(vp.getType())) {
			return new PortalPageRendable(vp.getId());
		}
		return null;
	}
}
