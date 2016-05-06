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

package cn.vlabs.duckling.vwb.service.dml.dml2html;

import java.io.IOException;
import org.jdom.Element;
import org.jdom.JDOMException;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class ParseDmlSectionElement extends AbstractParseDmlElement {
	@Override
	public void printAttribute(Element e,Dml2HtmlEngine dml2htmlengine) {

	}
	
	
	
	@Override
	public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
		
		dml2htmlengine.setSectionid(dml2htmlengine.getSectionid()+1);
		int sectionid=dml2htmlengine.getSectionid();
		if(!dml2htmlengine.isM_wysiwygEditorMode()){
			if(dml2htmlengine.getView_Mode().equals("0")){
				dml2htmlengine.getM_out().println("<div>");	
			}else{
				dml2htmlengine.getM_out().println("<div class=\"editTag\" style=\"float: right; margin-left: 5px;\" >");
				dml2htmlengine.getM_out().println("<a href=\"Edit.jsp?page="+dml2htmlengine.getPageName()+"&amp;section="+sectionid+"\" title=\"edit\">(编辑)</a>");
				dml2htmlengine.getM_out().println("</div>");
				dml2htmlengine.getM_out().print("<div class=\"section\" id=\"section"+sectionid+"\"");
				dml2htmlengine.getM_out().println(">");
			}

		}else {
			dml2htmlengine.getM_out().println("<div class=\"section\" >");	
		}
		try{
			d2h.getChildren(element,dml2htmlengine);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		dml2htmlengine.getM_out().println("</div>");

	}

}
