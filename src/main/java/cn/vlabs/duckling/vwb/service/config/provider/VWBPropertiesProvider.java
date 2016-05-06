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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import cn.vlabs.duckling.util.ContainerBaseDAO;

/**
 * Introduction Here.
 * 
 * @date 2010-5-6
 * @author dylan
 */
public class VWBPropertiesProvider extends ContainerBaseDAO {
	/*
	 * 删除VWB属性（暂时保留 但是不提供接口）
	 */
	private static final String deleteSql = "delete from vwb_properties where iSiteNum=? and strName=?";
	/*
	 * 插入VWB属性
	 */
	private static final String insertSql = "insert into vwb_properties (strName,strValue,iSiteNum) values(?,?,?)";
	private static final String queryByPrefix = "SELECT * FROM vwb_properties where iSiteNum=?  and strName like ?";
	/*
	 * 根据strName得到VWBProperties
	 */
	private static final String selectByName = "SELECT strValue FROM vwb_properties where strName=? and iSiteNum=?";

	private static final String sql = "select * from vwb_properties where iSiteNum=?";
	/*
	 * 修改VWB属性
	 */
	private static final String updateSql = "update vwb_properties set strValue=? where iSiteNum=? and strName=? ";

	public String getProperty(int siteId, String strName) {
		return getJdbcTemplate().query(selectByName,
				new Object[] { strName, siteId },
				new ResultSetExtractor<String>() {
					public String extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next())
							return rs.getString(1);
						else
							return null;
					}
				});
	}

	public Map<String, String> getPropertyStartWith(int siteId, String prefix) {
		Map<String, String> map = (Map<String, String>) getJdbcTemplate()
				.query(queryByPrefix, new Object[] { siteId, prefix+"%" },
						new ResultSetExtractor<Map<String, String>>() {
							public Map<String, String> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								HashMap<String, String> props = new HashMap<String, String>();
								while (rs.next()) {
									String value = rs.getString("strValue");
									if (value == null)
										value = "";
									props.put(rs.getString("strName"), value);
								}
								return props;
							}
						});
		return map;
	}

	public void insertProperty(final int siteId, final Properties props) {

		getJdbcTemplate().batchUpdate(insertSql,
				new BatchPreparedStatementSetter() {
					private Enumeration<?> iter = props.propertyNames();

					public int getBatchSize() {
						return props.size();
					}

					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						String key = (String) iter.nextElement();
						String value = props.getProperty(key);
						ps.setString(1, key);
						ps.setString(2, value);
						ps.setInt(3, siteId);
					}

				});
	}

	public Properties readAllProperties(int siteId) {
		return (Properties) getJdbcTemplate().query(sql,
				new Object[] { siteId }, new ResultSetExtractor<Properties>() {
					public Properties extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Properties prop = new Properties();
						while (rs.next()) {
							String value = rs.getString("strValue");
							if (value == null)
								value = "";
							prop.setProperty(rs.getString("strName"), value);
						}
						return prop;
					}
				});
	}

	public void removeProperty(int siteId, String key) {
		getJdbcTemplate().update(deleteSql, new Object[] { siteId, key });
	}

	public void updataProperty(int siteId, String key, String value) {
		getJdbcTemplate()
				.update(updateSql, new Object[] { value, siteId, key });
	}

	public void insertProperty(int siteId, String key, String value) {
		getJdbcTemplate()
				.update(insertSql, new Object[] { key, value, siteId });
	}

	public void updateVWBPpt(final int siteId, final Properties updateSet) {
		getJdbcTemplate().batchUpdate(updateSql,
				new BatchPreparedStatementSetter() {
					private Enumeration<?> iter = updateSet.propertyNames();

					public int getBatchSize() {
						return updateSet.size();
					}

					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						String key = (String) iter.nextElement();
						String value = updateSet.getProperty(key);
						ps.setString(1, value);
						ps.setInt(2, siteId);
						ps.setString(3, key);
					}
				});
	}
}
