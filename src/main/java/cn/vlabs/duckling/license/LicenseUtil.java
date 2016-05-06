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

package cn.vlabs.duckling.license;

import java.util.HashMap;
import java.util.Map;

import cn.vlabs.duckling.common.util.Base64;
import cn.vlabs.duckling.common.util.Base64.DecodingException;
import cn.vlabs.duckling.license.impl.LicenseAlogrithm;
import cn.vlabs.duckling.license.impl.SimpleLicenseAlogrithm;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * 产生和管理License的工具
 * @date 2011-11-29
 * @author xiejj@cnic.cn
 */
public class LicenseUtil {
	
	public static Map<Integer, String> PLUGINS = new HashMap<Integer, String>();
	
	static {
		PLUGINS.put(1, "短信通(uct)");
		PLUGINS.put(2, "课题经费管理(rdmis3)");
		PLUGINS.put(3, "任务管理(ewe)");
		PLUGINS.put(4, "团队日程(aat)");
		PLUGINS.put(5, "讨论区(bbs)");
		PLUGINS.put(6, "在线沟通(dchat)");
	}
	
	public static String generate(License license) throws InvalidLicenseException{
		LicenseAlogrithm  alog=new SimpleLicenseAlogrithm();
		byte[] result=alog.genrate(license);
		Base64Encoder encoder = new Base64Encoder();
		return encoder.encode(result);
	}
	
	public static License verify(String txt) throws InvalidLicenseException{
		try {
			byte[] bytes=Base64.decode(txt);
			LicenseAlogrithm  alog=new SimpleLicenseAlogrithm();
			License license = alog.verify(bytes);
			if (license.getProducts()==null || license.getProducts().length<1)
				throw new InvalidLicenseException("Empty license(no proudcts contained)");
			return license;
		} catch (DecodingException e) {
			throw  new InvalidLicenseException(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Arguments is required.");
			System.out.println("java -jar license.jar ${license.code}");
			return;
		}
		
		try {
			String licenseTxt = args[0];
			License license = LicenseUtil.verify(licenseTxt);
			if(license != null) {
				int[] products = license.getProducts();
				for(int productId : products) {
					System.out.println(PLUGINS.get(productId));
				}
			}
		} catch (Exception e) {
			System.err.println("Error ocurred while verify license. " + e);
			System.exit(1);
		}
	}
}
