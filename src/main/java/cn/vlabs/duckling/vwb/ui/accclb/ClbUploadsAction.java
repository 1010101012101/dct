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

package cn.vlabs.duckling.vwb.ui.accclb;

import java.io.IOException;
import java.io.Writer;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.json.simple.JSONObject;

import cn.cnic.esac.clb.util.XMLCharFilter;
import cn.vlabs.duckling.util.Utility;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;
import cn.vlabs.duckling.vwb.service.ddl.DDLService;
import cn.vlabs.duckling.vwb.service.flexsession.FlexSessionService;
import cn.vlabs.duckling.vwb.ui.XMLMessages;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * MyEclipse Struts Creation date: 05-06-2009 XDoclet definition:
 * 
 * @struts.action path="/clbUploadsAction" name="clbUploadsActionForm"
 *                scope="request" validate="true"
 */
public class ClbUploadsAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		VWBContext vwbcontext = VWBContext.createContext(request,
				VWBCommand.ATTACH, null);
		
		int siteId = vwbcontext.getSiteId();
		VWBSession vwbsession = vwbcontext.getVWBSession();
		DDLService ddlService = VWBContext.getContainer().getDDLService();
		AttachmentService attachmentService =VWBContext.getContainer().getAttachmentService();
		if (vwbsession.isAuthenticated()) {
			FlexSessionService flexSession = VWBContext.getContainer().getFlexSessionService();
			if (!flexSession.contains(request.getSession().getId())) {
				flexSession.save(request.getSession().getId(), vwbsession.getCurrentUser());
				flexSession.saveToken(request.getSession().getId(), vwbsession.getToken());
			}
		}
		if (attachmentService == null) {
			System.out.println("访问CLB服务器没有成功,可能是Session过期.");
			String result = XMLMessages.fail("Please login!");
			writeResponse(response, result);
			return null;
		}

		ClbUploadsActionForm docForm = (ClbUploadsActionForm) form;
		if (docForm.getFile() == null) {
			if (docForm.getAction() == null
					|| docForm.getAction().length() < 1) {
				String voname = Utility.getVoDisplayName(vwbcontext);
				String rootMsg = ClbXmlMessages.getRoot(voname);
				
				this.writeResponse(response, rootMsg);
			} else if (docForm.getAction().equals("mkdir")) {
				writeResponse(response, ClbXmlMessages.fail("Not support anymore."));
			} else if (docForm.getAction().equals("listFile")) {
				doListFile(attachmentService, docForm, response);
			} else if (docForm.getAction().equals("searchFile")) {
				doSearchFile(response,siteId, attachmentService,
						docForm);
			} else if (docForm.getAction().equals("lastUpload")) {
				doLastUpload(response, siteId, attachmentService);
			}
		} else {
			doCreateDocument(request, response,vwbcontext,
					ddlService, docForm);
		}
		return null;
	}
	
	private void doCreateDocument(HttpServletRequest request, HttpServletResponse response, VWBContext context,
			DDLService ddlService, ClbUploadsActionForm docForm) {
		FlexSessionService flexSession = VWBContext.getContainer().getFlexSessionService();
		Principal currentUser =flexSession.getCurrentUser(request.getParameter("JSESSIONID"));
		context.getVWBSession().setToken(flexSession.getToken(request.getParameter("JSESSIONID")));
		if (currentUser==null){
			currentUser = VWBSession.findSession(request).getCurrentUser();
		}
		
		FormFile f = docForm.getFile();
		String result = null;

		if (f != null && f.getFileSize() > 0) {
			JSONObject uploadResult= ddlService.upload(context, f);
			
			if(uploadResult==null||uploadResult.get("result")==null){
				result= "创建文件'" + f.getFileName() + "'操作失败！";
				writeResponse(response, XMLMessages.fail(result));
				return;
			}
			
			if(!(boolean)uploadResult.get("result")&&(int)uploadResult.get("errorCode")==2002){
				result= "存储文件'" + f.getFileName() + "'过程出错！请重新上传！";
				writeResponse(response, XMLMessages.fail(result));
				return;
			}
			
			
			result = "创建文件'" + f.getFileName() + "'操作成功！";
			// log upload file
			String signature = addAttachInfo(request, currentUser.getName(),docForm, "上传");
			result = "<hashid>" + (String)uploadResult.get("shareUrl")  + "</hashid><name>"
					+ XMLCharFilter.filter(f.getFileName()) + "</name>";
			result += "<signature>" + XMLCharFilter.filter(signature)
					+ "</signature>";
			result = XMLMessages.successWithoutEscape(result);
			writeResponse(response, result);
		} else {
			result = "创建文件操作失败！";
		}
		result = XMLMessages.fail(result);
		writeResponse(response, result);
	}

	private void doLastUpload(HttpServletResponse response,int siteId,
			AttachmentService attachmentService) {
		String baseUrl = "attach/";
		List<CLBAttachment> results = attachmentService.recentUploaded(siteId, 10);
		String filesXML=ClbXmlMessages.dumpFolderList(results, baseUrl);
		writeResponse(response, ClbXmlMessages.successWithoutEscape(filesXML));
	}

	private void doSearchFile(HttpServletResponse response,int siteId,
			AttachmentService attachmentService, ClbUploadsActionForm docForm) {
		String baseUrl = "attach/";
		if (docForm.getKeywords() != null && docForm.getKeywords().length() > 0){
			List<CLBAttachment> attachs = attachmentService.search(siteId, docForm.getKeywords());
			String fileXml = ClbXmlMessages.dumpFolderList(attachs, baseUrl);
			this.writeResponse(response, ClbXmlMessages.successWithoutEscape(fileXml));
		}
		else {
			String result = XMLMessages.fail("搜索关键为空！");
			writeResponse(response, result);
		}
	}

	private void doListFile(AttachmentService attachmentService, ClbUploadsActionForm docForm,
			HttpServletResponse response) {
		String folderXML = ClbXmlMessages.dumpFolderList(null, BASE_URL);
		writeResponse(response,ClbXmlMessages.successWithoutEscape(folderXML));
	}


	private String addAttachInfo(HttpServletRequest request,String userName,
			ClbUploadsActionForm docForm, String type) {
		// 获得签名
		String signature = "";
		if (docForm.getSignature() != null
				&& docForm.getSignature().equals("on")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			signature = "   【 " + userName	+ "  于 " + df.format(new Date()) + type + " 】";

		}
		return signature;
	}

	protected void writeResponse(HttpServletResponse response, String message) {
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

	private static String BASE_URL = "attach/";
	protected static Logger log = Logger.getLogger(ClbUploadsAction.class);
}
