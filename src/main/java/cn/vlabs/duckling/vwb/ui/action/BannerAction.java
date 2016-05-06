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

package cn.vlabs.duckling.vwb.ui.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.util.StringUtil;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.BannerService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date 2010-3-2
 * @author Fred Zhang (fred@cnic.cn)
 */
public class BannerAction extends BaseDispatchAction {
	private static Logger logger = Logger.getLogger(BannerAction.class);
	private static String REPLACE_LEFT_BANNER = "REPLACE_LEFT_BANNER";
	private static String REPLCAE_MIDDLE_BANNER = "REPLCAE_MIDDLE_BANNER";
	private static String REPLACE_RIGHT_BANNER = "REPLACE_RIGHT_BANNER";

	public ActionForward deleteBanner(ActionMapping p_mapping,
			ActionForm p_form, HttpServletRequest p_request,
			HttpServletResponse p_response) throws IOException {
		final DynaActionForm form = (DynaActionForm) p_form;
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		int siteId=context.getSiteId();
		if (context.hasAccess(p_response)) {
			String bannerId = form.getString("bannerId");
			Banner delBanner = VWBContext.getContainer().getBannerService()
					.getBannerById(siteId,Integer.parseInt(bannerId));
			if (delBanner != null
					&& (delBanner.getCreator().equals(
							context.getCurrentUser().getName()) || delBanner
							.getOwnedtype().equals("public"))) {
				VWBContext.getContainer().getBannerService().deleteBanner(siteId,Integer.parseInt(bannerId));
			}
			return this.showBanners(p_mapping, p_form, p_request, p_response);
		}

		return null;
	}

	public static String getExtentionFromFileName(String fileName) {
		return fileName != null && fileName.lastIndexOf(".") >= 0 ? fileName
				.substring(fileName.lastIndexOf("."), fileName.length()) : "";
	}

	public ActionForward openUpload(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		final DynaActionForm form = (DynaActionForm) p_form;
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			String bannerId = form.getString("bannerId");
			Banner banner = VWBContext.getContainer().getBannerService()
					.getBannerById(context.getSiteId(),Integer.parseInt(bannerId));
			p_request.setAttribute("updateBanner", banner);
			if (banner != null && banner.getBannerTitle() > 0) {
				String bannerTitleContent = context.getHTML(String.valueOf(banner.getBannerTitle()));
				p_request
						.setAttribute("bannerTitleContent", bannerTitleContent);
			}
			return this.layout(context, "/jsp/banner/updateBanner.jsp");
		}
		return null;
	}

	public ActionForward updateBannerPicture(ActionMapping p_mapping,
			ActionForm p_form, HttpServletRequest p_request,
			HttpServletResponse p_response) throws IOException {
		final DynaActionForm form = (DynaActionForm) p_form;
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			String bannerId = form.getString("bannerId");
			Banner banner = VWBContext.getContainer().getBannerService()
					.getBannerById(context.getSiteId(),Integer.parseInt(bannerId));
			saveBannerPictures(form,context ,banner);
			copyCssByType(banner,p_request);
			VWBContext.getContainer().getBannerService().updateBanner(context.getSiteId(),banner);
			ActionForward forward = new ActionForward();
			forward.setPath("5008?method=openUpload&bannerId=" + bannerId);
			forward.setRedirect(true);
			return forward;
		}
		return null;

	}

	public ActionForward preview(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			String publicBanner = p_request.getParameter("publicBanner");
			p_request.setAttribute("publicBanner", publicBanner);
			return this.layout(context, "/jsp/banner/newBanner.jsp");
		}
		return null;
	}

	public ActionForward saveBanner(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		final DynaActionForm form = (DynaActionForm) p_form;
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			String bannerName = form.getString("bannerName");
			ResourceBundle rb = context.getBundle("templates/default");
			if (bannerName == null || bannerName.trim().equals("")) {
				String publicBanner = p_request.getParameter("publicBanner");
				p_request.setAttribute("publicBanner", publicBanner);
				p_request.setAttribute("errorInfo",
						rb.getString("banner.name.notnull"));
				return this.layout(context, "/jsp/banner/newBanner.jsp");
			}
			Banner banner = (Banner) p_request.getSession().getAttribute(
					"newbanner");
			if (banner == null) {
				banner = new Banner();
			}
			saveBannerPictures(form, context, banner);
			
			banner.setFirstTitle(form.getString("firstTitle"));
			banner.setSecondTitle(form.getString("secondTitle"));
			banner.setThirdTitle(form.getString("thirdTitle"));

			banner.setName(bannerName);
			copyCssByType(banner,p_request);
			banner.setStatus(1);
			Boolean publicView = (Boolean) form.get("publicBanner");
			String ownedType = publicView != null && publicView.booleanValue() ? "public"
					: "private";
			banner.setOwnedtype(ownedType);
			banner.setCreator(context.getCurrentUser().getName());
			VWBContext.getContainer().getBannerService().createBanner(context.getSiteId(),banner);
			p_request.getSession().removeAttribute("newbanner");
			return this.showBanners(p_mapping, p_form, p_request, p_response);
		}
		return null;
	}

	private void saveBannerPictures(final DynaActionForm form,
			VWBContext context, Banner banner) throws FileNotFoundException,
			IOException {
		FormFile leftFile = (FormFile) form.get("leftFile");
		FormFile rightFile = (FormFile) form.get("rightFile");
		FormFile middleFile = (FormFile) form.get("middleFile");
		BannerService bannerService=VWBContext.getContainer().getBannerService();
		if (leftFile != null && leftFile.getFileSize() > 0) {
			String fileName = StringUtil.getRandomString(8) + "_Left"+ getExtentionFromFileName(leftFile.getFileName());
			int leftClbId=bannerService.savePicture(fileName, leftFile.getFileSize(), leftFile.getInputStream());
			banner.setLeftPictureClbId(leftClbId);
		}
		if (rightFile != null && rightFile.getFileSize() > 0) {
			String fileName = StringUtil.getRandomString(8) + "_Right"
					+ getExtentionFromFileName(rightFile.getFileName());
			int rightClbId=bannerService.savePicture(fileName, rightFile.getFileSize(), rightFile.getInputStream());
			banner.setRightPictureClbId(rightClbId);
		}
		if (middleFile != null && middleFile.getFileSize() > 0) {
			String fileName = StringUtil.getRandomString(8) + "_Middle"
					+ getExtentionFromFileName(middleFile.getFileName());
			int middleClbId=bannerService.savePicture(fileName, middleFile.getFileSize(), middleFile.getInputStream());
			banner.setMiddlePictureClbId(middleClbId);
		}
		bannerService.fillBannerFileUrl(banner);
	}

	public ActionForward saveBannerTitle(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		String titleName = p_request.getParameter("titleName");
		String inputValue = p_request.getParameter("update_value");
		inputValue = inputValue==null?"":inputValue.trim();
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		String bannerId = p_request.getParameter("bannerId");
		if (bannerId!=null && context.hasAccess(p_response)) {
			Banner banner = VWBContext.getContainer().getBannerService()
					.getBannerById(context.getSiteId(),Integer.parseInt(bannerId));
			if (banner == null) {
				PrintWriter writer = p_response.getWriter();
				writer.print(inputValue+" ");
				return null;
			}
			
		
			if(titleName!=null && titleName.trim().length()>0  ){
				if(titleName.equals("firstTitle"))
					banner.setFirstTitle(inputValue);
				else if(titleName.equals("secondTitle"))
					banner.setSecondTitle(inputValue);
				else if(titleName.equals("thirdTitle"))
					banner.setThirdTitle(inputValue);
				VWBContext.getContainer().getBannerService().updateBanner(context.getSiteId(),banner);
			}
			
		}
		PrintWriter writer = p_response.getWriter();
		writer.print(inputValue+" ");
		return null;
	}

	public static void copyCssByType(Banner banner,HttpServletRequest request) {
		String webRoot = request.getSession().getServletContext()
				.getRealPath("/");
		String cssPath = webRoot + "WEB-INF" + File.separator + "banner"
				+ File.separator + "type" + banner.getType() + File.separator
				+ "banner.css";
		try {
			FileInputStream input = new FileInputStream(cssPath);
			String content = FileUtil.readContents(input, "UTF-8");
			if (banner.getLeftPictureUrl()!= null) {
				content = content.replace(REPLACE_LEFT_BANNER,
						banner.getLeftPictureUrl());
			}
			if (banner.getMiddlePictureUrl() != null) {
				content = content.replace(REPLCAE_MIDDLE_BANNER,
						banner.getMiddlePictureUrl());
			}
			if (banner.getRightPictureUrl() != null) {
				content = content.replace(REPLACE_RIGHT_BANNER,
						banner.getRightPictureUrl());
			}
			byte[]  bytes=content.getBytes();
			ByteArrayInputStream in=new ByteArrayInputStream(content.getBytes());
			int cssClb=VWBContainerImpl.findContainer().getBannerService().savePicture("banner.css", bytes.length, in);
			banner.setCssClbId(cssClb);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void saveBannerFile(String fileName, InputStream in) {
		try {
			FileOutputStream out = new FileOutputStream(new File(fileName));
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.flush();
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("");
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public ActionForward viewBanner(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		final DynaActionForm form = (DynaActionForm) p_form;
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			String bannerId = form.getString("bannerId");
			Banner banner = VWBContext.getContainer().getBannerService()
					.getBannerById(context.getSiteId(),Integer.parseInt(bannerId));
			p_request.setAttribute("viewBanner", banner);
			if (banner != null && banner.getBannerTitle() > 0) {
				String bannerTitleContent = context.getHTML(String.valueOf(banner.getBannerTitle()));
				p_request.setAttribute("bannerTitleContent", bannerTitleContent);
			}
			if (banner == null) {
				return this.showBanners(p_mapping, p_form, p_request,
						p_response);
			}
			return this.layout(context, "/jsp/banner/viewBanner.jsp");
		}
		return null;
	}

	public ActionForward newBanner(ActionMapping p_mapping, ActionForm p_form,
			HttpServletRequest p_request, HttpServletResponse p_response)
			throws IOException {
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			return layout(context, "/jsp/banner/newBanner.jsp");
		}
		return null;
	}

	public ActionForward showBanners(ActionMapping p_mapping,
			ActionForm p_form, HttpServletRequest p_request,
			HttpServletResponse p_response) throws IOException {
		// 获取请求参数
		Resource res = this.getSavedViewPort(p_request);
		VWBContext context = VWBContext.createContext(p_request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(p_response)) {
			List<Banner> banners = null;
			String user = context.getCurrentUser().getName();
			banners = VWBContext.getContainer().getBannerService()
					.getAvailableBanners(user,context.getSite().getId());
			p_request.setAttribute("showbanners", banners);
			return this.layout(context, "/jsp/banner/showBanners.jsp");
		}
		return null;
	}
}
