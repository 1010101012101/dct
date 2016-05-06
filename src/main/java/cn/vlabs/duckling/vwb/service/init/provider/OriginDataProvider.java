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

package cn.vlabs.duckling.vwb.service.init.provider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import cn.vlabs.duckling.util.ContainerBaseDAO;
import cn.vlabs.duckling.vwb.service.init.InitProvider;
import cn.vlabs.duckling.vwb.service.init.SQLReader;

/**
 * @date 2013-3-31
 * @author xiejj@cstnet.cn
 */
public class OriginDataProvider extends ContainerBaseDAO implements InitProvider {
	public boolean init(int siteId, Map<String,String> params, String templateRootPath,String defaultDataPath) throws IOException {
		if (!executeSqlFile(defaultDataPath + "/tablesTemplate.sql")) {
			return false;
		}
		return true;
	}

	private boolean executeSqlFile(String file) throws IOException {
		boolean flag = false;
		SQLReader reader = new SQLReader(new FileInputStream(file), "UTF-8");
		String sql;
		try {
			while ((sql = reader.next()) != null) {
				getJdbcTemplate().execute(sql);
			}
			flag = true;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
		return flag;
	}
}
