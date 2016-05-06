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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.variable.NoSuchVariableException;
import cn.vlabs.duckling.vwb.service.variable.VariableService;

/**
 * Introduction Here.
 * 
 * @date Feb 25, 2010
 * @author xiejj@cnic.cn
 */
public class VariableTag extends VWBBaseTag {
	private static final long serialVersionUID = 1L;

	private String m_key = null;
	private String m_var = null;
	private String m_default = null;

	public void initTag() {
		super.initTag();
		m_key = m_default = m_var = null;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public void setVar(String arg) {
		m_var = arg;
	}

	public void setDefault(String arg) {
		m_default = arg;
	}

	@Override
	public int doVWBStart() throws JspException, IOException {
		VariableService service = VWBContext.getContainer()
				.getVariableService();
		if (m_var != null) {
			setAttribute(service);
		} else {
			printText(service);
		}
		return SKIP_BODY;
	}

	private void printText(VariableService service) throws IOException {
		JspWriter out = pageContext.getOut();

		String msg = null;
		Object value = null;
		String txtValue = null;

		try {
			value = readValue(service);
		} catch (NoSuchVariableException e) {
			msg = "No such variable: " + e.getMessage();
		} catch (IllegalArgumentException e) {
			msg = "Incorrect variable name: " + e.getMessage();
		}
		if (value != null) {
			txtValue = value.toString();
		} else {
			txtValue = m_default;
			if (txtValue == null) {
				txtValue = msg;
			}
		}
		out.write(TextUtil.replaceEntities(txtValue));
	}

	private void setAttribute(VariableService service) {
		try {
			pageContext.setAttribute(m_var, readValue(service));
		} catch (NoSuchVariableException e) {

		} catch (IllegalArgumentException e) {

		}
	}

	private Object readValue(VariableService service)
			throws NoSuchVariableException {
		Object value;
		if (m_default != null) {
			value = service.getValue(vwbcontext, vwbcontext.getSite()
					.getId(), m_key, m_default);
		} else {
			value = service.getValue(vwbcontext, vwbcontext.getSite()
					.getId(), m_key);
		}
		return value;
	}
}
