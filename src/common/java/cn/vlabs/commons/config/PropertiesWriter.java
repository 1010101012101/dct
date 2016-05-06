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
package cn.vlabs.commons.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

public class PropertiesWriter {
	public PropertiesWriter() {
		lines = new ArrayList<Line>();
		keyMaps = new HashMap<String, KeyValuePair>();
	}

	public void load(FileInputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				Comment comment = new Comment(line, true);
				lines.add(comment);
			} else {
				if (line.trim().length() == 0)
					lines.add(EmptyLine.line);
				else {
					KeyValuePair pair = KeyValuePair.parse(line);
					if (pair != null) {
						keyMaps.put(pair.key, pair);
						lines.add(pair);
					}
				}
			}

		}
	}

	public String getProperty(String key) {
		KeyValuePair pair = keyMaps.get(key);
		if (pair != null && !pair.isDeleted())
			return pair.getValue();
		return null;
	}

	public void remove(String key) {
		KeyValuePair pair = keyMaps.get(key);
		if (pair != null)
			pair.setDeleted(true);
	}

	public void setProperty(String key, String value) {
		KeyValuePair pair = keyMaps.get(key);
		if (pair == null) {
			pair = new KeyValuePair();
			pair.setKey(key);
			lines.add(pair);
		}
		pair.setDeleted(false);
		pair.setValue(value);
	}

	public boolean containsKey(String key) {
		return keyMaps.keySet().contains(key);
	}

	public void store(FileOutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out);
		for (Line line : lines) {
			writer.write(line.toString() + "\n");
		}
		writer.flush();
	}

	private HashMap<String, KeyValuePair> keyMaps;

	private ArrayList<Line> lines;

	private static interface Line {
	}

	private static class EmptyLine implements Line {
		public String toString() {
			return "";
		}

		public static final EmptyLine line = new EmptyLine();
	}

	private static class KeyValuePair implements Line {
		public KeyValuePair(String key, String value) {
			setKey(key);
			setValue(value);
			deleted = false;
		}

		public void setDeleted(boolean b) {
			this.deleted = b;
		}

		public static KeyValuePair parse(String line) {
			int pos = line.indexOf('=');
			if (pos == -1)
				return null;
			String keyPart = line.substring(0, pos);
			String valuePart = line.substring(pos + 1);
			KeyValuePair pair = new KeyValuePair(keyPart.trim(), valuePart
					.trim());
			return pair;
		}

		public KeyValuePair() {
			deleted = false;
		}

		public String toString() {
			if (isDeleted())
				return "";
			else
				return getKey() + "=" + getValue();
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void setValue(String value) {
			this.value = convert(value);
		}

		public String getValue() {
			return value;
		}

		public boolean isDeleted() {
			return deleted;
		}

		private String convert(String str){
			String tmp;
			StringBuffer sb = new StringBuffer(1000);
			char c;
			int i, j;
			sb.setLength(0);
			for(i = 0;i<str.length();i++){
				c = str.charAt(i);
				if (c > 255) {
					sb.append("\\u");
					j = (c >>> 8);
					tmp = Integer.toHexString(j);
					if (tmp.length() == 1) sb.append("0");
					sb.append(tmp);
					j = (c & 0xFF);
					tmp = Integer.toHexString(j);
					if (tmp.length() == 1) sb.append("0");
					sb.append(tmp);
				}
				else {
					sb.append(c);
				}
			}
			return sb.toString();
		}

		private String key;

		private String value;

		private boolean deleted;
	}

	private static class Comment implements Line {
		public Comment(String comment, boolean hasPrefix) {
			this.content = comment;
		}

		public Comment(String comment) {
			if (comment == null)
				this.content = "\n";
			else {
				comment = comment.trim();
				if (comment.length() == 0)
					this.content = "\n";
				else
					this.content = "#" + comment;
			}

		}

		public String toString() {
			return content;
		}

		private String content;
	}
}
