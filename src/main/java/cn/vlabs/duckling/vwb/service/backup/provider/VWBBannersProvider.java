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

package cn.vlabs.duckling.vwb.service.backup.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.vwb.service.backup.pagenation.CurrentPage;
import cn.vlabs.duckling.vwb.service.backup.pagenation.PaginationHelper;
import cn.vlabs.duckling.vwb.service.backup.pagenation.VWBBannerRowMapper;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.impl.ISimpleClbClient;
import cn.vlabs.duckling.vwb.service.banner.provider.SystemXmlBanner;
import cn.vlabs.rest.IFileSaver;

/**
 * Introduction Here.
 * @date May 7, 2010
 * @author zzb
 */
public class VWBBannersProvider extends BaseTemplateProvider {
	
	private static final String FILENAME = "/banner.xml";
	private static final String BANNER_PATH = "/bannerLoc/";
	 private ISimpleClbClient simpleClbClient;
	 
	public void setSimpleClbClient(ISimpleClbClient simpleClbClient) {
		this.simpleClbClient = simpleClbClient;
	}
	
	private static class BackupSaver implements IFileSaver{
		private String basePath;
		public BackupSaver(String basePath){
			this.basePath = basePath;
		}
		private String fileName;
		public void setFileName(String fileName){
			this.fileName = fileName;
		}
		public String getFileName(){
			return fileName;
		}
		
		private File getFullPath(){
			return new File(basePath+fileName);
		}
		@Override
		public void save(String filename, InputStream in) {
			this.setFileName(filename);
			File targetFile = getFullPath();
			try {
				FileUtil.copyInputStreamToFile(in, targetFile);
			} catch (IOException e) {
			}
		}
	}
	

	@Override
	public boolean backup (int siteId,String templatePath) {
		new File(templatePath).mkdirs();
		String filePath = templatePath + FILENAME;
		File file = new File(filePath);
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			return false;
		}
		List<Banner> banners = getBanners(siteId);
		List<SystemXmlBanner> xmlBanners=new ArrayList<SystemXmlBanner>();
		for(Banner banner : banners){
			String bannerName = banner.getDirName();
			String targetDir = templatePath + BANNER_PATH + bannerName+"/images/";
			SystemXmlBanner xmlBanner=new SystemXmlBanner();
			int leftPictureClbId=banner.getLeftPictureClbId();
			if(leftPictureClbId>0){
				BackupSaver leftPictureSaver=new BackupSaver(targetDir);
				simpleClbClient.getContent(leftPictureClbId, leftPictureSaver);
				xmlBanner.setLeftName(leftPictureSaver.getFileName());
			}
			
			int rightPictureClbId=banner.getRightPictureClbId();
			if(rightPictureClbId>0){
				BackupSaver rightPictureSaver=new BackupSaver(targetDir);
				simpleClbClient.getContent(rightPictureClbId, rightPictureSaver);
				xmlBanner.setRightName(rightPictureSaver.getFileName());
			}
			
			int middlePictureClbId=banner.getMiddlePictureClbId();
			if(middlePictureClbId>0){
				BackupSaver middlePictureSaver=new BackupSaver(targetDir);
				simpleClbClient.getContent(middlePictureClbId, middlePictureSaver);
				xmlBanner.setMiddleName(middlePictureSaver.getFileName());
			}
			
			int cssClbId=banner.getCssClbId();
			String cssDir = templatePath + BANNER_PATH + bannerName+"/";
			if(cssClbId>0){
				BackupSaver cssSaver=new BackupSaver(cssDir);
				simpleClbClient.getContent(cssClbId, cssSaver);
			}
			xmlBanner.setId(banner.getId());
			xmlBanner.setFirstTitle(banner.getFirstTitle());
			xmlBanner.setSecondTitle(banner.getSecondTitle());
			xmlBanner.setThirdTitle(banner.getThirdTitle());
			xmlBanner.setName(banner.getName());
			xmlBanner.setDirName(banner.getDirName());
			xmlBanner.setType(banner.getType());
			xmlBanner.setStatus(banner.getStatus());
			xmlBanner.setCreator(banner.getCreator());
			xmlBanner.setCreatedTime(banner.getCreatedTime());
			xmlBanner.setOwnedtype(banner.getOwnedtype());
			xmlBanner.setBannerProfile();
			xmlBanners.add(xmlBanner);
		}
		
		XSTREAM.toXML(xmlBanners, writer);
		return true;
	}

	/**
	 * Brief Intro Here
	 * @param
	 */
	private List<Banner> getBanners(int siteId) {
		String sqlCountRows = "select count(*) from vwb_banner where siteId="+siteId;
		String sqlFetchRows = "select * from vwb_banner where siteId="+siteId;
		
		List<Banner> banners = new ArrayList<Banner>();
		
		PaginationHelper<Banner> ph = new PaginationHelper<Banner>();
		int pageNo = 0;
		CurrentPage<Banner> cp;
		do {
			pageNo ++ ;
			cp = ph.fetchPage(getJdbcTemplate(), sqlCountRows,
					sqlFetchRows, null, pageNo, PAGESIZE, new VWBBannerRowMapper());
			List<Banner> items = cp.getPageItems();
			banners.addAll(items);
		} while(cp.getPagesAvailable() > cp.getPageNumber());
		return banners;
	}
	
}
