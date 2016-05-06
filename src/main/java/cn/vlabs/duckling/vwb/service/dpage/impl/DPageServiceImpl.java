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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.vlabs.duckling.vwb.service.dpage.CurrentPageResultSet;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageConstants;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.InvalidDPageDtoException;
import cn.vlabs.duckling.vwb.service.dpage.PageLock;
import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.dpage.data.SearchResult;
import cn.vlabs.duckling.vwb.service.dpage.provider.DPageProvider;
import cn.vlabs.duckling.vwb.service.event.DPageEvent;
import cn.vlabs.duckling.vwb.service.event.DPageEventType;
import cn.vlabs.duckling.vwb.service.event.VWBEvent;
import cn.vlabs.duckling.vwb.service.event.VWBEventService;
import cn.vlabs.duckling.vwb.service.viewport.ResourceConstants;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
import cn.vlabs.duckling.vwb.ui.action.page.PageUtil;
import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuItem;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class DPageServiceImpl implements DPageService {
	protected static final Logger log = Logger
			.getLogger(DPageServiceImpl.class);
	private int continuationEditMinutes;
	private DPageProvider dpageProvider;
	private PageLockService pageLockService;
	private ViewPortService viewportService;

	private boolean isContinuationEditTimeExpired(DPagePo old) {
		long deadline = continuationEditMinutes * 60 * 1000
				+ old.getTime().getTime();
		return System.currentTimeMillis() > deadline;
	}

	private boolean isValidDpage(DPage dpage) {
		boolean flag = true;
		if (dpage == null || StringUtils.isEmpty(dpage.getAuthor())
				|| dpage.getResourceId() <= 0) {
			flag = false;
		}
		return flag;
	}

	public void setPageLockService(PageLockService pageLockService) {
		this.pageLockService = pageLockService;
	}

	public DPage createDpage(DPage dpageDto) throws InvalidDPageDtoException {
		if (isValidDpage(dpageDto)) {
			DPagePo po = dpageProvider.getLatestByResourceId(
					dpageDto.getSiteId(), dpageDto.getResourceId());
			if (po != null) {
				throw new InvalidDPageDtoException(
						"DPageServiceInterface:createDpage(dpagedto is invalid)");
			}
			dpageDto.setTime(new Date());
			dpageDto.setVersion(DPageConstants.FIRST_VERSION_NUMBER);
			dpageProvider.create(DtoPoConvertor.convertDtoToPo(dpageDto));
			ViewPort vp = viewportService.getViewPort(dpageDto.getSiteId(),
					dpageDto.getResourceId());
			vp.setTitle(dpageDto.getTitle());
			try {
				viewportService.updateViewPort(dpageDto.getSiteId(), vp);
			} catch (ViewPortNotExistException e) {
				log.error("ViewPortNotExistException", e);
			}
		} else {
			throw new InvalidDPageDtoException(
					"DPageServiceInterface:createDpage(dpagedto property is invalid)");
		}
		return dpageDto;
	}

	public void deleteDpageByResourceId(int siteId, int resourceId) {
		// 检查：不能删除系统保留的页面
		DPagePo dpage = dpageProvider.getLatestByResourceId(siteId, resourceId);
		if (resourceId > 0 && dpage != null) {
			dpageProvider.deleteByResourceId(siteId, resourceId);
			VWBEvent event = new DPageEvent(
					DtoPoConvertor.convertPoToDto(dpage),
					DPageEventType.DPAGE_DELETE);
			VWBEventService.fireEvent(DPageService.class, event);
			// Don't remove data in resource_info table,
			// just clear any other settings.
			viewportService.removeMetaViewPort(siteId, resourceId);
		}
	}

	public List<LightDPage> getAllPage(int siteId) {
		return dpageProvider.getAllPages(siteId);
	}

	public List<DPage> getAllWeightPage(int siteId) {
		List<DPagePo> pages = dpageProvider.getAllWeightPages(siteId);
		List<DPage> pageDto = new ArrayList<DPage>();
		for (DPagePo po : pages) {
			pageDto.add(DtoPoConvertor.convertPoToDto(po));
		}
		return pageDto;
	}

	/**
	 * 得到当前页面锁 di
	 */
	public PageLock getCurrentLock(int siteId, int pageid) {
		return pageLockService.getCurrentLock(siteId, pageid);
	}

	public List<LightDPage> getDpagesSinceDate(int siteId, Date date) {
		return dpageProvider.getDpagesSinceDate(siteId, date);
	}

	public DPage getDpageVersionContent(int siteId, int resourceId, int version) {
		DPage dpage = null;
		if (DPageConstants.LATEST_VERSION_FLAG == version) {
			dpage = getLatestDpageByResourceId(siteId, resourceId);
		} else if (version <= 0) {
			// Hack for myspace
			// Version ever start from 1;
			return null;
		} else {
			DPagePo po = dpageProvider.getVersionContent(siteId, resourceId,
					version);
			if (po != null) {
				dpage = DtoPoConvertor.convertPoToDto(po);
			}
		}
		return dpage;
	}

	public List<DPage> getDpageVersionsByResourceId(int siteId, int resourceId) {
		List<DPagePo> pos = dpageProvider.getHistoryByResourceId(siteId,
				resourceId);
		List<DPage> dtos = new ArrayList<DPage>();
		for (DPagePo po : pos) {
			dtos.add(DtoPoConvertor.convertPoToDto(po));
		}
		return dtos;
	}

	public List<DPage> getDpageVersionsByResourceId(int siteId, int resourceId,
			int offset, int pageSize) {
		List<DPagePo> pos = dpageProvider.getHistoryByResourceId(siteId,
				resourceId, offset, pageSize);
		List<DPage> dtos = new ArrayList<DPage>();
		for (DPagePo po : pos) {
			dtos.add(DtoPoConvertor.convertPoToDto(po));
		}
		return dtos;
	}

	public List<DPage> getDpageVersionsByResourceIdA(int siteId, int resourceId) {
		List<DPagePo> pos = dpageProvider.getHistoryByResourceId(siteId,
				resourceId);
		Vector<DPage> dtos = new Vector<DPage>();
		for (DPagePo po : pos) {
			dtos.add(0, DtoPoConvertor.convertPoToDto(po));
		}
		return dtos;
	}

	public List<DPage> getDpageVersionsByResourceIdD(int siteId,
			int resourceId, int offset, int pageSize) {
		List<DPagePo> pos = dpageProvider.getHistoryByResourceId(siteId,
				resourceId, offset, pageSize);
		Vector<DPage> dtos = new Vector<DPage>();
		for (DPagePo po : pos) {
			dtos.add(0, DtoPoConvertor.convertPoToDto(po));
		}
		return dtos;
	}

	public DPage getLatestDpageByResourceId(int siteId, int resourceId) {
		DPagePo po = dpageProvider.getLatestByResourceId(siteId, resourceId);
		DPage dto = null;
		if (po != null) {
			dto = DtoPoConvertor.convertPoToDto(po);
		} else {
			ViewPort vp = this.viewportService.getViewPort(siteId, resourceId);
			if (vp != null) {
				dto = new DPage();
				dto.setAuthor(vp.getCreator());
				dto.setContent("");
				dto.setResourceId(resourceId);
				dto.setTime(vp.getCreateTime());
				dto.setTitle(vp.getTitle());
				dto.setSiteId(siteId);
			}
		}
		return dto;
	}

	public List<DPage> getLatestDpagesByResourceIds(int siteId,
			int[] resourceIds) {
		List<DPagePo> pos = dpageProvider.getLatestByResourceIds(siteId,
				resourceIds);
		List<DPage> dtos = new ArrayList<DPage>();
		for (DPagePo po : pos) {
			dtos.add(DtoPoConvertor.convertPoToDto(po));
		}
		return dtos;
	}

	public String getParentTitle(int siteId, int resourceId) {
		ViewPort vp = viewportService.getViewPort(siteId, resourceId);
		String title = "";
		if (vp.getParent() != 0) {
			DPagePo po = dpageProvider.getLatestByResourceId(siteId,
					vp.getParent());
			if (po != null) {
				title = po.getTitle();
			}
		}
		return title;
	}

	public boolean isDpageExist(int siteId, int resourceId) {
		return dpageProvider.isDpageExist(siteId, resourceId);
	}

	public List<DPageNodeInfo> listSubPage(int siteId, int resourceId) {
		if (ResourceConstants.ROOT == resourceId)
			resourceId = 0;
		return dpageProvider.getSubPages(siteId, resourceId);
	}

	public PageLock lockPage(int siteId, int pageid, String pagelocker,
			int pageVersion, String sessionId, String usrIp) {
		return pageLockService.lockPage(siteId, pageid, pagelocker,
				pageVersion, sessionId, usrIp);
	}

	public void moveDpageNode(int siteId, int resourceId, int parent) {
		ViewPort movedViewport = viewportService
				.getViewPort(siteId, resourceId);
		if (movedViewport == null) {
			return;
		}
		boolean isValidMove = PageUtil.isValidDPageTree(siteId, resourceId,
				parent, viewportService);
		if (isValidMove) {
			movedViewport.setParent(parent);
			try {
				viewportService.updateViewPort(siteId, movedViewport);
			} catch (ViewPortNotExistException e) {
				log.error("move resource error");
			}
		}
	}

	public List<LightDPage> searchDpageByTitle(int siteId, String title) {
		return dpageProvider.searchDpageByTitle(siteId, title);
	}

	/**
	 * 查找参数 begindate 查找从指定日期起更新的页面 begindate='2007-01-01 01:01:01' enddate
	 * 查找从指定日期起更新的页面 enddate='2010-01-01 01:01:01' prefix 页面名字的前缀 prefix=m
	 * suffix 页面名字的后缀 suffix=n user 页面修改人 user='fred@cnic.cn' count 查找结果个数
	 * count=20 operation 区分用户创建、编辑等动作 operation=CREATE or EDIT
	 */
	public List<DPage> searchPages(int siteId,
			Map<String, Object> searchedConditions) {
		List<DPage> dpages = new ArrayList<DPage>();
		List<DPagePo> pos = dpageProvider.searchPages(siteId,
				searchedConditions);
		for (DPagePo po : pos) {
			dpages.add(DtoPoConvertor.convertPoToDto(po));
		}
		return dpages;
	}

	/**
	 * 根据查询条件，查找资源的下级资源 searchedConditions key参数 begindate 查找从指定日期起的资源
	 * begindate='2007-01-01 01:01:01' enddate 查找到指定日期的资源 enddate='2010-01-01
	 * 01:01:01' prefix 资源名字的前缀 prefix=m suffix 资源名字的后缀 suffix=n user 资源创建人
	 * user='fred@cnic.cn' pageSize 查找结果每页个数 pageSize=20 currentPage 查看第几页
	 * currentPage=2
	 */
	public CurrentPageResultSet searchSubDpages(int siteId, int resourceId,
			Map<String, Object> searchedConditions) {
		List<DPage> dpages = new ArrayList<DPage>();
		SearchResult result = dpageProvider.searchSubDpages(siteId, resourceId,
				searchedConditions);
		for (DPagePo po : result.getDpages()) {
			dpages.add(DtoPoConvertor.convertPoToDto(po));
		}
		return new CurrentPageResultSet(dpages, result.getTotalCounts(),
				result.getCurrentPage(), result.getPageSize());
	}

	public void setContinuationEditMinutes(int minutes) {
		this.continuationEditMinutes = minutes;
	}

	public void setDpageProvider(DPageProvider provider) {
		this.dpageProvider = provider;
	}

	public void setViewPortService(ViewPortService viewportService) {
		this.viewportService = viewportService;
	}

	/**
	 * 关闭当前页面锁 di
	 */
	public void unlockPage(int siteId, int pageid) {
		pageLockService.unlockPage(siteId, pageid);
	}

	public void updateDpage(DPage dpageDto) throws InvalidDPageDtoException {
		if (isValidDpage(dpageDto)) {
			DPagePo old = dpageProvider.getLatestByResourceId(
					dpageDto.getSiteId(), dpageDto.getResourceId());
			if (old == null) {
				throw new InvalidDPageDtoException(
						"DPageServiceInterface:updateDpage(dpagedto is invalid)");
			} else {
				boolean isDifferentAuthor = !dpageDto.getAuthor().equals(
						old.getCreator());
				boolean isContinuationEditTimeExpired = isContinuationEditTimeExpired(old);
				boolean isChangeContent = !StringUtils.equals(old.getContent(),
						dpageDto.getContent());
				dpageDto.setTime(new Date(System.currentTimeMillis()));
				if (isChangeContent
						&& (isDifferentAuthor || isContinuationEditTimeExpired)) {
					dpageDto.setVersion(old.getVersion() + 1);
					dpageProvider.create(DtoPoConvertor
							.convertDtoToPo(dpageDto));
				} else {
					dpageDto.setVersion(old.getVersion());
					dpageProvider.update(DtoPoConvertor
							.convertDtoToPo(dpageDto));
				}
			}
			ViewPort vp = viewportService.getViewPort(dpageDto.getSiteId(),
					dpageDto.getResourceId());
			vp.setTitle(dpageDto.getTitle());
			try {
				viewportService.updateViewPort(dpageDto.getSiteId(), vp);
			} catch (ViewPortNotExistException e) {
				log.error("ViewPortNotExistException", e);
			}
		} else {
			throw new InvalidDPageDtoException(
					"DPageServiceInterface:updateDpage(dpagedto property is invalid)");
		}
	}
	
	
}
