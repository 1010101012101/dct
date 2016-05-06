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

package cn.vlabs.duckling.vwb.ui.rsi.api;

import cn.vlabs.duckling.common.transmission.SignedEnvelope;

/**
 * 客户身份信息
 * @date 2010-8-22
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VMT2AuthInfo implements AuthInfo{
    private SignedEnvelope  appSignedEnvelope;
    private String user;
    /**
     * 
     * @param appSignedEnvelope 应用签名
     * @param user  当前连接的用户
     */
    public VMT2AuthInfo(SignedEnvelope appSignedEnvelope,String user)
    {
    	this.appSignedEnvelope = appSignedEnvelope;
    	this.user = user;
    }
    public VMT2AuthInfo(SignedEnvelope appSignedEnvelope)
    {
    	this.appSignedEnvelope = appSignedEnvelope;
    }
    public VMT2AuthInfo(){
    	
    }
	/**
	 * @return the appSignedEnvelope
	 */
	public SignedEnvelope getAppSignedEnvelope() {
		return appSignedEnvelope;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
    
}
