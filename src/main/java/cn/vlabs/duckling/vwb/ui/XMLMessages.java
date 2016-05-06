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
package cn.vlabs.duckling.vwb.ui;

import cn.cnic.esac.clb.util.XMLCharFilter;

/**
 * 构建返回flex的clb的文件系统的xml文档
 * @author yhw
 *May 14, 2009
 */
public class XMLMessages {
    private static final String FMT_SUCCESS="<response><result>success</result>%s</response>";
    private static final String FMT_FAIL_WITH_MESSAGE="<response><result>fail</result><msg>%s</msg></response>";
    private static String escape(String msg){
        return XMLCharFilter.filter(msg);
    }

    /**
     * 失败消息
     * 
     * @param msg
     *            需要发出的失败原因。
     * @return 合成以后的消息。
     */
    public static String fail(String msg) {
    	return format(FMT_FAIL_WITH_MESSAGE, msg);
    }
    
    public static String format(String fmt, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof String) {
				args[i] = escape((String) args[i]);
			}
		}
		return String.format(fmt, args);
	}

	/**
     * 成功消息（不带返回值的）
     * 
     * @return XML格式的消息。
     */
    public static String success() {
    	return successWithoutEscape("");
    }
	
    /**
     * 成功消息（带返回值)
     * 
     * @param msg
     *            需要携带的返回值.
     * @return XML格式的消息。
     */
    public static String successWithoutEscape(String msg) {
    	return String.format(FMT_SUCCESS,msg);
    }
}