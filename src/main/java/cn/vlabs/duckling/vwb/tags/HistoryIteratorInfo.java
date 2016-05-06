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

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * Just provides the TEI data for HistoryIteratorTag.
 */
public class HistoryIteratorInfo extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo[] var = { new VariableInfo(data.getAttributeString("id"),
				"cn.vlabs.duckling.vwb.service.dpage.DPage", true,
				VariableInfo.NESTED) };

		return var;

	}
}
