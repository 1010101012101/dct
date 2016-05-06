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

package cn.vlabs.duckling.util;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

/**
 * 辅助分析附件链接类型的类
 * @author xiejj@cnic.cn
 * 
 * @creation Nov 12, 2010 11:20:28 AM
 */
public class AttachmentURL {
	public static AttachmentURL parse(HttpServletRequest req) {
		String hash = req.getPathInfo() == null ? "" : req.getPathInfo();
		hash = hash.length() > 1 ? hash.substring(1) : hash;
		try {
			hash = java.net.URLDecoder.decode(hash, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//Do nothing
		}

		String servletPath = req.getServletPath();
		boolean cachable = "/cachable".equals(servletPath);
		return new AttachmentURL(cachable, hash);
	}

	//附件下载时是否是可缓存的
	private boolean cachable;

	//附件的ID
	private int docid;

	//这个附件链接是否是正确的
	private boolean valid;

	//附件的版本，为空时表示最新版本
	private String version;

	private String suffix;

	public AttachmentURL(boolean cachable, int docid) {
		this(cachable, docid, null, null);
	}

	public AttachmentURL(boolean cachable, int docid, String suffix) {
		this(cachable, docid, suffix, null);
	}

	public AttachmentURL(boolean cachable, int docid, String version,
			String suffix) {
		valid = true;
		this.cachable = cachable;
		this.docid = docid;
		this.version = version;
		this.suffix = suffix;
	}

	public AttachmentURL(boolean cachable, String hash) {
		valid = isCLBHash(hash);
		this.cachable = cachable;
		if (valid) {
			String[] tmp = hash.split(",");
			if (tmp.length > 1) {
				this.version = tmp[tmp.length - 1].trim();
			}

			String pg = Utility.getFromBASE64(tmp[0]);
			String[] ss = pg.split(":", 5);
			this.suffix= ss[2];
			this.docid = Integer.parseInt(ss[3].trim());
		}
	}

	public boolean isValid() {
		return this.valid;
	}

	public boolean isCachable() {
		return this.cachable;
	}

	public int getDocID() {
		return this.docid;
	}

	public String getVersion() {
		return this.version;
	}

	public String buildURL(String prefix) {
		if (!prefix.endsWith("/")) {
			prefix = prefix + "/";
		}
		String servletPart;
		if (cachable)
			servletPart = "cachable/";
		else
			servletPart = "attach/";

		return prefix + servletPart + getHashId();
	}

	private String getHashId() {
		String hashid;
		if (suffix == null || suffix.length() == 0)
			hashid = "clb:clb:" + suffix + ":" + docid;
		else
			hashid = "clb:clb::" + docid;
		return Utility.getBASE64(hashid);
	}

	private boolean isCLBHash(String name) {
		int i = name.lastIndexOf(",");
		if (i == -1) {
			if (Utility.getFromBASE64(name).startsWith("clb:"))
				return true;
			return false;
		}
		if (Utility.getFromBASE64(name.substring(0, i)).startsWith("clb:"))
			return true;
		return false;
	}
}
