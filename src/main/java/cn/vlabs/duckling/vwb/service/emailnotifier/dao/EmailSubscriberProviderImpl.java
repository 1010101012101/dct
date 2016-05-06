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

package cn.vlabs.duckling.vwb.service.emailnotifier.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;
import cn.vlabs.duckling.vwb.service.emailnotifier.impl.EmailSubscriberProvider;

/**
 * Introduction Here.
 * 
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class EmailSubscriberProviderImpl extends ContainerBaseDAO implements
		EmailSubscriberProvider {
	protected static final Logger log = Logger
			.getLogger(EmailSubscriberProviderImpl.class);
	private Map<Integer, EmailSubscriber> EmailSubscriberCache = new HashMap<Integer, EmailSubscriber>();

	@Override
	public void createEmailSubscriber(final int siteId,
			List<EmailSubscriber> subscribers) {

		final String sql = "insert into vwb_email_notify (subscriber,receiver,rec_time,siteId,resourceId) values(?,?,?,?,?)";
		final List<EmailSubscriber> tsubscribers = subscribers;
		getJdbcTemplate().batchUpdate(sql,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return tsubscribers.size();
					}

					public void setValues(PreparedStatement ps, int count)
							throws SQLException {
						EmailSubscriber email = (EmailSubscriber) tsubscribers
								.get(count);
						int i = 0;
						ps.setString(++i, email.getNotify_creator());
						ps.setString(++i, email.getReceiver());
						ps.setInt(++i, email.getRec_time());
						ps.setInt(++i, siteId);
						ps.setString(++i, email.getresourceId());
					}
				});
	}

	@Override
	public void delete(int id) {
		getJdbcTemplate().update(
				"delete from vwb_email_notify where id=" + id);
		EmailSubscriberCache.remove(id);
	}

	@Override
	public List<EmailSubscriber> findEmailSubscribers(int siteId,
			String receiver, String pageName, int rec_time) {
		ArrayList<String> conditions = new ArrayList<String>();
		if (!StringUtils.isBlank(receiver)) {
			conditions.add(" receiver like'%" + receiver + "%' ");
		}

		if (!StringUtils.isBlank(pageName)) {
			if (!pageName.equals("*")) {
				conditions.add(" title like'%" + pageName + "%' ");
			}
		}
		if (rec_time != -1) {
			conditions.add(" rec_time='" + rec_time + "' ");
		}
		String wherestr = "";
		boolean first = true;
		for (String cond : conditions) {
			if (first) {
				wherestr = " where " + cond;
				first = false;
			} else {
				wherestr = wherestr + " and " + cond;
			}
		}
		String sql = "select a.*, b.title from vwb_email_notify as a LEFT JOIN vwb_resource_info as b on a.resourceId=b.id and a.siteId=? and b.siteId=? "
				+ wherestr;

		return (List<EmailSubscriber>) getJdbcTemplate().query(sql,
				new Object[] { siteId, siteId }, new EmailSubscriberMapper());
	}

	@Override
	public List<EmailSubscriber> getAllEmailSubscribers(int siteId) {
		String sql = "select a.*, b.title from vwb_email_notify a, vwb_resource_info b where a.resourceId=b.id and a.siteId=? and b.siteId=?";
		return (List<EmailSubscriber>) getJdbcTemplate().query(sql,
				new Object[] { siteId, siteId }, new EmailSubscriberMapper());
	}

	@Override
	public EmailSubscriber getEmailSubscriberById(int siteId, int id) {
		EmailSubscriber eSubscriber = null;
		String sql = "select a.*, b.title" +
				" from vwb_email_notify as a LEFT JOIN vwb_resource_info as b on a.resourceId=b.id and a.siteId=? and b.siteId=?" +
				" where a.id=?";
		eSubscriber = (EmailSubscriber) getJdbcTemplate().queryForObject(
				sql, new Object[] { siteId, siteId, id },
				new EmailSubscriberMapper());
		return eSubscriber;
	}

	@Override
	public void removeEmailSubscribers(int id) {
		String sql = "delete from vwb_email_notify where id =?";
		getJdbcTemplate().update(sql, new Object[] { id });
	}

	@Override
	public void updateEmailSubscriber(final int siteId,
			EmailSubscriber eSubscriber) {
		final EmailSubscriber tempeSubscriber = eSubscriber;
		getJdbcTemplate()
				.update("update vwb_email_notify set subscriber=?,receiver=?,rec_time=?,resourceId=?,siteId=? where id=?",
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement ps)
									throws SQLException {
								int i = 0;
								ps.setString(++i,
										tempeSubscriber.getNotify_creator());
								ps.setString(++i, tempeSubscriber.getReceiver());
								ps.setInt(++i, tempeSubscriber.getRec_time());
								ps.setString(++i,
										tempeSubscriber.getresourceId());
								ps.setInt(++i, siteId);
								ps.setInt(++i, tempeSubscriber.getId());

							}
						});
	}

	private static final String existSql = "select count(*) from vwb_email_notify where receiver=? and rec_time=? and siteId=? and resourceId=?";
	@Override
	public boolean isSubscribeExist(int siteId,EmailSubscriber sub) {
		int count = getJdbcTemplate().queryForInt(
				existSql,
				new Object[] { sub.getReceiver(), sub.getRec_time(),siteId,
						sub.getresourceId() });
		return count != 0;
	}

	private static final String querySubscribeByTime = "select a.*, b.title "
			+ "from vwb_email_notify as a LEFT JOIN vwb_resource_info as b on a.resourceId=b.id and a.siteId=? and b.siteId=? "
			+ "where rec_time=?";

	@Override
	public List<EmailSubscriber> findReceiveAt(int siteId, int receiveTime) {
		String sql = querySubscribeByTime;
		List<EmailSubscriber> eSubscribers = (List<EmailSubscriber>) getJdbcTemplate()
				.query(sql, new Object[] { siteId, siteId,receiveTime },
						new EmailSubscriberMapper());
		return eSubscribers;
	}

	private static final String querySubscribeByResourceId = "select a.*, b.title "
			+ "from vwb_email_notify as a LEFT JOIN vwb_resource_info as b on a.resourceId=b.id and a.siteId=? and b.siteId=? "
			+ "where resourceid=? or resourceid='*'";

	@Override
	public List<EmailSubscriber> findPageSubscribes(int siteId, int resourceId) {
		String sql = querySubscribeByResourceId;
		List<EmailSubscriber> eSubscribers = (List<EmailSubscriber>) getJdbcTemplate()
				.query(sql, new Object[] {siteId, siteId, resourceId },
						new EmailSubscriberMapper());
		return eSubscribers;
	}

	private static final String querySubscribeByReceiver = "select a.*, b.title "
			+ "from vwb_email_notify as a LEFT JOIN vwb_resource_info as b on a.resourceId=b.id and a.siteId=? and b.siteId=? "
			+ "where receiver=?";

	@Override
	public List<EmailSubscriber> findUserSubScribe(int siteId,String username) {
		String sql = querySubscribeByReceiver;
		List<EmailSubscriber> eSubscribers = (List<EmailSubscriber>) getJdbcTemplate()
				.query(sql, new Object[] {siteId, siteId, username },
						new EmailSubscriberMapper());
		return eSubscribers;
	}
}
