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

/**
 * @date 2010-5-13
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteRequestItem {
	private String siteAdmin;
	private String siteName;
	private String[] domainNames;
	private String policy;
	private String email;
	private String smtp;
	private String smtpUser;
	private String smtpPassword;
	private String template;
	private String skin;
	private boolean smtpCertificate;
	private String publishState;
    /**
     * 获取站点管理员
     * @return
     */
	public String getSiteAdmin() {
		return siteAdmin;
	}
    /**
     * 设置站点管理员
     * @param siteAdmin
     */
	public void setSiteAdmin(String siteAdmin) {
		this.siteAdmin = siteAdmin;
	}

	/**
	 * 是否支持smtp认证
	 * @return the smtpCertificate
	 */
	public boolean isSmtpCertificate() {
		return smtpCertificate;
	}

	/**
	 * 设置是否支持smtp认证
	 * 
	 * @param smtpCertificate  false 不支持 ture支持
	 */
	public void setSmtpCertificate(boolean smtpCertificate) {
		this.smtpCertificate = smtpCertificate;
	}

	/**
	 * 获取电子邮箱
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置系统默认的电子邮箱
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取smtp
	 * @return the smtp
	 */
	public String getSmtp() {
		return smtp;
	}

	/**
	 * 设置smtp
	 * @param smtp the smtp to set
	 */
	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	/**
	 * 获取smtp用户
	 * @return the smtpUser
	 */
	public String getSmtpUser() {
		return smtpUser;
	}

	/**
	 * 设置smtp用户
	 * @param smtpUser the smtpUser to set
	 */
	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	/**
	 * 获取smtp用户密码
	 * @return the smtpPassword
	 */
	public String getSmtpPassword() {
		return smtpPassword;
	}

	/**
	 * 设置smtp用户密码
	 * @param smtpPassword the smtpPassword to set
	 */
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}


	/**
	 * 获取站点模板
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * 设置站点模板
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * 
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(String policy) {
		this.policy = policy;
	}

	/**
	 * 获取站点的名称
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * 设置站点的名称
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * 获取站点的域名<br>
	 * 第一个域名为主域名，其它的为辅助域名
	 * @return the domainName
	 */
	public String[] getDomainNames() {
		return domainNames;
	}

	/**
	 * 设置站点域名
	 * @param domainName
	 *            the domainName to set
	 */
	public void setDomainNames(String[] domainNames) {
		this.domainNames = domainNames;
	}
	public void setPublisheState(String state){
		this.publishState = state;
	}
	public String getPublisheState() {
		return publishState;
	}
	public String getSkin() {
		return skin;
	}
	public void setSkin(String skin) {
		this.skin = skin;
	}
	
	

}
