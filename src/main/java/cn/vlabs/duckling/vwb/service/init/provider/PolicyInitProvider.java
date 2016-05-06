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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.KeyConstants;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.service.auth.policy.SitePolicy;
import cn.vlabs.duckling.vwb.service.auth.policy.SitePolicyRowMapper;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.spi.VWBContainer;

/**
 * Policy文件的初始化
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class PolicyInitProvider extends ContainerBaseDAO implements InitProvider{

	
	private static final String createSitePolicySQL = "insert into vwb_policy "+ "(siteId,policy) "+ " values(?,?)";
	private static final String findSitePolicyBySiteId = "select * from vwb_policy where siteId=?";
	private static final String updateSitePolicy = "update vwb_policy set siteId=?,policy=? where id=?";

	private String getVoGroup(int siteId){
		VWBContainer container =  VWBContainerImpl.findContainer();
		String voname = container.getSiteConfig().getProperty(siteId, KeyConstants.SITE_UMT_VO_KEY);
		return container.getUserService().getVOGroup(voname);
	}
	
	@Override
	public boolean init(int siteId, Map<String, String> params,
			String templatePath, String defaultDataPath) throws IOException {
		SitePolicy sitePolicy=getSitePolicy(siteId);
		if(sitePolicy==null){
			sitePolicy=new SitePolicy();
			sitePolicy.setSiteId(siteId);
		}
		if(StringUtils.isBlank(sitePolicy.getPolicy())){
			String policychoice = templatePath + "/duckling.policy."+ params.get(KeyConstants.SITE_ACCESS_OPTION_KEY);
			if(!new File(policychoice).exists()){
				policychoice = defaultDataPath + "/duckling.policy."+ params.get(KeyConstants.SITE_ACCESS_OPTION_KEY);
			}
			if (!new File(policychoice).exists()) {
				policychoice = defaultDataPath+ "/duckling.policy";
			}
			String policyContent = FileUtils.readFileToString(new File(policychoice),"utf-8");
			policyContent = policyContent.replaceAll("VO",getVoGroup(siteId));
			sitePolicy.setPolicy(policyContent);
			if(sitePolicy.getId()>0){
				updateSitePolicy(sitePolicy);
			}else{
				saveSitePolicy(sitePolicy);
			}
		}
		return true;
	}
	
	
	private void saveSitePolicy(final SitePolicy sitePolicy) {
		if (sitePolicy == null)
			return;
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				int i = 0;
				PreparedStatement ps = conn
						.prepareStatement(createSitePolicySQL);
				ps.setInt(++i, sitePolicy.getSiteId());
				ps.setString(++i, sitePolicy.getPolicy());
				return ps;
			}
		});
	}
	
	public SitePolicy getSitePolicy(int siteId) {
		SitePolicy result = null;
		List<SitePolicy> sitePolicys = new ArrayList<SitePolicy>();
		sitePolicys = getJdbcTemplate().query(findSitePolicyBySiteId,new Object[]{siteId},
				new SitePolicyRowMapper());
		if (sitePolicys != null && sitePolicys.size() > 0) {
			result = sitePolicys.get(0);
		}
		return result;
	}
	
	public void updateSitePolicy(final SitePolicy sitePolicy) {
		getJdbcTemplate().update(updateSitePolicy,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int i = 0;
						ps.setInt(++i, sitePolicy.getSiteId());
						ps.setString(++i, sitePolicy.getPolicy());
						ps.setInt(++i, sitePolicy.getId());
					}
				});
	}
	
	
	
}
