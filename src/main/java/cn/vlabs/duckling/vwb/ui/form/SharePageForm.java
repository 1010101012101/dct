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

package cn.vlabs.duckling.vwb.ui.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class SharePageForm extends ActionForm {
	private static final long serialVersionUID = 1L;

	/** pattern property */
	private String method;
	private String emailaddress;
	private String objectIds;
	private String emailContent;
	// 邮件title
	private String emailTitle;

	public SharePageForm() {
		// field=AUTHOR;
	}

	/**
	 * Method validate
	 * 
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
		// return null;
	}

	/**
	 * Method reset
	 * 
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.emailaddress = null;
	}

	/**
	 * Returns the pattern.
	 * 
	 * @return String
	 */
	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String pattern) {
		this.emailaddress = pattern;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public String setEmailTitle(String pattern) {
		return emailTitle = pattern;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String pattern) {
		this.emailContent = pattern;
	}

	public String getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(String objectIds) {
		this.objectIds = objectIds;
	}

	/**
	 * Returns the method.
	 * 
	 * @return String
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Set the method.
	 * 
	 * @param method
	 *            The method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

}
