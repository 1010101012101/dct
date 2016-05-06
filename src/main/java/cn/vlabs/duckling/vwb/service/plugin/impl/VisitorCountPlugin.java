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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.util.Map;

import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;

/**
 * @date 2011-1-5
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VisitorCountPlugin extends AbstractDPagePlugin {
	private final static String divViewTemplate = "<div id='visitorCountId' class='visitorCountClass'>siteVisitorCount</div>";

	public String execute(VWBContext context, Map<String, String> params)
			throws PluginException {

		SiteMetaInfo site = this.getSite();
		if (site != null) {
			String siteVisitorCount = VWBContext.getContainer().getSiteConfig()
					.getProperty(site.getId(), Attributes.VISITOR_SITE_COUNT);
			return divViewTemplate
					.replace("siteVisitorCount", siteVisitorCount);
		}
		return null;
	}

}
