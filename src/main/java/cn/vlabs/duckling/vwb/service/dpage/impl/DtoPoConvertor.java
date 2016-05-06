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

package cn.vlabs.duckling.vwb.service.dpage.impl;

import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.data.DPagePo;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;

/**
 * Introduction Here.
 * 
 * @date 2010-2-8
 * @author euniverse
 */
public class DtoPoConvertor {
	private DtoPoConvertor() {
	}

	public static DPagePo convertDtoToPo(DPage dto) {
		DPagePo po = new DPagePo();
		po.setContent(dto.getContent());
		po.setCreator(dto.getAuthor());
		po.setResourceId(dto.getResourceId());
		po.setTime(dto.getTime());
		po.setVersion(dto.getVersion());
		po.setTitle(dto.getTitle());
		po.setSiteId(dto.getSiteId());
		return po;
	}

	public static DPage convertPoToDto(DPagePo po) {
		DPage dto = new DPage();
		dto.setContent(po.getContent());
		dto.setAuthor(po.getCreator());
		dto.setResourceId(po.getResourceId());
		dto.setTime(po.getTime());
		dto.setVersion(po.getVersion());
		dto.setSize(po.getSize());
		dto.setTitle(po.getTitle());
		dto.setSiteId(po.getSiteId());
		return dto;
	}

	public static DPage convertLightPoToDto(LightDPage po) {
		DPage dto = new DPage();
		dto.setAuthor(po.getAuthor());
		dto.setResourceId(po.getResourceId());
		dto.setTime(po.getTime());
		dto.setVersion(po.getVersion());
		dto.setTitle(po.getTitle());
		return dto;
	}
}
