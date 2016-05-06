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

package cn.vlabs.duckling.vwb.ui.command;

import java.security.Permission;

/**
 * Introduction Here.
 * 
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
public abstract class AbstractCommand implements Command {
	private String m_action;

	private String m_contentTemplate;

	private Permission m_permission;

	private Object m_target;

	private String m_urlpattern;

	public AbstractCommand(String action, Object target, Permission permission,
			String contentJsp, String urlpattern) {
		m_action = action;
		m_target = target;
		m_permission = permission;
		m_contentTemplate = contentJsp;
		m_urlpattern = urlpattern;
	}

	public final String getAction() {
		return m_action;
	}

	public final String getContentJSP() {
		return m_contentTemplate;
	}

	public final Permission getRequiredPermission() {
		return m_permission;
	}

	public final Object getTarget() {
		return m_target;
	}

	public final String getURLPattern() {
		return m_urlpattern;
	}

	public String toString() {
		return "Command"
				+ "[context="
				+ m_action
				+ ","
				+ "urlPattern="
				+ m_urlpattern
				+ ","
				+ "jsp="
				+ m_contentTemplate
				+ (m_target == null ? "" : ",target=" + m_target
						+ m_target.toString()) + "]";
	}
}
