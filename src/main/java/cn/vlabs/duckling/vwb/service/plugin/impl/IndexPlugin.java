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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.io.StringWriter;
import java.security.ProviderException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * 
 * @date 2010-3-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class IndexPlugin extends AbstractDPagePlugin {
	private static final int DEFAULT_ITEMS_PER_LINE = 0;
	private static final Logger log = Logger.getLogger(IndexPlugin.class);

	private static final String PARAM_EXCLUDE = "exclude";
	private static final String PARAM_INCLUDE = "include";
	private static final String PARAM_ITEMS_PER_LINE = "itemsPerLine";

	private StringWriter m_bodyPart = new StringWriter();
	private int m_currentNofPagesOnLine = 0;
	private Pattern m_excludePattern;
	private StringWriter m_headerPart = new StringWriter();
	private Pattern m_includePattern;
	private int m_itemsPerLine;
	private String m_previousPageFirstLetter = "";

	private void addLetterHeaderWithLine(final String firstLetter) {
		m_bodyPart.write("\n<br /><br />" + "<span class=\"section\">"
				+ "<a name=\"" + firstLetter + "\">" + firstLetter
				+ "</a></span>" + "<hr />\n");
	}

	private void addLetterToIndexHeader(final String firstLetter) {
		final boolean noLetterYetInTheIndex = !""
				.equals(m_previousPageFirstLetter);

		if (noLetterYetInTheIndex) {
			m_headerPart.write(" - ");
		}

		m_headerPart.write("<a href=\"#" + firstLetter + "\">" + firstLetter
				+ "</a>");
	}

	private void addPageToIndex(VWBContext context, LightDPage curPage) {
		final boolean notFirstPageOnLine = 2 <= m_currentNofPagesOnLine;

		if (notFirstPageOnLine) {
			m_bodyPart.write(",&nbsp; ");
		}
		String url = context.getURL(VWBContext.VIEW,
				Integer.toString(curPage.getResourceId()));
		m_bodyPart.write("<a href=\"" + url + "\">" + curPage.getTitle()
				+ "</a>");
	}

	private void breakLineIfTooLong() {
		final boolean limitReached = m_itemsPerLine == m_currentNofPagesOnLine;

		if (limitReached) {
			m_bodyPart.write("<br />\n");
			m_currentNofPagesOnLine = 0;
		}
	}

	private void buildIndexPageHeaderAndBody(VWBContext context,
			final Collection<LightDPage> allPages) {
		PatternMatcher matcher = new Perl5Matcher();

		for (Iterator<LightDPage> i = allPages.iterator(); i.hasNext();) {
			LightDPage curPage = i.next();

			if (matcher.matches(curPage.getTitle(), m_includePattern)) {
				if (!matcher.matches(curPage.getTitle(), m_excludePattern)) {
					++m_currentNofPagesOnLine;

					String pageNameFirstLetter = curPage.getTitle()
							.substring(0, 1).toUpperCase();
					boolean sameFirstLetterAsPreviousPage = m_previousPageFirstLetter
							.equals(pageNameFirstLetter);

					if (!sameFirstLetterAsPreviousPage) {
						addLetterToIndexHeader(pageNameFirstLetter);
						addLetterHeaderWithLine(pageNameFirstLetter);

						m_currentNofPagesOnLine = 1;
						m_previousPageFirstLetter = pageNameFirstLetter;
					}

					addPageToIndex(context, curPage);
					breakLineIfTooLong();
				}
			}
		} // for
	}

	/**
	 * Gets all pages, then sorts them.
	 */
	private Collection<LightDPage> getAllPagesSortedByName(VWBContext context) {
		Collection<LightDPage> result = new TreeSet<LightDPage>(
				new Comparator<LightDPage>() {
					public int compare(LightDPage o1, LightDPage o2) {
						if (o1 == null || o2 == null)
							return 0;
						return o1.getTitle().compareTo(o2.getTitle());
					}
				});

		try {
			Collection<LightDPage> allPages =VWBContainerImpl.findContainer()
					.getDpageService().getAllPage(getSite().getId());
			result.addAll(allPages);
		} catch (ProviderException e) {
			log.fatal("PageProvider is unable to list pages: ", e);
		}

		return result;
	}

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {

		//
		// Parse arguments and create patterns.
		//
		PatternCompiler compiler = new GlobCompiler();
		m_itemsPerLine = TextUtil.parseIntParameter(
				(String) params.get(PARAM_ITEMS_PER_LINE),
				DEFAULT_ITEMS_PER_LINE);
		try {
			String ptrn = (String) params.get(PARAM_INCLUDE);
			if (ptrn == null)
				ptrn = "*";
			m_includePattern = compiler.compile(ptrn);

			ptrn = (String) params.get(PARAM_EXCLUDE);
			if (ptrn == null)
				ptrn = "";
			m_excludePattern = compiler.compile(ptrn);
		} catch (MalformedPatternException e) {
			throw new PluginException("Illegal pattern detected."); // FIXME,
																	// make a
																	// proper
																	// error.
		}

		//
		// Get pages, then sort.
		//

		Collection<LightDPage> allPages = getAllPagesSortedByName(context);

		//
		// Build the page.
		//
		buildIndexPageHeaderAndBody(context, allPages);

		StringBuffer res = new StringBuffer();

		res.append("<div class=\"index\">\n");
		res.append("<div class=\"header\">\n");
		res.append(m_headerPart.toString());
		res.append("</div>\n");
		res.append("<div class=\"body\">\n");
		res.append(m_bodyPart.toString());
		res.append("</div>\n</div>\n");

		return res.toString();
	}

}
