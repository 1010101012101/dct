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

package cn.vlabs.duckling.vwb.service.viewport;

import java.io.Serializable;
import java.util.Date;

/**
 * 显示窗口
 * 
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public class ViewPort implements Cloneable,Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DISABLED = -1;

	public static final int ENABLED = 1;

	public static final int INHERIT = 0;

	public static boolean isInherit(int value) {
		return (value == INHERIT);
	}

	private int acl = INHERIT;

	private int banner = INHERIT;

	private Date createTime;

	private String creator;

	private int footer = INHERIT;

	private int id;

	private int leftMenu = INHERIT;

	private int parent = INHERIT;

	private int siteId;

	private String title;

	private int topMenu = INHERIT;

	private int trail = INHERIT;

	private String type;

	public Object clone() {
		ViewPort vp = new ViewPort();
		vp.setSiteId(siteId);
		vp.setTitle(title);
		vp.setBanner(banner);
		vp.setCreateTime(createTime);
		vp.setFooter(footer);
		vp.setLeftMenu(leftMenu);
		vp.setParent(parent);
		vp.setCreator(creator);
		vp.setTopMenu(topMenu);
		vp.setTrail(trail);
		vp.setId(id);
		vp.setType(type);
		vp.setAclPolicy(acl);
		return vp;
	}

	public int getAclPolicy() {
		return acl;
	}

	public int getBanner() {
		return banner;
	}

	public Date getCreateTime() {
		return (Date) createTime.clone();
	}

	public String getCreator() {
		return creator;
	}

	public int getFooter() {
		return footer;
	}

	public int getId() {
		return id;
	}

	public int getLeftMenu() {
		return leftMenu;
	}

	public int getParent() {
		return parent;
	}

	/**
	 * Brief Intro Here
	 * 
	 * @param
	 */
	public int getPart(ViewPortType part) {
		int result = DISABLED;
		switch (part) {
		case BANNER:
			result = banner;
			break;
		case FOOTER:
			result = footer;
			break;
		case LEFT_MENU:
			result = leftMenu;
			break;
		case TOP_MENU:
			result = topMenu;
			break;
		case TRAIL:
			result = trail;
			break;
		case ACL:
			result = acl;
		}

		return result;
	}

	public int getSiteId() {
		return siteId;
	}

	public String getTitle() {
		return title;
	}

	public int getTopMenu() {
		return topMenu;
	}

	public int getTrail() {
		return trail;
	}

	public String getType() {
		return type;
	}

	public boolean isAclInherit() {
		return acl == INHERIT;
	}

	public boolean isRoot() {
		return parent == 0;
	}

	public boolean isShowBanner() {
		return banner != DISABLED;
	}

	public boolean isShowFooter() {
		return footer != DISABLED;
	}

	public boolean isShowLeftMenu() {
		return leftMenu != DISABLED;
	}

	public boolean isShowTopMenu() {
		return topMenu != DISABLED;
	}

	public boolean isShowTrail() {
		return trail != DISABLED;
	}

	public void setAclPolicy(int policy) {
		this.acl = policy;
	}

	public void setBanner(int banner) {
		this.banner = banner;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = (Date) createTime.clone();
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	public void setFooter(int footer) {
		this.footer = footer;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setLeftMenu(int leftMenu) {
		this.leftMenu = leftMenu;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTopMenu(int topMenu) {
		this.topMenu = topMenu;
	}

	public void setTrail(int trail) {
		this.trail = trail;
	}

	public void setType(String type) {
		this.type = type;
	}
}
