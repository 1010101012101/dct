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

package cn.vlabs.duckling.vwb.service.idgen;

/**
 * @date 2013-5-23
 * @author liji@cstnet.cn
 */
public interface IKeyGenerator {
	/**
	 * 设置最大ID读取工具包
	 * @param reader 最大ID的读取类
	 */
	void setMaxIdReader(IMaxIdReader reader);
	/**
	 * 获得下一个ID
	 * @param groupId 每个分组的ID
	 * @return 新产生的下一个ID
	 */
	int getNextID(int groupId);
}
