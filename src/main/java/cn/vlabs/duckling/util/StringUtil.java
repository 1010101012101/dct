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

package cn.vlabs.duckling.util;

import java.util.Random;

/**
 * Introduction Here.
 * @date 2010-2-8
 * @author euniverse
 */
public class StringUtil {
	public static String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer(
				"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	/**
	 * Deal with the special characters such as '<', '>', '&', '"', '\n', '\r'
	 */
	public static String normalizeString(String p_string) {
		StringBuffer sb = new StringBuffer();
		int len = (p_string != null) ? p_string.length() : 0;

		for (int i = 0; i < len; i++) {
			char c = p_string.charAt(i);
			sb.append(normalizeChar(c));
		}
		return sb.toString();
	}

	public static String normalizeChar(char p_char) {
		StringBuffer sb = new StringBuffer();
		switch (p_char) {
		case '<':
			sb.append("&lt;");
			break;
		case '>':
			sb.append("&gt;");
			break;
		case '&':
			sb.append("&amp;");
			break;
		case '"':
			sb.append("&quot;");
			break;
		case '\r':
		case '\n':
		case '\\':
			sb.append("&#").append(Integer.toString(p_char)).append(";");
			break;
		default:
			sb.append(p_char);
			break;
		}
		return sb.toString();
	}

}
