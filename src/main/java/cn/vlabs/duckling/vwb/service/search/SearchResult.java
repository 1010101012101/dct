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

package cn.vlabs.duckling.vwb.service.search;

import java.util.Comparator;

import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 * 这个是原Lucene Search方案中搜索服务的遗留物需要以后添加别的方式来实现搜索页面的功能
 * @date Mar 4, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public interface SearchResult extends Comparator<SearchResult> {
    /**
     *  Return the page.
     *  
     *  @return the WikiPage object containing this result
     */
    public DPage getPage();

    /**
     *  Returns the score.
     *  
     *  @return A positive score value.  Note that there is no upper limit for the score.
     */

    public int getScore();
    
    
    /**
     * Collection of XHTML fragments representing some contexts in which
     * the match was made (a.k.a., "snippets").
     *
     * @return the search results
     * @since 2.4
     */
    public String[] getContexts();
}
