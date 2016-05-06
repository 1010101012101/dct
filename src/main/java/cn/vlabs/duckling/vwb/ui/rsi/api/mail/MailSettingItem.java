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

package cn.vlabs.duckling.vwb.ui.rsi.api.mail;

/**
 * @date 2010-5-18
 * @author Fred Zhang (fred@cnic.cn)
 */
public class MailSettingItem {
	private String passWord;
	private String userName;
	private String smtpServer;
	private String replyAddress;
	private String sendAddress;
	private boolean smtpAuth;

	/**
	 * @return the passWord
	 */
	public String getPassWord() {
		return passWord;
	}

	/**
	 * @param passWord the passWord to set
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the smtpServer
	 */
	public String getSmtpServer() {
		return smtpServer;
	}

	/**
	 * @param smtpServer the smtpServer to set
	 */
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	/**
	 * @return the replyAddress
	 */
	public String getReplyAddress() {
		return replyAddress;
	}

	/**
	 * @param replyAddress the replyAddress to set
	 */
	public void setReplyAddress(String replyAddress) {
		this.replyAddress = replyAddress;
	}

	/**
	 * @return the sendAddress
	 */
	public String getSendAddress() {
		return sendAddress;
	}

	/**
	 * @param sendAddress the sendAddress to set
	 */
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	/**
	 * @return the smtpAuth
	 */
	public boolean isSmtpAuth() {
		return smtpAuth;
	}

	/**
	 * @param smtpAuth the smtpAuth to set
	 */
	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
}
