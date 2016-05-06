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

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigReader;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public abstract class AbstractViewPortInitProvider extends ContainerBaseDAO
		implements InitProvider {
	protected static Logger log = Logger.getLogger(PortalInitProvider.class);
	protected static final String SYS_USER_NAME = "System"; // The default creator.
	private ConfigReader cr = null;
	private String sCreateSql = "INSERT INTO vwb_resource_info(siteId,type,"
			+ "title,parent, trail,left_menu, top_menu,footer,banner,create_time,creator,acl)"
			+ " VALUES(?,?,?,?,  ?,?,?,?, ?,?,?,?)";
	private String sCreateSqlWithId = "INSERT INTO vwb_resource_info(id,siteId,type,"
			+ "title,parent,trail, left_menu, top_menu,footer,banner,create_time,creator, acl)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public AbstractViewPortInitProvider() {
		cr = new ConfigReader();
	}

	protected ViewPort convertToViewPort(ConfigItem item) {
		ViewPort vp = new ViewPort();
		vp.setId(item.getId());
		vp.setBanner(item.getBanner());
		vp.setCreateTime(Calendar.getInstance().getTime());
		vp.setCreator(SYS_USER_NAME);
		vp.setTrail(item.getTrail());
		vp.setFooter(item.getFooter());
		vp.setLeftMenu(item.getLeftmenu());
		vp.setParent(item.getParent());
		vp.setTitle(item.getTitle());
		vp.setTopMenu(item.getTopmenu());
		vp.setType(item.getType());
		return vp;
	}

	protected int create(final int siteId,final ViewPort vp) {
		GeneratedKeyHolder keyVP = new GeneratedKeyHolder();

		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				boolean bWithId = vp.getId() > 0;
				PreparedStatement statement = conn.prepareStatement(
						bWithId ? sCreateSqlWithId : sCreateSql,
						PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				if (bWithId) {
					statement.setInt(++i, vp.getId());
				}
				statement.setInt(++i, siteId);
				statement.setString(++i, vp.getType());
				statement.setString(++i, vp.getTitle());
				statement.setInt(++i, vp.getParent());
				statement.setInt(++i, vp.getTrail());
				statement.setInt(++i, vp.getLeftMenu());
				statement.setInt(++i, vp.getTopMenu());
				statement.setInt(++i, vp.getFooter());
				statement.setInt(++i, vp.getBanner());
				if (vp.getCreateTime() == null) {
					statement.setTimestamp(++i, new Timestamp(Calendar
							.getInstance().getTimeInMillis()));
				} else {
					statement.setTimestamp(++i, new Timestamp(vp
							.getCreateTime().getTime()));
				}
				statement.setString(++i, vp.getCreator());
				statement.setInt(++i, vp.getAclPolicy());
				return statement;
			}
		}, keyVP);
		vp.setId(keyVP.getKey().intValue());
		return vp.getId();
	}

	protected List<ConfigItem> initItemFromXML(String filePath)
			throws IOException {
		FileInputStream fisDPageConf = null;
		List<ConfigItem> items = null;

		try {
			fisDPageConf = new FileInputStream(filePath);
			items = cr.fromXML(fisDPageConf);
		} catch (IOException ioex) {
			log.error("配置文件读取错误：" + filePath);
			throw ioex;
		}
		return items;
	}
}
