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
package cn.vlabs.duckling.common.crypto;

import java.util.Date;

/**
 * 加密模块的接口
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 4, 2009 11:07:14 AM
 */
public interface IKey {
	/**
	 * 加密
	 * @param bytes 明文
	 * @return 密文
	 */
	byte[] encrypt(byte[] bytes);
	/**
	 * 解密
	 * @param bytes 密文
	 * @return 明文
	 */
	byte[] decrypt(byte[] bytes);
	/**
	 * 创建时间
	 * @return 返回该密码的创建时间
	 */
	Date getCreateTime();
	/**
	 * 重新产生Key
	 *
	 */
	void generate();
}
