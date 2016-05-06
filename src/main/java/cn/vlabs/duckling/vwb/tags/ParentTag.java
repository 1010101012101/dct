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

package cn.vlabs.duckling.vwb.tags;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/*
 * 导航链接Tag
 * add by diyanliang 2010-3-1
 * */
public class ParentTag extends VWBBaseTag {
	protected String m_pageName = null;

	private static final long serialVersionUID = 325728232423423420L;

	String linkclass = "class=\"wikipage\"";

	String parentclass = "class=\"wikipage\"";

	public ParentTag() {
		super();
	}
	private Stack<PathElement> getPath(Resource resource){
		Set<Integer> nodes= new HashSet<Integer>();
		nodes.add(Integer.valueOf(resource.getResourceId()));
		
		Stack<PathElement> stack = new Stack<PathElement>();
		ViewPortService viewPortService = VWBContext.getContainer().getViewPortService();
		ViewPort vp =viewPortService.getViewPort(vwbcontext.getSiteId(),resource.getResourceId());
		while (vp!=null){
			PathElement pe = new PathElement(vp);
			stack.push(pe);
			if (!vp.isRoot()&&vp.getParent()>0&&!nodes.contains(Integer.valueOf(vp.getParent())))
			{
				nodes.add(Integer.valueOf(vp.getParent()));
			    vp = viewPortService.getViewPort(vwbcontext.getSiteId(),vp.getParent());
			}
			else
			{
				vp=null;
			}
			
		};

		return stack;
	}
	private class PathElement{
		private ViewPort m_vp;
		private String m_title;
		
		public PathElement(ViewPort vp){
			m_vp=vp;
			m_title=parseTitle();
		}
		
		public String getTitle(){
			return m_title;
		}
		private String parseTitle(){
			String title = m_vp.getTitle();
			if (StringUtils.isEmpty(title)){
				title = Integer.toString(m_vp.getId());
			}
			title = title.trim();
			return title;
		}
		public String makeLink(String className){
			return makeLink(m_title,className);
		}
		public String makeCollpaseLink(String className){
			return makeLink(collapseTitle(m_title),className);
		}
		private String collapseTitle(String title){
			return title.length() <= 4 ? title
					: title.substring(0, 4) + "...";
		}

		private String makeLink(String title, String className){
			return "<a "+className+" href=\"" +vwbcontext.getViewURL(m_vp.getId())+"\""+" title=\""+m_vp.getTitle()+"\""+">"+title+"</a>";
		}
	}
	private String buildFullTitle(Stack<PathElement> path){
		StringBuffer title = new StringBuffer();
		for (PathElement pe :path){
			title.append(pe.getTitle());
		}
		return title.toString();
	}
	
	private String makeLinkPath(Stack<PathElement> path){
		if (requireCollpase(path)){
			return printCollapsePath(path);
		}else{
			return printFullPath(path); 
		}
	}
	
	private boolean requireCollpase(Stack<PathElement> path) {
		String fullTitle = buildFullTitle(path);
		boolean requireCollapse=fullTitle.length()>50;
		return requireCollapse;
	}
	private String printCollapsePath(Stack<PathElement> path) {
		StringBuffer html = new StringBuffer();
		PathElement pe;
		boolean first=true;
		boolean collapsePrinted = false;
		while ( path.size()>0){
			pe=path.pop();
			if (!first){
				if (path.size()>3){
					if (!collapsePrinted){
						collapsePrinted=true;
						html.append("&nbsp;&gt;&nbsp;");
						html.append("<font color=\"#c0c0c0\">......</font>");
					}
				}else{
					// print ">" split
					html.append("&nbsp;&gt;&nbsp;");
					html.append(pe.makeCollpaseLink(linkclass));
				}
			}else{
				first=false;
				html.append(pe.makeCollpaseLink(parentclass));
			}
		}
		return html.toString();
	}
	private String printFullPath(Stack<PathElement> path) {
		StringBuffer html = new StringBuffer();
		PathElement pe;
		int count=0;
		while (path.size()>0){
			count++;
			String fragment;
			pe=path.pop();
			if (path.size()>0){
				fragment= pe.makeLink(parentclass);
			}else{
				fragment=pe.makeLink(linkclass);
			}
			if (count>1)
				html.append("&nbsp;&gt;&nbsp;");
			html.append(fragment);
		}
		return html.toString();
	}
	public int doVWBStart() throws Exception {
		Stack<PathElement> path = getPath(vwbcontext.getResource());
		pageContext.getOut().print(makeLinkPath(path));
		return SKIP_BODY;
	}

}
