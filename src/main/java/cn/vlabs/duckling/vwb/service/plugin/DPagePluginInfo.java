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
 * 
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class DPagePluginInfo {
	private String m_className;
	private Class<?> m_clazz;

	public static DPagePluginInfo newInstance(Class<?> clazz) {
		DPagePluginInfo info = new DPagePluginInfo(clazz.getName());
		return info;
	}

	private DPagePluginInfo(String className) {
		setClassName(className);
	}

	private void setClassName(String fullClassName) {
		m_className = fullClassName;
	}

	/**
	 * Returns the full class name of this object.
	 * 
	 * @return The full class name of the object.
	 */
	public String getClassName() {
		return m_className;
	}

	/**
	 * Creates a new plugin instance.
	 * 
	 * @return A new plugin.
	 * @throws ClassNotFoundException
	 *             If the class declared was not found.
	 * @throws InstantiationException
	 *             If the class cannot be instantiated-
	 * @throws IllegalAccessException
	 *             If the class cannot be accessed.
	 */
	public AbstractDPagePlugin newPluginInstance()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (m_clazz == null) {
			m_clazz = Class.forName(m_className);
		}

		return (AbstractDPagePlugin) m_clazz.newInstance();
	}
}
