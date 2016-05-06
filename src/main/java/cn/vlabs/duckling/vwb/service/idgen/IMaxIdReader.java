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
 * @author xiejj
 */
public interface IMaxIdReader {
	/**
	 * 获得对应分组的最大ID
	 * @param groupId 分组的ID
	 * @return 对应分组的最大ID，如果对应的分组没有ID，请返回0
	 */
	int getMaxId(int groupId);
}
