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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
/*
 * html向dml转换
 */
public class Dml2Html {
	private Dml2HtmlEngine newdml2htmlengine = new Dml2HtmlEngine();
	private static final String CYBERNEKO_PARSER = "org.cyberneko.html.parsers.SAXParser";

	// 主调用map 存所有元素 其中key=“元素名” value="储存下级元素都有什么的map"
	private static HashMap<String, AbstractParseDmlElement> hs = new HashMap<String, AbstractParseDmlElement>();

	static {
		ParseDmlPluginElement parsedmlpluginelement = new ParseDmlPluginElement();
		ParseDmlSectionElement parsedmlsectionelement = new ParseDmlSectionElement();
		ParserDmlLinkElement parserdmllinkelement = new ParserDmlLinkElement();
		ParserDmlStyleElement parserdmlstyleelement = new ParserDmlStyleElement();
		PaserDmlCommElement paserdmlcommelement = new PaserDmlCommElement();
		PaserDmlPreElement paserdmlpreelement = new PaserDmlPreElement();
		PaserDmlSpanElement paserdmlspanelement = new PaserDmlSpanElement();
		PaserDmlHeadingElement paserdmlheadingelement = new PaserDmlHeadingElement();

		// 主调用map赋值
		hs.put("d_link", parserdmllinkelement);
		hs.put("d_plugin", parsedmlpluginelement);
		hs.put("d_section", parsedmlsectionelement);
		hs.put("br", parserdmlstyleelement);
		hs.put("hr", parserdmlstyleelement);

		hs.put("pre", paserdmlpreelement);
		hs.put("span", paserdmlspanelement);

		hs.put("body", paserdmlcommelement);
		hs.put("b", paserdmlcommelement);
		hs.put("div", paserdmlcommelement);
		hs.put("em", paserdmlcommelement);
		hs.put("h1", paserdmlheadingelement);
		hs.put("h2", paserdmlheadingelement);
		hs.put("h3", paserdmlheadingelement);
		hs.put("h4", paserdmlheadingelement);
		hs.put("img", paserdmlcommelement);
		hs.put("li", paserdmlcommelement);
		hs.put("ol", paserdmlcommelement);
		hs.put("p", paserdmlcommelement);
		hs.put("strike", paserdmlcommelement);
		hs.put("strong", paserdmlcommelement);
		hs.put("sub", paserdmlcommelement);
		hs.put("sup", paserdmlcommelement);
		hs.put("font", paserdmlcommelement);
		hs.put("table", paserdmlcommelement);
		hs.put("tbody", paserdmlcommelement);
		hs.put("thead", paserdmlcommelement);
		hs.put("tfoot", paserdmlcommelement);
		hs.put("td", paserdmlcommelement);
		hs.put("tr", paserdmlcommelement);
		hs.put("u", paserdmlcommelement);
		hs.put("ul", paserdmlcommelement);
		hs.put("i", paserdmlcommelement);
		hs.put("th", paserdmlcommelement);
		hs.put("embed", paserdmlcommelement);
		hs.put("font", paserdmlcommelement);
	}

	/*
	 * 构造函数
	 */
	public Dml2Html() {
	}

	/*
	 * 构造函数
	 */
	public Dml2Html(String dml, Dml2HtmlEngine dml2htmlengine)
			throws JDOMException, IOException {

		if (dml != null && !"".equals(dml)) {
			Element e = getRootElement(dml);
			this.newdml2htmlengine = dml2htmlengine;
			getChildren(e, newdml2htmlengine);
		} else {
			newdml2htmlengine.getM_out().println("");
		}

	}

	/*
	 * 取到body元素
	 */
	public Element getRootElement(String html) throws JDOMException,
			IOException {
		Element belement = null;
		SAXBuilder builder = new SAXBuilder(CYBERNEKO_PARSER, true);
		Document doc = builder.build(new StringReader(html));
		Element htmlelement = doc.getRootElement();
		if (htmlelement != null) {
			if (htmlelement.getName().toLowerCase().equals("html")) {
				if (htmlelement.getChildren().size() > 0)
					belement = (Element) htmlelement.getChildren().get(0);
				else
					belement = new Element("p");
			}
		}
		return belement;
	}

	/*
	 * 判断当前元素是否有子元素 并做遍历 需要特殊处理的有“br hr D_link D_puglin D_section ”
	 */
	protected void getChildren(Element parentelement,
			Dml2HtmlEngine dml2htmlengine) throws IOException, JDOMException {
		for (Iterator<?> i = parentelement.getContent().iterator(); i.hasNext();) {
			Object ec = i.next();
			if (ec instanceof Element) {
				Element childelement = (Element) ec;
				String cename = childelement.getName().toLowerCase();
				AbstractParseDmlElement parser = (AbstractParseDmlElement) hs
						.get(cename);
				if (parser != null) {
					parser.printElement(childelement, dml2htmlengine);
				} else {
					PaserDmlCommElement paserdmlcommelement = new PaserDmlCommElement();
					paserdmlcommelement.printElement(childelement,
							dml2htmlengine);
				}

			} else if (ec instanceof Text) {
				printText(ec, dml2htmlengine);
			}
		}

	}

	/*
	 * 打印我们不支持的元素
	 */
	protected void printErrElement(Element element,
			Dml2HtmlEngine dml2htmlengine) throws IOException, JDOMException {
		String thisename = element.getName();

		dml2htmlengine.getM_out().print("<");
		dml2htmlengine.getM_out().print(thisename);
		dml2htmlengine.getM_out().print(">");
		for (Iterator<?> i = element.getContent().iterator(); i.hasNext();) {
			Object c = i.next();
			if (c instanceof Element) {
				getChildren(element, dml2htmlengine);
			} else if (c instanceof Text) {
				printText(c, dml2htmlengine);
			}
		}
		dml2htmlengine.getM_out().print("</");
		dml2htmlengine.getM_out().print(thisename);
		dml2htmlengine.getM_out().print(">");
	}

	/*
	 * 输出Text
	 */
	private void printText(Object element, Dml2HtmlEngine dml2htmlengine)
			throws IOException, JDOMException {
		Text t = (Text) element;
		String s = t.getText();
		if (!s.equals("")) {
			s = s.replaceAll("&", "&amp;");
			s = s.replaceAll(">", "&gt;");
			s = s.replaceAll("<", "&lt;");
			if (dml2htmlengine.getPreType() > 0) {
				dml2htmlengine.getM_out().print(s);
			} else {
				s = s.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]", "");
				dml2htmlengine.getM_out().println(s);
			}
		}
	}

	/*
	 * 输出DML
	 */
	public String getHTMLString() {
		String res = "";
		res = newdml2htmlengine.getM_outTimmer().toString();
		res = res.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029]", "");
		return res;
	}

	public Element getHTMLDom() {
		String res = "";
		Element reelement = null;
		res = newdml2htmlengine.getM_outTimmer().toString();
		res = res.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029]", "");
		try {
			reelement = getRootElement(res);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return reelement;
	}

	/*
	 * 辅助方法转Unicode用
	 */
	public String ChineseToUnicode(String s) {
		String as[] = new String[s.length()];
		String s1 = "";
		for (int i = 0; i < s.length(); i++) {
			as[i] = Integer.toHexString(s.charAt(i) & 0xffff);
			s1 = s1 + "\\u" + as[i];
		}
		return s1;
	}
}
