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

package cn.vlabs.duckling.vwb.ui.action.page;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.InvalidDPageDtoException;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ResourceConstants;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortNotExistException;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;

/**
 * Introduction Here.
 * 
 * @date 2010-3-4
 * @author euniverse
 */
public class PageSettingAction extends BaseDispatchAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String parameter = mapping.getParameter();
		if (parameter != null && request.getParameter(parameter) == null) {
			return this.show(mapping, form, request, response);
		}

		return super.execute(mapping, form, request, response);
	}

	public ActionForward show(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = null;
		if (Resource.TYPE_DPAGE.equals(res.getType())) {
			context = VWBContext.createContext(p_request,
					DPageCommand.EDIT_INFO, res);
			p_request.setAttribute("saveUrlForSetting", res.getResourceId()
					+ "?method=save&a=setting");
		} else if (Resource.TYPE_PORTAL.equals(res.getType())) {
			context = VWBContext.createContext(p_request, PortalCommand.CONFIG,
					res);
			p_request.setAttribute("saveUrlForSetting", "?method=save");
			p_request.setAttribute("portalSetting",
					"var portalSetting = 'true'");
		}
		DPageService dpageService = VWBContext.getContainer().getDpageService();
		if (context.hasAccess(p_response)) {
			int siteId =context.getSiteId();
			DPage dpage = dpageService.getLatestDpageByResourceId(
					siteId, res.getResourceId());
			p_request.setAttribute("currentSettingPage", dpage);
			ViewPort dpagevp = VWBContext.getContainer().getViewPortService().getViewPort(siteId,res.getResourceId());
			String dpageParentTitle = "";
			if (dpagevp.getParent() > 0) {
				DPage parentDPage = dpageService.getLatestDpageByResourceId(siteId,
								dpagevp.getParent());
				dpageParentTitle = parentDPage.getTitle() + "("
						+ parentDPage.getResourceId() + ")";
			}
			if (dpage != null) {
				p_request.setAttribute("parentResourceId", dpagevp.getParent());
				p_request.setAttribute("parentPageTitle", dpageParentTitle);
				p_request.setAttribute("voGroup", context.getVOGroup());
				initialLeftMenu(siteId,p_request, dpagevp);
				initialNavPage(siteId,p_request, dpagevp);
				intialPrivs(p_request, dpagevp);
				intialBanner(siteId,p_request, dpagevp, context.getCurrentUser()
						.getName());
				intialFooter(context.getSiteId(),p_request, dpagevp);
				intialTrail(p_request, dpagevp);
			} else {
				p_request.setAttribute("errorInfo",
						"The page " + res.getTitle()
								+ " you want to set is not existent!");
			}
			if (dpagevp.getId() == ResourceConstants.ROOT) {
				p_request.setAttribute("root_page", "disabled=\"disabled\"");
			}

			return this.layout(context, "/jsp/page/pagesetting.jsp");
		}
		return null;
	}

	/**
	 * @param p_request
	 * @param dpagevp
	 */
	private void intialTrail(HttpServletRequest p_request, ViewPort viewport) {
		if (viewport.getTrail() == 0) {
			p_request.setAttribute("inheritTrail", "yes");
		} else if (viewport.getTrail() == 1) {
			p_request.setAttribute("inheritTrail", "settting");
		} else if (viewport.getTrail() == -1) {
			p_request.setAttribute("inheritTrail", "notrail");
		}
	}

	private void intialFooter(int siteId,HttpServletRequest p_request, ViewPort viewport) {
		if (viewport.getFooter() == 0) {
			p_request.setAttribute("inheritFooter", "yes");
		} else if (viewport.getFooter() == -1) {
			p_request.setAttribute("inheritFooter", "noFooter");
		} else {
			p_request.setAttribute("inheritFooter", "select");
			p_request.setAttribute("selectFooterId", viewport.getFooter());
			DPage footer = VWBContainerImpl.findContainer().getDpageService().getLatestDpageByResourceId(
					siteId, viewport.getFooter());
			p_request.setAttribute("selectFooterTitle", footer.getTitle() + "("
					+ footer.getResourceId() + ")");
		}

	}

	private void intialBanner(int siteId,HttpServletRequest p_request, ViewPort viewport,
			String user) {
		List<Banner> banners = VWBContainerImpl.findContainer().getBannerService()
				.getAvailableBanners(user,siteId);
		Banner banner = null;
		if (viewport.getBanner() == 0) {
			p_request.setAttribute("inheritBanner", "yes");
		} else if (viewport.getBanner() == -1) {
			p_request.setAttribute("inheritBanner", "noBanner");
		} else if (viewport.getBanner() == -2) {
			p_request.setAttribute("inheritBanner", "skinBanner");
		} else {
			banner = VWBContainerImpl.findContainer().getBannerService()
					.getBannerById(siteId,viewport.getBanner());
			if (banner == null) {
				p_request.setAttribute("inheritBanner", "yes");
			} else {
				p_request.setAttribute("inheritBanner", "select");
				p_request.setAttribute("selectBanner", viewport.getBanner());
			}
		}
		if (banners.size() == 0 && banner == null) {
			p_request.setAttribute("noBannersFlag", "true");
		} else {
			p_request.setAttribute("noBannersFlag", "false");
		}
		boolean selectisInMyBanners = false;
		for (Banner b : banners) {

			if (banner != null && banner.getId() == b.getId()) {
				selectisInMyBanners = true;
				b.setSelected(true);
			}
		}
		if (!selectisInMyBanners && banner != null) {
			banner.setSelected(true);
			banners.add(banner);
		}
		p_request.setAttribute("bannerList", banners);
	}

	public ActionForward save(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {

		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = null;
		if (Resource.TYPE_DPAGE.equals(res.getType())) {
			context = VWBContext.createContext(p_request,
					DPageCommand.EDIT_INFO, res);
		} else if (Resource.TYPE_PORTAL.equals(res.getType())) {
			context = VWBContext.createContext(p_request, PortalCommand.CONFIG,
					res);
		}
		int siteId = context.getSiteId();
		boolean exeflag = true;
		if (context.hasAccess(p_response)) {
			DPage dpage = VWBContext
					.getContainer()
					.getDpageService()
					.getLatestDpageByResourceId(siteId,
							res.getResourceId());
			if (dpage != null) {
				ViewPort dpagevp = VWBContext.getContainer().getViewPortService().getViewPort(siteId,
						res.getResourceId());
				if (setParentPage(siteId,p_request, dpagevp)
						&& this.setFooterPage(siteId,p_request, dpagevp)
						&& this.setLeftPage(siteId,p_request, dpagevp)
						&& this.setNavPage(siteId,p_request, dpagevp)
						&& this.setBanner(siteId,p_request, dpagevp)
						&& this.setPrv(siteId,p_request, dpagevp)
						&& this.setTrail(p_request, dpagevp)) {
					try {
						VWBContext.getContainer().getViewPortService()
								.updateViewPort(siteId,dpagevp);
					} catch (ViewPortNotExistException e) {
						exeflag = false;
					}
				} else {
					exeflag = false;
				}
			}
		}
		if (exeflag) {
			p_response.sendRedirect(context.getURL(VWBContext.VIEW,
					Integer.toString(res.getResourceId())));
			return null;
		} else {
			return this.show(p_mapping, p_form, p_request, p_response);
		}
	}

	/**
	 * @param p_request
	 * @param dpagevp
	 * @return
	 */
	private boolean setTrail(HttpServletRequest p_request, ViewPort dpagevp) {
		String inheritTrail = p_request.getParameter("inheritTrail");
		if ("yes".equals(inheritTrail)) {
			dpagevp.setTrail(0);
		} else if ("settting".equals(inheritTrail)) {
			dpagevp.setTrail(1);
		} else if ("notrail".equals(inheritTrail)) {
			dpagevp.setTrail(-1);
		}
		return true;
	}

	private boolean setPrv(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		try {
			String inheritPrv = p_request.getParameter("inheritPrv");
			if ("yes".equals(inheritPrv)) {
				if (dpagevp.getId() != ResourceConstants.ROOT) {
					VWBContainerImpl.findContainer().getAuthorizationService().updateViewPortAcl(siteId,
							dpagevp.getId(), null);
					dpagevp.setAclPolicy(ViewPort.INHERIT);
				}
			} else if ("no".equals(inheritPrv)) {
				@SuppressWarnings("unchecked")
				List<PagePrivVo> privs = (List<PagePrivVo>) p_request
						.getSession().getAttribute("pagePrivileges");
				if ("true".equals(p_request.getSession().getAttribute(
						"updatePrivileges"))) {
					VWBContainerImpl.findContainer().getAuthorizationService().updateViewPortAcl(siteId,
							dpagevp.getId(), toACL(dpagevp.getId(), privs));
					dpagevp.setAclPolicy(ViewPort.ENABLED);
				}
			}
		} catch (Throwable e) {

		} finally {
			p_request.getSession().removeAttribute("updatePrivileges");
			p_request.getSession().removeAttribute("pagePrivileges");
		}

		return true;
	}

	private Acl toACL(int resourceid, List<PagePrivVo> privs) {

		Acl acl = null;
		if (privs != null && privs.size() > 0) {
			acl = new Acl();
			for (PagePrivVo vo : privs) {
				AclEntry entry = new AclEntry();
				if ("user".equals(vo.getType())) {
					UserPrincipal up = new UserPrincipal(vo.getPureName(),
							vo.getPureName(), vo.getType());
					entry.setPrincipal(up);
				} else if ("group".equals(vo.getType())) {
					GroupPrincipal gp = new GroupPrincipal(vo.getName());
					entry.setPrincipal(gp);
				} else if ("role".equals(vo.getType())) {
					if (vo.getName().indexOf("(All)") > 0) {
						vo.setName("All");
					}
					Role rp = new Role(vo.getName());
					entry.setPrincipal(rp);
				}
				StringBuffer actions = new StringBuffer();
				if ("true".equals(vo.getView())) {
					actions.append("view");
				}
				if ("true".equals(vo.getEdit())) {
					if (actions.length() > 0)
						actions.append(",");
					actions.append("edit");
				}
				entry.addPermission(PermissionFactory.createPermission(
						resourceid, Resource.TYPE_DPAGE, actions.toString()));
				acl.addEntry(entry);
			}
		}
		return acl;
	}

	private boolean setBanner(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		boolean flag = false;
		String inheritBanner = p_request.getParameter("inheritBanner");
		if ("yes".equals(inheritBanner)) {
			if (dpagevp.getId() != ResourceConstants.ROOT)
				dpagevp.setBanner(0);
		} else if ("select".equals(inheritBanner)) {
			String selectBanner = p_request.getParameter("selectBanner");
			dpagevp.setBanner(Integer.parseInt(selectBanner));
			if (dpagevp.getId() == ResourceConstants.ROOT) {
				VWBContainerImpl.findContainer().getBannerService().setDefaultBanner(siteId,Integer.parseInt(selectBanner));
			}
		} else if ("noBanner".equals(inheritBanner)) {
			dpagevp.setBanner(-1);
		} else if ("skinBanner".equals(inheritBanner)) {
			dpagevp.setBanner(-2);
		}
		flag = true;
		return flag;
	}

	private boolean setLeftPage(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		boolean flag = true;
		String inheritLeft = p_request.getParameter("inheritLeft");
		if ("select".equals(inheritLeft)) {
			String selectLeftPageTitle = p_request
					.getParameter("selectLeftPageTitle");
			if (selectLeftPageTitle != null
					&& !selectLeftPageTitle.trim().equals("")) {
				int navId = 0;
				try {
					navId = this.extractid(selectLeftPageTitle);
					if ((VWBContainerImpl.findContainer().getDpageService().getLatestDpageByResourceId(
							siteId, navId) != null)) {
						dpagevp.setLeftMenu(navId);
					} else {
						p_request.setAttribute("selectLeftPageError",
								"select.page.noexistence");
						flag = false;
					}
				} catch (Exception e) {
					p_request.setAttribute("selectLeftPageError",
							"select.page.noexistence");
					flag = false;
				}
			} else {
				p_request.setAttribute("selectLeftPageError",
						"select.page.noexistence");
				flag = false;
			}

		} else if ("yes".equals(inheritLeft)) {
			if (dpagevp.getId() != ResourceConstants.ROOT)
				dpagevp.setLeftMenu(0);
		} else if ("noLeft".equals(inheritLeft)) {
			dpagevp.setLeftMenu(-1);
		} else if ("new".equals(inheritLeft)) {
			String newLeftPageTitle = p_request
					.getParameter("newLeftPageTitle");
			if (newLeftPageTitle != null && !newLeftPageTitle.trim().equals("")) {
				int newLeftId = createViewPort(siteId,p_request, dpagevp.getId(),
						newLeftPageTitle, Resource.TYPE_DPAGE);
				this.createDpage(p_request, newLeftPageTitle, newLeftId);
				dpagevp.setLeftMenu(newLeftId);
			}
		}
		return flag;
	}

	private boolean setFooterPage(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		boolean flag = true;
		String inheritFooter = p_request.getParameter("inheritFooter");
		if ("select".equals(inheritFooter)) {
			String selectFooterTitle = p_request
					.getParameter("selectFooterTitle");
			if (selectFooterTitle != null
					&& !selectFooterTitle.trim().equals("")) {
				int footerId = 0;
				try {
					footerId = this.extractid(selectFooterTitle);
					if (VWBContainerImpl.findContainer().getDpageService().getLatestDpageByResourceId(
							siteId, footerId) != null) {
						dpagevp.setFooter(footerId);
					} else {
						p_request.setAttribute("selectFooterPageError",
								"select.page.noexistence");
						flag = false;
					}
				} catch (Exception e) {
					p_request.setAttribute("selectFooterPageError",
							"select.page.noexistence");
					flag = false;
				}
			} else {
				p_request.setAttribute("selectFooterPageError",
						"select.page.noexistence");
				flag = false;
			}

		} else if ("noFooter".equals(inheritFooter)) {
			dpagevp.setFooter(-1);
		} else {
			if (dpagevp.getId() != ResourceConstants.ROOT)
				dpagevp.setFooter(0);
		}
		return flag;
	}

	private boolean setNavPage(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		boolean flag = true;
		String inheritNav = p_request.getParameter("inheritNav");
		if ("select".equals(inheritNav)) {
			String selectNavPageTitle = p_request
					.getParameter("selectNavPageTitle");
			if (selectNavPageTitle != null
					&& !selectNavPageTitle.trim().equals("")) {
				int navId = 0;
				try {
					navId = this.extractid(selectNavPageTitle);
					if (VWBContainerImpl.findContainer().getDpageService().getLatestDpageByResourceId(
							siteId, navId) != null) {
						dpagevp.setTopMenu(navId);
					} else {
						p_request.setAttribute("selectNavPageError",
								"select.page.noexistence");
						flag = false;
					}
				} catch (Exception e) {
					p_request.setAttribute("selectNavPageError",
							"select.page.noexistence");
					flag = false;
				}
			} else {
				p_request.setAttribute("selectNavPageError",
						"select.page.noexistence");
				flag = false;
			}

		} else if ("noTop".equals(inheritNav)) {
			dpagevp.setTopMenu(-1);
		} else if ("new".equals(inheritNav)) {
			String newNavPageTitle = p_request.getParameter("newNavPageTitle");
			if (newNavPageTitle != null && !newNavPageTitle.trim().equals("")) {
				int newNavId = createViewPort(siteId,p_request, dpagevp.getId(),
						newNavPageTitle, Resource.TYPE_DPAGE);
				this.createDpage(p_request, newNavPageTitle, newNavId);
				dpagevp.setTopMenu(newNavId);
			}
		} else {
			if (dpagevp.getId() != ResourceConstants.ROOT)
				dpagevp.setTopMenu(0);
		}
		return flag;
	}

	private int createViewPort(int siteId,HttpServletRequest p_request, int mainResource,
			String title, String resouceType) {
		ViewPort vp = new ViewPort();
		vp.setCreator(VWBSession.findSession(p_request).getCurrentUser()
				.getName());
		vp.setCreateTime(new Date());
		vp.setParent(mainResource);
		vp.setTitle(title);
		vp.setType(resouceType);// Resource.TYPE_DPAGE
		return VWBContainerImpl.findContainer().getViewPortService().createViewPort(siteId,vp);
	}

	private DPage createDpage(HttpServletRequest p_request, String title,
			int resourceId) {
		DPage dpage = new DPage();
		dpage.setAuthor(VWBSession.findSession(p_request).getCurrentUser()
				.getName());
		dpage.setContent("");
		dpage.setTime(new Date());
		dpage.setResourceId(resourceId);
		dpage.setTitle(title);
		dpage.setVersion(1);
		try {
			dpage = VWBContainerImpl.findContainer().getDpageService()
					.createDpage(dpage);
		} catch (InvalidDPageDtoException e) {
		}
		return dpage;
	}

	private boolean setParentPage(int siteId,HttpServletRequest p_request, ViewPort dpagevp) {
		boolean flag = true;
		try {
			if (dpagevp.getId() != ResourceConstants.ROOT) {
				String parentTitle = p_request.getParameter("parentPageTitle");
				if (parentTitle != null && !parentTitle.trim().equals("")) {
					int parentId = this.extractid(parentTitle);
					if (parentId == dpagevp.getId()) {
						p_request.setAttribute("parentError",
								"parent.page.same");
						flag = false;
					} else if (!PageUtil.isValidDPageTree(siteId,dpagevp.getId(),
							parentId, VWBContainerImpl.findContainer().getViewPortService())) {
						p_request.setAttribute("parentError",
								"parent.page.recursive");
						flag = false;
					} else if (VWBContainerImpl.findContainer().getDpageService()
							.getLatestDpageByResourceId(siteId, parentId) != null) {
						dpagevp.setParent(parentId);
					} else {
						p_request.setAttribute("parentError",
								"select.page.noexistence");
						flag = false;
					}
				} else {
					dpagevp.setParent(0);
				}
			}
		} catch (Exception e) {
			p_request.setAttribute("parentError", "select.page.noexistence");
			flag = false;
		}
		return flag;
	}

	private void intialPrivs(HttpServletRequest p_request, ViewPort viewport) {
		String allow = "true";
		p_request.setAttribute("allow", allow);
		p_request.getSession().removeAttribute("pagePrivileges");
		p_request.getSession().removeAttribute("updatePrivileges");
		if (viewport.isAclInherit()) {
			p_request.setAttribute("inheritPrv", "yes");
		} else {
			p_request.setAttribute("inheritPrv", "no");
		}
	}

	private void initialNavPage(int siteId,HttpServletRequest p_request, ViewPort viewport) {
		if (viewport.getTopMenu() == 0) {
			p_request.setAttribute("inheritNav", "yes");
		} else if (viewport.getTopMenu() == -1) {
			p_request.setAttribute("inheritNav", "noTop");
		} else {
			DPageService dpageService = VWBContainerImpl.findContainer().getDpageService();
			if (dpageService.isDpageExist(siteId, viewport.getTopMenu())) {
				p_request.setAttribute("inheritNav", "select");
				DPage navpage = dpageService.getLatestDpageByResourceId(
						siteId, viewport.getTopMenu());
				p_request.setAttribute("selectNavPageTitle", navpage.getTitle()
						+ "(" + navpage.getResourceId() + ")");
			} else {
				p_request.setAttribute("inheritNav", "new");
				ViewPort dpagevp = VWBContainerImpl.findContainer().getViewPortService().getViewPort(siteId,viewport.getTopMenu());
				p_request.setAttribute("newNavPageTitle", dpagevp.getTitle());
				p_request.setAttribute("newNavResourceid", dpagevp.getId());
			}
		}
	}

	private Pattern idFilterPattern = Pattern.compile("^.+\\((.*)\\)$");

	private int extractid(String titile) throws Exception {

		int id = 0;
		try {
			Matcher matcher = idFilterPattern.matcher(titile);
			if (matcher.matches()) {
				id = Integer.parseInt(matcher.group(1));
			}
		} catch (Exception e) {
			log.error("error extract id from title " + titile);
			throw e;
		}
		return id;
	}

	private void initialLeftMenu(int siteId,HttpServletRequest p_request, ViewPort viewport) {
		if (viewport.getLeftMenu() < 0) {
			p_request.setAttribute("inheritLeft", "noLeft");
		} else if (viewport.getLeftMenu() == 0) {
			p_request.setAttribute("inheritLeft", "yes");
		} else {
			DPageService dpageService = VWBContainerImpl.findContainer().getDpageService();
			if (dpageService.isDpageExist(siteId, viewport.getLeftMenu())) {
				p_request.setAttribute("inheritLeft", "select");
				DPage leftPage = dpageService.getLatestDpageByResourceId(
						siteId, viewport.getLeftMenu());
				p_request.setAttribute("selectLeftPageTitle",
						leftPage.getTitle() + "(" + leftPage.getResourceId()
								+ ")");
			} else {
				p_request.setAttribute("inheritLeft", "new");
				ViewPort dpagevp = VWBContainerImpl.findContainer().getViewPortService().getViewPort(siteId,viewport.getLeftMenu());
				p_request.setAttribute("newLeftPageTitle", dpagevp.getTitle());
				p_request.setAttribute("leftMenuResourceid", dpagevp.getId());

			}
		}
	}
}
