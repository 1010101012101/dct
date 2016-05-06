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
package cn.vlabs.duckling.vwb.service.share;

public class AccessRecord {
	public static final int RANDOM_LENGTH = 24;
	public static final int URL_LENGTH = 256;
	public static final int RECORD_LENGTH = 4 + 8 + 8 + RANDOM_LENGTH * 2
			+ URL_LENGTH * 2;

	private int ID;
	private String hash;
	private String URL;
	private long accessTime;
	private long shareTime;

	public AccessRecord() {
		this.ID = 0;
		this.hash = null;
		this.URL = null;
		this.accessTime = 0;
		this.shareTime = 0;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return this.ID;
	}

	public void sethash(String hash) {
		StringBuilder builder = null;
		if (hash != null)
			builder = new StringBuilder(hash);
		else
			builder = new StringBuilder(RANDOM_LENGTH);
		builder.setLength(RANDOM_LENGTH); // 最长字符长度

		this.hash = builder.toString();
		;
	}

	public String gethash() {
		return this.hash;
	}

	public void setURL(String URL) {
		/*
		 * StringBuilder builder = null; if(URL != null) builder = new
		 * StringBuilder(URL); else builder = new StringBuilder(URL_LENGTH);
		 * builder.setLength(URL_LENGTH); // 最长字符长度
		 * 
		 * this.URL = builder.toString();;
		 */
		this.URL = URL;
	}

	public String getURL() {
		return this.URL;
	}

	public void setAccessTime(long accessTime) {
		this.accessTime = accessTime;
	}

	public long getAccessTime() {
		return this.accessTime;
	}

	public void setShareTime(long shareTime) {
		this.shareTime = shareTime;
	}

	public long getShareTime() {
		return this.shareTime;
	}

	public static int size() {
		return 4 + 8 + 8 + RANDOM_LENGTH * 2 + URL_LENGTH * 2;
	}
}
