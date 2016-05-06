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

package cn.vlabs.duckling.vwb.service.dpage.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.dpage.data.DPageNodeInfo;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.dpage.data.SearchResult;
import cn.vlabs.duckling.vwb.service.viewport.ResourceConstants;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author euniverse
 */
public class DPageProviderImpl extends ContainerBaseDAO implements
		DPageProvider {
	protected static final Logger log = Logger
			.getLogger(DPageProviderImpl.class);
	private static final String insertDPage = "insert into vwb_dpage_content_info "
			+ "(resourceid,siteId, version,change_time,content,change_by,title) "
			+ " values(?,?,?,?,?,?,?)";
	private static final String deleteDPage = "delete from vwb_dpage_content_info where resourceid=? and siteId=?";

	public DPagePo create(DPagePo dpage) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final DPagePo tempDpage = dpage;
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(insertDPage,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(++i, tempDpage.getResourceId());
				ps.setInt(++i, tempDpage.getSiteId());
				ps.setInt(++i, tempDpage.getVersion());
				ps.setTimestamp(++i, new Timestamp(tempDpage.getTime()
						.getTime()));
				ps.setString(++i, tempDpage.getContent());
				ps.setString(++i, tempDpage.getCreator());
				ps.setString(++i, tempDpage.getTitle());
				return ps;
			}
		}, keyHolder);
		dpage.setId(Integer.valueOf(keyHolder.getKey().intValue()));
		return dpage;
	}

	public void deleteByResourceId(int siteId, int resourceId) {
		getJdbcTemplate().update(deleteDPage,
				new Object[] { resourceId, siteId });
	}

	public void deleteByResourceId(int siteId, int[] resourceIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from vwb_dpage_content_info where siteId=" + siteId
				+ " and resourceid in (0");
		for (int resourceId : resourceIds) {
			sql.append(",").append(resourceId);
		}
		sql.append(")");
		getJdbcTemplate().update(sql.toString());
	}

	public List<DPagePo> getHistoryByResourceId(int siteId, int resourceId) {
		String sql = "select * from vwb_dpage_content_info where resourceid=? and siteId=? order by version desc";
		List<DPagePo> dpages = getJdbcTemplate().query(sql,
				new Object[] { resourceId, siteId }, new DPageMapper());
		return dpages;
	}

	public List<DPagePo> getHistoryByResourceId(int siteId, int resourceId,
			int offset, int pageSize) {
		String sql = "select resourceid,version,change_time,change_by,title,length(content) size from vwb_dpage_content_info where siteId=? and resourceid=?  order by version asc limit ?,?";
		List<DPagePo> dpages = getJdbcTemplate().query(sql,
				new Object[] { siteId, resourceId, offset, pageSize },
				new RowMapper<DPagePo>() {
					public DPagePo mapRow(ResultSet rs, int index)
							throws SQLException {
						DPagePo dpage = new DPagePo();
						dpage.setResourceId(rs.getInt("resourceid"));
						dpage.setVersion(rs.getInt("version"));
						dpage.setTime(rs.getTimestamp("change_time"));
						dpage.setCreator(rs.getString("change_by"));
						dpage.setTitle(rs.getString("title"));
						dpage.setSize(rs.getInt("size"));
						return dpage;
					}
				});
		return dpages;
	}

	public DPagePo getLatestByResourceId(int siteId, int resourceId) {
		String sql = "SELECT * FROM vwb_dpage_content_info where siteId=? and resourceid=? order by version desc limit 1";
		List<DPagePo> dpages = getJdbcTemplate().query(sql,
				new Object[] { siteId, resourceId }, new DPageMapper());
		if (dpages.size() > 0) {
			return dpages.get(0);
		} else {
			return null;
		}
	}

	public List<DPagePo> getLatestByResourceIds(int siteId, int[] resourceIds) {
		StringBuffer in = new StringBuffer();
		in.append("0");
		for (int resourceId : resourceIds) {
			in.append(",").append(resourceId);
		}
		String sql = "SELECT a.* FROM vwb_dpage_content_info a, (select resourceid,count(*) as version from vwb_dpage_content_info where siteId="
				+ siteId
				+ " and resourceid in("
				+ in.toString()
				+ ") group by resourceid ) b where a.resourceid=b.resourceid and a.version=b.version";
		return getJdbcTemplate().query(sql, new DPageMapper());
	}

	public void update(DPagePo dpage) {
		final DPagePo dpageTemp = dpage;
		getJdbcTemplate()
				.update("update vwb_dpage_content_info set change_time=?,content=?,change_by=? ,title=? where siteId=? and resourceid=? and version=?",
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement ps)
									throws SQLException {
								int i = 0;
								ps.setTimestamp(++i, new Timestamp(dpageTemp
										.getTime().getTime()));
								ps.setString(++i, dpageTemp.getContent());
								ps.setString(++i, dpageTemp.getCreator());
								ps.setString(++i, dpageTemp.getTitle());
								ps.setInt(++i, dpageTemp.getSiteId());
								ps.setInt(++i, dpageTemp.getResourceId());
								ps.setInt(++i, dpageTemp.getVersion());
							}
						});
	}

	public int getDpageCount(int siteId) {
		return getJdbcTemplate()
				.queryForInt(
						"select count(*) from vwb_dpage_content_info where siteId=? group by resourceid",
						new Object[] { siteId });
	}

	public List<LightDPage> getDpagesSinceDate(int siteId, Date date) {
		String sql = "SELECT a.id,a.resourceid,a.version,a.change_time,a.change_by,a.title FROM vwb_dpage_content_info a, (select resourceid,max(version) as version from vwb_dpage_content_info  where siteId=? group by resourceid ) b where a.siteId=? and a.resourceid=b.resourceid and a.version=b.version and change_time>?";
		return getJdbcTemplate().query(sql,
				new Object[] { siteId, siteId, date }, new LightDPageMapper());
	}

	public DPagePo getVersionContent(int siteId, int resourceId, int version) {
		String sql = "SELECT * FROM vwb_dpage_content_info where siteId=? and resourceid=? and version=?";
		List<DPagePo> dpages = getJdbcTemplate()
				.query(sql, new Object[] { siteId, resourceId, version },
						new DPageMapper());
		if (dpages.size() > 0) {
			return dpages.get(0);
		} else {
			return null;
		}
	}

	public boolean isDpageExist(int siteId, int resourceId) {
		return getJdbcTemplate()
				.queryForInt(
						"select count(*) from vwb_dpage_content_info where siteId=? and resourceid=?",
						new Object[] { siteId, resourceId }) > 0;
	}

	public List<DPageNodeInfo> getRootPages(int siteId) {
		return getSubPages(siteId, 0);
	}

	// TODO 这里的逻辑需要整理
	public List<DPageNodeInfo> getSubPages(int siteId, int resourceId) {
		final Map<Integer, DPageNodeInfo> dpagemap = new TreeMap<Integer, DPageNodeInfo>();
		String sql = "SELECT a.id,a.resourceid,a.version,a.change_time,a.change_by,a.title FROM vwb_dpage_content_info a, (select resourceId,count(*) as version from vwb_dpage_content_info where siteId=? group by resourceid ) b where a.siteId=? and a.resourceId in (select id from vwb_resource_info where siteId=? and id!="
				+ ResourceConstants.ROOT
				+ " and parent="
				+ resourceId
				+ " and type='DPage') and a.resourceid=b.resourceid and a.version=b.version order by a.id";
		List<LightDPage> dpages = getJdbcTemplate()
				.query(sql, new Object[] { siteId, siteId, siteId },
						new LightDPageMapper());
		StringBuffer in = new StringBuffer();
		in.append("(0");
		for (LightDPage dpage : dpages) {
			in.append(",").append(dpage.getResourceId());
			DPageNodeInfo info = new DPageNodeInfo();
			info.setAuthor(dpage.getAuthor());
			info.setDate(dpage.getTime());
			info.setResourceId(dpage.getResourceId());
			info.setTitle(dpage.getTitle());
			// info.setPageorder(dpage.getPageorder());
			dpagemap.put(Integer.valueOf(dpage.getResourceId()), info);
		}
		in.append(")");
		dpages = null;
		sql = "SELECT distinct parent FROM vwb_resource_info where siteId=? and parent!=0 and parent in "
				+ in.toString();
		getJdbcTemplate().query(sql, new Object[] { siteId }, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				Integer resourceId = Integer.valueOf(rs.getInt("parent"));
				dpagemap.get(resourceId).setSubPages(true);
				return resourceId;
			}
		});
		return new ArrayList<DPageNodeInfo>(dpagemap.values());
	}

	/**
	 * 根据查询条件，查找资源的下级资源 searchedConditions key参数 begindate 查找从指定日期起的资源
	 * begindate='2007-01-01 01:01:01' enddate 查找到指定日期的资源 enddate='2010-01-01
	 * 01:01:01' prefix 资源名字的前缀 prefix=m suffix 资源名字的后缀 suffix=n user 资源创建人
	 * user='fred@cnic.cn' pageSize 查找结果每页个数 pageSize=20 currentPage 查看第几页
	 * currentPage=2
	 */
	public SearchResult searchSubDpages(int siteId, int resourceId,
			Map<String, Object> searchedConditions) {

		final StringBuffer WHERESQL = new StringBuffer();
		final StringBuffer LIMIT = new StringBuffer();
		final String selectCountRow = "select found_rows() as rowsize";
		final String selectPages = "select SQL_CALC_FOUND_ROWS d.resourceId, r.title, max(d.change_time) change_time ";
		final String from = " from vwb_resource_info r inner join vwb_dpage_content_info d on r.id=d.resourceId ";
		WHERESQL.append(" where r.siteId=? and d.siteId=? and r.parent=? ");
		StringBuffer nameCondtion = new StringBuffer();
		nameCondtion
				.append(searchedConditions.get("prefix") != null ? searchedConditions
						.get("prefix") : "");
		nameCondtion.append("%");
		nameCondtion
				.append(searchedConditions.get("suffix") != null ? searchedConditions
						.get("suffix") : "");
		// start merge sql
		if (nameCondtion.length() > 1) {
			WHERESQL.append("and r.title like ? ");
		}
		if (searchedConditions.get("beginDate") != null
				&& searchedConditions.get("endDate") != null) {
			WHERESQL.append(" and d.change_time between ? and ? ");
		}
		if (searchedConditions.get("user") != null) {
			WHERESQL.append("and d.change_by like ? ");
		}
		WHERESQL.append(" group by d.resourceId order by change_time desc ");
		int iCurrentPage = 1;
		int iPageSize = 100;
		int totalCounts = 20;
		try {
			String currentPage = (String) searchedConditions.get("currentPage");
			if (currentPage != null && !currentPage.trim().equals("")) {
				iCurrentPage = Integer.parseInt(currentPage);
			}
			String pageSize = (String) searchedConditions.get("pageSize");
			if (pageSize != null && !pageSize.trim().equals("")) {
				iPageSize = Integer.parseInt(pageSize);
			}
			LIMIT.append(" limit " + ((iCurrentPage - 1) * iPageSize) + " ,"
					+ iPageSize);
		} catch (Throwable e) {
			log.error("Error:when parsing then currentPage or pageSize.");
		}
		Connection connection = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		boolean aotoCommit = true;
		PreparedStatement pstmt = null;
		final List<DPagePo> dpages = new ArrayList<DPagePo>();
		try {
			connection = getJdbcTemplate().getDataSource().getConnection();
			// 保证rs1,rs2的结果在同一事物中，关闭自动提交
			aotoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(selectPages + from
					+ WHERESQL.toString() + LIMIT.toString());
			int index = 1;
			pstmt.setInt(index, siteId);
			index++;
			pstmt.setInt(index, siteId);
			index++;
			pstmt.setInt(index, resourceId);
			index++;
			if (nameCondtion.length() > 1) {
				pstmt.setString(index, nameCondtion.toString());
				index++;
				nameCondtion.delete(0, nameCondtion.length() - 1);
			}
			if (searchedConditions.get("beginDate") != null
					&& searchedConditions.get("endDate") != null) {
				pstmt.setTimestamp(index, new Timestamp(
						((Date) searchedConditions.get("beginDate")).getTime()));
				index++;
				pstmt.setTimestamp(index, new Timestamp(
						((Date) searchedConditions.get("endDate")).getTime()));
				index++;
			}
			if (searchedConditions.get("user") != null) {
				pstmt.setString(index,
						"%" + (String) searchedConditions.get("user") + "%");
			}

			rs1 = pstmt.executeQuery();
			// rs1,rs2在同一事务中执行
			pstmt = connection.prepareStatement(selectCountRow);
			rs2 = pstmt.executeQuery();
			connection.commit();
			while (rs1.next()) {
				// DPagePo page = new DPagePo(m_WikiEngine, rs1.getString(
				// "page_name" ) );
				DPagePo page = new DPagePo();
				page.setResourceId(rs1.getInt("resourceId"));
				page.setTitle(rs1.getString("title"));
				page.setTime(new java.util.Date(rs1.getTimestamp("change_time")
						.getTime()));
				dpages.add(page);
			}
			if (rs2.next()) {
				totalCounts = rs2.getInt("rowsize");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error:when get the sub page of " + resourceId);
		} finally {
			try {
				connection.setAutoCommit(aotoCommit);
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException ex) {
				log.error("close pstmt error");
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				log.error("close connection error");
			}
		}
		return new SearchResult(dpages, totalCounts, iCurrentPage, iPageSize);

	}

	/**
	 * 查找参数 begindate 查找从指定日期起更新的页面 begindate='2007-01-01 01:01:01' enddate
	 * 查找从指定日期起更新的页面 enddate='2010-01-01 01:01:01' prefix 页面名字的前缀 prefix=m
	 * suffix 页面名字的后缀 suffix=n user 页面修改人 user='fred@cnic.cn' count 查找结果个数
	 * count=20 operation 区分用户创建、编辑等动作 operation=CREATE or EDIT
	 */
	public List<DPagePo> searchPages(int siteId,
			Map<String, Object> searchedConditions) {
		final StringBuffer SQL = new StringBuffer();
		SQL.append("select resourceId, title, max(change_time) change_time ,change_by from vwb_dpage_content_info where siteId="
				+ siteId + " ");
		StringBuffer titleCondtion = new StringBuffer();
		titleCondtion
				.append(searchedConditions.get("prefix") != null ? searchedConditions
						.get("prefix") : "");
		titleCondtion.append("%");
		titleCondtion
				.append(searchedConditions.get("suffix") != null ? searchedConditions
						.get("suffix") : "");
		// start merge sql
		if (titleCondtion.length() > 1) {
			SQL.append("and title like ? ");
		}
		if (searchedConditions.get("beginDate") != null
				&& searchedConditions.get("endDate") != null) {
			SQL.append("and change_time between ? and ? ");
		}
		if (searchedConditions.get("user") != null) {
			SQL.append("and change_by like ? ");
		}
		String operation = (String) searchedConditions.get("operation");
		if ("CREATE".equals(operation)) {
			// 只取第一版本的页面，既用户创建的页面
			SQL.append(" and version=1 ");
		} else if ("EDIT".equals(operation)) {
			// 只取大于第一版本的页面，既用户编辑的页面
			SQL.append(" and version>1 ");
		}
		SQL.append(" group by resourceId order by change_time desc ");
		if (searchedConditions.get("count") != null) {
			SQL.append("limit 0," + (Integer) searchedConditions.get("count"));
		}
		// end merge sql
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		final List<DPagePo> dpages = new ArrayList<DPagePo>();
		try {
			connection = getJdbcTemplate().getDataSource().getConnection();
			pstmt = connection.prepareStatement(SQL.toString());
			int index = 1;
			if (titleCondtion.length() > 1) {
				pstmt.setString(index, titleCondtion.toString());
				index++;
				titleCondtion.delete(0, titleCondtion.length() - 1);
			}
			if (searchedConditions.get("beginDate") != null
					&& searchedConditions.get("endDate") != null) {
				pstmt.setTimestamp(index, new Timestamp(
						((Date) searchedConditions.get("beginDate")).getTime()));
				index++;
				pstmt.setTimestamp(index, new Timestamp(
						((Date) searchedConditions.get("endDate")).getTime()));
				index++;
			}
			if (searchedConditions.get("user") != null) {
				pstmt.setString(index,
						"%" + (String) searchedConditions.get("user") + "%");
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				DPagePo page = new DPagePo();
				page.setResourceId(rs.getInt("resourceId"));
				page.setTitle(rs.getString("title"));
				page.setTime(new java.util.Date(rs.getTimestamp("change_time")
						.getTime()));
				page.setCreator(rs.getString("change_by"));
				dpages.add(page);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException ex) {
				log.error("close pstmt error");
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				log.error("close connection error");
			}
		}
		return dpages;
	}

	public List<LightDPage> searchDpageByTitle(int siteId, String title) {
		String sql = "SELECT id,resourceId,max(version) version,change_time,change_by,title FROM vwb_dpage_content_info where siteId=? and title like ? group by resourceId";
		return getJdbcTemplate().query(sql,
				new Object[] { siteId, "%" + title + "%" },
				new LightDPageMapper());
	}

	public List<LightDPage> getAllPages(int siteId) {
		String sql = "SELECT a.id,a.resourceId,a.version,a.change_time,a.change_by,a.title  FROM vwb_dpage_content_info a, (select resourceid,max(version) as version from vwb_dpage_content_info where siteId=?  group by resourceid ) b where a.siteId=? and a.resourceid=b.resourceid and a.version=b.version";
		return getJdbcTemplate().query(sql, new Object[] { siteId, siteId },
				new LightDPageMapper());
	}

	public List<DPagePo> getAllWeightPages(int siteId) {
		String sql = "SELECT a.siteId,a.id,a.resourceId,a.version,a.change_time,a.change_by,a.title,a.content FROM vwb_dpage_content_info a, (select resourceid,siteId, max(version) as version from vwb_dpage_content_info where siteId=? group by resourceid ) b where a.siteId=? and a.resourceid=b.resourceid and a.version=b.version";
		return getJdbcTemplate().query(sql, new Object[] { siteId, siteId },
				new DPageMapper());
	}
}
