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
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import cn.vlabs.duckling.vwb.VWBContext;

/**
 * Introduction Here.
 * @date Mar 1, 2010
 * @author xiejj@cnic.cn
 */
public class ContentTag extends VWBBaseTag {
	private static final long serialVersionUID = 0L;

	private HashMap<String, String> m_mappings = new HashMap<String, String>();

	public void setView(String s) {
		m_mappings.put(VWBContext.VIEW, s);
	}

	public void setDiff(String s) {
		m_mappings.put(VWBContext.DIFF, s);
	}

	public void setInfo(String s) {
		m_mappings.put(VWBContext.INFO, s);
	}

	public void setPreview(String s) {
		m_mappings.put(VWBContext.PREVIEW, s);
	}

	public void setConflict(String s) {
		m_mappings.put(VWBContext.CONFLICT, s);
	}

	public void setFind(String s) {
		m_mappings.put(VWBContext.FIND, s);
	}

	public void setError(String s) {
		m_mappings.put(VWBContext.ERROR, s);
	}

	public void setEdit(String s) {
		m_mappings.put(VWBContext.EDIT, s);
	}

	public void setAttach(String s) {
		m_mappings.put(VWBContext.ATTACH, s);
	}

	public int doVWBStart() throws Exception {
		return SKIP_BODY;
	}

	public final int doEndTag() throws JspException {
		try {
			String requestContext = vwbcontext.getAction();
			String contentTemplate = (String) m_mappings.get(requestContext);

			if (contentTemplate == null) {
				contentTemplate = vwbcontext.getContentJSP();
			}

			if (contentTemplate == null) {
				throw new JspException(
						"This template uses <wiki:Content/> in an unsupported context: "
								+ requestContext);
			}

			pageContext.include(contentTemplate);
		} catch (ServletException e) {
			log.warn(
					"Including failed, got a servlet exception from sub-page. "
							+ "Rethrowing the exception to the JSP engine.", e);
			throw new JspException(e.getMessage());
		} catch (IOException e) {
			log.warn("I/O exception - probably the connection was broken. "
					+ "Rethrowing the exception to the JSP engine.", e);
			throw new JspException(e.getMessage());
		}

		return EVAL_PAGE;
	}
}
