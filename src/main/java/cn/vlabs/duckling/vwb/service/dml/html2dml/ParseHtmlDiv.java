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

package cn.vlabs.duckling.vwb.service.dml.html2dml;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
public class ParseHtmlDiv extends AbstractParseHtmlElement {

	public ParseHtmlDiv() {
	}

	@Override
	public void printAttribute(Element e, Html2DmlEngine html2dmlengine) {
		Map<String, String> map = new ForgetNullValuesLinkedHashMap();
		map.put("style", e.getAttributeValue("style"));
		map.put("class", e.getAttributeValue("class"));
		map.put("id", e.getAttributeValue("id"));
		if (map.size() > 0) {
			for (Iterator<Map.Entry<String, String>> ito = map.entrySet().iterator(); ito.hasNext();) {
				Map.Entry<String, String> entry =ito.next();
				if (!entry.getValue().equals("")) {
					html2dmlengine.getM_out().print(
							" " + entry.getKey() + "=\"" + entry.getValue()
									+ "\"");
				}
			}
		}

	}

	@Override
	public void printElement(Element e, Html2DmlEngine html2dmlengine) {
		if (IsNotDiv2Section(e)) {
			html2dmlengine.getM_out().print("<D_section");
			if (html2dmlengine.getPreType() > 0) {
				html2dmlengine.getM_out().print(">");
			} else {
				html2dmlengine.getM_out().println(">");
			}
			try {
				h2d.getChildren(e, html2dmlengine);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (JDOMException e1) {
				e1.printStackTrace();
			}
			if (html2dmlengine.getPreType() > 0) {
				html2dmlengine.getM_out().print("</D_section>");
			} else {
				html2dmlengine.getM_out().println("</D_section>");
			}
		} else {
			html2dmlengine.getM_out().print("<div");
			printAttribute(e, html2dmlengine);
			if (html2dmlengine.getPreType() > 0) {
				html2dmlengine.getM_out().print(">");
			} else {
				html2dmlengine.getM_out().println(">");
			}
			try {
				h2d.getChildren(e, html2dmlengine);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (JDOMException e1) {
				e1.printStackTrace();
			}
			if (html2dmlengine.getPreType() > 0) {
				html2dmlengine.getM_out().print("</div>");
			} else {
				html2dmlengine.getM_out().println("</div>");
			}
		}
	}

	/*
	 * 判断是否需要将div转换成Section
	 */
	public boolean IsNotDiv2Section(Element e) {
		boolean reb = false;
		String strattibute = "";
		Attribute attibute = e.getAttribute("class");
		if (attibute != null) {
			strattibute = attibute.getValue().toString();
			// System.out.println(strattibute);
		}
		if (strattibute.equals("section")) {
			reb = true;
		}
		return reb;

	}

	/*
	 * 判断是否需要将div转换成puglin
	 */
	public boolean IsNotDiv2Plugin(Element e) {
		boolean reb = false;
		String strattibute = "";
		org.jdom.Attribute attibute = e.getAttribute("class");
		if (attibute != null) {
			strattibute = attibute.getValue().toString();
			if (strattibute.equals("plugin")) {
				reb = true;
			}
		}

		return reb;

	}

	/*
	 * 将转换后的puglin插件按照DML形势输出 plugin在html中表现形势 <div class="plugin"> <div
	 * class="parameter" style="display:none"> key1=value1; key2=value2; ...
	 * </div> </div>
	 */
	public void printPlugin(Element element, Html2DmlEngine html2dmlengine) {
		html2dmlengine.getM_out().println("<D_plugin>");
		List<?> parameterList = element.getChildren();
		Element childelement = (Element) parameterList.get(0);
		String strParameter = childelement.getValue();
		String[] strParameters = strParameter.split(";");
		for (int j = 0; j < strParameters.length; j++) {
			String[] strkeyvalue = strParameters[j].split("=");
			if (strkeyvalue.length == 2)
				html2dmlengine
						.getM_out()
						.println(
								"<D_parameter "
										+ strkeyvalue[0]
												.trim()
												.replaceAll(
														"[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]",
														"") + "=\""
										+ strkeyvalue[1] + "\"/>");
		}
		html2dmlengine.getM_out().println("</D_plugin>");

	}
}
