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

package cn.vlabs.duckling.vwb.service.init.provider;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pluto.driver.config.db.DBPortalPageService;
import org.apache.pluto.driver.config.db.PortletInfo;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.PortalItem;
import cn.vlabs.duckling.vwb.service.resource.config.PortletItem;

/**
 * 
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class PortalInitProvider extends AbstractViewPortInitProvider {
	private static final String insertPage = "insert into vwb_portal_page(siteId,resourceId, title, uri) values(?,?, ?,?)";
	private static final String insertPortlet = "insert into vwb_portal_portlets(siteId,resourceId, context, name) values(?,?, ?, ?)";

	public boolean init(int siteId, Map<String,String> params,String templatePath,String defaultDataPath) throws IOException {
		String portalFile = templatePath+ "/portals.xml";
		
		List<ConfigItem> portalPages = initItemFromXML(portalFile);
		if (portalPages != null) {
			for (ConfigItem item : portalPages) {
				create(siteId,convertToViewPort(item));
				createPortalPage(siteId,convertToPageConfig((PortalItem) item));
			}
		}
		return true;
	}

	private PageConfig convertToPageConfig(PortalItem item) {
		PageConfig config = new PageConfig();
		config.setResourceId(item.getId());
		config.setTitle(item.getTitle());
		config.setUri(item.getUri());
		if (item.getPortlets() != null) {
			for (PortletItem portlet : item.getPortlets()) {
				config.addPortlet(portlet.getContext(), portlet.getName());
			}
		}
		return config;
	}

	private void createPortalPage(final int siteId,final PageConfig convertToPageConfig) {
		getJdbcTemplate().update(
				insertPage,
				new Object[] { siteId,
						convertToPageConfig.getResourceId(),
						convertToPageConfig.getTitle(),
						convertToPageConfig.getUri() });
		final Collection<PortletInfo> c = DBPortalPageService
				.convert(convertToPageConfig.getPortletIds());
		if (c != null && c.size() > 0) {
			getJdbcTemplate().batchUpdate(insertPortlet,
					new BatchPreparedStatementSetter() {
						private Iterator<PortletInfo> iter = c.iterator();

						public int getBatchSize() {
							return c.size();
						}

						public void setValues(PreparedStatement ps, int count)
								throws SQLException {
							PortletInfo portletId = (PortletInfo) iter.next();
							int i = 0;
							ps.setInt(++i, siteId);
							ps.setInt(++i, convertToPageConfig.getResourceId());
							ps.setString(++i, portletId.getContextPath());
							ps.setString(++i, portletId.getName());
						}

					});
		}
	}

}
