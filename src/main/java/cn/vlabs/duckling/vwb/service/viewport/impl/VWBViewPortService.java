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

package cn.vlabs.duckling.vwb.service.viewport.impl;

import java.util.ArrayList;
import java.util.List;

import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.idgen.IKeyGenerator;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ResourceConstants;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortType;

/**
 * 该类是负责查询ViewPort.
 * 
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public class VWBViewPortService implements ViewPortService {
	private VWBCacheService cacheService;
	private IKeyGenerator keyGen;
	private TreeViewProvider m_tree_view;
	private ViewPortProvider m_view_provider;

	private void flushMappedView(int siteId) {
		List<ViewPort> viewports = m_view_provider.getAllViewPort(siteId);
		List<Integer> viewIds = new ArrayList<Integer>();
		for (ViewPort viewport : viewports) {
			viewIds.add(viewport.getId());
		}
		cacheService.removeBulk(siteId, viewIds);
	}

	public int createViewPort(int siteId, ViewPort vp) {
		vp.setId(keyGen.getNextID(siteId));
		vp.setSiteId(siteId);
		return m_view_provider.create(vp);
	}

	public int getMappedAclId(int siteId, int id) {
		ViewPort viewPort = this.getMappedView(siteId, id);
		if (viewPort != null) {
			return viewPort.getAclPolicy();
		} else {
			return -1;
		}
	}

	public ViewPort getMappedView(int siteId, int resourceId) {
		ViewPort mapped = (ViewPort) this.cacheService.getFromCache(siteId,
				resourceId);
		if (mapped != null) {
			return mapped;
		}
		ViewPort viewport = m_view_provider.getViewPort(siteId, resourceId);
		if (viewport == null) {
			return null;
		}

		mapped = (ViewPort) viewport.clone();
		ViewPort root = m_view_provider.getViewPort(siteId,
				ResourceConstants.ROOT);
		if (mapped.isShowBanner()) {
			int parentid = m_tree_view.get(ViewPortType.BANNER, siteId,
					resourceId);
			ViewPort parent = m_view_provider.getViewPort(siteId, parentid);
			mapped.setBanner(ViewPort.isInherit(parent.getBanner()) ? root
					.getBanner() : parent.getBanner());
		}

		// LeftMenu
		if (mapped.isShowLeftMenu()) {
			int parentid = m_tree_view.get(ViewPortType.LEFT_MENU, siteId,
					resourceId);
			ViewPort parent = m_view_provider.getViewPort(siteId, parentid);
			mapped.setLeftMenu(ViewPort.isInherit(parent.getLeftMenu()) ? root
					.getLeftMenu() : parent.getLeftMenu());
		}

		// TopMenu
		if (mapped.isShowTopMenu()) {
			int parentid = m_tree_view.get(ViewPortType.TOP_MENU, siteId,
					resourceId);
			ViewPort parent = m_view_provider.getViewPort(siteId, parentid);
			mapped.setTopMenu(ViewPort.isInherit(parent.getTopMenu()) ? root
					.getTopMenu() : parent.getTopMenu());
		}

		// Footer
		if (mapped.isShowFooter()) {
			int parentid = m_tree_view.get(ViewPortType.FOOTER, siteId,
					resourceId);
			ViewPort parent = m_view_provider.getViewPort(siteId, parentid);
			mapped.setFooter(ViewPort.isInherit(parent.getFooter()) ? root
					.getFooter() : parent.getFooter());
		}

		// Trail
		if (mapped.isShowTrail()) {
			int parentid = m_tree_view.get(ViewPortType.TRAIL, siteId,
					resourceId);
			ViewPort parent = m_view_provider.getViewPort(siteId, parentid);
			mapped.setTrail(ViewPort.isInherit(parent.getTrail()) ? root
					.getTrail() : parent.getTrail());
		}
		// Acl
		int aclId = m_tree_view.get(ViewPortType.ACL, siteId, resourceId);
		mapped.setAclPolicy(aclId);
		cacheService.putInCache(siteId, mapped.getId(), mapped);
		return mapped;
	}

	public ViewPort getViewPort(int siteId, int resourceId) {
		return m_view_provider.getViewPort(siteId, resourceId);
	}

	public boolean isDpagaeType(int siteId, int resourceId) {
		ViewPort vp = getViewPort(siteId, resourceId);
		if (vp != null && Resource.TYPE_DPAGE.equals(vp.getType())) {
			return true;
		}
		return false;
	}

	public void removeMetaViewPort(int siteId, int vid) {
		int iParentID = m_view_provider.getViewPort(siteId, vid).getParent();
		flushMappedView(siteId);
		m_view_provider.updateSon(siteId, vid, iParentID);
	}

	public void removeViewPort(int siteId, int vid) {
		removeMetaViewPort(siteId, vid);
		m_view_provider.remove(siteId, vid);

	}

	public List<ViewPort> searchResourceByTitle(int siteId, String title) {
		return m_view_provider.searchResourceByTitle(siteId, title);
	}

	public void setCacheService(VWBCacheService cache) {
		cacheService = cache;
		cacheService.setModulePrefix("mvp");
	}

	public void setKeyGenerator(IKeyGenerator keygen) {
		this.keyGen = keygen;
	}

	public void setTreeViewProvider(TreeViewProvider provider) {
		m_tree_view = provider;
	}

	public void setViewPortProvider(ViewPortProvider provider) {
		m_view_provider = provider;
	}

	public void updateBaseInfo(int siteId, ViewPort vp) {
		if (vp == null) {
			throw new IllegalArgumentException(
					"ViewPort can't be null while update it.");
		}
		vp.setSiteId(siteId);
		m_view_provider.update(vp);
	}

	public void updateViewPort(int siteId, ViewPort vp)
			throws ViewPortNotExistException {
		if (vp == null)
			throw new IllegalArgumentException(
					"ViewPort can't be null while update it.");
		ViewPort old = m_view_provider.getViewPort(siteId, vp.getId());
		vp.setSiteId(siteId);
		if (old == null) {
			throw new ViewPortNotExistException(vp);
		}

		m_view_provider.update(vp);
		flushMappedView(siteId);
	}
}
