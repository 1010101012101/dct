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

package cn.vlabs.duckling.vwb.tags;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.service.plugin.PluginService;

/**
 * Inserts any Wiki plugin. The body of the tag becomes then the body for the
 * plugin.
 * <P>
 * <B>Attributes</B>
 * </P>
 * <UL>
 * <LI>plugin - name of the plugin you want to insert.
 * <LI>args - An argument string for the tag.
 * </UL>
 * 
 * @author Yong Ke
 */
public class PluginTag extends VWBBodyTag {
	private static final long serialVersionUID = 0L;

	private String m_plugin;
	private String m_args;

	private boolean m_evaluated = false;

	public void release() {
		super.release();
		m_plugin = m_args = null;
		m_evaluated = false;
	}

	public void setPlugin(String p) {
		m_plugin = p;
	}

	public void setArgs(String a) {
		m_args = a;
	}

	public int doVWBStart() throws JspException, IOException {
		m_evaluated = false;
		return EVAL_BODY_BUFFERED;
	}

	private String executePlugin(String plugin, String args, String body)
			throws PluginException, IOException {
		PluginService pm = VWBContext.getContainer().getPluginService();

		m_evaluated = true;

		Map<String, String> argmap = pm.parseArgs(args);

		if (body != null) {
			argmap.put("_body", body);
		}

		String result = pm.execute(vwbcontext, plugin, argmap);

		return result;
	}

	public int doEndTag() throws JspException {
		if (!m_evaluated) {
			try {
				pageContext.getOut().write(
						executePlugin(m_plugin, m_args, null));
			} catch (Exception e) {
				log.error("Failed to insert plugin", e);
				throw new JspException("Tag failed, check logs: "
						+ e.getMessage());
			}
		}
		return EVAL_PAGE;
	}

	public int doAfterBody() throws JspException {
		try {
			BodyContent bc = getBodyContent();

			getPreviousOut().write(
					executePlugin(m_plugin, m_args,
							((bc != null) ? bc.getString() : null)));
		} catch (Exception e) {
			log.error("Failed to insert plugin", e);
			throw new JspException("Tag failed, check logs: " + e.getMessage());
		}

		return SKIP_BODY;
	}
}
