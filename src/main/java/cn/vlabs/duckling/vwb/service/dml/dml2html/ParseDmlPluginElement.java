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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */

public class ParseDmlPluginElement extends AbstractParseDmlElement {

	public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
		// TODO Auto-generated method stub

	}

	/*
	 * 将转换后的puglin插件按照DML形势输出 plugin在html中表现形势 <span class="plugin"> <span
	 * class="parameter" style="display:none"> key1=value1; key2=value2; ...
	 * </span> </span>
	 * 
	 * <img contenteditable="false" class="plugin"
	 * src="scripts/fckeditor/editor/images/plugin.gif"
	 * title="name='SessionsPlugin';property='users';" alt="" />
	 * 
	 * 
	 * 
	 * 
	 * plugin 在dml中表现形式<D_plugin> <D_parameter name="SessionsPlugin"/>
	 * <D_parameter property="users"/></D_plugin>
	 */
	@Override
	public void printElement(Element element, Dml2HtmlEngine dml2htmlengine) {

		org.jdom.Attribute srcattibute = element.getAttribute("src");

		String srccalue = dml2htmlengine.getBaseurl()
				+ "scripts/fckeditor/editor/images/plugin.gif";
		if (srcattibute != null) {
			srccalue = srcattibute.getValue();
			srccalue = srccalue
					.replace("$baseurl", dml2htmlengine.getBaseurl());
		}
		Map<String, String> parsedParams = new HashMap<String, String>();
		for (Iterator<?> i = element.getContent().iterator(); i.hasNext();) {
			Object c = i.next();
			if (c instanceof Element) {
				Element e = (Element) c;
				String n = e.getName().toLowerCase();
				if ("d_parameter".equalsIgnoreCase(n)) {
					List<?> attributeslist = e.getAttributes();
					parsedParams.put(
							((Attribute) attributeslist.get(0)).getName(),
							((Attribute) attributeslist.get(0)).getValue());
				}

			}

		}
		if (dml2htmlengine.isM_wysiwygEditorMode()) {
			dml2htmlengine.getM_out().print(
					"<img contentEditable = \"false\"; class=\"plugin\" src=\""
							+ srccalue + "\" alt=\"\" ");

			org.jdom.Attribute height = element.getAttribute("height");
			if (height != null) {
				dml2htmlengine.getM_out().println(
						" height=\"" + height.getValue() + "\" ");
			}

			org.jdom.Attribute width = element.getAttribute("width");
			if (width != null) {
				dml2htmlengine.getM_out().println(
						" width=\"" + width.getValue() + "\" ");
			}
			dml2htmlengine.getM_out().print("title=\"");
			for (String key : parsedParams.keySet()) {
				dml2htmlengine.getM_out().println(
						key + "='" + parsedParams.get(key) + "';");
			}
			dml2htmlengine.getM_out().print("\" />");
		} else {
			try {
				String pluginname = (String) parsedParams.get("name");

				org.jdom.Attribute height = element.getAttribute("height");
				if (height != null) {
					parsedParams.put("offsetHeight", height.getValue());
				}

				org.jdom.Attribute width = element.getAttribute("width");
				if (width != null) {
					parsedParams.put("offsetWidth", width.getValue());
				}

				String resrt = dml2htmlengine.getDmlcontext().pluginexecute(
						pluginname, parsedParams);
				dml2htmlengine.getM_out().println(resrt);
			} catch (Exception e) {
				dml2htmlengine.getM_out().println(
						"plugin.error.pluginfailed:" + e);
				e.printStackTrace();
			}

		}

	}

}
