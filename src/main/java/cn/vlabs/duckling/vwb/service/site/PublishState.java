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
 * 给站点加上是否已发布的状态。
 * 
 * @date 2011-11-15
 * @author xiejj@cnic.cn
 */
public class PublishState implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	public final static PublishState PUBLISHED = new PublishState("published");
	public final static PublishState INTERNAL = new PublishState("internal");

	private PublishState(String value) {
		this.value = value;
	};

	public String getValue() {
		return value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof PublishState) {
			return value.equals(((PublishState) obj).value);
		}
		return false;
	}

	public static PublishState valueOf(String value) {
		PublishState state = null;
		if ("internal".equals(value)) {
			state = INTERNAL;
		} else {
			state = PUBLISHED;
		}
		return state;
	}
}
