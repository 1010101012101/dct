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

package cn.vlabs.duckling.vwb.service.idgen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.init.InitProvider;

/**
 * @date 2013-6-6
 * @author xiejj@cstnet.cn
 */
public class MyisamKeyGenerator extends ContainerBaseDAO implements
		IKeyGenerator, InitProvider {
	private IMaxIdReader reader;

	@Override
	public int getNextID(final int siteId) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = "insert into vwb_key_generate(siteId) values(?)";
				PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, siteId);
				return pst;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	public void setMaxIdReader(IMaxIdReader reader) {
		this.reader = reader;
	}

	@Override
	public boolean init(int siteId, Map<String, String> params,
			String templatePath, String defaultDataPath) throws IOException {
		int maxId = reader.getMaxId(siteId);
		String sql = "insert into vwb_key_generate(id, siteId) values(?,?)";
		getJdbcTemplate().update(sql, maxId, siteId);
		return true;
	}
}
