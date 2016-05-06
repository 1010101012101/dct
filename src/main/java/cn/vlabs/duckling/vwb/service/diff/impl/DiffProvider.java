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

import cn.vlabs.duckling.vwb.VWBContext;

/**
 * Provides an SPI for creating a diff between two page versions.
 */
public interface DiffProvider {
	/**
	 * The return string is to be XHTML compliant ready to display html. No
	 * further processing of this text will be done by the wiki engine.
	 * 
	 * @return An XHTML diff.
	 * @param context
	 *            The Wiki Context
	 * @param oldWikiText
	 *            the old text
	 * @param newWikiText
	 *            the new text
	 */
	public String makeDiffHtml(VWBContext context, String oldWikiText,
			String newWikiText);

	/**
	 * The return string is to be XHTML compliant ready to display html. No
	 * further processing of this text will be done by the wiki engine.
	 * 
	 * @return An XHTML diff.
	 * @param context
	 *            The VWB Context
	 * @param pageid
	 *            The page's ID
	 * @param verFirst
	 *            The first version NO of the page which to be compared.
	 * @param verSecond
	 *            The second version NO of the page which to be compared with.
	 */
	public String makeDiffHtml(VWBContext context, int pageid, int verFirst,
			int verSecond) throws NoPageContentFoundException;

	/* Return some description of the DiffProvider. */
	public String getProviderInfo();

	/**
	 * If there is no diff provider set, this provider will work instead.
	 */
	public static class NullDiffProvider implements DiffProvider {
		/**
		 * {@inheritDoc}
		 */
		public String makeDiffHtml(VWBContext ctx, String oldWikiText,
				String newWikiText) {
			return "You are using the NullDiffProvider, check your properties file.";
		}

		/**
		 * {@inheritDoc}
		 */
		public String getProviderInfo() {
			return "NullDiffProvider";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * cn.vlabs.duckling.vwb.services.diff.DiffProvider#makeDiff(cn.vlabs
		 * .duckling.vwb.VWBContext, int, int, int)
		 */
		public String makeDiffHtml(VWBContext context, int pageid,
				int verFirst, int verSecond) throws NoPageContentFoundException {
			return "You are using the NullDiffProvider, check your properties file.";
		}
	}

}
