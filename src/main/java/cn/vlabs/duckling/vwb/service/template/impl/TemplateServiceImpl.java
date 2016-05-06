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

package cn.vlabs.duckling.vwb.service.template.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.document.CreateInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.vwb.service.template.RemoteTemplate;
import cn.vlabs.duckling.vwb.service.template.SiteTemplate;
import cn.vlabs.duckling.vwb.service.template.TemplateService;
import cn.vlabs.rest.IFileSaver;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * 模板服务
 * 
 * @date 2013-4-1
 * @author xiejj@cstnet.cn
 */
public class TemplateServiceImpl implements TemplateService {
	private class TemplateSaver implements IFileSaver {
		private File file;

		public File getTmpFile() {
			return file;
		}

		@Override
		public void save(String filename, InputStream in) {
			try {
				file = File.createTempFile("template", ".zip", tmppath);
				FileUtil.copyInputStreamToFile(in, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private DocumentService docService;
	private TemplateDAO templateDao;
	private File templateRoot;

	private File remoteTemplateRoot;
	private File tmppath;
	public void setTempPath(String tempPath){
		tmppath= new File(tempPath);
		if (!tmppath.exists()){
			tmppath.mkdirs();
		}
	}

	private void download(RemoteTemplate template, String templatePath) {
		if (template != null) {
			TemplateSaver fs = new TemplateSaver();
			docService.getContent(template.getClbId(),fs);
			Zipper.unZip(fs.getTmpFile().getAbsolutePath(), templatePath);
		}
	};

	private boolean existInLocal(String templateName) {
		File templatePath = new File(templateRoot, templateName);
		return (templatePath.exists());
	}

	private boolean existInRemote(String templateName) {
		return templateDao.getTemplate(templateName) != null;
	}

	private SiteTemplate getLocalTemplate(String templateName) {
		File templatePath = new File(templateRoot, templateName);
		if (templatePath.exists()) {
			SiteTemplate template = new SiteTemplate();
			template.setName(templateName);
			template.setPath(templatePath.getAbsolutePath());
			template.setType(SiteTemplate.TYPE_TEMPLATE);
			return template;
		}
		return null;
	}

	private ArrayList<SiteTemplate> readFromDir() {
		File[] dirs = templateRoot.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() && !pathname.getName().equals("admin")) {
					return true;
				} else {
					return false;
				}
			}
		});
		ArrayList<SiteTemplate> result = new ArrayList<SiteTemplate>();
		for (File dir : dirs) {
			SiteTemplate template = new SiteTemplate();
			template.setName(dir.getName());
			template.setPath(dir.getAbsolutePath());
			template.setType(SiteTemplate.TYPE_TEMPLATE);
			result.add(template);
		}
		return result;
	}

	private UpdateInfo uploadToClb(String templateName, File tempFile)
			throws FileNotFoundException {
		StreamInfo stream = new StreamInfo();
		stream.setFilename(templateName + ".zip");
		FileInputStream in = new FileInputStream(tempFile);
		stream.setInputStream(in);
		stream.setLength(tempFile.length());

		CreateInfo ci = new CreateInfo();
		ci.setIsPub(0);
		ci.setKeywords("");
		ci.setSummary("");
		ci.setTitle(templateName + ".zip");
		return docService.createDocument(ci, stream);
	}

	@Override
	public SiteTemplate checkAndDownload(String templateName) {
		SiteTemplate template = getLocalTemplate(templateName);
		if (template != null) {
			return template;
		}

		RemoteTemplate remoteTemplate = templateDao.getTemplate(templateName);
		if (remoteTemplate != null) {
			File templatePath = new File(remoteTemplateRoot, templateName);
			if (!templatePath.exists()) {
				download(remoteTemplate, templatePath.getAbsolutePath());
			}
			remoteTemplate.setPath(templatePath.getAbsolutePath());
			return remoteTemplate;
		}
		return null;
	}

	@Override
	public boolean exist(String templateName) {
		return (existInLocal(templateName) || existInRemote(templateName));
	}

	@Override
	public List<String> getAllSiteTemplate() {
		ArrayList<SiteTemplate> localTemplates = readFromDir();
		List<RemoteTemplate> remoteTemplates = templateDao.getAllTemplate();
		List<String> r = new ArrayList<String>();
		for (SiteTemplate template:localTemplates){
			r.add(template.getName());
		}
		for (SiteTemplate template:remoteTemplates){
			r.add(template.getName());
		}
		return r;		
	}
	
	@Override
	public List<SiteTemplate> getAllLocalTemplate(){
		ArrayList<SiteTemplate> localTemplates = readFromDir();
		return localTemplates;
	}
	

	public void setClbConnection(CLBConnection conn) {
		this.docService = CLBServiceFactory.getDocumentService(conn);
	}

	public void setTemplateDAO(TemplateDAO templateDao) {
		this.templateDao = templateDao;
	}

	public void setTemplateRoot(String templateRoot) {
		this.templateRoot = new File(templateRoot);
	}

	public void setRemoteTemplateRoot(String path) {
		this.remoteTemplateRoot = new File(path);
		if (!remoteTemplateRoot.exists()){
			remoteTemplateRoot.mkdirs();
		}
	}

	@Override
	public void upload(String templateName, String templatePath, String type)
			throws IOException {
		File tempFile = File.createTempFile("template", ".zip", tmppath);
		Zipper.zip(templatePath, tempFile.getAbsolutePath());

		UpdateInfo ui = uploadToClb(templateName, tempFile);

		RemoteTemplate template = new RemoteTemplate();
		template.setClbId(ui.getDocid());
		template.setType(type);
		template.setName(templateName);
		if (templateDao.getTemplate(templateName)==null){
			templateDao.create(template);
		}else{
			templateDao.update(template);
		}
	}

	@Override
	public void deleteTemplate(String templateName) {
		RemoteTemplate remoteTemplate = templateDao.getTemplate(templateName);
		docService.delete(remoteTemplate.getClbId());
		templateDao.deleteTemplate(templateName);
	}
}
