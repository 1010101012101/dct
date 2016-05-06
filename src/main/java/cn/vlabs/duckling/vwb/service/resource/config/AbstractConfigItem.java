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

package cn.vlabs.duckling.vwb.service.resource.config;


import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * 配置项
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public abstract class AbstractConfigItem implements ConfigItem{
	public abstract String getType();
	
	public void setId(int id) {
		this.id = id;
	}

	public String getLeftMenu() {
		return leftMenu;
	}

	public void setLeftMenu(String leftMenu) {
		this.leftMenu = leftMenu;
	}

	public String getTopMenu() {
		return topMenu;
	}

	public void setTopMenu(String topMenu) {
		this.topMenu = topMenu;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public void setTrail(String trail) {
		this.trail = trail;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public int getFooter() {
		return iFooter;
	}

	public int getBanner() {
		return iBanner;
	}

	public int getTopmenu() {
		return iTopmenu;
	}

	public int getLeftmenu() {
		return iLeftmenu;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getParent() {
		return parent;
	}
	public String getTitle(){
		return title;
	}
	public int getTrail(){
		return iTrail;
	}
	public void init(){
		iLeftmenu=parseDigit(this.leftMenu);
		iTopmenu=parseDigit(this.topMenu);
		iFooter=parseDigit(this.footer);
		iBanner=parseLetters(this.banner);
		iTrail=parseLetters(this.trail);
	}

	private int parseLetters(String s){
		if ("no".equalsIgnoreCase(s))
			return DISABLED;
		if ("inherit".equalsIgnoreCase(s))
			return INHERIT;
		if ("yes".equalsIgnoreCase(s))
			return ENABLED;
		if("INHERITSKIN".equalsIgnoreCase(s))
		{
			return INHERITSKIN;
		}
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return INHERIT;
		}
	}
	private int parseDigit(String s){
		if ("no".equalsIgnoreCase(s))
			return DISABLED;
		if ("inherit".equalsIgnoreCase(s))
			return INHERIT;
		try{
			return Integer.parseInt(s);
		}catch (NumberFormatException e){
			return INHERIT;
		}
	}
	
	
	private int parent=0;
	@XStreamOmitField
	private int iLeftmenu=0;
	@XStreamOmitField
	private int iTopmenu=0;
	@XStreamOmitField
	private int iBanner=0;
	@XStreamOmitField
	private int iFooter=0;
	@XStreamOmitField
	private int iTrail=0;
	
	private static final int INHERIT=0;
	private static final int DISABLED=-1;
	private static final int ENABLED=1;
	private static final int INHERITSKIN = -2;
	int id;
	private String leftMenu;
	private String topMenu;
	private String banner;
	private String footer;
	private String trail;
	private String title;
}
