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

package cn.vlabs.duckling.vwb.ui.rsi.profile;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.rsi.AdminAccessService;
import cn.vlabs.duckling.vwb.ui.rsi.api.language.LanguageRequest;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceException;

/**
 * @date 2010-8-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteLanguageService extends AdminAccessService {
	public Object getLanguage(RestSession session, Object message)
			throws ServiceException {
		LanguageRequest request = (LanguageRequest) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		String language = container.getSiteConfig().getProperty(request.getSiteId(),
				KeyConstants.SITE_LANGUAGE);
		if (StringUtils.isEmpty(language)) {
			Locale locale = this.getRequest().getLocale();
			language = locale.getLanguage() + "_" + locale.getCountry();
		}

		return language;
	}

	public Object setLanguage(RestSession session, Object message)
			throws ServiceException {
		LanguageRequest request = (LanguageRequest) message;
		VWBContainer container = VWBContainerImpl.findContainer();
		Properties properteis = new Properties();
		properteis.setProperty(KeyConstants.SITE_LANGUAGE,
				request.getLanguage());
		container.getSiteConfig().setProperty(request.getSiteId(), properteis);
		return null;
	}
}
