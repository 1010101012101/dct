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
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.MimeType;
import cn.vlabs.rest.IFileSaver;

//实现接口FileSaver;
public class AttSaver implements IFileSaver {
	private static Logger log = Logger.getLogger(AttSaver.class);
	private OutputStream out = null;

	private HttpServletResponse m_res = null;

	private HttpServletRequest m_req = null;

	public AttSaver(HttpServletResponse res, HttpServletRequest req) {
		this.m_res = res;
		this.m_req = req;
	}

	public void save(String filename, InputStream in) {
		try {
			filename = java.net.URLDecoder.decode(filename, "UTF-8");
			String mimetype = getMimeType(m_req, filename);
			m_res.setContentType(mimetype);
			String agent = m_req.getHeader("USER-AGENT");

			String suffix = filename.substring(filename.indexOf(".") + 1,
					filename.length());

			m_res.setContentType(MimeType.getContentType(suffix));
			if (filename.indexOf("swf") != -1) {
				m_res.setContentType("application/x-shockwave-flash");
			} else {
				if (null != agent && -1 != agent.indexOf("MSIE")) {

					String codedfilename = java.net.URLEncoder.encode(filename,
							"UTF-8");
					codedfilename = StringUtils.replace(codedfilename, "+",
							"%20");
					if (codedfilename.length() > 150) {
						codedfilename = new String(filename.getBytes("GBK"),
								"ISO8859-1");
						codedfilename = StringUtils.replace(codedfilename, " ",
								"%20");
					}
					m_res.setHeader("Content-Disposition",
							"attachment;filename=\"" + codedfilename + "\"");
				} else if (null != agent && -1 != agent.indexOf("Firefox")) {
					String codedfilename = javax.mail.internet.MimeUtility
							.encodeText(filename, "UTF-8", "B");

					m_res.setHeader("Content-Disposition",
							"attachment;filename=\"" + codedfilename + "\"");
				} else {
					String codedfilename = java.net.URLEncoder.encode(filename,
							"UTF-8");
					m_res.setHeader("Content-Disposition",
							"attachment;filename=\"" + codedfilename + "\"");
				}
			}

			if (out == null)
				out = m_res.getOutputStream();
			int read = 0;
			byte buf[] = new byte[4096];
			while ((read = in.read(buf, 0, 4096)) != -1) {
				out.write(buf, 0, read);
			}
			if (out != null) {
				out.close();
			}
		}catch (IOException e) {
			log.info(String.format("Client %s aborted while download file %s, error message:%s",m_req.getRemoteAddr(), filename,e.getMessage()));
		}
	}

	private static String getMimeType(HttpServletRequest req, String fileName) {
		String mimetype = null;

		if (req != null) {
			ServletContext s = req.getSession().getServletContext();

			if (s != null) {
				mimetype = s.getMimeType(fileName.toLowerCase());
			}
		}

		if (mimetype == null) {
			mimetype = "application/binary";
		}

		return mimetype;
	}

	public void save(InputStream in, String filename) throws IOException {
		save(filename, in);
	}

}
