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
import java.util.List;

import cn.vlabs.duckling.api.vmt.entity.User;
import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.common.transmission.SignedEnvelope;

/**
 * @author euniverse
 * 
 */
public interface IUserService {
	String getTrueName(String name);

	List<Principal> getUserPrincipal(String userName, String voName);

	String queryUser(String user);

	void createVo(String voName, String voDisplayName, String operator)
			throws DuplicatedVODisplayExcpetion, NoPermissionException;

	String getVOGroup(String vo);

	void addAnonymousToVO(String voName);

	/**
	 * @param voName
	 * @param name
	 * @param b
	 * @return
	 */
	GroupNode getTreeGroupNode(String voName, String name, boolean b);

	/**
	 * @param voName
	 * @param groupName
	 * @param positionName
	 * @return
	 */
	PositionNode getTreePositionNode(String voName, String groupName,
			String positionName);

	String getVODisplayName(String voName);

	void removeUserFromGroup(String voName, String groupName, String userName);

	void addUserTOVO(String voName, String uesrName);

	void addUserToGroup(String voName, String groupName, String userName);

	boolean isApplyInVo(String voName, String userName);

	void applyUserToVo(String voName, String userName, String displayName,
			String password);

	void removeAnonymousFromVO(String voName);
	boolean verifyByAppId(SignedEnvelope envelope);

	/**
	 * @param voName
	 * @param groupName
	 * @param positionName
	 * @return
	 */
	List<User> getPositionUsers(String voName, String groupName,
			String positionName);
}