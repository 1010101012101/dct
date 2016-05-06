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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.duckling.util.Utility;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * MyEclipse Struts Creation date: 08-30-2007 XDoclet definition:
 * 
 * @struts.action path="/updateCLB" name="updateCLBForm" input="/updateCLB.jsp"
 *                scope="request" validate="true"
 */
public class UpdateCLBAction extends Action {
	/*
	 * Generated Methods
	 */

	static Logger log = Logger.getLogger(UpdateCLBAction.class);

	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		UpdateCLBForm updateCLBForm = (UpdateCLBForm) form;
		return updateAttachFromFck(mapping, updateCLBForm, request,
				response);
	}

	public ActionForward updateAttachFromFck(ActionMapping mapping,
			UpdateCLBForm updateCLBForm, HttpServletRequest request,
			HttpServletResponse response) {

		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ATTACH, null);

		VWBSession session = context.getVWBSession();
		if (session.isAuthenticated()) {
			// 取出传递的参数,然后分析出信息,并取出附件的信息
			String act = request.getParameter("act");
			request.setAttribute("fileName", "");
			if ("upload".equals(act)) {// 进行实际的上载
				dealUploadFackAttach(mapping, updateCLBForm, request, response);
			} else {// 初始提交
				String docAttach = request.getParameter("docAttach");

				if (docAttach == null) {
					docAttach = "";
				}
				updateCLBForm.setAttach(docAttach);

				// 取出附件的信息
				try {
					String[] tmp = docAttach.split(",");
					String pg = Utility.getFromBASE64(tmp[0]);
					String[] ss = pg.split(":", 5);
					int docid = Integer.parseInt(ss[3].trim());
					updateCLBForm.setDocid(ss[3].trim());
					MetaInfo meta = VWBContext.getContainer()
							.getAttachmentService().getMetaInfo(docid);
					request.setAttribute("attachTitle", meta.filename);
					request.setAttribute("page", request.getParameter("page"));

				} catch (Exception e) {
					log.error(e);
					request.setAttribute("error", "doc.nodoc");

				}
			}
		}
		return mapping.findForward("updateFckAttach");
	}

	private void dealUploadFackAttach(ActionMapping mapping,
			UpdateCLBForm updateCLBForm, HttpServletRequest request,
			HttpServletResponse response) {

		// 上载并更新
		String docid = updateCLBForm.getDocid();
		String str = updateCLBForm.getComment();
		FormFile file = updateCLBForm.getFile();
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ATTACH, null);

		VWBSession session = context.getVWBSession();
		try {
			StreamInfo stream = new StreamInfo();
			stream.setFilename(file.getFileName());
			stream.setInputStream(file.getInputStream());
			stream.setLength(file.getFileSize());

			// 增加对文件长度的判断
			if (stream.getLength() <= 0 || (stream.getFilename() == null)
					|| (stream.getFilename().equals(""))) {
				log.error("上传文档长度小于等于零，上传失败！");
				request.setAttribute("error", "doc.zerolength");
				return;
			}
			AttachmentService attachService = VWBContext.getContainer()
					.getAttachmentService();
			String author = session.getCurrentUser().getName();
			String parentPage = request.getParameter("page");
			attachService.updateDocument(context.getSite().getId(), parentPage,
					author, Integer.parseInt(docid), stream);

		} catch (FileNotFoundException e) {
			log.debug(e);
			request.setAttribute("error", "nofile");

			return;
		}

		catch (IOException e) {
			log.debug(e);
			request.setAttribute("error", "localIO");
			return;
		} catch (NumberFormatException e) {
			log.debug(e);
			request.setAttribute("error", "update");
			return;
		}
		str = file.getFileName();
		request.setAttribute("fileName", str);
		if ((str != null) && (!str.equals(""))) {
			String hashid;
			if (str.lastIndexOf(".") != -1) {
				hashid = "clb:clb:" + str.substring(str.lastIndexOf(".") + 1)
						+ ":" + docid;
			} else
				hashid = "clb:clb::" + docid;
			hashid = Utility.getBASE64(hashid);
			updateCLBForm.setAttach(hashid);
		}
		try {
			request.setAttribute("attachTitle", file.getFileName());

		} catch (NumberFormatException e) {
			log.debug(e);
			request.removeAttribute("version");
		}

		request.setAttribute("succ", "ok");

	}
}
