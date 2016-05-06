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

package cn.vlabs.duckling.vwb.service.site;

import java.io.Serializable;
import java.util.Date;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteMetaInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private SiteState state;
	/**
	 * 暂存属性，不在数据库表vwb_site中
	 */
	private String umtVo;
	/**
	 * 暂存属性，不在数据库表vwb_site中
	 */
	private String siteName;
	private Date createTime;
	private PublishState published;

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the umtVo
	 */
	public String getUmtVo() {
		return umtVo;
	}

	/**
	 * @param umtVo
	 *            the umtVo to set
	 */
	public void setUmtVo(String umtVo) {
		this.umtVo = umtVo;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public boolean isWorking() {
		return SiteState.WORK.equals(state);
	}

	public boolean isHangup() {
		return SiteState.HANGUP.equals(state);
	}

	public boolean isPublished() {
		return PublishState.PUBLISHED.equals(published);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public SiteState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(SiteState state) {
		this.state = state;
	}

	/**
	 * @return
	 */
	public boolean isUnInit() {
		return false;
	}

	/**
	 * @return the published
	 */
	public PublishState getPublished() {
		return published;
	}

	/**
	 * @param published
	 *            the published to set
	 */
	public void setPublished(PublishState published) {
		this.published = published;
	}

}
