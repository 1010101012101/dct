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

package cn.vlabs.duckling.vwb.service.dpage.data;

import java.util.List;

/**
 * Introduction Here.
 * @date 2010-3-4
 * @author euniverse
 */
public class SearchResult {
    private List<DPagePo> dpages;
    private int totalCounts;
    private int currentPage;
    private int pageSize;
	public SearchResult(List<DPagePo> dpages, int totalCounts, int currentPage,
			int pageSize) {
		this.dpages = dpages;
		this.totalCounts = totalCounts;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}
	/**
	 * @return the dpages
	 */
	public List<DPagePo> getDpages() {
		return dpages;
	}
	/**
	 * @param dpages the dpages to set
	 */
	public void setDpages(List<DPagePo> dpages) {
		this.dpages = dpages;
	}
	/**
	 * @return the totalCounts
	 */
	public int getTotalCounts() {
		return totalCounts;
	}
	/**
	 * @param totalCounts the totalCounts to set
	 */
	public void setTotalCounts(int totalCounts) {
		this.totalCounts = totalCounts;
	}
	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
