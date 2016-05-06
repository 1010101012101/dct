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
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class ParseHtmlSpan extends AbstractParseHtmlElement {
	@Override
	public void printAttribute(Element e, Html2DmlEngine html2dmlengine) {
		Map<String, String> map = new ForgetNullValuesLinkedHashMap();
		map.put("style", e.getAttributeValue("style"));
		map.put("class", e.getAttributeValue("class"));
		if (map.size() > 0) {
			for (Iterator<Map.Entry<String, String>> ito = map.entrySet()
					.iterator(); ito.hasNext();) {
				Map.Entry<String, String> entry = ito.next();
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
		// 设pre标志位
		if (IsNPreSpan(e)) {
			html2dmlengine.setPreType(html2dmlengine.getPreType() + 1);
		}

		html2dmlengine.getM_out().print("<span");
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

		// 设pre标志位
		if (IsNPreSpan(e)) {
			html2dmlengine.setPreType(html2dmlengine.getPreType() - 1);
		}
		if (html2dmlengine.getPreType() > 0) {
			html2dmlengine.getM_out().print("</span>");
		} else {
			html2dmlengine.getM_out().println("</span>");
		}

	}

	private boolean IsNPreSpan(Element e) {
		boolean reb = false;
		String classstr = e.getAttributeValue("style");
		if (classstr != null) {
			int startnum = classstr.indexOf("white-space");
			if (startnum != -1) {
				if (classstr.length() > startnum + 11) {
					String startstr = classstr.substring(startnum + 11);
					int endnum = startstr.indexOf(";");
					if (endnum != -1) {
						String prestr = startstr.substring(0, endnum);
						if (prestr.indexOf("pre") != -1)
							reb = true;
					}
				}

			}
			// if("font-family: monospace; white-space: pre;".equals(classstr)||classstr.indexOf("pre;")!=-1)
			// reb=true;
		}
		return reb;

	}

}
