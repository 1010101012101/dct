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

package cn.vlabs.duckling.vwb.ui.action;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @date 2014-6-12
 * @author xuzhijian
 */
public class HtmlValidateUtil {

	private static final Logger   log = Logger.getLogger(HtmlValidateUtil.class);
    public static boolean checkHtmlTextValidate(String content){
    	if(StringUtils.isBlank(content)){
    		return true;
    	}
    	
    	if(!containsInvalidateUrl(content)){
    		return true;
    	}
    	
    	
		SAXBuilder builder = new SAXBuilder("org.cyberneko.html.parsers.SAXParser", true);
		try {
			Document doc = builder.build(new StringReader(content));
			Element e = doc.getRootElement();
			List<Element> forms = new ArrayList<Element>();
			fillFormElement(e, forms);
			for (Element form : forms) {
				String actionStr=form.getAttributeValue("action");
				getInvalidateFormAction().contains(actionStr);
				return false;
			}
		} catch (Throwable e) {
			log.error("解析html出错！", e);
		}

		return true;
}

private static void fillFormElement(Element element, List<Element> forms) {
	if ("FORM".equalsIgnoreCase(element.getName())) {
		forms.add(element);
	} else {
		@SuppressWarnings("unchecked")
		List<Element> children = element.getChildren();
		for (Element child : children) {
			fillFormElement(child, forms);
		}
	}

}

public static List<String> getInvalidateFormAction(){
	List<String> result=new ArrayList<String>();
	result.add("https://www.paypal.com/cgi-bin/webscr");
	return result;
}

public static String disabledSubmit(String html){
	html=StringUtils.replace(html, "submit", "button");
	for(String url:getInvalidateFormAction()){
		html=StringUtils.replace(html, url, "");
	}
	return html;
}

private static boolean containsInvalidateUrl(String html){
	if(StringUtils.isBlank(html)){
		return false;
	}
	
	for(String str:getInvalidateFormAction()){
		if(StringUtils.contains(html, str)){
			return true;
		}
	}
	
	return false;
	
}



}
