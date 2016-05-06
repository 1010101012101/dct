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
package cn.vlabs.duckling.vwb.service.banner.provider;

import java.io.InputStream;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBPasswdInfo;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.duckling.vwb.service.banner.impl.ISimpleClbClient;
import cn.vlabs.rest.IFileSaver;
import cn.vlabs.rest.ServiceContext;
import cn.vlabs.rest.stream.StreamInfo;

public class SimpleClbClient implements ISimpleClbClient {
    private CLBConnection conn;
    public static final int MAX_CONN = 20;
    private String fileAccessMode;
    

    public CLBConnection getConn() {
        return conn;
    }

    public void setConn(CLBConnection conn) {
        this.conn = conn;
    }

    @Override
    public int createFile(String filename, long length, InputStream in) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        CreateInfo info = new CreateInfo();
        info.title = filename;
        info.isPub = 1;
        StreamInfo stream = new StreamInfo();
        stream.setFilename(filename);
        stream.setLength(length);
        stream.setInputStream(in);
        return dService.createDocument(info, stream).docid;
    }

    @Override
    public int updateFile(int docid, String filename, long length, InputStream in) {
        return 0;
    }

    @Override
    public void getContent(int docid, IFileSaver fs) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        dService.getContent(docid, fs);
    }
    
    @Override
	public void getContent(int[] docids, IFileSaver fs) {
    	DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        dService.getContent(docids, fs);
	}
    

    @Override
    public void init(String serverUrl, String clbUserName, String clbPassword,String clbVersion,String fileAccessMode) {
        ServiceContext.setMaxConnection(MAX_CONN, MAX_CONN);
        CLBPasswdInfo auth = new CLBPasswdInfo();
        auth.setUsername(clbUserName);
        auth.setPassword(clbPassword);
        this.fileAccessMode = fileAccessMode;
        this.conn = CLBServiceFactory.getClbConnection(serverUrl,auth,true,clbVersion);
    }

    @Override
    public void destroy() {
        conn = null;
    }

    @Override
    public MetaInfo getMeta(int docid) {
        return null;
    }

    @Override
    public String getDirectURL(int imgId) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        return dService.getContentURL(imgId, "latest");
    }

    @Override
    public String getFileAccessMode() {
        return fileAccessMode;
    }

	@Override
	public void delete(int docid) {
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		dService.delete(docid);
	}


}
