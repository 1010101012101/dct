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
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.XMLMessages;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date Mar 5, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class PageManageAction extends BaseAction {
	
	public static final String  ROOT_PAGE = "T_dct_root_page";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);
		if (context.hasAccess(response)) {

			DPageService dps = VWBContext.getContainer().getDpageService();

			String command = request.getParameter("action");
			String result = "";
			if (command == null) {
				log.fatal("Flex pageManage Client didn't send any command!");
				result = XMLMessages.fail("-1");
			} else if (command.equals("listRoot")) {
				List<DPageNodeInfo> pages = dps.listSubPage(context.getSite().getId(),0);
	            result = getPages(pages);
			} else if (command.equals("listSubPages")) {
				String pid = request.getParameter("id");
				if (pid == null) {
					log.fatal("Flex pageManage Client didn't send any pageId!");
					result = XMLMessages.fail("-1");
				} else {
					int pid_i = Integer.parseInt(pid);
					
					List<DPageNodeInfo> pages = dps.listSubPage(context.getSite().getId(),pid_i);
		            result = getPages(pages);
				}
			} else if (command.equals("delPage")) {
				String pid = request.getParameter("id");
				if (pid == null || pid.trim().equals("")) {
					log.fatal("Flex pageManage Client didn't send any pageId!");
					result = XMLMessages.fail("-1");
				} else {
					int pid_i = Integer.parseInt(pid);
					
					int delParentID = VWBContext.getContainer().getViewPortService().getViewPort(context.getSite().getId(),pid_i).getParent();
		            String delParentTitle = dps.getParentTitle(context.getSite().getId(),pid_i);
		                 
		            dps.deleteDpageByResourceId(context.getSite().getId(),pid_i);
		            
		            result = delComplete(dps.listSubPage(context.getSite().getId(),delParentID), String.valueOf(delParentID), delParentTitle);
				}
			} else if (command.equals("movePage")) {
				String pid = request.getParameter("id");
				String parentid = request.getParameter("parentid");
				if (pid == null || parentid == null) {
					log.fatal("Flex pageManage Client didn't send any pageId or parentId!");
					result = XMLMessages.fail("-1");
				} else {
					int pid_i = Integer.parseInt(pid);
					int parentid_i = Integer.parseInt(parentid);
					dps.moveDpageNode(context.getSite().getId(),pid_i, parentid_i);
					result = XMLMessages.success();
				}
			}
			
			writeResponse(response, result);
		}

		//return doLayout(context);
		return null;
	}
	
	private String parseXML(String inputStr){
		return inputStr.replaceAll("<", "&lt;").replaceAll("&", "&amp;")
			.replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
	}

	private String getPages(List<DPageNodeInfo> pages) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("<pages>");
		for (DPageNodeInfo page : pages) {
			sb.append("<pageName id='" + page.getResourceId() + "' title= '" + parseXML(page.getTitle()) 
					+ "' isSubPages= '" + page.isSubPages() + "' author='"
					+ parseXML(page.getAuthor()) + "' createDate='"
					+ sdf.format(page.getDate()) + "' />");
		}
		sb.append("</pages>");
		String result = XMLMessages.successWithoutEscape(sb.toString());
		return result;
	}
	
	//parent - parent title
	private String delComplete(List<DPageNodeInfo> pages, String parentId, String parentTitle) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append("<pages>");
		sb.append("<pageName id='" + parentId  + "' title= '" + parseXML(parentTitle) + "' isSubPages='isParent' />");
		for (DPageNodeInfo page : pages) {
			sb.append("<pageName id='" + page.getResourceId() + "' title= '" + parseXML(page.getTitle())
					+ "' isSubPages= '" + page.isSubPages() + "' author='"
					+ parseXML(page.getAuthor()) + "' createDate='"
					+ sdf.format(page.getDate()) + "' />");
		}
		sb.append("</pages>");
		String result = XMLMessages.successWithoutEscape(sb.toString());
		return result;
	}

	private void writeResponse(HttpServletResponse response, String message) {
		try {
			response.setContentType("text/xml");
			response.setCharacterEncoding("utf-8");
			Writer writer = response.getWriter();
			writer.write(message);
			writer.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	protected static Logger log = Logger.getLogger(PageManageAction.class);
}
