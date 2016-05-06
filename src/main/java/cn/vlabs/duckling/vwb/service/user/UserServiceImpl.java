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

package cn.vlabs.duckling.vwb.service.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.api.VMTAppConnection;
import cn.vlabs.duckling.api.auth.SignatureInfo;
import cn.vlabs.duckling.api.vmt.VMTServiceFactory;
import cn.vlabs.duckling.api.vmt.entity.ApplyUser;
import cn.vlabs.duckling.api.vmt.entity.Position;
import cn.vlabs.duckling.api.vmt.entity.User;
import cn.vlabs.duckling.api.vmt.entity.VO;
import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.api.vmt.exception.EntityNotExistException;
import cn.vlabs.duckling.api.vmt.exception.VMTRemoteException;
import cn.vlabs.duckling.api.vmt.service.VoService;
import cn.vlabs.duckling.api.vmt.service.VoTreeService;
import cn.vlabs.duckling.api.vmt.service.pubkey.DucklingKeyManager;
import cn.vlabs.duckling.common.transmission.SignedEnvelope;

/**
 * @author euniverse
 * 
 */
public class UserServiceImpl implements IUserService {
	
	private static Logger log = Logger.getLogger(UserServiceImpl.class);
	private String keyDir;
	private DucklingKeyManager keyManager;
	private String localName;
	private VMTServiceFactory serviceFactory;
	private String vmtUrl;
	private VMTServiceFactory getServiceFactory() {
		if (serviceFactory == null) {
			initServiceFactory();
		}
		return serviceFactory;
	}
	private void initServiceFactory() {
		try {
			SignatureInfo sg = new SignatureInfo(keyManager.getAppSignatureEnvelope());
			VMTAppConnection vmtApp = new VMTAppConnection(vmtUrl, sg);
			serviceFactory = new VMTServiceFactory(vmtApp);
		} catch (VMTRemoteException e) {
			log.error("Error:login to vmt(url:"
					+ vmtUrl
					+ ")");
			throw new RuntimeException(e);
		}
	}
	public void addAnonymousToVO(String voName) {
		addUserTOVO(voName, "anonymous@root.umt");
	}

	public void addUserToGroup(String voName, String groupName, String userName) {
		try {
			getServiceFactory().getGroupService().addUserToGroup(voName,
					"root", userName);
		} catch (EntityNotExistException e) {
			e.printStackTrace();
		}

	}
	public void addUserTOVO(String voName, String userName) {
		this.addUserToGroup(voName, this.getVOGroup(voName), userName);
	}

	public void applyUserToVo(String voName, String userName,
			String displayName, String password) {
		ApplyUser user = new ApplyUser();
		user.setDisplayName(displayName);
		user.setPassword(password);
		user.setUserName(userName);
		user.setVoName(voName);
		try {
			getServiceFactory().getVoService().applyUserToVo(user);
		} catch (VMTRemoteException e) {

			e.printStackTrace();
		}

	}

	public void createVo(String voName, String voDisplayName, String siteAdmin)
			throws DuplicatedVODisplayExcpetion {
		VoService vos = getServiceFactory().getVoService();
		try {
			if (vos.existsVO(voName)) {
				throw new DuplicatedVODisplayExcpetion("Error:the voName of "
						+ voName + " is already exist", new Exception());
			}
			VO vo = new VO(voName, voDisplayName);
			if (vos.createVO(vo)) {
				getServiceFactory().getPositionService().addUserVOAdmin(voName,
						siteAdmin);
			} else {
				log.error("Error:create vo error");
				throw new RuntimeException("Error:create vo error");
			}
		} catch (VMTRemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public GroupNode getTreeGroupNode(String voName, String name,
			boolean recursive) {
		VoTreeService voTreeService = getServiceFactory().getVOTreeService();
		return voTreeService.getTreeNodes(voName, name, recursive);
	}

	public PositionNode getTreePositionNode(String voName, String groupName,
			String positionName) {
		VoTreeService voTreeService = getServiceFactory().getVOTreeService();
		PositionNode positionNode = new PositionNode();
		Position position = new Position();
		position.setGroupName(groupName);
		position.setVoName(voName);
		position.setName(positionName);
		positionNode.setPosition(position);
		positionNode.addChildren(voTreeService.getPositionSubLayerNodes(voName,
				groupName, positionName));
		return positionNode;
	}

	/**
	 * 获取用户的真名
	 */
	public String getTrueName(String name) {
		User ouser = null;
		try {
			ouser = getServiceFactory().getUserService().getUser(name);
		} catch (VMTRemoteException e) {
			log.error("Error:when get the truename of user (" + name + ")", e);
		}
		if (ouser != null) {
			return ouser.getDisplayName();
		}
		return null;
	}

	public List<Principal> getUserPrincipal(String userName, String voName) {
		List<Principal> principals = null;
		try {
			principals = getServiceFactory().getUserService()
					.getPrincipalsByVoName(userName, voName);
		} catch (VMTRemoteException e) {
			log.error("Error:get the principals of user(" + userName + ")", e);
		}
		if (principals == null) {
			log.warn("No user principals info readed from vmt(user=" + userName
					+ ", vo=" + voName + ").");
			principals = new ArrayList<Principal>();
		} else {

		}
		return principals;
	}

	public String getVODisplayName(String voName) {
		String voDisplay = null;
		try {
			VO vo = getServiceFactory().getVoService().getVO(voName);
			if (vo != null) {
				voDisplay = vo.getDisplayName();
			}
		} catch (VMTRemoteException e) {
			e.printStackTrace();
		}
		return voDisplay;
	}

	public String getVOGroup(String vo) {
		return "root";
	}

	public void init(){
		keyManager = DucklingKeyManager
				.getInstatnce(keyDir, vmtUrl, localName);
	}

	public boolean isApplyInVo(String voName, String userName) {
		boolean flag = true;
		try {
			flag = (Boolean) getServiceFactory().getVoService().isApplyInVo(
					voName, userName);
		} catch (VMTRemoteException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public String queryUser(String user) {
		User ouser = null;
		try {
			ouser = getServiceFactory().getUserService().getUser(user);
		} catch (VMTRemoteException e1) {
			log.error("Error:when get the truename of user (" + user + ")", e1);
		}
		if (ouser != null) {
			return ouser.getDisplayName() + "(" + user + ")";
		}
		return user;
	}

	public void removeAnonymousFromVO(String voName) {
		removeUserFromGroup(voName, getVOGroup(voName), "anonymous@root.umt");
	}

	public void removeUserFromGroup(String voName, String groupName,
			String userName) {
		getServiceFactory().getGroupService().removeUserFromGroup(voName,
				"root", userName);
	}

	public void setKeyDir(String keyDir){
		this.keyDir = keyDir;
	}

	public void setLocalName(String localName){
		this.localName = localName;
	}

	public void setVmtUrl(String vmtUrl){
		this.vmtUrl = vmtUrl;
	}
	@Override
	public boolean verifyByAppId(SignedEnvelope envelope) {
		return keyManager.verifyByAppId(envelope);
	}
	@Override
	public List<User> getPositionUsers(String voName, String groupName,
			String positionName){
		Position position = new Position();
		position.setGroupName(groupName);
		position.setVoName(voName);
		position.setName(positionName);
		return getServiceFactory().getPositionService().getPositionUsers(position);
	}
	
}