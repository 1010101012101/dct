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
package cn.vlabs.commons.xml;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateFormatter {
	public static Date parse(String date) {
		try {
			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String formateDate(Date d) {
		return df.format(d);
	}

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd   HH:mm:ss");
}