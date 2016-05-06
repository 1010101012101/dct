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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.util.Utility;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dml.html2dml.HtmlStringToDMLTranslator;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.PageLock;
import cn.vlabs.duckling.vwb.service.dpage.TempPage;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;

/**
 * 编辑器和浏览的功能都在这里
 * 
 * @date 2010-3-2
 * @author diyanliang@cnic.cn
 */
public class EditPageAction extends BaseDispatchAction {

	/**
	 * 加载编辑器
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.EDIT, res);
		context.setWysiwygEditorMode(VWBContext.EDITOR_MODE);
		if (!context.hasAccess(response)) {
			return null;
		}
		DPage dpage = (DPage) res;
		VWBContainer container = VWBContext.getContainer();
		DPageService dpageSerivce = container.getDpageService();
		boolean isDpageExists = dpageSerivce.isDpageExist(context.getSiteId(),res.getResourceId());
		String innerHTML = "";
		if (isDpageExists) {
			int version = getRequestVersion(request);
			if (version != VWBContext.LATEST_VERSION) {
				String pageid = String.valueOf(dpage.getResourceId());
				innerHTML = context.getHTML(pageid, version);
			} else {
				innerHTML = context.getHTML(dpage);
			}
		}
		loadEditor(request, innerHTML);
		return layout(context, "EditContent.jsp");
	}

	public ActionForward saveexit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String htmlText = request.getParameter("fixDomStr");
		
		
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,DPageCommand.INFO, res);
		if(!HtmlValidateUtil.checkHtmlTextValidate(htmlText)){
			ResourceBundle rb=context.getBundle("templates/default");
			writeToResponse(response, rb.getString("action.savepage.containInvalidateForm") );
			return null;
			
		}

		// 判断页面版本是否冲突
		String lockVersion = request.getParameter("lockVersion");

		DPage lockeddpage = (DPage) res;
		Date mverion = lockeddpage.getTime();
		if (!lockVersion.equals(String.valueOf(mverion))) {// 有冲突
			if (!context.hasAccess(response)) {
				return null;
			}
			// 有冲突自动保存
			String textWithoutMetaData = "";
			if (htmlText != null) {
				textWithoutMetaData = new HtmlStringToDMLTranslator()
						.translate(htmlText, context);
			}
			DPage dpage = new DPage();
			dpage.setResourceId(res.getResourceId());
			UserPrincipal p = (UserPrincipal) context.getCurrentUser();
			dpage.setAuthor(p.getFullName() + "(" + p.getName() + ")");
			dpage.setTitle(request.getParameter("title"));
			dpage.setContent(textWithoutMetaData);
			VWBContext.getContainer().getSaveTempDpageService()
					.saveTempDpage(context.getSite().getId(),dpage, textWithoutMetaData);
			// 自动保存结束
			String editurl = context.getEditURL(res.getResourceId());
			request.setAttribute("editurl", editurl);
			request.setAttribute("pageTitle", request.getParameter("title"));
			request.setAttribute("myinner", (htmlText));
			boolean isDpageExists = VWBContext.getContainer().getDpageService().isDpageExist( context.getSite().getId(),
					res.getResourceId());
			String otherhtmlText = "";
			if (isDpageExists) {
				DPage oldpage = (DPage) res;
				otherhtmlText = context.getHTML(oldpage);
			}
			request.setAttribute("otherinner", (otherhtmlText));
			return layout(context, "ConflictContent.jsp");
		} else {
			// 权限开始
			if (!context.hasAccess(response)) {
				return null;
			}
			// 保存开始
			String textWithoutMetaData = "";
			if (htmlText != null) {
				context.setUseDData(true);
				textWithoutMetaData = new HtmlStringToDMLTranslator()
						.translate(htmlText, context);
			}
			int resourceid = res.getResourceId();
			DPage dpage = new DPage();
			dpage.setResourceId(res.getResourceId());
			UserPrincipal p = (UserPrincipal) context.getCurrentUser();
			dpage.setAuthor(p.getFullName() + "(" + p.getName() + ")");
			dpage.setTitle(request.getParameter("title"));
			dpage.setContent(textWithoutMetaData);
			dpage.setSiteId(context.getSite().getId());
			boolean isDpageExists = VWBContext.getContainer().getDpageService().isDpageExist(context.getSite().getId(),resourceid);
			if (isDpageExists){
				VWBContext.getContainer().getDpageService().updateDpage(dpage);
			}else{
				VWBContext.getContainer().getDpageService().createDpage(dpage);
			}
			// 清除临时保存
			VWBContext.getContainer().getSaveTempDpageService()
					.cleanTempPage(context.getSite().getId(),resourceid, p);
			// 页面解锁
			VWBContext.getContainer().getDpageService().unlockPage(context.getSite().getId(),resourceid);
			// 转发
			String url = context.getViewURL(resourceid);
			response.sendRedirect(url);
		}

		return null;
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.VIEW, res);
		int resourceid = res.getResourceId();

		// 清除临时保存
		UserPrincipal p = (UserPrincipal) context.getCurrentUser();
		VWBContext.getContainer().getSaveTempDpageService()
				.cleanTempPage(context.getSite().getId(),resourceid, p);
		// 页面解锁
		String usrIp = Utility.getIpAddr(request);
		PageLock pagelock = (PageLock) VWBContext.getContainer().getDpageService()
				.getCurrentLock(context.getSite().getId(),res.getResourceId());
		if (pagelock != null) {
			String usr = p.getFullName() + "(" + p.getName() + ")";
			if (usr.equals(pagelock.getPagelocker())
					&& usrIp.equals(pagelock.getUsrIp()))
				VWBContext.getContainer().getDpageService().unlockPage(context.getSite().getId(),resourceid);
		}

		String url = context.getViewURL(resourceid);
		response.sendRedirect(url);
		return null;
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Resource res = this.getSavedViewPort(request);
		String lockVersion = request.getParameter("lockVersion");
		request.setAttribute("lockVersion", lockVersion);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.INFO, res);
		String htmlText = request.getParameter("fixDomStr");
		String textWithoutMetaData = "";
		if (htmlText != null) {
			textWithoutMetaData = new HtmlStringToDMLTranslator().translate(
					htmlText, context);
			textWithoutMetaData = VWBContext.getContainer().getRenderingService().getHTML(context,
					textWithoutMetaData);
		}
		DPage dpage = new DPage();
		dpage.setResourceId(res.getResourceId());
		UserPrincipal p = (UserPrincipal) context.getCurrentUser();
		dpage.setAuthor(p.getFullName() + "(" + p.getName() + ")");
		dpage.setTitle(request.getParameter("title"));
		dpage.setContent(textWithoutMetaData);
		request.setAttribute("editDpage", dpage);
		htmlText = TextUtil.replaceEntities(htmlText);
		request.setAttribute("htmlText", htmlText);
		return layout(context, "PreviewContent.jsp");
	}

	public ActionForward pvtoed(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String innerHTML = request.getParameter("fixDomStr");
		loadEditor(request, innerHTML);
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.EDIT, res);
		if (!context.hasAccess(response)) {
			return null;
		}
		return layout(context, "EditContent.jsp");
	}

	public void loadEditor(HttpServletRequest request, String innerHTML) {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				DPageCommand.EDIT, res);
		boolean isDpageExists = VWBContext.getContainer().getDpageService().isDpageExist(context.getSiteId(),res.getResourceId());
		DPage dpage = (DPage) res;
		if (isDpageExists) {
			String originalData = dpage.getContent();
			String pageAsHtml = StringEscapeUtils.escapeJavaScript(innerHTML);
			dpage.setContent(pageAsHtml);
			// 调用临时保存页面显示
			List<String> listTempPage = tempPageFunc(context, dpage, originalData);
			if (listTempPage != null) {
				context.setWysiwygEditorMode(VWBContext.EDITOR_MODE);
				String TempHTML = VWBContext.getContainer().getRenderingService().getHTML(context,
						listTempPage.get(0));
				context.setWysiwygEditorMode("not");
				request.setAttribute("restoreType", 1);
				TempHTML = TextUtil.replaceEntities(TempHTML);
				request.setAttribute("strTempPage", TempHTML);
				request.setAttribute("TempPageData", listTempPage.get(1));
			}
		} else {
			String pageAsHtml = StringEscapeUtils.escapeJavaScript(innerHTML);
			dpage.setContent(pageAsHtml);
		}
		// 页面锁处理
		// 先看页面有没有锁
		String usrIp = Utility.getIpAddr(request);
		PageLock pagelock = (PageLock) VWBContext.getContainer().getDpageService().getCurrentLock(context.getSiteId(),
				res.getResourceId());
		if (pagelock == null) {// 没有锁上锁;
			UserPrincipal p = (UserPrincipal) context.getCurrentUser();
			String pagelocker = p.getFullName() + "(" + p.getName() + ")";
			String sesion = request.getSession().getId();
			pagelock = VWBContext.getContainer().getDpageService().lockPage(context.getSiteId(),res.getResourceId(),
					pagelocker, dpage.getVersion(), sesion, usrIp);
			request.setAttribute("lockType", "unlocked");
		} else {// 有锁
			UserPrincipal p = (UserPrincipal) context.getCurrentUser();
			String usr = p.getFullName() + "(" + p.getName() + ")";
			if (!usrIp.equals(pagelock.getUsrIp())
					|| !usr.equals(pagelock.getPagelocker())) {
				request.setAttribute("locker", pagelock.getPagelocker());
				request.setAttribute("lockSession", request.getSession()
						.getId());
				request.setAttribute("lockType", "locked");
			}
		}

		// 是否使用dData
		String useddata = context.getProperty("duckling.ddata");
		if ("true".equals(useddata))
			request.setAttribute("useddata", "true");
		else
			request.setAttribute("useddata", "false");
		request.setAttribute("lockVersion", dpage.getTime());
		request.setAttribute("editDpage", dpage);
		String strlocal = request.getLocale().toString();
		strlocal = strlocal.toLowerCase().replaceAll("_", "-");
		request.setAttribute("locale", strlocal);
	}

	/**
	 * 调用临时保存页面显示
	 * 
	 * @param
	 */
	private List<String> tempPageFunc(VWBContext context, DPage page,
			String originalData) {
		UserPrincipal p = (UserPrincipal) context.getCurrentUser();
		int pageid = page.getResourceId();
		TempPage tempPage = VWBContext.getContainer().getSaveTempDpageService().getTempPage(context.getSite().getId(), pageid, p);
		// 如果为真提示用户是否恢复自动备份内容
		if (tempPage!=null && !originalData.equals(tempPage.getContent())) {
			List<String> list = new ArrayList<String>();
			list.add(0, tempPage.getContent());
			list.add(1, tempPage.getFormattedTime());
			return list;
		}
		return null;
	}

	private int getRequestVersion(HttpServletRequest request) {
		String version = request.getParameter("version");
		if (version != null) {
			try {
				return Integer.parseInt(version);
			} catch (NumberFormatException e) {
				log.warn(e.getMessage());
			}
		}
		return VWBContext.LATEST_VERSION;
	}
	
	 private void writeToResponse(HttpServletResponse response, String xml)
	    {
	        response.setContentType("text/html;charset=UTF-8");
	        try
	        {
	            //String output = "<result>" + xml + "</result>";
	            Writer wr = response.getWriter();
	            wr.write(xml);
	            wr.close();
	        }
	        catch (IOException e)
	        {
	            log.debug("Write xml to response error!", e);
	        }
	    }    
}
