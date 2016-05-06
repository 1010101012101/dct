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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.CLBException;
import cn.vlabs.clb.api.ResourceNotFound;
import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.duckling.util.AttachmentURL;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.ui.base.BaseServlet;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * @author Yong Ke
 * 
 */
public class CLBAttachmentServlet extends BaseServlet {

	private class AttachmentStrategy implements DownloadStrategy {
		private VWBContext context;

		private HttpServletResponse res;

		public AttachmentStrategy(VWBContext context, HttpServletResponse res) {
			this.context = context;
			this.res = res;
		}

		public int getMaxAge() {
			return 0;
		}

		public void onException(CLBException e) throws IOException {
			res.addDateHeader("Last-Modified", -1);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		public void onForbidden(AccessForbidden e) throws IOException {
			res.addDateHeader("Last-Modified", -1);
			if (context.getVWBSession().isAnonymous()) {
				if (!context.hasAccess(res))
					return;
			} else
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		public void onNotFound(ResourceNotFound e) throws IOException {
			res.addDateHeader("Last-Modified", -1);
			res.sendError(HttpServletResponse.SC_FOUND);
			return;
		}

	}

	private static class CachableStrategy implements DownloadStrategy {
		private HttpServletResponse res;

		public CachableStrategy(HttpServletResponse res) {
			this.res = res;
		}

		public int getMaxAge() {
			//8 hours
			return 28800;
		}

		public void onException(CLBException e) throws IOException {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		public void onForbidden(AccessForbidden e) throws IOException {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
		}

		public void onNotFound(ResourceNotFound e) throws IOException {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private static interface DownloadStrategy {
		int getMaxAge();

		void onException(CLBException e) throws IOException;

		void onForbidden(AccessForbidden e) throws IOException;

		void onNotFound(ResourceNotFound e) throws IOException;

	}

	private static Logger log = Logger.getLogger(CLBAttachmentServlet.class);

	private static final long serialVersionUID = 3257225521875313204L;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		VWBContext context = VWBContext.createContext(req, VWBCommand.ATTACH, null);
		AttachmentURL attach = AttachmentURL.parse(req);
		if (attach.isValid())
			download(context, attach, req, res);

	}

	private void download(VWBContext context, AttachmentURL attach,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, NumberFormatException, ServletException {
		DownloadStrategy strategy = getStrategy(context, attach, res);
		AttachmentService acs = VWBContext.getContainer().getAttachmentService();
		
		try {
			MetaInfo meta = acs.getMetaInfo(attach.getDocID());
			//附件在浏览器上最长的缓存时间
			int age = strategy.getMaxAge();

			if (isModified(meta, req)) {
				sendModifiedHeader(age, meta, res);
				res.setHeader("Content-Length",Integer.toString(meta.getSize()));
				AttSaver fs = new AttSaver(res, req);
				if (attach.getVersion() == null
						|| (attach.getVersion().equals(""))) {
					acs.getContent(attach.getDocID(), fs);
				} else {
					acs.getContent(attach.getDocID(), attach
							.getVersion(), fs);
				}
				log.debug("成功下载文档" + attach.getDocID());
			} else {
				sendNotModifiedHeader(age, meta, res);
			}
			
		} catch (AccessForbidden e) {
			log.warn("对ID为" + attach.getDocID() + "文档的访问被拒绝！");
			strategy.onForbidden(e);
		} catch (ResourceNotFound e) {
			log.warn("没有找到ID为" + attach.getDocID() + "的文档！");
			strategy.onNotFound(e);
		} catch (CLBException e) {
			log.warn("获取ID为" + attach.getDocID() + "的文档时，返回CLBException错误！");
			strategy.onException(e);
		};
	}

	private DownloadStrategy getStrategy(VWBContext context,
			AttachmentURL attach, HttpServletResponse res) {
		if (attach.isCachable())
			return new CachableStrategy(res);
		else
			return new AttachmentStrategy(context, res);
	}

	private boolean isModified(MetaInfo meta, HttpServletRequest request) {
		long browserModifyTime = request.getDateHeader("If-Modified-Since");
		return (browserModifyTime != meta.lastUpdate.getTime());
	}

	private void sendModifiedHeader(int age, MetaInfo meta,
			HttpServletResponse res) {
		res.setHeader("Cache-Control", "max-age=" + age);
		res.addDateHeader("Last-Modified", meta.lastUpdate.getTime());
	}

	private void sendNotModifiedHeader(int age, MetaInfo meta,
			HttpServletResponse res) {
		res.setHeader("Cache-Control", "max-age=" + age);
		res.addDateHeader("Last-Modified", meta.lastUpdate.getTime());
		log.debug("读取缓存文档" + meta.docid);
		res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	}
}
