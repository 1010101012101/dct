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

import java.io.IOException;
import java.io.Writer;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class WhitespaceTrimWriter extends Writer {

	private StringBuffer m_result = new StringBuffer();

	private StringBuffer m_buffer = new StringBuffer();

	public void flush() {
		if (m_buffer.length() > 0) {
			String s = m_buffer.toString();
			s = s.replaceAll("\r\n", "\n");
			m_result.append(s);
			m_buffer = new StringBuffer();
		}
	}

	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		m_buffer.append(arg0, arg1, arg2);
	}

	public void close() throws IOException {
	}

	public String toString() {
		flush();
		return m_result.toString();
	}
}
