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
package cn.vlabs.duckling.common.util;

public final class ClassUtil {
	private ClassUtil() {

	}

	public static Object classInstance(String p_className) {
		Object obj = null;
		if (p_className == null || p_className.length() == 0) {
		}
		try {
			Class classz = loadClass(p_className);
			obj = classz.newInstance();
		} catch (IllegalAccessException e) {
		} catch (InstantiationException e) {
		}

		return obj;
	}

	/**
	 * Return class new instance with class name.
	 */
	public static Class loadClass(String p_className) {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			if (classLoader == null) {
				classLoader = ClassUtil.class.getClassLoader();
			}
			Class classz = classLoader.loadClass(p_className);
			return classz;
		} catch (ClassNotFoundException e) {

		}
		return null;
	}

}
