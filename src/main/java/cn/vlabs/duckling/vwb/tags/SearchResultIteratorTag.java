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
import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.search.SearchResult;

public class SearchResultIteratorTag extends IteratorTag {
	private static final long serialVersionUID = 0L;

	private int m_maxItems;
	private int m_count = 0;
	private int m_start = 0;

	static Logger log = Logger.getLogger(SearchResultIteratorTag.class);

	public void release() {
		super.release();
		m_maxItems = m_count = 0;
	}

	public void setMaxItems(int arg) {
		m_maxItems = arg;
	}

	public void setStart(int arg) {
		m_start = arg;
	}

	public final int doStartTag() {
		//
		// Do lazy eval if the search results have not been set.
		//
		if (m_iterator == null) {
			Collection<?> searchresults = (Collection<?>) pageContext.getAttribute(
					"searchresults", PageContext.REQUEST_SCOPE);
			setList(searchresults);

			int skip = 0;

			// Skip the first few ones...
			m_iterator = searchresults.iterator();
			while (m_iterator.hasNext() && (skip++ < m_start))
				m_iterator.next();
		}

		m_count = 0;

		return nextResult();
	}

	private int nextResult() {
		if (m_iterator != null && m_iterator.hasNext()
				&& m_count++ < m_maxItems) {
			SearchResult r = (SearchResult) m_iterator.next();

			// Create a vwb context for the result
			// HttpServletRequest request = vwbcontext.getHttpRequest();

			// Stash it in the page context
			pageContext.setAttribute(VWBBaseTag.ATTR_CONTEXT, vwbcontext,
					PageContext.REQUEST_SCOPE);
			pageContext.setAttribute(getId(), r);

			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

	public int doAfterBody() {
		if (bodyContent != null) {
			try {
				JspWriter out = getPreviousOut();
				out.print(bodyContent.getString());
				bodyContent.clearBody();
			} catch (IOException e) {
				log.error("Unable to get inner tag text", e);
				// FIXME: throw something?
			}
		}

		return nextResult();
	}

	public int doEndTag() {
		m_iterator = null;

		return super.doEndTag();
	}
}
