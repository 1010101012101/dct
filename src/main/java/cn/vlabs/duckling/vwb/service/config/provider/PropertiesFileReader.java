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

package cn.vlabs.duckling.vwb.service.config.provider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



/**
 * 配置文件读取工具
 * @date May 10, 2010
 * @author xiejj@cnic.cn
 */
public class PropertiesFileReader extends PropertyReader {
	private static Logger log=Logger.getLogger(PropertiesFileReader.class);
	public PropertiesFileReader(String configFile){
		super(load(configFile));
	}
	
	private static Properties load(String configFile) {
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			if (StringUtils.isEmpty(configFile)) {
				log.error("配置文件路径未设置。");
			} else {
				fis = new FileInputStream(configFile);
				props.load(fis);
			}
		} catch (FileNotFoundException e) {
			log.error("配置文件" + configFile + "未找到。");
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (IOException ioe) {
				log.error(ioe);
			}
		}
		return props;
	}
}
