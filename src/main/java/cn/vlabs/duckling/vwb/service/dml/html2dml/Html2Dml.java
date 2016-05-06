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
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import cn.vlabs.duckling.util.FileUtil;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
/*
 * html向dml转换
 */
public class Html2Dml {
	private Html2DmlEngine newhtml2dmlengine = new Html2DmlEngine();
	private final String CYBERNEKO_PARSER = "org.cyberneko.html.parsers.SAXParser";
	// dml中需要移除的元素列表
	private String removeEname = "script|style|link|object|param";

	// 为了便于支持dml没有的元素做了一个全部元素的列表
	private static HashMap<String, AbstractParseHtmlElement> allemap = new HashMap<String, AbstractParseHtmlElement>();
	private static HashMap<String, AbstractParseHtmlElement> noddatamap = new HashMap<String, AbstractParseHtmlElement>();

	/*
	 * 生成二维map 第一级key=“元素名” value="下级元素的map" 第二级 key="元素名" value="元素解析类实例"
	 */
	static {
		// 实例化各个元素解析类
		ParseHtmlA parsehtmla = new ParseHtmlA();
		ParseHtmlB parsehtmlb = new ParseHtmlB();
		ParseHtmlBr parsehtmlbr = new ParseHtmlBr();
		ParseHtmlDiv parsehtmldiv = new ParseHtmlDiv();
		ParseHtmlEm parsehtmlem = new ParseHtmlEm();
		ParseHtmlH1 parsehtmlh1 = new ParseHtmlH1();
		ParseHtmlH2 parsehtmlh2 = new ParseHtmlH2();
		ParseHtmlH3 parsehtmlh3 = new ParseHtmlH3();
		ParseHtmlH4 parsehtmlh4 = new ParseHtmlH4();
		ParseHtmlHr parsehtmlhr = new ParseHtmlHr();
		ParseHtmlImg parsehtmlimg = new ParseHtmlImg();
		ParseHtmlLi parsehtmlli = new ParseHtmlLi();
		ParseHtmlOl parsehtmlol = new ParseHtmlOl();
		ParseHtmlP parsehtmlp = new ParseHtmlP();
		ParseHtmlPre parsehtmlpre = new ParseHtmlPre();
		ParseHtmlSpan parsehtmlspan = new ParseHtmlSpan();
		ParseHtmlStrike parsehtmlstrike = new ParseHtmlStrike();
		ParseHtmlStrong parsehtmlstrong = new ParseHtmlStrong();
		ParseHtmlSub parsehtmlsub = new ParseHtmlSub();
		ParseHtmlSup parsehtmlsup = new ParseHtmlSup();
		ParseHtmlTable parsehtmltable = new ParseHtmlTable();
		ParseHtmlTbody parsehtmltbody = new ParseHtmlTbody();
		ParseHtmlThead parsehtmlthead = new ParseHtmlThead();
		ParseHtmlTfoot parsehtmltfoot = new ParseHtmlTfoot();
		ParseHtmlTd parsehtmltd = new ParseHtmlTd();
		ParseHtmlTr parsehtmltr = new ParseHtmlTr();
		ParseHtmlU parsehtmlu = new ParseHtmlU();
		ParseHtmlUl parsehtmlul = new ParseHtmlUl();
		ParseHtmlI parsehtmli = new ParseHtmlI();
		ParseHtmlTh parsehtmlth = new ParseHtmlTh();
		ParseHtmlEmbed parsehtmlembed = new ParseHtmlEmbed();
		ParseHtmlFont parsehtmlfont = new ParseHtmlFont();

		// 为了便于支持dml没有的元素做了一个全部元素的列表
		allemap.put("a", parsehtmla);
		allemap.put("b", parsehtmlb);
		allemap.put("br", parsehtmlbr);
		allemap.put("div", parsehtmldiv);
		allemap.put("em", parsehtmlem);
		allemap.put("h1", parsehtmlh1);
		allemap.put("h2", parsehtmlh2);
		allemap.put("h3", parsehtmlh3);
		allemap.put("h4", parsehtmlh4);
		allemap.put("hr", parsehtmlhr);
		allemap.put("img", parsehtmlimg);
		allemap.put("li", parsehtmlli);
		allemap.put("ol", parsehtmlol);
		allemap.put("p", parsehtmlp);
		allemap.put("pre", parsehtmlpre);
		allemap.put("span", parsehtmlspan);
		allemap.put("strike", parsehtmlstrike);
		allemap.put("strong", parsehtmlstrong);
		allemap.put("sub", parsehtmlsub);
		allemap.put("sup", parsehtmlsup);
		allemap.put("table", parsehtmltable);
		allemap.put("tbody", parsehtmltbody);
		allemap.put("thead", parsehtmlthead);
		allemap.put("tfoot", parsehtmltfoot);
		allemap.put("td", parsehtmltd);
		allemap.put("tr", parsehtmltr);
		allemap.put("u", parsehtmlu);
		allemap.put("ul", parsehtmlul);
		allemap.put("i", parsehtmli);
		allemap.put("th", parsehtmlth);
		allemap.put("embed", parsehtmlembed);
		allemap.put("font", parsehtmlfont);

		noddatamap.put("a", parsehtmla);
		noddatamap.put("b", parsehtmlb);
		noddatamap.put("br", parsehtmlbr);
		noddatamap.put("div", parsehtmldiv);
		noddatamap.put("em", parsehtmlem);
		noddatamap.put("h1", parsehtmlh1);
		noddatamap.put("h2", parsehtmlh2);
		noddatamap.put("h3", parsehtmlh3);
		noddatamap.put("h4", parsehtmlh4);
		noddatamap.put("hr", parsehtmlhr);
		noddatamap.put("img", parsehtmlimg);
		noddatamap.put("li", parsehtmlli);
		noddatamap.put("ol", parsehtmlol);
		noddatamap.put("p", parsehtmlp);
		noddatamap.put("pre", parsehtmlpre);
		noddatamap.put("span", parsehtmlspan);
		noddatamap.put("strike", parsehtmlstrike);
		noddatamap.put("strong", parsehtmlstrong);
		noddatamap.put("sub", parsehtmlsub);
		noddatamap.put("sup", parsehtmlsup);
		noddatamap.put("table", parsehtmltable);
		noddatamap.put("tbody", parsehtmltbody);
		noddatamap.put("thead", parsehtmlthead);
		noddatamap.put("tfoot", parsehtmltfoot);
		noddatamap.put("td", parsehtmltd);
		noddatamap.put("tr", parsehtmltr);
		noddatamap.put("u", parsehtmlu);
		noddatamap.put("ul", parsehtmlul);
		noddatamap.put("i", parsehtmli);
		noddatamap.put("th", parsehtmlth);
		noddatamap.put("embed", parsehtmlembed);
		noddatamap.put("font", parsehtmlfont);

	}
	public Html2Dml(){};
	/*
	 * 构造函数
	 */
	public Html2Dml(String html, Html2DmlEngine html2dmlengine)
			throws JDOMException, IOException {
		if (html != null && !"".equals(html)) {
			this.newhtml2dmlengine = html2dmlengine;
			Element e = getRootElement(html);
			getChildren(e, html2dmlengine);
		} else {
			newhtml2dmlengine.getM_out().println("");
		}
	}

	/*
	 * 取到body元素
	 */
	private Element getRootElement(String html) throws JDOMException,
			IOException {
		Element belement = null;
		SAXBuilder builder = new SAXBuilder(CYBERNEKO_PARSER, true);
		Document doc = builder.build(new StringReader(html));
		Element htmlelement = doc.getRootElement();
		if (htmlelement != null) {
			if (htmlelement.getName().toLowerCase().equals("html")) {
				belement = (Element) htmlelement.getChildren().get(0);
			}
		}
		return belement;
	}

	/*
	 * 判断当前元素是否有子元素 并做遍历
	 */
	protected void getChildren(Element parentelement,
			Html2DmlEngine html2dmlengine) throws IOException, JDOMException {

		String useddata = html2dmlengine.getVwbcontext().getProperty("duckling.ddata");
		if (parentelement != null) {
			String pename = parentelement.getName().toLowerCase();
			for (Iterator<?> i = parentelement.getContent().iterator(); i
					.hasNext();) {
				Object c = i.next();
				if (c instanceof Element) {
					Element childelement = (Element) c;
					String ename = childelement.getName().toLowerCase();
					if ("true".equals(useddata)) {
						if (allemap.containsKey(ename)) {
							AbstractParseHtmlElement parser = (AbstractParseHtmlElement) allemap
									.get(ename);
							parser.printElement(childelement, html2dmlengine);
						} else {
							ParseHtmlOtherE parser = new ParseHtmlOtherE();
							parser.printElement(childelement, html2dmlengine);
						}
					} else {
						AbstractParseHtmlElement parser = getParser(ename);
						parser.printElement(childelement, html2dmlengine);
					}

				} else if (c instanceof Text) {
					printText(c, html2dmlengine, pename);

				}
			}
		}
	}

	private AbstractParseHtmlElement getParser(String elName) {
		if (noddatamap.containsKey(elName)) {
			return noddatamap.get(elName);
		} else {
			return new ParseHtmlOtherE();
		}
	}

	/*
	 * 打印我们不支持的元素
	 */
	protected void printErrElement(Element element,
			Html2DmlEngine html2dmlengine) throws IOException, JDOMException {
		String thisename = element.getName().toLowerCase();
		if (!thisename.matches(removeEname)) {
			html2dmlengine.getM_out().print("<");
			html2dmlengine.getM_out().print(thisename);
			List<?> attributelist = element.getAttributes();
			for (int i = 0; i < attributelist.size(); i++) {
				Attribute attribute = (Attribute) attributelist.get(i);
				html2dmlengine.getM_out().print(
						" " + attribute.getName() + "=\""
								+ attribute.getValue() + "\"");
			}
			html2dmlengine.getM_out().print(">");
			for (Iterator<?> i = element.getContent().iterator(); i.hasNext();) {
				Object c = i.next();
				if (c instanceof Element) {
					getChildren(element, html2dmlengine);
				} else if (c instanceof Text) {
					printText(c, html2dmlengine, thisename);
				}
			}
			html2dmlengine.getM_out().print("</");
			html2dmlengine.getM_out().print(thisename);
			html2dmlengine.getM_out().print(">");
		}
	}

	/*
	 * 输出Text
	 */
	private void printText(Object element, Html2DmlEngine html2dmlengine,
			String parentname) throws IOException, JDOMException {

		Text t = (Text) element;
		String s = t.getText();
		if (isNotPlugin(s)) {
			int beginindex = s.indexOf("[{");
			int endindex = s.indexOf("}]");
			String pluginstr = s.substring(beginindex + 2, endindex);

			String beginstr = s.substring(0, beginindex);
			if (!beginstr.equals("")) {
				beginstr = beginstr.replaceAll("&", "&amp;");
				beginstr = beginstr.replaceAll(">", "&gt;");
				beginstr = beginstr.replaceAll("<", "&lt;");
				html2dmlengine.getM_out().println(beginstr);
			}
			parserPluginString(pluginstr, html2dmlengine);
			String endstr = s.substring(endindex + 2, s.length());
			if (!endstr.equals("")) {
				endstr = endstr.replaceAll("&", "&amp;");
				endstr = endstr.replaceAll(">", "&gt;");
				endstr = endstr.replaceAll("<", "&lt;");
				html2dmlengine.getM_out().println(endstr);
			}
		} else {
			if (!s.equals("")) {
				s = s.replaceAll("&", "&amp;");
				s = s.replaceAll(">", "&gt;");
				s = s.replaceAll("<", "&lt;");
				if (html2dmlengine.getPreType() > 0) {
					html2dmlengine.getM_out().print(s);
				} else {
					s = s.replaceAll("[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]",
							"");

					if (!s.equals("") && "body".equalsIgnoreCase(parentname)) {// 这部操作是防止在空白地方不输入元素标签直接输入文字的处理
						html2dmlengine.getM_out().println("<p>" + s + "</p>");
					} else {
						html2dmlengine.getM_out().println(s);
					}

				}
			}
		}

	}

	/*
	 * 输出DMLString
	 */
	public String getDMLString() throws JDOMException, IOException {
		return newhtml2dmlengine.getM_outTrimmer().toString();
	}

	/*
	 * 输出DMLDomTree 返回以body作为根节点的DomTree
	 */
	public Element getDMLDomTree() throws JDOMException, IOException {
		String strDmlstr = newhtml2dmlengine.getM_outTrimmer().toString();
		Element belement = null;
		SAXBuilder builder = new SAXBuilder(CYBERNEKO_PARSER, true);
		Document doc = builder.build(new StringReader(strDmlstr));
		Element htmlelement = doc.getRootElement();
		if (htmlelement != null) {
			if (htmlelement.getName().toLowerCase().equals("html")) {
				belement = (Element) htmlelement.getChildren().get(0);
			}
		}
		return belement;

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

	/*
	 * 判断是否是plugin 只用在dml安装时候
	 */
	public boolean isNotPlugin(String str) {
		boolean reb = false;
		int beginindex = str.indexOf("[{");
		int endindex = str.indexOf("}]");
		if (beginindex != -1 && endindex != -1 && endindex > beginindex) {
			reb = true;
		}

		return reb;
	}

	/*
	 * 辅助方法解析plugin 只用在dml安装时候
	 */
	public static final String PARAM_BOUNDS = "_bounds";

	public void parserPluginString(String pluginstr,
			Html2DmlEngine html2dmlengine) {
		String PLUGIN_INSERT_PATTERN = "\\{?(INSERT)?\\s*([\\w\\._]+)[ \\t]*(WHERE)?[ \\t]*";
		PatternCompiler compiler = new Perl5Compiler();
		Pattern m_pluginPattern = null;
		try {
			m_pluginPattern = compiler.compile(PLUGIN_INSERT_PATTERN);
		} catch (MalformedPatternException e) {
			e.printStackTrace();
		}
		PatternMatcher matcher = new Perl5Matcher();
		if (matcher.contains(pluginstr, m_pluginPattern)) {
			MatchResult res = matcher.getMatch();

			String plugin = res.group(2);
			String args = pluginstr
					.substring(
							res.endOffset(0),
							pluginstr.length()
									- (pluginstr.charAt(pluginstr.length() - 1) == '}' ? 1
											: 0));
			try {

				Map<String, String> arglist = parseArgs(args);
				outPlugin(plugin, arglist, html2dmlengine);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static final String PARAM_CMDLINE = "_cmdline";
	public static final String PARAM_BODY = "_body";

	public Map<String, String> parseArgs(String argstring) throws IOException {
		HashMap<String, String> arglist = new HashMap<String, String>();

		//
		// Protection against funny users.
		//
		if (argstring == null)
			return arglist;

		StringReader in = new StringReader(argstring);
		StreamTokenizer tok = new StreamTokenizer(in);
		int type;

		String param = null;
		String value = null;

		tok.eolIsSignificant(true);

		boolean potentialEmptyLine = false;
		boolean quit = false;

		while (!quit) {
			String s;

			type = tok.nextToken();

			switch (type) {
			case StreamTokenizer.TT_EOF:
				quit = true;
				s = null;
				break;

			case StreamTokenizer.TT_WORD:
				s = tok.sval;
				potentialEmptyLine = false;
				break;

			case StreamTokenizer.TT_EOL:
				quit = potentialEmptyLine;
				potentialEmptyLine = true;
				s = null;
				break;

			case StreamTokenizer.TT_NUMBER:
				s = Integer.toString(new Double(tok.nval).intValue());
				potentialEmptyLine = false;
				break;

			case '\'':
				s = tok.sval;
				break;

			default:
				s = null;
			}

			//
			// Assume that alternate words on the line are
			// parameter and value, respectively.
			//
			if (s != null) {
				if (param == null) {
					param = s;
				} else {
					value = s;

					arglist.put(param, value);

					param = null;
				}
			}
		}

		//
		// Now, we'll check the body.
		//

		if (potentialEmptyLine) {
			StringWriter out = new StringWriter();
			FileUtil.copyContents(in, out);

			String bodyContent = out.toString();

			if (bodyContent != null) {
				arglist.put(PARAM_BODY, bodyContent);
			}
		}

		return arglist;
	}

	/*
	 * 输出plugin在dml安装时候用
	 */
	public void outPlugin(String name, Map<String, String> map,
			Html2DmlEngine html2dmlengine) {
		html2dmlengine.getM_out().println("<D_plugin>");
		html2dmlengine.getM_out().println(
				"<D_parameter name=\"" + name + "\"/>");

		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			html2dmlengine.getM_out().println(
					"<D_parameter "
							+ key.trim().replaceAll(
									"[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]",
									"") + "=\"" + value + "\"/>");

		}
		html2dmlengine.getM_out().println("</D_plugin>");

	}

}
