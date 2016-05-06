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

package cn.vlabs.duckling.vwb.service.event;

import java.util.EventObject;

/**
 * Introduction Here.
 * @date 2010-2-23
 * @author euniverse
 */
public abstract class VWBEvent extends EventObject {
	private static final long serialVersionUID = 1829433967558773960L;
	private final long m_when;
	private Object m_type;
	public VWBEvent(Object source) {
		super(source);
		m_when = System.currentTimeMillis();
	}

	public long getWhen() {
		return m_when;
	}

	public void setType(Object type){
		this.m_type=type;
	}

	public Object getType(){
		return m_type;
	}
}
