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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.duckling.SpringBaseTest;
import cn.vlabs.rest.IFileSaver;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * @date 2013-5-21
 * @author xiejj
 */
public class AttachmentServiceTest extends SpringBaseTest {
	private static class TestSaver implements IFileSaver {
		private String content;
		public String  getContent(){
			return content;
		}
		@Override
		public void save(String filename, InputStream in) {
			try {
				content = FileUtils.readFully(new InputStreamReader(in, "UTF-8"));
			} catch (IOException e) {
				
			}

		}
	}
	private AttachmentService attach;
	private String parentPage = "1";

	private int siteId = 1;

	private StreamInfo getStreamInfo(String filename)
			throws FileNotFoundException {
		File file = new File(filename);
		StreamInfo streamInfo = new StreamInfo();
		streamInfo.setFilename(file.getName());
		streamInfo.setLength(file.length());
		streamInfo.setInputStream(new FileInputStream(file));
		return streamInfo;
	}

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		this.attach = (AttachmentService) factory.getBean("attachmentService");
	}

	@After
	protected void tearDown() throws Exception {
		this.attach = null;
		super.tearDown();
	}

	@Test
	public void testGetMetaInfo() throws IOException {

		StreamInfo stream = getStreamInfo("test/VWBContext_Test.xml");
		int docid = attach.createDocument(siteId, parentPage, "admin@root.umt",
				false, stream);
		stream.getInputStream().close();
		assertTrue(docid > 0);

		MetaInfo meta = attach.getMetaInfo(docid);
		assertEquals(meta.getFilename(), stream.getFilename());
		assertEquals(meta.getVersion(), "1");
		// 验证更新操作
		stream = getStreamInfo("WebRoot/WEB-INF/conf/VWBContext.xml");
		UpdateInfo updateInfo = attach.updateDocument(siteId, parentPage,
				"admin@root.umt", docid, stream);
		stream.getInputStream().close();
		// 验证更新以后的MetaInfo
		assertEquals(updateInfo.getVersion(), "2");
		assertEquals(updateInfo.getDocid(), docid);
		meta = attach.getMetaInfo(docid);
		assertEquals(meta.getVersion(), "2");

		// 验证最近更新
		List<CLBAttachment> attachs = attach.recentUploaded(siteId, 1);
		assertEquals(attachs.size(), 1);
		assertEquals(attachs.get(0).getClbId(), docid);
		assertEquals(attachs.get(0).getCLBVersion(), "2");
		// 验证getCLBAttatchmentChanged
		Date before = new Date();
		before = DateUtils.addHours(before, -1);
		attachs = attach.getCLBAttatchmentChanged(siteId, before, new Date());

		boolean found = false;
		assertTrue(attachs.size() > 0);
		for (CLBAttachment att : attachs) {
			if (att.getClbId() == docid && "2".equals(att.getCLBVersion())) {
				found = true;
			}
		}
		assertTrue("new updated attachment should be found", found);

		// 验证search
		attachs = attach.search(siteId, "VWBContext");
		assertNotNull(attachs);
		assertTrue(attachs.size() >= 2);
		assertTrue(attachs.get(0).getFileName().startsWith("VWBContext"));

		String url = attach.getDirectUrl(docid);
		assertNotNull(url);

		String url2 = attach.getDirectUrl(docid, "2");
		assertNotNull(url2);

		// 验证下载
		TestSaver saver = new TestSaver();
		attach.getContent(docid,saver);
		String contentLatest = saver.getContent();
		
		attach.getContent(docid, "2", saver);
		String contentV2 = saver.getContent();
		assertEquals(contentLatest, contentV2);
	}
}
