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

package cn.vlabs.duckling.vwb.ui.rsi.profile;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.vlabs.duckling.vwb.ui.rsi.api.menu.MenuItem;

/**
 * @date 2010-5-18
 * @author Fred Zhang (fred@cnic.cn)
 */
public class LeftMenuUtil {
	public static String generateContent(List<MenuItem> items) {
		Collections.sort(items, new Comparator<MenuItem>() {
			public int compare(MenuItem leftitem1, MenuItem leftitem2) {
				if (leftitem1.getIndex() > leftitem2.getIndex()) {
					return 1;
				}
				return -1;
			}
		});
		StringBuffer content = new StringBuffer();
		content.append("<ul>");
		for (int i = 0; i < items.size(); i++) {
			content.append(items.get(i).toString());
		}
		content.append("</ul>");
		return content.toString();
	}

	public static List<MenuItem> extractLeftMenuItemFromContent(String content) {
		List<MenuItem> items = new ArrayList<MenuItem>();
		int index = 0;
		int disableIndex=0;
		SAXBuilder builder = new SAXBuilder(
				"org.cyberneko.html.parsers.SAXParser", true);
		try {
			Document doc = builder.build(new StringReader(content));
			Element e = doc.getRootElement();
			List<Element> uls = new ArrayList<Element>();
			fillULElement(e, uls);
			for (Element u : uls) {
				@SuppressWarnings("unchecked")
				List<Element> lis = u.getChildren();
				for (Element li : lis) {
					Element a = (Element) li.getChildren().get(0);
					if (a != null) {
						MenuItem item = new MenuItem();
						String visible = li.getAttributeValue("style");
						if (visible == null
								|| visible != null
								&& visible.toLowerCase()
										.indexOf("display:none") < 0) {
							item.setVisible(true);
						}

						item.setName(a.getText() != null ? a.getText().trim()
								: "");
						item.setALinkClass(a.getAttributeValue("class"));
						item.setHref(a.getAttributeValue("href"));
						item.setTitle(a.getAttributeValue("title"));
						item.setModule(a.getAttributeValue("module"));
						if(a.getAttributeValue("available")==null||"".equals(a.getAttributeValue("available").trim())){
							item.setAvailable(true);
						}else{
							item.setAvailable("true".equals(a.getAttributeValue("available")));
						}
						if(item.isAvailable()){
							item.setIndex(++index);
						}else{
							item.setIndex(--disableIndex);
						}
						items.add(item);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return items;

	}

	private static void fillULElement(Element element, List<Element> uls) {
		if ("UL".equalsIgnoreCase(element.getName())) {
			uls.add(element);
		} else {
			@SuppressWarnings("unchecked")
			List<Element> children = element.getChildren();
			for (Element child : children) {
				fillULElement(child, uls);
			}
		}

	}

	public static void main(String agrs[]) {
		extractLeftMenuItemFromContent("<ul><li><A cLAss=\"aa\" HREF='http://link.com'>tesaaat</a></li></ul><ul><LI><a href='http://link.com'>test</a></li></ul>");
	}
}
