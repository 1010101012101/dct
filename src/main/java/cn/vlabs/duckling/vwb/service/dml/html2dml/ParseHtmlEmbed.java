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
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;

import cn.vlabs.duckling.common.util.Base64;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */

public class ParseHtmlEmbed extends AbstractParseHtmlElement {
	private static final String[] ATTRIBUTE_NAMES = { "class", "style", "alt",
			"height", "width", "type", "pluginspage", "wmode" };
	private static final String ATTRIBUTE_SRC="src";

	@Override
	public void printAttribute(Element e, Html2DmlEngine html2dmlengine) {
		PrintWriter writer = html2dmlengine.getM_out();
		for (String attr:ATTRIBUTE_NAMES){
			String attrValue=e.getAttributeValue(attr);
			if (StringUtils.isNotBlank(attrValue)){
				writer.printf(" %s=\"%s\"", attr, e.getAttributeValue(attr));
			}
		}
		
		String srcValue=e.getAttributeValue(ATTRIBUTE_SRC);
		if (srcValue!=null){
			String dmlSrc=html2dmlengine.findAttachment(srcValue, html2dmlengine);
			if (dmlSrc!=null){
				writer.printf(" %s=\"%s\"", "dmlsrc", dmlSrc);
			}else{
				writer.printf(" %s=\"%s\"", "src", srcValue);
			}
		}
	}

	@Override
	public void printElement(Element e, Html2DmlEngine html2dmlengine) {
		html2dmlengine.getM_out().print("<embed ");
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
			html2dmlengine.getM_out().print("</embed >");
		} else {
			html2dmlengine.getM_out().println("</embed >");
		}
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		try {
			byte[] b = Base64.decode(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

}
