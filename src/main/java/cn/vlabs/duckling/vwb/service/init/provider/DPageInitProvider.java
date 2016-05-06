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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageConstants;
import cn.vlabs.duckling.vwb.service.dpage.InvalidDPageDtoException;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.impl.DtoPoConvertor;
import cn.vlabs.duckling.vwb.service.resource.config.ConfigItem;
import cn.vlabs.duckling.vwb.service.resource.config.DPageItem;

import com.mysql.jdbc.Statement;

/**
 * 页面初始化功能
 * 
 * @date 2013-3-31
 * @author xiejj
 */
public class DPageInitProvider extends AbstractViewPortInitProvider {
	@Override
	public boolean init(int siteId, Map<String,String> params,String templatePath,String defaultDataPath) throws IOException {
		String pagesDir=templatePath+ "/pages/";
		String filePath=templatePath+ "/dpages.xml";
		
		File fDpagesXML = new File(filePath);
		if (!fDpagesXML.exists()){
			filePath = defaultDataPath + "/dpages.xml";
			pagesDir = defaultDataPath + "/pages/";
		}
		
		List<ConfigItem> dpages = initItemFromXML(filePath);
		if (dpages != null) {
			for (ConfigItem item : dpages) {
				create(siteId,convertToViewPort(item));
				DPageItem dpage = (DPageItem) item;
				try {
					importDPageContent(siteId,dpage, pagesDir);
				} catch (InvalidDPageDtoException e) {
					log.error("Fail to load site's page data",e);
					return false;
				}
			}
		}
		return true;
	}
	
	private DPage importDPageContent(int siteId,DPageItem item, String parent)
			throws IOException, InvalidDPageDtoException {
		DPage dpage = new DPage();
		dpage.setResourceId(item.getId());
		try {
			if (item.getFile() != null
					&& item.getFile().trim().length() > 0
					&& new File(parent + File.separator + item.getFile())
							.exists()) {
				String content = FileUtils.readFileToString(new File(parent
						+ File.separator + item.getFile()), "UTF-8");
				dpage.setContent(content);
				dpage.setAuthor(SYS_USER_NAME);
				dpage.setTitle(item.getTitle());
				dpage.setTime(new Date());
				dpage.setVersion(DPageConstants.FIRST_VERSION_NUMBER);
				dpage = createDpage(siteId,DtoPoConvertor.convertDtoToPo(dpage));
			}

		} catch (IOException ioex) {
			log.error("生成dPage页面内容时出现错误，资源ID:" + item.getId() + "，dPage文件名称:"
					+ item.getFile());
			throw ioex;
		}
		return dpage;
	}
	private DPage createDpage(final int siteId,final DPagePo dpage) {
		final String sql = "insert into vwb_dpage_content_info "
				+ "(siteId,resourceid,version,change_time,content,change_by,title) "
				+ " values(?,?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				int i = 0;
				ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(++i, siteId);
				ps.setInt(++i, dpage.getResourceId());
				ps.setInt(++i, dpage.getVersion());
				ps.setTimestamp(++i, new Timestamp(dpage.getTime()
						.getTime()));
				ps.setString(++i, dpage.getContent());
				ps.setString(++i, dpage.getCreator());
				ps.setString(++i, dpage.getTitle());
				return ps;
			}
		}, keyHolder);
		dpage.setId(Integer.valueOf(keyHolder.getKey().intValue()));
		return DtoPoConvertor.convertPoToDto(dpage);
	}
}
