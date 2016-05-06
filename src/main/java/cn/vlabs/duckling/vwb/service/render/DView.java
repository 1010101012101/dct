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

package cn.vlabs.duckling.vwb.service.render;

/**
 * 显示组件
 * 
 * @date Feb 3, 2010
 * @author xiejj@cnic.cn
 */
public class DView {
	private Rendable m_leftMenu;

	private Rendable m_topMenu;

	private Rendable m_content;

	private Rendable m_footer;

	private Rendable m_banner;
	
	private boolean m_showTrail;
	
	private boolean m_showUserBox;
	private boolean m_showSearchBox;
	
	public Rendable getLeftMenu() {
		return m_leftMenu;
	}

	public Rendable getTopMenu() {
		return m_topMenu;
	}

	public Rendable getFooter() {
		return m_footer;
	}

	public Rendable getContent() {
		return m_content;
	}

	public Rendable getBanner() {
		return m_banner;
	}

	public void setContent(Rendable content) {
		m_content = content;
	}

	public void setBanner(Rendable banner) {
		m_banner = banner;
	}

	public void setLeftMenu(Rendable leftMenu) {
		m_leftMenu = leftMenu;
	}

	public void setFooter(Rendable footer) {
		m_footer = footer;
	}

	public void setTopMenu(Rendable r) {
		m_topMenu = r;
	}
	
	public void setShowTrail(boolean trail){
		this.m_showTrail=trail;
	}
	public boolean isShowTrail(){
		return m_showTrail;
	}
	
	public void setShowUserbox(boolean userbox){
		this.m_showUserBox=userbox;
	}
	public boolean isShowUserbox(){
		return m_showUserBox;
	}
	
	public void setShowSearchbox(boolean searchbox){
		this.m_showSearchBox=searchbox;
	}
	public boolean isShowSearchbox(){
		return m_showSearchBox;
	}
}
