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

package cn.vlabs.duckling.vwb.ui.accclb;

import java.util.List;

import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;
import cn.vlabs.duckling.vwb.ui.XMLMessages;

/**
 * @date 2013-3-27
 * @author xiejj
 */
public class ClbXmlMessages extends XMLMessages {
	private static final String FILE_MSG_TMPL = "<entry type='file'"
			+ " title='%s' name='%s' path='/'" + " docid='%d' hashid='%s'"
			+ " creator='%s' lastupdate='%s'"
			+ " length='%d' version='' summary='' isCurrent='' isRemoved=''/>";

	private static final String ROOT_MSG_TMPL = "<response><result>success</result>"
			+ "<dirs>"
			+ "<dir type='home' path='' name='我的空间' display='我的空间'/>"
			+ "<dir type='vo' path='' name='vo/%s' display='%s'/>"
			+ "</dirs>"
			+ "</response>";
	/**
	 * 构建用户浏览子目录树的xml文档
	 * 
	 * @param folderlist
	 * @param baseURL
	 * @return
	 */
	public static String dumpFolderList(List<CLBAttachment> attachs,
			String baseURL) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<files>");
		if (attachs!=null){
			for (CLBAttachment entry : attachs) {
				String hashid = AccessCLBHelper.getHashId(entry.getFileName(),
						Integer.toString(entry.getClbId()), baseURL);
				String entryStr = format(FILE_MSG_TMPL, entry.getFileName(),
						entry.getFileName(), entry.getClbId(), hashid,
						entry.getAuthor(), entry.getLastModifiedCustom("yyyy-MM-dd hh:mm"),
						entry.getLength());
				buffer.append(entryStr);
			}
		}
		buffer.append("</files>");
		return buffer.toString();
	}

	public static String getRoot(String voName) {
		return format(ROOT_MSG_TMPL, voName, voName);
	};

}