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

package cn.vlabs.duckling.vwb.tags;

import javax.servlet.jsp.JspException;

import cn.vlabs.duckling.util.Constant;
import cn.vlabs.duckling.vwb.service.auth.permissions.PagePermission;
import cn.vlabs.duckling.vwb.service.render.impl.DPageRendable;

/**
 * Introduction Here.
 * @date Mar 11, 2010
 * @author xiejj@cnic.cn
 */
public class LeftMenuTag extends RenderTag {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int doVWBStart() throws JspException{
		if (content instanceof DPageRendable){
			if (!vwbcontext.checkPermission(new PagePermission(Integer.toString(content.getId()), PagePermission.VIEW_ACTION))){
				content = new DPageRendable(Constant.DEFAULT_LEFT_MENU);
			}
		}
		return super.doVWBStart();
	}

}