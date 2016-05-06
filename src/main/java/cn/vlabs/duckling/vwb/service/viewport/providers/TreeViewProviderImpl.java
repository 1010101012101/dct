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

package cn.vlabs.duckling.vwb.service.viewport.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortType;
import cn.vlabs.duckling.vwb.service.viewport.impl.TreeViewProvider;

/**
 * Introduction Here.
 * 
 * @date 2010-2-4
 * @author euniverse
 */
public class TreeViewProviderImpl extends ContainerBaseDAO implements
		TreeViewProvider {
	private class InheritPart {
		private String m_sql;

		private ViewPortType m_type;

		public InheritPart(ViewPortType type, String procedure) {
			this.m_type = type;
			this.m_sql = "call " + procedure + "(?,?)";
		}

		public int getInherit(final int siteid, final int vid) {
			Integer result;
			final ViewPortParentRelation vpr = new ViewPortParentRelation();
			vpr.setChild(vid);
			getJdbcTemplate().execute(new ConnectionCallback<Integer>() {
				public Integer doInConnection(Connection con)
						throws SQLException, DataAccessException {
					PreparedStatement cst = con.prepareStatement(m_sql);
					cst.setInt(1, siteid);
					cst.setInt(2, vid);
					ResultSet rs = cst.executeQuery();
					if (rs.next()) {
						vpr.setParent(rs.getInt("ovid"));
						vpr.setGeneration(rs.getInt("generation"));
					}
					return null;
				}
			});
			result = vpr.getParent();
			return result.intValue();
		}

		public ViewPortType getType() {
			return m_type;
		}
	}

	private Map<ViewPortType, InheritPart> inheritPartMap = new HashMap<ViewPortType, InheritPart>();

	private void createCache(ViewPortType type, String procedure) {
		InheritPart part = new InheritPart(type, procedure);
		inheritPartMap.put(part.getType(), part);
	}

	@Override
	public int get(ViewPortType type, int siteId, int vid) {
		return inheritPartMap.get(type).getInherit(siteId, vid);
	}

	public void init() {
		createCache(ViewPortType.LEFT_MENU, "findleftmenu");
		createCache(ViewPortType.TOP_MENU, "findtopmenu");
		createCache(ViewPortType.BANNER, "findbanner");
		createCache(ViewPortType.FOOTER, "findfooter");
		createCache(ViewPortType.TRAIL, "findtrail");
		createCache(ViewPortType.ACL, "findacl");
	}
}
