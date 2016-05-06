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

package cn.vlabs.duckling.vwb.service.plugin;

/**
 * Introduction Here.
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 0L;

	private final Throwable m_throwable;

	public PluginException(String message) {
		super(message);
		m_throwable = null;
	}

	public PluginException(String message, Throwable original) {
		super(message);
		m_throwable = original;
	}

	public Throwable getRootThrowable() {
		return m_throwable;
	}
}
