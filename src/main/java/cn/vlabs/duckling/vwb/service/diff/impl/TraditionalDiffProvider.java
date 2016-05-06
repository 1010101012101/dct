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


package cn.vlabs.duckling.vwb.service.diff.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.i18n.DucklingMessage;

/**
 * 
 * @author Yong Ke
 */

public class TraditionalDiffProvider extends ContainerBaseDAO implements
		DiffProvider {
	private static final Logger log = Logger
			.getLogger(TraditionalDiffProvider.class);

	private static final String CSS_DIFF_ADDED = "<tr><td class=\"diffadd\">";
	private static final String CSS_DIFF_REMOVED = "<tr><td class=\"diffrem\">";
	private static final String CSS_DIFF_UNCHANGED = "<tr><td class=\"diff\">";
	private static final String CSS_DIFF_CLOSE = "</td></tr>" + Diff.NL;

	private static final String queryContentSQL = "SELECT content FROM vwb_dpage_content_info WHERE siteId=? and resourceid=? and version=?";

	/**
	 * @see com.ecyrd.jspwiki.WikiProvider#getProviderInfo()
	 */
	public String getProviderInfo() {
		return "TraditionalDiffProvider";
	}

	/**
	 * Makes a diff using the BMSI utility package. We use our own diff printer,
	 * which makes things easier.
	 */
	public String makeDiffHtml(VWBContext ctx, String p1, String p2) {
		String diffResult = "";

		try {
			String[] first = Diff.stringToArray(TextUtil.replaceEntities(p1));
			String[] second = Diff.stringToArray(TextUtil.replaceEntities(p2));
			Revision rev = Diff.diff(first, second, new MyersDiff());

			if (rev == null || rev.size() == 0) {
				// No difference

				return "";
			}

			StringBuffer ret = new StringBuffer(rev.size() * 20); // Guessing
																	// how big
																	// it will
																	// become...

			ret.append("<table class=\"diff\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			rev.accept(new RevisionPrint(ctx, ret));
			ret.append("</table>\n");

			return ret.toString();
		} catch (DifferentiationFailedException e) {
			diffResult = "makeDiff failed with DifferentiationFailedException";
			log.error(diffResult, e);
		}

		return diffResult;
	}

	public static final class RevisionPrint implements RevisionVisitor {
		private StringBuffer m_result = null;

		private ResourceBundle m_rb;

		private RevisionPrint(VWBContext ctx, StringBuffer sb) {
			m_result = sb;

			m_rb = ctx.getBundle(DucklingMessage.CORE_RESOURCES);

		}

		public void visit(Revision rev) {
			// GNDN (Goes nowhere, does nothing)
		}

		public void visit(AddDelta delta) {
			Chunk changed = delta.getRevised();
			print(changed, m_rb.getString("diff.traditional.added"));
			changed.toString(m_result, CSS_DIFF_ADDED, CSS_DIFF_CLOSE);
		}

		public void visit(ChangeDelta delta) {
			Chunk changed = delta.getOriginal();
			print(changed, m_rb.getString("diff.traditional.changed"));
			changed.toString(m_result, CSS_DIFF_REMOVED, CSS_DIFF_CLOSE);
			delta.getRevised().toString(m_result, CSS_DIFF_ADDED,
					CSS_DIFF_CLOSE);
		}

		public void visit(DeleteDelta delta) {
			Chunk changed = delta.getOriginal();
			print(changed, m_rb.getString("diff.traditional.removed"));
			changed.toString(m_result, CSS_DIFF_REMOVED, CSS_DIFF_CLOSE);
		}

		private void print(Chunk changed, String type) {
			m_result.append(CSS_DIFF_UNCHANGED);

			String[] choiceString = {
					m_rb.getString("diff.traditional.oneline"),
					m_rb.getString("diff.traditional.lines") };
			double[] choiceLimits = { 1, 2 };

			MessageFormat fmt = new MessageFormat("");

			fmt.setLocale(m_rb.getLocale());
			ChoiceFormat cfmt = new ChoiceFormat(choiceLimits, choiceString);
			fmt.applyPattern(type);
			Format[] formats = { NumberFormat.getInstance(), cfmt,
					NumberFormat.getInstance() };
			fmt.setFormats(formats);

			Object[] params = { new Integer(changed.first() + 1),
					new Integer(changed.size()), new Integer(changed.size()) };
			m_result.append(fmt.format(params));
			m_result.append(CSS_DIFF_CLOSE);
		}
	}

	/**
	 * Get the specific version content of page which pageid is given. Brief
	 * Intro Here
	 * 
	 * @param int pageid - The page's ID
	 * @param int version - The version specified.
	 * @return - Return the content of the page at the version.
	 */
	public String getPageContentByVersion(int siteId, int pageid, int version)
			throws NoPageContentFoundException {
		String objContent = getJdbcTemplate().query(queryContentSQL,
				new Integer[] {siteId, pageid, version }, new ResultSetExtractor<String>() {
					public String extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getString("content");
						}
						return null;
					}
				});
		if (objContent == null) {
			throw new NoPageContentFoundException(pageid, version);
		} else {
			return (String) objContent;
		}
	}

	/**
	 * Makes a diff using the BMSI utility package. We use our own diff printer,
	 * which makes things easier.
	 */
	public String makeDiffHtml(VWBContext ctx, int pageid, int verFirst,
			int verSecond) throws NoPageContentFoundException {
		String contentFirst = "";
		String contentSecond = "";
		try {
			contentFirst = this.getPageContentByVersion(ctx.getSite().getId(),pageid, verFirst);
			contentSecond = this.getPageContentByVersion(ctx.getSite().getId(), pageid, verSecond);
		} catch (NoPageContentFoundException ncfex) {
			log.error("The specific version content not found, the difference action broken. ");
			throw ncfex;
		}
		return makeDiffHtml(ctx, contentFirst, contentSecond);
	}
}
