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

package cn.vlabs.duckling.vwb.ui.rsi.api;


/**
 * Introduction Here.
 * @date 2010-4-19
 * @author Fred Zhang (fred@cnic.cn)
 */
public class DCTRsiErrorCode {
   private DCTRsiErrorCode()
   {
	   
   }
//   /*
//    * dct error code between 100000 and 199999
//    */
//   public static final int DCT_ERROR_CODE_START = 100000;
//   public static final int DCT_ERROR_CODE_END = 199999;
//   
//   
   //check excpetion code
   public static final int EMPTY_ERROR = 100001;
   public static final int FORBIDDEN_ERROR = 100002;
   public static final int LOGIN_ERROR = 100003;
   public static final int CONFLICT_ERROR = 100004;
   public static final int PARAMETER_INVALID_ERROR = 100005;
   public static final int SITE_DOMAIN_CONFLICT = 100006;
   
   
   
   
}
