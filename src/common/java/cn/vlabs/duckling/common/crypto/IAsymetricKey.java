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
/**
 * 非对称密钥系统
 * @author xiejj@cnic.cn
 * 
 * @creation Dec 4, 2009 11:12:07 AM
 */
public interface IAsymetricKey extends IKey {
	/**
	 * 签名
	 * @param bytes 原文
	 * @return 签名
	 */
	byte[] sign(byte[] bytes);
	/**
	 * 验证签名是否正确
	 * @param bytes 原文
	 * @param signature 签名
	 * @return 如果验证成功返回true, 否则返回false
	 */
	boolean verify(byte[] bytes, byte[] signature);
}
