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

import java.util.Date;

/**
 * Introduction Here.
 * @date 2010-3-4
 * @author euniverse
 */
public class DPageNodeInfo {

    private String title;
    private int resourceId;
    private boolean subPages;
    private String author;
    private Date  date;
    private Integer pageorder;

    /**
	 * @return the pageorder
	 */
	public Integer getPageorder() {
		return pageorder;
	}

	/**
	 * @param pageorder the pageorder to set
	 */
	public void setPageorder(Integer pageorder) {
		this.pageorder = pageorder;
	}

	/**
	 * @return the resourceId
	 */
	public int getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isSubPages()
    {
        return subPages;
    }

    public void setSubPages(boolean subPages)
    {
        this.subPages = subPages;
    }
    public String toString()
    {
        return "dpage (resourceId:"+resourceId+"   title:"+title+")   has subPage:"+subPages;
    }
}
