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

package cn.vlabs.duckling.vwb.service.dml.html2dml;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
public class ForgetNullValuesLinkedHashMap extends
		LinkedHashMap<String, String> {
	private static final long serialVersionUID = 0L;

	public String put(String key, String value) {
		if (StringUtils.isNotBlank(value)) {
			return super.put(key, value);
		}
		return null;
	}
}