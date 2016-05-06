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
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

import cn.vlabs.duckling.util.AttachmentURL;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class PaserDmlCommElement extends AbstractParseDmlElement {

	@Override
	public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
		List<?> attList=e.getAttributes();
		for(int i=0;i<attList.size();i++){
			String strAttName=((Attribute)attList.get(i)).getName();
			String strAttValue=((Attribute)attList.get(i)).getValue();
			if(strAttName.equals("dmlsrc")){//处理img flash时候用
				AttachmentURL au=new AttachmentURL(true,strAttValue);
				int docid=au.getDocID();
				String link=null;
				if(au.getVersion()==null||au.getVersion().equals("")){
					link= dml2htmlengine.getDmlcontext().getDirectUrl(docid);
				}else{
					link=dml2htmlengine.getDmlcontext().getDirectUrl(docid, au.getVersion());
				}
				dml2htmlengine.getM_out().print( " src=\"" + link + "\"" );
				if (dml2htmlengine.isM_wysiwygEditorMode()){
					dml2htmlengine.getM_out().print( " dmlsrc=\"" + strAttValue + "\"" );
				}
			}else{
				dml2htmlengine.getM_out().print( " " + strAttName+ "=\"" + strAttValue + "\"" );
			}
			
		}

	}

	@Override
	public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
		dml2htmlengine.getM_out().print("<"+element.getName().toLowerCase());
		printAttribute(element,  dml2htmlengine);
		if(dml2htmlengine.getPreType()>0){
			dml2htmlengine.getM_out().print(">");
		}else{
			dml2htmlengine.getM_out().println(">");
		}
		try {
			d2h.getChildren(element, dml2htmlengine);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		
		if(dml2htmlengine.getPreType()>0){
			dml2htmlengine.getM_out().print("</"+element.getName().toLowerCase()+">");
		}else{
			dml2htmlengine.getM_out().println("</"+element.getName().toLowerCase()+">");
		}
	}
}
