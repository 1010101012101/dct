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
package cn.vlabs.duckling.vwb.ui.action.page;

import java.util.HashSet;
import java.util.Set;

import cn.vlabs.duckling.vwb.service.viewport.ViewPort;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

public class PageUtil {
	public static boolean isValidDPageTree(int siteId, int resourceId,
			int parent, ViewPortService viewService) {
		Set<Integer> nodes = new HashSet<Integer>();
		nodes.add(Integer.valueOf(resourceId));
		int tempParent = parent;
		boolean isValid = true;
		while (true) {
			if (nodes.contains(Integer.valueOf(tempParent))) {
				isValid = false;
				break;
			} else {
				nodes.add(Integer.valueOf(tempParent));
				if (tempParent == 0) {
					break;
				}
				ViewPort vp = viewService.getViewPort(siteId, tempParent);
				if (vp != null) {
					tempParent = vp.getParent();
				} else {
					break;
				}
			}
		}
		return isValid;
	}
}
