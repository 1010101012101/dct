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

package cn.vlabs.duckling.vwb.service.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provide all international resource value.
 */
public class DucklingMessage {
	/* 缺省国际化资源文件名 */
	public static final String CORE_RESOURCES = "CoreResources";

	/* 缺省的国际化资源文件配置参数名 */
	private static final Locale DEFAULT_LOCALE = new Locale("");
	private String bundleName ;
	public void setBundleName(String bundleName){
		this.bundleName = bundleName;
	}
	/**
	 * Returns a String from the bundle of DUCKLING_I18N_BUNDLE_NAME using
	 * English as the default locale.
	 * 
	 * @param key
	 *            Key to find
	 * @return The English string
	 * @throws MissingResourceException
	 *             If there is no such key
	 */
	public String get(String key) throws MissingResourceException {
		return get(bundleName, Locale.getDefault(), key);
	}

	/**
	 * Finds a resource bundle.
	 * 
	 * @param bundle
	 *            The ResourceBundle to find. Must exist.
	 * @param locale
	 *            The Locale to use. Set to null to get the default locale.
	 * @return A localized string
	 * @throws MissingResourceException
	 *             If the key cannot be located at all, even from the default
	 *             locale.
	 */
	public static ResourceBundle getBundle(String bundle, Locale locale)
			throws MissingResourceException {
		if (locale == null)
			locale = Locale.getDefault();

		ResourceBundle b = ResourceBundle.getBundle(bundle, locale);

		return b;
	}

	public static ResourceBundle getBundle(String bundle, Locale prefer,
			Enumeration<Locale> iter) throws MissingResourceException {
		Locale locale = DEFAULT_LOCALE;
		if (prefer != null) {
			ResourceBundle b = ResourceBundle.getBundle(bundle, prefer);
			if (b.getLocale().equals(prefer))
				return b;
		}

		if (iter != null) {
			while (iter.hasMoreElements()) {
				Locale l = (Locale) iter.nextElement();
				ResourceBundle b = ResourceBundle.getBundle(bundle, l);
				if (b.getLocale().equals(l)) {
					return b;
				}
			}
		}
		return ResourceBundle.getBundle(bundle, locale);
	}

	/**
	 * If you are too lazy to open your own bundle, use this method to get a
	 * string simply from a bundle.
	 * 
	 * @param bundle
	 *            Which bundle the string is in
	 * @param locale
	 *            Locale to use - null for default
	 * @param key
	 *            Which key to use.
	 * @return A localized string (or from the default language, if not found)
	 * @throws MissingResourceException
	 *             If the key cannot be located at all, even from the default
	 *             locale.
	 */
	public String get(String bundle, Locale locale, String key)
			throws MissingResourceException {
		return getBundle(bundle, locale).getString(key);
	}

}
