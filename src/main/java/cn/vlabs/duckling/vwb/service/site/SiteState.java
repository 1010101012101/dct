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

package cn.vlabs.duckling.vwb.service.site;

import java.io.Serializable;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class SiteState implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	public final static SiteState WORK = new SiteState("work");
	public final static SiteState HANGUP = new SiteState("hangup");
	public final static SiteState UNINIT = new SiteState("uninit");

	private SiteState(String value) {
		this.value = value;
	};

	public String getValue() {
		return value;
	}
	public boolean equals(Object obj){
		if (obj instanceof SiteState){
			return value.equals(((SiteState)obj).value);
		}
		return false;
	}
	public static SiteState valueOf(String value) {
		SiteState state = null;
		if ("work".equals(value)) {
			state = WORK;
		} else if ("hangup".equals(value)) {
			state = HANGUP;
		} else if ("uninit".equals(value)) {
			state = UNINIT;
		}
		return state;
	}
}
