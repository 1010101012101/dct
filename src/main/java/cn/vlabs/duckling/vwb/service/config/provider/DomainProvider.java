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

package cn.vlabs.duckling.vwb.service.config.provider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.service.config.SiteItem;
import cn.vlabs.duckling.vwb.service.config.impl.IDomainProvider;

/**
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public class DomainProvider extends ContainerBaseDAO implements IDomainProvider {
	private static final String queryByDomain = "select iSiteNum from vwb_properties where strName like ? and strValue=?";
	private static final String queryAllSiteDomain = "select iSiteNum ,strValue,strName from vwb_properties where strName like ?";
	private static final String queryAllSiteName = "select iSiteNum ,strValue from vwb_properties where strName like ?";

	@Override
	public int getSiteId(String domain) {
		return (Integer) getJdbcTemplate().query(queryByDomain,
				new Object[] { getDomainKeyPrefix(), domain },
				new ResultSetExtractor<Integer>() {
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt(1);
						}
						return -1;
					}
				});
	}

	private String getDomainKeyPrefix() {
		return KeyConstants.SITE_DOMAIN_KEY + "%";
	}

	@Override
	public Map<Integer, SiteItem> getAllSiteItems() {
		final Map<Integer, SiteItem> sites = new HashMap<Integer, SiteItem>();

		getJdbcTemplate().query(queryAllSiteDomain,
				new Object[] { getDomainKeyPrefix() }, new RowMapper<Integer>() {
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Integer siteId = Integer.valueOf(rs.getInt("iSiteNum"));
						SiteItem site = null;
						if (!sites.containsKey(siteId)) {
							site = new SiteItem();
							site.setSiteId(siteId.intValue());
							sites.put(siteId, site);
						}
						site = sites.get(siteId);
						String domainKey = rs.getString("strName");
						String domainValue = rs.getString("strValue");
						if (KeyConstants.SITE_DOMAIN_KEY.equals(domainKey)) {
							site.setMainDomain(domainValue);
						} else {
							site.setAuxiliaryDomain(domainValue);
						}

						return null;
					}
				});
		getJdbcTemplate().query(queryAllSiteName,
				new Object[] { KeyConstants.SITE_NAME_KEY }, new RowMapper<Integer>() {
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Integer siteId = Integer.valueOf(rs.getInt("iSiteNum"));
						SiteItem site = sites.get(siteId);
						if (site == null) {
							site = new SiteItem();
							site.setSiteId(siteId.intValue());
							sites.put(siteId, site);

						}
						String nameValue = rs.getString("strValue");
						site.setSiteName(nameValue);
						return null;
					}
				});
		return sites;
	}
}
