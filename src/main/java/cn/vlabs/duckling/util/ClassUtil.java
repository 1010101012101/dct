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

import java.util.Iterator;
import java.util.List;

/**
 * Introduction Here.
 * 
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class ClassUtil {
	/**
	 * Attempts to find a class from a collection of packages. This will first
	 * attempt to find the class based on just the className parameter, but
	 * should that fail, will iterate through the "packages" -list, prefixes the
	 * package name to the className, and then tries to find the class again.
	 * 
	 * @param packages
	 *            A List of Strings, containing different package names.
	 * @param className
	 *            The name of the class to find.
	 * @return The class, if it was found.
	 * @throws ClassNotFoundException
	 *             if this particular class cannot be found from the list.
	 */
	public static Class<?> findClass(List<String> packages, String className)
			throws ClassNotFoundException {
		ClassLoader loader = ClassUtil.class.getClassLoader();

		try {
			return loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			for (Iterator<String> i = packages.iterator(); i.hasNext();) {
				String packageName = (String) i.next();

				try {
					return loader.loadClass(packageName + "." + className);
				} catch (ClassNotFoundException ex) {
					// This is okay, we go to the next package.
				}
			}
		}

		throw new ClassNotFoundException("Class '" + className
				+ "' not found in search path!");
	}
}
