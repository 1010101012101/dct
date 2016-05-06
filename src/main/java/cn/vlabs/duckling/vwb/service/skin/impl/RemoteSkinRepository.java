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

package cn.vlabs.duckling.vwb.service.skin.impl;

import java.io.InputStream;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.clb.api.trivial.ITrivialSpaceService;
import cn.vlabs.clb.api.trivial.TrivialSpace;
import cn.vlabs.duckling.vwb.service.skin.RemoteSkin;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * 远程CLB的存储方式
 * 
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class RemoteSkinRepository {
	private CLBConnection conn;
	private StreamInfo createStreamInfo(InputStream in, int length) {
		StreamInfo stream = new StreamInfo();
		stream.setFilename("skin.zip");
		stream.setInputStream(in);
		stream.setLength(length);
		return stream;
	}

	private DocumentService getDocumentService() {
		return CLBServiceFactory.getDocumentService(conn);
	}

	private ITrivialSpaceService getTrivialService() {
		return CLBServiceFactory.getTrivialSpaceService(conn);
	}

	public RemoteSkin createSkin(InputStream in, int length) {
		DocumentService docs = getDocumentService();
		CreateInfo ci = new CreateInfo();
		ci.setIsPub(0);
		ci.setKeywords("");
		ci.setSummary("");
		ci.setTitle("skin.zip");

		UpdateInfo ui = docs.createDocument(ci, createStreamInfo(in, length));
		RemoteSkin skin = new RemoteSkin();
		skin.setClbId(ui.getDocid());

		ITrivialSpaceService trivials = getTrivialService();
		TrivialSpace space = trivials.createTrivialSpace(ui.getDocid(), ui.getVersion(), "skin");
		skin.setSpace(space.getSpaceName());

		return skin;
	}

	public void removeSkin(RemoteSkin skin) {
		getTrivialService().removeTrivialSpace(skin.getSpace());
		getDocumentService().delete(skin.getClbId());
	}

	public void setClbConnection(CLBConnection conn) {
		this.conn = conn;
	}

	public void updateSkin(int docid, String space, InputStream in, int length) {
		DocumentService docs = getDocumentService();
		StreamInfo stream = createStreamInfo(in, length);
		UpdateInfo ui = docs.update(docid, "", stream);

		ITrivialSpaceService trivials = getTrivialService();
		trivials.updateTrivialSpace(docid, ui.getVersion(), "skin", space);
	}
}
