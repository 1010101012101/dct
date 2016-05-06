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

package cn.vlabs.duckling.vwb.service.share.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.share.AccessRecord;

/**
 * @date May 24, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class SharePageAccessProviderImpl extends ContainerBaseDAO implements
		SharePageAccessProvider {

	protected static final Logger log = Logger
			.getLogger(SharePageAccessProviderImpl.class);

	private static final String insert = "insert into vwb_share_acl(hash, URL, accessTime, shareTime) values(?, ?, ?, ?)";
	private static final String update = "update vwb_share_acl set hash = ?, URL = ?, accessTime = ?, shareTime = ? where ID = ?";
	private static final String select = "select * from vwb_share_acl where ID = ?";

	public int add(final AccessRecord ar) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				int i=0;
				PreparedStatement pst = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
				pst.setString(++i, ar.gethash());
				pst.setString(++i, ar.getURL());
				pst.setLong(++i, ar.getAccessTime());
				pst.setLong(++i, ar.getShareTime());
				return pst;
			}
			
		},keyHolder);
		return keyHolder.getKey().intValue();
	}

	public void update(AccessRecord ar) {
		int ID = ar.getID();
		String hash = ar.gethash();
		String URL = ar.getURL();
		long accessTime = ar.getAccessTime();
		long shareTime = ar.getShareTime();

		getJdbcTemplate().update(update,
				new Object[] { ID, hash, URL, accessTime, shareTime });
	}

	public AccessRecord get(int ID) {
		List<AccessRecord> accessRecords = getJdbcTemplate()
				.query(select, new Object[] { ID },
						new SharePageAccessMapper());

		if (accessRecords.size() == 0)
			return null;
		else
			return accessRecords.get(0);
	}

}
