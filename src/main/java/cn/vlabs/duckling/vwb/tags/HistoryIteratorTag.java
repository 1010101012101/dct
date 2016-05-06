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
import java.security.ProviderException;
import java.util.Collection;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 * Iterates through tags.
 * 
 * <P>
 * <B>Attributes</B>
 * </P>
 * <UL>
 * <LI>page - Page name to refer to. Default is the current page.
 * </UL>
 * 
 * @author Yong Ke
 */

// FIXME: Too much in common with IteratorTag - REFACTOR
public class HistoryIteratorTag extends IteratorTag {
	private static final long serialVersionUID = 0L;

	static Logger log = Logger.getLogger(HistoryIteratorTag.class);
	
	private int m_start;
    private int m_pagesize;

	public final int doStartTag() {
		vwbcontext = (VWBContext) pageContext.getAttribute(
				VWBContext.CONTEXT_KEY, PageContext.REQUEST_SCOPE);

		DPage page = (DPage) vwbcontext.getResource();

		try {
//			if(m_start < 0) m_start = 0;
			if(m_pagesize < 0) m_pagesize =-1;
			if (page != null && vwbcontext.resourceExists(page.getResourceId())) {
				Collection<DPage> versions = null;
				if(m_start < 0){	// Get all versions
					versions = VWBContext.getContainer().getDpageService()
					.getDpageVersionsByResourceId(vwbcontext.getSite().getId(),page.getResourceId());
				}else{	// Get versions for page
					versions = VWBContext.getContainer().getDpageService()
						.getDpageVersionsByResourceIdD(vwbcontext.getSite().getId(),page.getResourceId(),m_start-1, m_pagesize);
				}
				
				if (versions == null) {
					// There is no history
					return SKIP_BODY;
				}

				m_iterator = versions.iterator();

				if (m_iterator.hasNext()) {
					refreshContext((Resource)m_iterator.next());
				} else {
					return SKIP_BODY;
				}
			}

			return EVAL_BODY_BUFFERED;
		} catch (ProviderException e) {
			log.fatal(
					"Provider failed while trying to iterator through history",
					e);
			// FIXME: THrow something.
		}

		return SKIP_BODY;
	}

	public final int doAfterBody() {
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

		if (m_iterator != null && m_iterator.hasNext()) {

			refreshContext((Resource)m_iterator.next());
			return EVAL_BODY_BUFFERED;
		}

		return SKIP_BODY;
	}

	private void refreshContext(Resource resource) {
		vwbcontext.targetCommand(resource);
		pageContext.setAttribute(getId(), resource,
				PageContext.PAGE_SCOPE);
	}

	/**
	 * @param m_start the m_start to set
	 */
	public void setStart(int start) {
		this.m_start = start;
	}

	/**
	 * @param m_pagesize the m_pagesize to set
	 */
	public void setPagesize(int pagesize) {
		this.m_pagesize = pagesize;
	}
}
