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

package cn.vlabs.duckling.vwb.service.init.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.util.FileUtil;
import cn.vlabs.duckling.vwb.service.banner.Banner;
import cn.vlabs.duckling.vwb.service.banner.impl.ISimpleClbClient;
import cn.vlabs.duckling.vwb.service.banner.provider.SystemXmlBanner;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigReader;

import com.thoughtworks.xstream.XStream;

/**
 * @date 2013-3-31
 * @author xiejj
 */
public class BannerInitProvider extends ContainerBaseDAO implements InitProvider {
	private static final String createBannerSQL = "insert into vwb_banner "
			+ "(siteId,dirName,name,status,type,creator,createdTime,leftPictureClbId,rightPictureClbId,middlePictureClbId,cssClbId,pageId,ownedtype,bannerProfile) "
			+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String REPLACE_LEFT_BANNER = "REPLACE_LEFT_BANNER";
	private static String REPLACE_RIGHT_BANNER = "REPLACE_RIGHT_BANNER";
	private static String REPLCAE_MIDDLE_BANNER = "REPLCAE_MIDDLE_BANNER";

	 private ISimpleClbClient simpleClbClient;
	 
	private void createBanner(final int siteId,Banner banner) {
		final Banner tempBanner = banner;
		if (banner == null)
			return;
		banner.setBannerProfile();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				int i = 0;
				PreparedStatement ps = conn
						.prepareStatement(createBannerSQL);
				ps.setInt(++i, siteId);
				ps.setString(++i, tempBanner.getDirName());
				ps.setString(++i, tempBanner.getName());
				ps.setInt(++i, tempBanner.getStatus());
				ps.setInt(++i, tempBanner.getType());
				ps.setString(++i, tempBanner.getCreator());
				ps.setTimestamp(++i, new Timestamp(tempBanner.getCreatedTime()
						.getTime()));
				ps.setInt(++i, tempBanner.getLeftPictureClbId());
				ps.setInt(++i, tempBanner.getRightPictureClbId());
				ps.setInt(++i, tempBanner.getMiddlePictureClbId());
				ps.setInt(++i, tempBanner.getCssClbId());
				ps.setInt(++i, tempBanner.getBannerTitle());
				ps.setString(++i, tempBanner.getOwnedtype());
				ps.setString(++i, tempBanner.getBannerProfile());
				return ps;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<Banner> initBannerFromXml(String bannerfilename,String bannerDir,int siteId)
			throws FileNotFoundException {
		XStream xstream = new ConfigReader().getStream();
		Reader reader = new InputStreamReader(new FileInputStream(bannerfilename), Charset.forName("UTF-8"));
		List<SystemXmlBanner> systemXmlBanners = (List<SystemXmlBanner>) xstream.fromXML(reader);
		List<Banner> readBanners = new ArrayList<Banner>();
		if(systemXmlBanners!=null&&systemXmlBanners.size()>0){
			for(SystemXmlBanner banner:systemXmlBanners){
				String dir=banner.getDirName();
				String leftName=banner.getLeftName();
				String rightName=banner.getRightName();
				String middleName=banner.getMiddleName();
				String imgsUri=bannerDir+"/"+dir+"/images/";
				
				if(!StringUtils.isBlank(leftName)){
					File file=new File(imgsUri+leftName);
					int leftClbId=simpleClbClient.createFile(leftName, file.length(), new FileInputStream(file));
					banner.setLeftPictureClbId(leftClbId);
					banner.setLeftPictureUrl(simpleClbClient.getDirectURL(banner.getLeftPictureClbId()));
				}
				
				if(!StringUtils.isBlank(rightName)){
					File file=new File(imgsUri+rightName);
					int rightClbId=simpleClbClient.createFile(rightName, file.length(), new FileInputStream(file));
					banner.setRightPictureClbId(rightClbId);
					banner.setRightPictureUrl(simpleClbClient.getDirectURL(banner.getRightPictureClbId()));
				}
				
				if(!StringUtils.isBlank(middleName)){
					File file=new File(imgsUri+middleName);
					int middleClbId=simpleClbClient.createFile(middleName, file.length(), new FileInputStream(file));
					banner.setMiddlePictureClbId(middleClbId);
					banner.setMiddlePictureUrl(simpleClbClient.getDirectURL(banner.getMiddlePictureClbId()));
				}
				
			
				String cssUri=bannerDir+"/"+dir+"/banner.css";
				FileInputStream input = new FileInputStream(cssUri);
				String content;
				try {
					content = FileUtil.readContents(input, "UTF-8");
					if (banner.getLeftPictureUrl()!= null) {
						content = content.replace(REPLACE_LEFT_BANNER,
								banner.getLeftPictureUrl());
					}
					if (banner.getMiddlePictureUrl() != null) {
						content = content.replace(REPLCAE_MIDDLE_BANNER,
								banner.getMiddlePictureUrl());
					}
					if (banner.getRightPictureUrl() != null) {
						content = content.replace(REPLACE_RIGHT_BANNER,
								banner.getRightPictureUrl());
					}
					byte[]  bytes=content.getBytes();
					ByteArrayInputStream in=new ByteArrayInputStream(content.getBytes());
					int cssClb=simpleClbClient.createFile("banner.css", bytes.length, in);
					banner.setCssClbId(cssClb);
				} catch (IOException e) {
					e.printStackTrace();
				}
				readBanners.add(banner);
			}
		}
		return readBanners;
	}

	public boolean init(int siteId, Map<String, String> params,
			String templatePath,String defaultDataPath) throws IOException {
		String bannerDir = templatePath + "/bannerLoc";
		String bannerfilename = templatePath + "/banner.xml";
		if(!(new File(bannerDir).exists())){
			bannerDir=defaultDataPath+"/bannerLoc";
		}
		List<Banner> list=initBannerFromXml(bannerfilename,bannerDir,siteId);
		for (Banner banner : list) {
			createBanner(siteId,banner);
		}
		return true;
	}
	
	public void setSimpleClbClient(ISimpleClbClient simpleClbClient) {
		this.simpleClbClient = simpleClbClient;
	}
}