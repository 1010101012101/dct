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

package cn.vlabs.duckling.vwb.service.login;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import cn.vlabs.duckling.vwb.VWBException;

/**
 * 认证服务
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface AuthenticationService {
	/**
	 * 登录
	 * @param request
	 * @return
	 * @throws VWBException
	 */
    boolean login(int siteId,HttpServletRequest request) throws VWBException;
    /**
     * 退出
     * @param request
     */
    void logout(HttpServletRequest request);
    /**
     * 退出时用户下线
     * 
     * @param siteId
     * @param sessionId
     */
    void userOffline(String sessionId);
    /**
     * 获得当前的在线用户
     * @param siteId
     * @return
     */
    Collection<Principal> getOnlineUsers(int siteId);
}
