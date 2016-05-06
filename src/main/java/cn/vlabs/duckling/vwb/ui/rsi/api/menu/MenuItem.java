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

package cn.vlabs.duckling.vwb.ui.rsi.api.menu;

/**
 * @date 2010-5-17
 * @author Fred Zhang (fred@cnic.cn)
 */
public class MenuItem {
	private int index;
	private String name;
	private String href;
	private String title;
	private boolean visible;
	private String module;
	private boolean available;
	private String aLinkClass;
	public String getValue()
	{
		return toString();
	}
	
    /**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String toString()
    {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("<li ");
    	if(!visible)
    	{
    		buffer.append("style=\"display:none\"");
    	}
        buffer.append(">");
    	buffer.append("<a ");
    	if(title!=null)
    	{
    		buffer.append("title=\"").append(title).append("\"");
    	}
    	if(aLinkClass!=null&&aLinkClass.trim().length()>0)
    	{
    		buffer.append(" class=\"").append(aLinkClass).append("\"");
    	}
    	if(href!=null)
    	{
    		buffer.append(" href=\"").append(href).append("\"");
    	}
    	
    	if(module!=null&&!"".equals(module)){
    		buffer.append(" module=\"").append(module).append("\"");
    	}
    	
    	buffer.append(" available=\"").append(available).append("\"");
    	
    	buffer.append(">");
    	buffer.append(name).append("</a>").append("</li>");
    	return buffer.toString();
    }
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the display
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href
	 *            the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the aLinkClass
	 */
	public String getALinkClass() {
		return aLinkClass;
	}

	/**
	 * @param linkClass
	 *            the aLinkClass to set
	 */
	public void setALinkClass(String linkClass) {
		aLinkClass = linkClass;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	
	
	
	

}
