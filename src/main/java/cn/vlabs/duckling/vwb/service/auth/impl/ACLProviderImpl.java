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

package cn.vlabs.duckling.vwb.service.auth.impl;

import java.security.Permission;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.auth.permissions.PermissionFactory;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class ACLProviderImpl extends ContainerBaseDAO implements ACLProvider {
	protected static final Logger log = Logger.getLogger(ACLProviderImpl.class);
	private VWBCacheService cache;

	private Acl aclEntryPosToAcl(List<ACLEntryPo> entrys) {
		Acl acl = null;
		if (entrys != null && entrys.size() > 0) {
			acl = new Acl();
			for (ACLEntryPo po : entrys) {
				AclEntry entry = new AclEntry();
				if ("user".equals(po.getType())) {
					UserPrincipal up = new UserPrincipal(po.getEid(),
							po.getEid(), po.getType());
					entry.setPrincipal(up);
				} else if ("group".equals(po.getType())) {
					GroupPrincipal gp = new GroupPrincipal(po.getEid());
					entry.setPrincipal(gp);
				} else if ("role".equals(po.getType())) {
					Principal gp = new Role(po.getEid());
					entry.setPrincipal(gp);
				}
				entry.addPermission(PermissionFactory.createPermission(
						po.getResourceId(), po.getResourceType(),
						po.getAction()));
				acl.addEntry(entry);
			}
		}
		return acl;
	}

	private List<ACLEntryPo> aclToAclEntryPos(int siteId, int resourceId,
			Acl acl) {
		List<ACLEntryPo> pos = new ArrayList<ACLEntryPo>();
		for (Enumeration<AclEntry> e = acl.entries(); e.hasMoreElements();) {
			AclEntry entry = (AclEntry) e.nextElement();
			Principal principal = entry.getPrincipal();
			ACLEntryPo po = new ACLEntryPo();
			if (principal instanceof UserPrincipal) {
				po.setType("user");
			} else if (principal instanceof GroupPrincipal) {
				po.setType("group");
			} else if (principal instanceof Role) {
				po.setType("role");
			}
			po.setEid(principal.getName());
			StringBuffer actoins = new StringBuffer();
			for (Enumeration<Permission> ps = entry.permissions(); ps
					.hasMoreElements();) {
				Permission p = (Permission) ps.nextElement();
				actoins.append(p.getActions());
			}
			po.setAction(actoins.toString());
			po.setResourceType(PermissionFactory.getResourceType(acl));
			po.setResourceId(resourceId);
			po.setSiteid(siteId);
			pos.add(po);
		}
		return pos;
	}

	private ACLEntryPo create(ACLEntryPo po) {
		final String sql = "insert into vwb_resource_acl "
				+ "(siteId, resourceId,type,eid,action,resourceType) "
				+ " values(?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final ACLEntryPo entrypo = po;
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				int i = 0;
				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(++i, entrypo.getSiteId());
				ps.setInt(++i, entrypo.getResourceId());
				ps.setString(++i, entrypo.getType());
				ps.setString(++i, entrypo.getEid());
				ps.setString(++i, entrypo.getAction());
				ps.setString(++i, entrypo.getResourceType());
				return ps;
			}
		}, keyHolder);
		po.setId(Integer.valueOf(keyHolder.getKey().intValue()));
		return po;
	}

	private void delete(int siteId, int entryId, String eId) {
		getJdbcTemplate()
				.update("delete from vwb_resource_acl where siteId=? and resourceId=? and eid=?",
						new Object[] { siteId, entryId, eId });
	}

	private List<ACLEntryPo> getPos(int siteId, int resourceId) {
		String sql = "SELECT * FROM vwb_resource_acl where siteId=? and resourceId=?";
		return getJdbcTemplate().query(sql,
				new Object[] { siteId, resourceId }, new ACLEntryMapper());
	}

	private void update(ACLEntryPo po) {
		getJdbcTemplate()
				.update("update vwb_resource_acl set action=? where siteId=? and resourceId=? and eid=?",
						new Object[] { po.getAction(), po.getSiteId(),
								po.getResourceId(), po.getEid() });
	}

	public Acl getAcl(int siteId, int resourceId) {
		String key = String.valueOf(resourceId);
		Acl acl = (Acl) cache.getFromCache(siteId, key);
		if (acl == null) {
			acl = aclEntryPosToAcl(getPos(siteId, resourceId));
			cache.putInCache(siteId, key, acl);
		}
		;
		return acl;
	}

	public void removeAcl(int siteId, int resourceId) {
		getJdbcTemplate().update(
				"delete from vwb_resource_acl where siteId=? and resourceid=?",
				new Object[] { siteId, resourceId });
		cache.removeEntry(siteId, String.valueOf(resourceId));
	}

	public void setCache(VWBCacheService cache) {
		this.cache = cache;
		this.cache.setModulePrefix("acl");
	}

	public void updateAcl(int siteId, int resourceId, Acl acl) {
		Acl oldacl = this.getAcl(siteId, resourceId);
		List<ACLEntryPo> oldPos = null;
		if (oldacl != null) {
			oldPos = aclToAclEntryPos(siteId, resourceId, oldacl);
		}

		List<ACLEntryPo> pos = aclToAclEntryPos(siteId, resourceId, acl);
		List<ACLEntryPo> update = new ArrayList<ACLEntryPo>();
		List<ACLEntryPo> create = new ArrayList<ACLEntryPo>();
		List<ACLEntryPo> delete = new ArrayList<ACLEntryPo>();
		for (ACLEntryPo po : pos) {
			if (oldPos != null && oldPos.size() > 0) {
				for (int i = 0; i < oldPos.size(); i++) {
					ACLEntryPo oldPo = oldPos.get(i);
					if (oldPo.getEid().equals(po.getEid())) {
						update.add(po);
						oldPos.remove(i);
						break;
					}
					if (i + 1 == oldPos.size()) {
						create.add(po);
					}
				}
			} else {
				create.add(po);
			}
		}
		if (oldPos != null)
			delete.addAll(oldPos);
		for (ACLEntryPo po : delete) {
			delete(siteId, po.getResourceId(), po.getEid());
		}
		for (ACLEntryPo po : update) {
			update(po);
		}
		for (ACLEntryPo po : create) {
			create(po);
		}
		cache.removeEntry(siteId, Integer.toString(resourceId));
	}

}
