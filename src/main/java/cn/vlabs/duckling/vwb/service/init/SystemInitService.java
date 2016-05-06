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

package cn.vlabs.duckling.vwb.service.init;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.config.IContainerConfig;

/**
 * Introduction Here.
 * 
 * @date 2010-5-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SystemInitService extends ContainerBaseDAO {
	private final static Logger log = Logger.getLogger(SystemInitService.class);
	private String webRoot = null;
	private IContainerConfig config = null;

	public void setConfig(IContainerConfig config) {
		this.config = config;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public void initSystemData() {
		// 创建系统数据
		executeSqlFile(webRoot + "/WEB-INF/conf/dct.sql");
	}

	public boolean hasInital() {
		Connection conn = null;
		Connection conn1 = null;
		PreparedStatement pstmt = null;
		boolean hasInitial = false;
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			String ip = config.getProperty("database.ip", "localhost:3306");

			String user = config.getProperty("c3p0.username", "root");
			String password = config.getProperty("c3p0.password", "root");
			String url = "jdbc:mysql://" + ip
					+ "/?useUnicode=true&characterEncoding=utf8";
			conn = DriverManager.getConnection(url, user, password);
			String databaseName = config.getProperty("database", "duckling");
			pstmt = conn.prepareStatement("show databases");
			ResultSet rs = pstmt.executeQuery();
			boolean exist = false;
			while (rs.next()) {
				if (databaseName.equals(rs.getString(1))) {
					exist = true;
					break;
				}
			}
			// 创建数据库
			if (!exist) {
				pstmt = conn.prepareStatement("CREATE database IF NOT EXISTS "
						+ databaseName + " CHARACTER SET utf8");
				pstmt.execute();
			}

			if (checkTable("vwb_site")) {
				hasInitial = true;
			}
		} catch (Throwable e) {
			log.error("ifNotExistsCreateDataBase error", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
				if (conn1 != null) {
					conn1.close();
				}
			} catch (SQLException e) {
				log.error("close connection or pstmt error");
			}
		}
		return hasInitial;
	}

	private boolean executeSqlFile(String file) {
		boolean flag = false;
		SQLReader reader = null;
		String sql;
		try {
			reader = new SQLReader(new FileInputStream(file), "UTF-8");
			while ((sql = reader.next()) != null) {
				getJdbcTemplate().execute(sql);
			}
			flag = true;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException();
		} finally {
			if (reader != null)
				reader.close();
		}
		return flag;
	}

	private boolean checkTable(String tablename) {
		boolean found = false;
		try {
			int count = getJdbcTemplate().queryForInt(
					"select count(*) from " + tablename);
			if (count > 0) {
				found = true;
			}
		} catch (Throwable se) {

		}
		return found;
	}
}
