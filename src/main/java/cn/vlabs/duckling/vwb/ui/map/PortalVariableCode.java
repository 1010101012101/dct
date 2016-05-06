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
package cn.vlabs.duckling.vwb.ui.map;

//SESSION表示在具体的会话建立时才共享的变量
//GLOBAL表示一直存在的变量/即表示应用启动后一直共享

public class PortalVariableCode {
    //传递到request/response的变量名（SESSION相关）
    public final static String DEFINE_SESSION_MAP_CODE="SESSION_MAP_CODE";
    //传递到request/response的变量名（SESSION不相关）
    public final static String DEFINE_GLOBAL_MAP_CODE="GLOBAL_MAP_CODE";
    //最后一个浏览的页面ID，默认为Main的ID String
    //public final static String SESSION_LAST_PAGE="LAST_PAGE";
    //用户身份信息cn.vlabs.simpleAuth.Principal[]
    public final static String SESSION_PRINCIPALS="PRINCIPALS";
    //用户名EID String
    public final static String SESSION_USERNAME="USERNAME";
    //用户名TRUENAME String
    public final static String SESSION_TRUENAME="TRUENAME";
    //VO代号 String
    public final static String GLOBAL_VO="VO_ID";
    //UMT地址IP String
    public final static String GLOBAL_UMT="UMT_URL";
    //CLB地址IP String
    public final static String GLOBAL_CLB="CLB_URL";
    //DLOG地址IP String
    public final static String GLOBAL_DLOG="DLOG_URL";
    //UCT地址IP String
    public final static String GLOBAL_UCT="UCT_URL";
    //BaseURL String
    public final static String GLOBAL_BASEURL="BASEURL";
    //ApplicationName String
    public final static String GLOBAL_APPLICATION_NAME="APPLICATION_NAME";
}
