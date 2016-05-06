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

package cn.vlabs.duckling.vwb.ui.rsi.api.skins;


/**
 * @date 2010-5-30
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SkinItem {
	public void setGlobal(boolean global){
		this.m_global=global;
	}
	public String getWebPath(){
		return m_path;
	}
    public void setWebPath(String webPath)
    {
    	m_path = webPath;
    }
	public String getThumb() {
		return m_thumb;
	}
    public void setThumb(String thumb)
    {
    	m_thumb =  thumb;
    }
	public String getName() {
		return m_name;
	}
	public void setName(String name)
	{
		m_name = name;
	}
	
	public boolean isGlobal(){
		return m_global;
	}
	public boolean isCurrentSkin() {
		return currentSkin;
	}
	public void setCurrentSkin(boolean currentSkin) {
		this.currentSkin = currentSkin;
	}
	
	private boolean m_global;
	private String m_name;
	private String m_path;
	private String m_thumb;
	private boolean currentSkin;
	private String m_template;
	private boolean available;
	public String getTemplate() {
		return m_template;
	}
	public void setTemplate(String template) {
		this.m_template = template;
	}
	
	public boolean isAvailable(){
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	
	
	
}
