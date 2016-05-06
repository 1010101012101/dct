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

package cn.vlabs.duckling.vwb.service.auth.policy;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.impl.PolicyProvider;

/**
 * Introduction Here.
 * 
 * @date May 7, 2010
 * @author zzb
 */
public class PolicyProviderImpl extends ContainerBaseDAO implements PolicyProvider {
	private static final String createSitePolicySQL = "insert into vwb_policy (siteId,policy) values(?,?)";
	
	private static final String findSitePolicyBySiteId = "select policy from vwb_policy where siteId=?";
	private static final Logger log = Logger
			.getLogger(PolicyProviderImpl.class);
	private static final String updateSitePolicy = "update vwb_policy set policy=? where siteId=?";

	private String readPolicyContent(int siteId) {
		List<String> result = getJdbcTemplate().queryForList(findSitePolicyBySiteId,new Object[]{siteId},String.class);
		if (result.size()>0){
			return result.get(0);
		}else{
			return null;
		}
	}

	@Override
	public Acl getAcl(int siteId) {
		String aclString = readPolicyContent(siteId);
		BufferedReader br = new BufferedReader(new StringReader(aclString));
		AuthorizationFileParser afp = new AuthorizationFileParser(br);
		try {
			return afp.parseEntry();
		} catch (AuthorizationSyntaxParseException
				| AuthorizationLexicParseException e) {
			log.error("parsing policy content failed", e);
		} finally {
			afp.close();
		}
		return null;
	}
	
	@Override
	public void setAcl(int siteId, Acl acl) {
		String policyStr = PolicyUtil.acl2PolicyString(acl);
		if (readPolicyContent(siteId)!=null){
			getJdbcTemplate().update(updateSitePolicy,new Object[]{policyStr,siteId});
		}else{
			getJdbcTemplate().update(createSitePolicySQL, new Object[]{siteId, policyStr});
		}
	}
}
