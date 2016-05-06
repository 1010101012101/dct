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

package cn.vlabs.duckling.vwb.service.dpage;

import java.util.Collection;
import java.util.ResourceBundle;


/**
 * Introduction Here.
 * @date 2010-2-24
 * @author Fred Zhang (fred@cnic.cn)
 */
public class CurrentPageResultSet {
    /**
     * 分页数据
     */
    private Collection<DPage> data = null;
    /**
     * 当前页
     */
    private int curPage;
    /**
     * 每页显示的记录数
     */
    private int pageSize;
    /**
     * 记录行数
     */
    private int rowsCount;
    /**
     * 页数
     */
    private int pageCount;
    private String firstPage = "第一页";
    private String lastPage = "尾页";
    private String nextPage = "下一页";
    private String previousPage = "上一页";
    private String total = "共";
    private String recodes = "条记录";
    private String change = "转";
    private String the = "第";
    private String page = "页";
    public void setRb(ResourceBundle rb)
    {
        firstPage = rb.getString("fenye.firstPage");
        lastPage = rb.getString("fenye.lastPage");
        nextPage = rb.getString("fenye.nextPage");
        previousPage = rb.getString("fenye.previousPage");
        total = rb.getString("fenye.total");
        recodes = rb.getString("fenye.recodes");
        change = rb.getString("fenye.change");
        the = rb.getString("fenye.the");
        page = rb.getString("fenye.page");
    }

    public CurrentPageResultSet(Collection<DPage> data, int rowCount,int curPage, int pageSize) {
        this.data = data;
        this.curPage = curPage;
        this.pageSize = pageSize;
        this.rowsCount = rowCount;
        this.pageCount = (int) Math.ceil((double) rowsCount / pageSize);
        if(this.curPage>pageCount){
            this.curPage = pageCount;
        }
        if(this.curPage<1){
            this.curPage =1;
        }
    }

    /**
     * getCurPage:返回当前的页数
     * 
     * @return int
     */
    public int getCurPage() {
        return curPage;
    }

    /**
     * getPageSize：返回分页大小
     * 
     * @return int
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * getRowsCount：返回总记录行数
     * 
     * @return int
     */
    public int getRowsCount() {
        return rowsCount;
    }

    /**
     * getPageCount：返回总页数
     * 
     * @return int
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 第一页
     * 
     * @return int
     */
    public int first() {
        return 1;
    }

    /**
     * 最后一页
     * 
     * @return int
     */
    public int last() {
        return pageCount;
    }

    /**
     * 上一页
     * 
     * @return int
     */
    public int previous() {
        return (curPage - 1 < 1) ? 1 : curPage - 1;
    }

    /**
     * 下一页
     * 
     * @return int
     */
    public int next() {
        return (curPage + 1 > pageCount) ? pageCount : curPage + 1;
    }

    /**
     * 第一页
     * 
     * @return boolean
     */
    public boolean isFirst() {
        return (curPage <= 1) ? true : false;
    }

    /**
     * 第一页
     * 
     * @return boolean
     */
    public boolean isLast() {
        return (curPage >= pageCount) ? true : false;
    }

    /**
     * 获取当前页数据
     * 
     * @return Collection
     */
    public Collection<DPage> getData() {
        
        return this.data;
    }

    /**
     * 获取工具条
     * 
     * @return String
     */
    public String getToolBar(String fileName) {
        String temp = "";
        if (fileName.indexOf("?") == -1) {
            temp = "?";
        } else {
            temp = "&";
        }
        String str = "<form method='post' name='frmPage' action='" + fileName
                + "'>";
        str += "<p>";
        if (isFirst())
            str += firstPage+"  "+previousPage+"&nbsp;";
        else {
            str += "<a href='" + fileName + temp + "cur_page=1'>"+firstPage+"</a>&nbsp;";
            str += "<a href='" + fileName + temp + "cur_page=" + (curPage - 1)
                    + "'>上一页</a>&nbsp;";
        }
        if (isLast())
            str += nextPage+" "+lastPage+"&nbsp;";
        else {
            str += "<a href='" + fileName + temp + "cur_page=" + (curPage + 1)
                    + "'>"+nextPage+"</a>&nbsp;";
            str += "<a href='" + fileName + temp + "cur_page=" + pageCount
                    + "'>"+lastPage+"</a>&nbsp;";
        }
        str += "&nbsp;"+total+"<b>" + rowsCount + "</b>"+recodes+"&nbsp;";
        if (pageCount > 0) {
            str += "&nbsp;"+change+"<select name='page' onChange=\"location='"
                    + fileName + temp
                    + "cur_page='+this.options[this.selectedIndex].value\">";
            for (int i = 1; i <= pageCount; i++) {
                if (i == curPage)
                    str += "<option value='" + i + "' selected>"+this.the + i
                            +page+"</option>";
                else
                    str += "<option value='" + i + "'>"+this.the + i + page+"</option>";
            }
            str += "</select>";
        }
        str += "</p></form>";
        return str;
    }
}
