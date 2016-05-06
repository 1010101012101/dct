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

package cn.vlabs.duckling.vwb;

/**
 * Introduction Here.
 * @date 2010-5-10
 * @author Fred Zhang (fred@cnic.cn)
 */
public class KeyConstants {
  private KeyConstants(){}
  public final static String APPID_PREFIX_KEY = "app.appid.prefix";
  public final static String DLOG_APPID_KEY = "app.appid";
  
  public final static String SITE_TEMPLATE_KEY = "duckling.template.name";
  public final static String SITE_SKIN_KEY = "duckling.skin.name";
  public final static String SITE_UMT_VO_KEY = "duckling.umt.vo";
  
  public final static String SITE_DOMAIN_KEY = "duckling.domain";
  public final static String SITE_BASEURL_KEY = "duckling.baseURL";
  public final static String SITE_NAME_KEY = "duckling.site.name";
  public static final String TEMPLATE_NAME = "duckling.template.name";
  public static final String SKIN_NAME = "duckling.skin.name";
  public static final String SKIN_SHARED = "duckling.skin.global";
  public final static String SITE_MAIL_KEY = "email.address";
  public final static String SITE_MAIL_USERNAME = "email.username";
  public final static String SITE_MAIL_PASSWORD = "email.password";
  public final static String SITE_MAIL_FORMADDRESS = "email.fromAddress";
  public final static String SITE_MAIL_AUTH_KEY = "email.mail.smtp.auth";
  public final static String SITE_LOCAL_USER="duckling.clb.localuser";
  public final static String SITE_SMTP_HOST_KEY = "email.mail.smtp.host";
  public final static String SITE_ACCESS_OPTION_KEY = "site.access.option";
  public final static String SITE_DATE_FORMAT = "duckling.dateformat";
  public final static String PROP_DLOG="app.dlog.service";
  public final static String PROP_UMT = "duckling.umt.server";
  public final static String PROP_SEND_OFFSET = "emailnotifier.sendOffset";
  public static final String ENCODING = "duckling.encoding";
  
  public final static String CONTAINER_CLB_SERVICE_URL="duckling.clb.service";
  public final static String CONTAINER_UMT_APP_USER="duckling.umt.user";
  public final static String CONTAINER_UMT_APP_PASS = "duckling.umt.pass";
  public final static String SITE_LANGUAGE = "default.language";
  public final static String SITE_USER_PRINCIPAL = "site_user_principal";
  public final static String SITE_PUBLISHED="site_published";
}
