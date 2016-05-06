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

package cn.vlabs.duckling.vwb.service.ddl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.json.simple.JSONObject;

import cn.vlabs.duckling.api.vmt.entity.User;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;

/**
 * @date 2015-12-24
 * @author xiejj@cstnet.cn
 */
public class DDLServiceImpl implements DDLService {
	private static final Logger log = Logger.getLogger(DDLService.class);
	private static final String folderName = "/conferenceFiles";
	private static final String uploadFileFolderPath = "/";
	private static final String folderTitle = "conferenceFiles";
	private RestClient client = new RestClient();
	private VWBCacheService cache;
	private ISiteConfig config;
	private String ddlBaseUrl;
	private DDLSpaceDAO teamSpaces;
	private TokenEncryptor encryptor;
	public void setEncryptor(TokenEncryptor encryptor){
		this.encryptor = encryptor;
	}
	/**
	 * 在DDL的空间中创建分享目录
	 * 
	 * @param teamCode
	 *            空间Code
	 * @param token
	 *            用户的Token
	 * @return 创建成功后的目录ID
	 * @throws IOException
	 *             出现网络异常时的错误
	 */
	private boolean createFolder(String teamCode, String token)
			throws IOException {
		String createTeamUrl = getFunctionUrl("/resource/folders");
		JSONObject object = client.httpPost(createTeamUrl, token, "teamCode",
				teamCode, "title", folderTitle, "ifExisted", "return");
		// 创建目录失败
		if (object == null || object.get("path") == null) {
			return false;
		}
		return true;
	}

	/**
	 * 创建团队空间
	 * 
	 * @param teamCode
	 *            团队空间编码
	 * @param description
	 *            团队空间的描述
	 * @param token
	 *            当前用户的Token
	 * @return 创建完成以后空间的team_code
	 * @throws IOException
	 *             出现网络操作错误抛出
	 */
	private String createSpace(String teamCode, String description, String token)
			throws IOException {
		String createTeamUrl = getFunctionUrl("/teams");
		JSONObject object = client.httpPost(createTeamUrl, token, "teamCode",
				teamCode, "displayName", description, "description",
				description, "autoTeamCode", "true","type","conference");
		// 团队创建失败
		if (object == null || object.get("teamCode") == null) {
			return null;
		}
		return (String) object.get("teamCode");
	}
	
	private synchronized DDLSpace createSpace(int siteId, String token) {
		DDLSpace space=null;
		String siteName = "csp_"
				+ config.getProperty(siteId, "duckling.site.name");
		String description = config.getProperty(siteId, "duckling.domain");
		try {
			String teamCode = createSpace(siteName, description,
					token);

			space = new DDLSpace();
			space.setCode(teamCode);
			space.setSiteId(siteId);
			teamSpaces.save(space);
		} catch (IOException e) {
			log.error("Create team in DDL failed", e);
		}
		return space;
	}

	private String getFunctionUrl(String function) {
		return ddlBaseUrl + function;
	}

	public void setBaseUrl(String url) {
		this.ddlBaseUrl = url;
	}
	public void setSiteConfig(ISiteConfig config) {
		this.config = config;
	}

	public void setTeampSpaceDAO(DDLSpaceDAO dao) {
		this.teamSpaces = dao;
	}
	public void setCacheService(VWBCacheService cache){
		this.cache = cache;
	}
	/**
	 * 上传文件并分享
	 * 
	 * @param siteId
	 *            站点的ID
	 * @param token
	 *            用户身份Token
	 * @param file
	 *            上传的文件对象
	 * @return 上传成功后返回分享URL
	 */
	public JSONObject upload(VWBContext context, FormFile file) {
		int siteId = context.getSiteId();
		String token = context.getVWBSession().getToken();
		DDLSpace space = teamSpaces.find(siteId);
		if (space == null) {
			space = initCreateSpace(siteId, token,context.getVO());
		}
		
		if (space != null) {
			String teamCode = space.getCode();
			
			return uploadAndShare(teamCode, uploadFileFolderPath, file,
					token);
		} else {
			log.error("{conference:\""
					+ config.getProperty(siteId, "duckling.domain")
					+ "\"} getting team error.");
		}
		return null;

	}

	/**
	 * 上传文件到DDL空间
	 * 
	 * @param teamCode
	 * @param parentRid
	 * @param file
	 * @param token
	 * @return
	 * @throws IOException 
	 */
	private JSONObject uploadAndShare(String teamCode, String path,
			FormFile file, String token){
		try {
			String url = getFunctionUrl("/resource/fileShared")+"?teamCode="+teamCode;
			JSONObject object = client.httpUpload(url, "file",
					file.getFileName(), file.getInputStream(), token,
					 "path", path, "ifExisted", "update");

			object.put("result", true);
			return object;
		}catch (IOException e) {
			log.error("Doing upload and share operation failed.", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String createToken(VWBContext context) {
		if ( context.getCurrentUser()!=null){
			String currentUser =context.getCurrentUser().getName();
			Random random = new Random();
			JSONObject obj = new JSONObject();
			obj.put("email", currentUser);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			obj.put("date", format.format(new Date()));
			obj.put("random", Integer.valueOf(random.nextInt()));
			return encryptor.encrypt(obj.toString());
		}else{
			return "";
		}
	}
	
	@Override
	public String getTeamCodeAndInit(HttpServletRequest request) {
		VWBSession session=VWBSession.findSession(request);
		int siteId = session.getSiteId();
		String token = session.getToken();
		DDLSpace space = (DDLSpace) cache.getFromCache(siteId, "teamcode");
		if (space==null){
			space = teamSpaces.find(siteId);
			if (space == null) {
				VWBContext context = VWBContext.createContext(request, VWBContext.EDIT);
				if (context.hasAccess()){
					space = initCreateSpace(siteId, token,context.getVO());
				}
			}
			cache.putInCache(siteId, "teamcode", space);
		}
		
		if (space!=null){
			return space.getCode();
		}else {
			return null;
		}
	}
	
	@Override
	public String getTeamCode(HttpServletRequest request) {
		VWBSession session=VWBSession.findSession(request);
		int siteId = session.getSiteId();
		DDLSpace space = (DDLSpace) cache.getFromCache(siteId, "teamcode");
		if (space==null){
			space = teamSpaces.find(siteId);
			if (space == null) {
				return null;
			}
		}
		return space.getCode();
	}
	
	
	private boolean addAddmin(String token,String teamCode, String[] userIds, String[] userNames){
		//String addUrl=getAddAdminUrl();
		
		if(userIds==null||userNames==null||userIds.length<1||userNames.length<1){
			return true;
		}
		
		Map<String,String[]> params=new HashMap<String,String[]>();
		params.put("teamCode", new String[]{teamCode});
		params.put("uids",userIds);
		for(int i=0;i<userNames.length;i++){
			if(StringUtils.isBlank(userNames[i])){
				userNames[i]=StringUtils.split(userIds[i], "@")[0];
			}
		}
		params.put("names", userNames);
		params.put("auth", new String[]{"admin"});
		String addUrl=this.getFunctionUrl("/team/memberBatchAdd");
		try {
			client.httpPost(addUrl, token, params);
		} catch (IOException e) {
			log.error("add admin Error",e);
			return false;
		}
		return true;
	}
	
	
	private synchronized DDLSpace initCreateSpace(int siteId,String token,String voName){
		DDLSpace result=this.createSpace(siteId, token);
		if(result==null){
			return null;
		}
		
		String teamCode=result.getCode();
		List<User> userList=VWBContainerImpl.findContainer().getUserService().getPositionUsers(voName, "root", "组管理员");
		String[] userNames=new String[userList.size()];
		String[] userIds= new String[userList.size()];
		if(userList!=null){
			for(int i=0;i<userList.size();i++){
				userIds[i]=userList.get(i).getName();
				userNames[i]=userList.get(i).getDisplayName();
			}
		}
		
		addAddmin(token,teamCode,userIds,userNames);
		return result;
		
		
	}
	
}
