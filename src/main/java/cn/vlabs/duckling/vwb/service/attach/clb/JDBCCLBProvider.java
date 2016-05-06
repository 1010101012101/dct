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

package cn.vlabs.duckling.vwb.service.attach.clb;

import java.security.ProviderException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;

/**
 * Provides a database-based repository for DUCKLING CLB based on
 * JDBCAttachmentProvider.java. MySQL commands to create the tables are provided
 * in the code comments.
 * <p/>
 * 
 */
public class JDBCCLBProvider extends ContainerBaseDAO {
	private static final String sClbInsertSql = "INSERT INTO vwb_attach(siteId, resourceId, clbId, filename, change_time, length, change_by, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String sClbListChangedSql_BeginToEnd = "SELECT  * FROM vwb_attach WHERE siteId=? and change_time BETWEEN ? AND ? ORDER BY change_time DESC";
	private static final String sClbRecentModified = "select * from vwb_attach where siteId=? order by change_time desc limit ?";
	private static final String sClbSearch = "select * from vwb_attach where siteId=? and filename like ? order by change_time desc";

	protected static final Logger log = Logger
			.getLogger(JDBCCLBProvider.class);

	private static RowMapper<CLBAttachment> rowMapper = new RowMapper<CLBAttachment>() {
		public CLBAttachment mapRow(ResultSet rs, int indxt) throws SQLException {
			CLBAttachment att = new CLBAttachment(rs.getString("resourceid"),
					rs.getString("filename"), rs.getString("version"));
			att.setSiteId(rs.getInt("siteId"));
			att.setAuthor(rs.getString("change_by"));
			att.setTime(rs.getTimestamp("change_time"));
			att.setClbId(rs.getInt("clbId"));
			att.setLength(rs.getInt("length"));
			att.setCLBVersion(rs.getString("version"));
			return att;
		}
	};

	public String getProviderInfo() {
		return "JDBC attachment provider implementation.";
	}

	public void putCLBAttachmentData(final CLBAttachment att)
			throws ProviderException {
		getJdbcTemplate().update(
				sClbInsertSql,
				new Object[] { att.getSiteId(), att.getResourceId(), att.getClbId(),
						att.getFileName(),
						new java.sql.Timestamp(att.getTime().getTime()),
						att.getLength(), att.getAuthor(), att.getVersion(),
						});
	}

	public List<CLBAttachment> listAllChanged(final int siteId, final Date begin, final Date end)
			throws ProviderException {
		List<CLBAttachment> changedList = this.getJdbcTemplate().query(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(
							Connection conn) throws SQLException {
						PreparedStatement statement = conn.prepareStatement(sClbListChangedSql_BeginToEnd);
						int i = 0;
						statement.setInt(++i, siteId);
						statement.setTimestamp(++i, new java.sql.Timestamp(
								begin.getTime()));
						statement.setTimestamp(++i,
								new java.sql.Timestamp(end.getTime()));
						return statement;
					}
				},rowMapper);

		return changedList;
	}

	public List<CLBAttachment> findRecentUploaded(int siteId, int num) {
		return getJdbcTemplate().query(sClbRecentModified,
				new Object[] { siteId, num }, rowMapper);
	}

	public List<CLBAttachment> search(int siteId, String filename) {
		if (filename.indexOf('%')!=-1){
			filename=filename.replaceAll("%", "");
		}
		filename="%"+filename+"%";
		return getJdbcTemplate().query(sClbSearch,
				new Object[] {siteId, filename }, rowMapper);
	}
}