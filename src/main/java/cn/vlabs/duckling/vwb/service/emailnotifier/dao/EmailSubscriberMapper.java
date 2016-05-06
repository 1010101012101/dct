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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;

/**
 * Introduction Here.
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class EmailSubscriberMapper implements RowMapper<EmailSubscriber> {
	public EmailSubscriber mapRow(ResultSet rs, int index) throws SQLException {
	       EmailSubscriber eSubscriber = new EmailSubscriber();
	        eSubscriber.setId(rs.getInt("id"));
	        eSubscriber.setNotify_creator(rs.getString("subscriber"));
	        String title = rs.getString("title");
	        if(title==null)
	        	title="*";
	        eSubscriber.setPageTitle(title);
	        eSubscriber.setRec_time(rs.getInt("rec_time"));
	        eSubscriber.setReceiver(rs.getString("receiver"));
	        eSubscriber.setresourceId(rs.getString("resourceId"));
	        return eSubscriber;
	}	
}
