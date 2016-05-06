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
package cn.vlabs.commons.xml;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLReader {
	protected XMLReader(Element root) {
		this.root = root;
	}

	public static XMLReader newReader(String xml, String rootElement) {
		Element root;
		try {
			StringReader reader = new StringReader(xml);
			InputSource source = new InputSource(reader);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder		dombuilder = factory.newDocumentBuilder();
			root = dombuilder.parse(source).getDocumentElement();
			reader.close();
			if (rootElement.equals(root.getTagName())) {
				return new XMLReader(root);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static XMLReader newReader(InputStream xmlIn, String rootElement) {
		Element root;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder		dombuilder = factory.newDocumentBuilder();
			root = dombuilder.parse(xmlIn).getDocumentElement();
			if (rootElement.equals(root.getTagName())) {
				return new XMLReader(root);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Iterator iterator(String field) {
		ArrayList<Node> list = getElementsByTagName(field);
		if (list != null && list.size() >= 1) {
			return new NodeIterator(list);
		} else
			return NodeIterator.NULL_ITER;
	}

	private ArrayList<Node> getElementsByTagName(String field) {
		ArrayList<Node> result = new ArrayList<Node>();
		NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equals(field)) {
				result.add(node);
			}
		}
		return result;
	}

	public String getInnnerText() {
		Node child = root.getFirstChild();
		if (child != null)
			return child.getNodeValue();
		else
			return null;
	}

	public XMLReader getSubElement(String field) {
		ArrayList<Node> nodes = getElementsByTagName(field);
		if (nodes.size() != 0)
			return new XMLReader((Element) nodes.get(0));
		else
			return null;
	}

	public String getString(String field) {
		NodeList list = root.getElementsByTagName(field);
		if (list != null && list.getLength() >= 1) {
			Node child = list.item(0).getFirstChild();
			if (child != null)
				return child.getNodeValue();
			else
				return null;
		} else
			return null;
	}

	public boolean getBoolean(String field) {
		String value = getString(field);
		return Boolean.parseBoolean(value);
	}
	
	public int getInt(String field) {
		String value = getString(field);
		return Integer.parseInt(value);
	}

	public long getLong(String field) {
		String value = getString(field);
		return Long.parseLong(value);
	}

	public Date getDate(String field) {
		String dateString = getString(field);
		Date d = DateFormatter.parse(dateString);
		return d;
	}

	private void setRoot(Element root) {
		this.root = root;
	}

	private Element root;

//    protected static DocumentBuilder dombuilder;
//	static {
//		try {
//			DocumentBuilderFactory factory = DocumentBuilderFactory
//					.newInstance();
//			dombuilder = factory.newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			// Only occured on debug
//			e.printStackTrace();
//		}
//	}

	private static class NodeIterator implements Iterator {
		NodeIterator(ArrayList<Node> list) {
			this.list = list;
			if (list != null) {
				index = 0;
				size = list.size();
				r = new XMLReader((Element) list.get(0));
			}
		}

		public boolean hasNext() {
			return (list != null && index < size);
		}

		public Object next() {
			r.setRoot((Element) list.get(index));
			index++;
			return r;
		}

		public void remove() {
			// do nothing
		}

		private int size;

		private int index;

		private ArrayList<Node> list;

		private XMLReader r;

		public static final NodeIterator NULL_ITER = new NodeIterator(null);
	}

}
