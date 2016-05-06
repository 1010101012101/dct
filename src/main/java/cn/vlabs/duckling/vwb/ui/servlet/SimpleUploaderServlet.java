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

package cn.vlabs.duckling.vwb.ui.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.AttachmentURL;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * Introduction Here.
 * 
 * @date 2010-3-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SimpleUploaderServlet extends HttpServlet {
	/**
     * 
     */
	private static final long serialVersionUID = -6378442381470378270L;

	// private static String baseDir;
	private static boolean debug = false;

	private static boolean enabled = false;

	private static Set<String> allowedExtensions;

	private static Set<String> deniedExtensions;

	Logger log = Logger.getLogger(this.getClass().getName());

	private ResourceBundle rb = null;
	private static final String RETVAL = "retVal";
	private static final String FILEURL = "fileUrl";
	private static final String ERRORMESSAGE = "errorMessage";

	public void init(ServletConfig config) throws ServletException {

		debug = (new Boolean(config.getInitParameter("debug"))).booleanValue();
		log.debug("---- SimpleUploaderServlet initialization started ----");
		enabled = Boolean.valueOf(config.getInitParameter("enabled"));
		allowedExtensions = parseParams(config
				.getInitParameter("AllowedExtensionsFile"));
		deniedExtensions = parseParams(config
				.getInitParameter("DeniedExtensionsFile"));

		log.debug("---- SimpleUploaderServlet initialization completed ----");

	}

	private Set<String> parseParams(String str) {
		log.debug(str);
		String[] strArr = str.split("\\|");
		Set<String> tmp = new HashSet<String>();
		if (str.length() > 0) {
			for (int i = 0; i < strArr.length; ++i) {
				log.debug(i + " - " + strArr[i]);
				tmp.add(strArr[i].toLowerCase());
			}
		}
		return tmp;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.debug("--- BEGIN DOPOST ---");

		request.setCharacterEncoding("UTF-8");
		request.setAttribute(RETVAL, "0");
		request.setAttribute(FILEURL, "");
		request.setAttribute(ERRORMESSAGE, "");
		String newName = "";
		String hash = "";
		// 初始化环境变量
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ATTACH, null);
		rb = context.getBundle("templates/default");

		if (!context.getVWBSession().isAuthenticated()) {
			request.setAttribute(RETVAL, "212");
			request.setAttribute(ERRORMESSAGE,
					rb.getString("uploader.nopermission"));
		} else {
			if (enabled) {
				try {
					Map<String, Object> fields = parseFileItem(request);

					FileItem uplFile = (FileItem) fields.get("NewFile");
					String fileNameLong = uplFile.getName();
					fileNameLong = fileNameLong.replace('\\', '/');
					String[] pathParts = fileNameLong.split("/");
					String fileName = pathParts[pathParts.length - 1];

					long fileSize = uplFile.getSize();
					InputStream in = uplFile.getInputStream();

					if (fileSize == 0 || in == null) {
						if (debug)
							log.error("The upload file size is 0 or inputstream is null!");
						request.setAttribute(RETVAL, "211");
						request.setAttribute(ERRORMESSAGE,
								rb.getString("error.doc.zerolength"));
					} else {
						HashMap<String, String> hashMap = new HashMap<String, String>();
						hashMap.put("title", fileName);
						hashMap.put("summary", "");
						hashMap.put("page", (String) fields.get("pageName"));
						hashMap.put("signature",
								(String) fields.get("signature"));
						hashMap.put("rightType",
								(String) fields.get("rightType"));
						hashMap.put("cachable", (String) fields.get("cachable"));
						hash = executeUpload(request, context, in, fileName,
								fileSize, hashMap, response);
					}
					newName = fileName;
				} catch (Exception ex) {
					log.debug("Error on upload picture", ex);
					request.setAttribute(RETVAL, "203");
					request.setAttribute(ERRORMESSAGE,
							rb.getString("uploader.parsepara"));
				}
			} else {
				request.setAttribute(RETVAL, "1");
				request.setAttribute(ERRORMESSAGE,
						rb.getString("uploader.invalidxml"));
			}
		}

		printScript(request, response, newName, hash);

		log.debug("--- END DOPOST ---");
	}

	private void printScript(HttpServletRequest request,
			HttpServletResponse response, String newName, String hash)
			throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		// add by diyanliang 09-6-24 解决上传文件里面有'，"后调用js出错的bug
		if (newName != null) {
			newName = newName.replace("'", "");
			newName = newName.replace("\"", "");
		}

		out.println("<script type=\"text/javascript\">");
		out.println("window.parent.OnUploadCompleted("
				+ request.getAttribute(RETVAL) + ",'"
				+ request.getAttribute(FILEURL) + "','" + newName + "','"
				+ request.getAttribute(ERRORMESSAGE) + "','" + hash + "');");
		out.println("</script>");
		out.flush();
		out.close();
	}

	private Map<String, Object> parseFileItem(HttpServletRequest request)
			throws FileUploadException, UnsupportedEncodingException {
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		upload.setHeaderEncoding("UTF-8");
		List<?> items = upload.parseRequest(request);

		Map<String, Object> fields = new HashMap<String, Object>();

		Iterator<?> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (item.isFormField()) {
				fields.put(item.getFieldName(), item.getString("UTF-8"));
			} else {
				fields.put(item.getFieldName(), item);
			}
		}
		return fields;
	}

	protected String executeUpload(HttpServletRequest request,
			VWBContext context, InputStream data, String filename,
			long contentLength, HashMap<String, String> hashMap,
			HttpServletResponse response) {
		String pageName = "Main";
		String signature;

		String created = null;
		String title;
		if (hashMap != null) {
			title = hashMap.get("title");
			pageName = hashMap.get("page");
			if (pageName == null || pageName.equals(""))
				pageName = "Main";
			signature = hashMap.get("signature");
			if (signature == null || signature.equals(""))
				signature = "off";
		} else
			return null;

		if (!isTypeAllowed(filename)) {
			request.setAttribute(RETVAL, "205");
			request.setAttribute(ERRORMESSAGE,
					rb.getString("uploader.filetype"));
			return null;
		}
		if (filename == null || filename.trim().length() == 0) {
			log.error("Empty file name given.");
			request.setAttribute(RETVAL, "207");
			request.setAttribute(ERRORMESSAGE,
					rb.getString("uploader.emptyname"));
			return null;
		}
		//
		// Should help with IE 5.22 on OSX
		//
		filename = filename.trim();
		//
		// Remove any characters that might be a problem. Most
		// importantly - characters that might stop processing
		// of the URL.
		//
		filename = StringUtils.replaceChars(filename, "#?\"'", "____");
		if (data == null) {
			log.error("File could not be opened.");
			request.setAttribute(RETVAL, "208");
			request.setAttribute(ERRORMESSAGE,
					rb.getString("uploader.notopenfile"));

			return null;
		}
		StreamInfo stream = new StreamInfo();
		stream.setFilename(filename);
		stream.setInputStream(data);
		stream.setLength(contentLength);
		SiteMetaInfo site =  VWBContainerImpl.findSite(request);
		AttachmentService attachementService =VWBContainerImpl.findContainer().getAttachmentService();
		boolean cachable = "true".equalsIgnoreCase(hashMap.get("cachable"));
		try {
			VWBSession session = VWBSession.findSession(request);
			int docid = attachementService.createDocument(site.getId(),pageName, session
					.getCurrentUser().getName(), true, stream);
			if (docid == 0) {
				log.error("在clb中创建文档失败！");
				request.setAttribute(RETVAL, "209");
				request.setAttribute(ERRORMESSAGE, rb.getString("error.create"));
				return null;
			}
			AttachmentURL attach;
			if (title.lastIndexOf('.') != -1) {
				String suffix = title.substring(title.lastIndexOf('.') + 1);
				attach = new AttachmentURL(cachable, docid, suffix);
			} else {
				attach = new AttachmentURL(cachable, docid);
			}

			// 获得单个文档
			log.info("成功创建文档:id=" + docid + ";title=" + filename + "。");

			// 添加附件到当前页面
			// 获得签名
			if (signature.equals("on")) {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				signature = "   【 " + context.getCurrentUser().getName()
						+ "  于 " + df.format(Calendar.getInstance().getTime())
						+ " 上传 】";
				request.setAttribute(FILEURL, signature);
			}

			// 成功，将hash值传出。
			created = attach.buildURL(context.getBaseURL());

			request.setAttribute(RETVAL, "0");
			request.setAttribute(ERRORMESSAGE, rb.getString("uploader.success"));
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(RETVAL, "210");
			request.setAttribute(ERRORMESSAGE, rb.getString("uploader.upload"));
			return null;
		}

		return created;

	}

	private boolean isTypeAllowed(String filename) {
		if (filename == null || filename.length() == 0)
			return false;

		String ext = filename.toLowerCase();

		ext = ext.toLowerCase();
		if (allowedExtensions.contains(ext)) {
			return true;
		}
		if (deniedExtensions.contains(ext)) {
			return false;
		}
		return true;
	}
}
