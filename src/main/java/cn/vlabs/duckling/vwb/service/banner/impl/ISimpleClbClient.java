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
package cn.vlabs.duckling.vwb.service.banner.impl;

import java.io.InputStream;

import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.rest.IFileSaver;

public interface ISimpleClbClient {

	int createFile(String filename, long length, InputStream in);
	
	int updateFile(int docid, String filename, long length, InputStream in);
	
	 void getContent(int docid, IFileSaver fs);
	 
	 void getContent(int[] docids, IFileSaver fs);
	 
	void init(String serverUrl, String clbUserName, String clbPassword,String clbVersion,String fileAccessMode);
	
	void destroy();
	
	MetaInfo getMeta(int docid);
	
	String getDirectURL(int imgId);
	
	String getFileAccessMode();
	
	void delete(int docid);
}
