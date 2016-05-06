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

package cn.vlabs.duckling.vwb.service.ddl;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @date 2015-12-23
 * @author xiejj@cstnet.cn
 */
public final class NameValuePairUtil {
	public static List<NameValuePair> assemble(String... params){
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		if (params!=null && params.length>0){
			assert params.length % 2 ==0:"Params's length must be even number";
			for (int i=0;i<params.length/2;i++){
				paramsList.add(new BasicNameValuePair(params[i*2], params[i*2+1]));
			}
		}
		return paramsList;
	}
}
