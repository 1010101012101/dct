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

package cn.vlabs.duckling.license.impl;

import cn.vlabs.duckling.license.InvalidLicenseException;
import cn.vlabs.duckling.license.License;

/**
 * 授权的验证程序
 * @date 2011-11-29
 * @author xiejj@cnic.cn
 */
public interface LicenseAlogrithm {
	/**
	 * 验证证书的有效性
	 * @param license
	 * @return 如果验证正确，返回License实例对象。
	 */
	License verify(byte[] license) throws InvalidLicenseException;
	/**
	 * 产生证书
	 * @param license
	 * @return
	 * @throws InvalidLicenseException 
	 */
	byte[] genrate(License license) throws InvalidLicenseException;
}
