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

import javax.servlet.ServletContext;

import cn.vlabs.installer.version.ProductInfo;
import cn.vlabs.installer.version.VersionUtil;

/**
 * Introduction Here.
 * 
 * @date Feb 26, 2010
 * @author key
 */
public class Constant {
	/**
	 * The maximum size of attachments that can be uploaded.
	 */
	public static final String CLB_FILE_MAXSIZE = "clb.attachment.maxsize";

	/**
	 * A space-separated list of attachment types which can be uploaded
	 */
	public static final String CLB_ALLOWEDEXTENSIONS = "clb.attachment.allowed";

	/**
	 * A space-separated list of attachment types which cannot be uploaded
	 */
	public static final String CLB_FORDBIDDENEXTENSIONS = "clb.attachment.forbidden";

	/**
	 * Property name for where the CLB work directory should be. If not
	 * specified, reverts to ${java.tmpdir}.
	 */
	public static final String CLB_WORKDIR = "clb.workDir";

	/**
	 * Property name of the local user for where the CLB access the files.
	 */
	// public static final String CLB_LOCALUSER = "clb.localuser";

	public static String VERSION;
	public static String CSP_VERSION;

	public final static String DUCKLING_NAME = "Duckling ";

	public final static String DUCKLING_VER = "3.1";

	public final static String DUCKLING_WEB = "http://duckling.escience.cn/";
	
	public final static String CSP_WEB = "http://csp.escience.cn/";

	public final static String DUCKLING_DATEFORMAT = "duckling.default.dateformat";
	public static final String ALLOW_COOKIE_ASSERTION = "duckling.cookie.allowAssertion";

	public static final String CONTAINER_AUTHENTICATED = "duckling.container.authenticated";
	public final static int DEFAULT_FRONT_PAGE = 1;
	public static final int DEFAULT_LEFT_MENU = 2;
	/**
	 * Default Message page
	 */
	public static final int DEFAULT_MESSAGE_PAGE = 65535;

	/**
	 * Default footer id
	 */
	public static final int DEFAULT_FOOTER = 4;
	/**
	 * Default top menu id
	 */
	public static final int DEFAULT_TOP_MENU = 3;
	public static final int DPAGE_LATEST_VERSION = -1;
	public static final int WELCOME_FRONT_PAGE = 22;
	
	public static String getVersion(ServletContext context) {
		if (VERSION == null) {
			VERSION = readVersion(context.getRealPath("/WEB-INF/dct.ver"));
		}
		return VERSION;
	}

	public static String getCspVersion(ServletContext context) {
		if (CSP_VERSION == null) {
			CSP_VERSION = readVersion(context
					.getRealPath("/../csp/WEB-INF/csp.ver"));
		}
		return CSP_VERSION;
	}

	private static String readVersion(String filename) {
		ProductInfo pi = VersionUtil.fromFile(filename);
		if (pi != null) {
			String result = pi.getProduct().toUpperCase() + " " + pi.getVersion();
			return result;
		} else {
			return "";
		}
	}
}
