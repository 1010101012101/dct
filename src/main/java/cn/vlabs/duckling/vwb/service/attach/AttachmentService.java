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

package cn.vlabs.duckling.vwb.service.attach;

import java.util.Date;
import java.util.List;

import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.rest.IFileSaver;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * Introduction Here.
 * @date Feb 3, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public interface AttachmentService {
	/**
	 * 查询附件的元信息
	 * @param session
	 * @param docid
	 * @return
	 */
	MetaInfo getMetaInfo(int docid);
	/**
	 * 创建文档
	 * @param parentId	附件所在页面
	 * @param author	作者
	 * @param isPub		是否是公开的文档
	 * @param filename	文件名
	 * @param filesize	文件长度
	 * @param in		文件输入流
	 * @return			新创建的文档的ID
	 */
	int createDocument(int siteId, String parentId, String author, boolean isPub,StreamInfo stream);
	/**
	 * 更新文档
	 * @param session
	 * @param i
	 * @param comment
	 * @param stream
	 * @return
	 */
	UpdateInfo updateDocument(int siteId, String parentId,String author, int docid,StreamInfo stream);
	/**
	 * 下载最新文件内容
	 * @param vwbsession
	 * @param docid
	 * @param fs
	 */
	void getContent(int docid, IFileSaver fs);
	/**
	 * 下载文件特定版本的内容
	 * @param vwbsession
	 * @param docid
	 * @param version
	 * @param fs
	 */
	void getContent(int docid, String version, IFileSaver fs);
	/**
	 * Accquire document direct access url.
	 * @param docid
	 * @return
	 */
	String getDirectUrl(int docid);
	/**
	 * 返回最近上传的文档
	 * @return
	 */
	List<CLBAttachment> recentUploaded(int siteId, int num);
	/**
	 * 以关键字搜索文件名的搜索结果
	 * @param filename
	 * @return
	 */
	List<CLBAttachment> search(int siteId, String filename);
	/**
	 * @param docid
	 * @param version
	 * @return
	 */
	String getDirectUrl(int docid, String version);
	/**
	 * @param time
	 * @param end
	 * @return
	 */
	List<CLBAttachment> getCLBAttatchmentChanged(int siteId, Date time, Date end);
}