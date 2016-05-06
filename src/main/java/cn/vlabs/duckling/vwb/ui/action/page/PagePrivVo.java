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

/**
 * Introduction Here.
 * @date 2010-3-9
 * @author Fred Zhang (fred@cnic.cn)
 */
public class PagePrivVo {
    private String type;
    
    private String name;
    
    private String view;
    
    private String edit;
    
    private String rename;
    
    private String truename;
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setRename(String rename) {
        this.rename = rename;
    }
    
    public String getRename() {
        return this.rename;
    }
    
    public void setName(String name) {
        if ((name.indexOf("(") != -1) && (name.indexOf(")") != -1)) {
            this.truename = name.substring(0, name.indexOf("("));            
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setTrueName(String truename) {
        this.truename = truename;
    }
    
    public String getTrueName() {
        return this.truename;
    }
    
    public String getPureName() {
        if ((truename == null) || truename.equals(""))
            return this.name;
        else {
            if ((name.indexOf("(") != -1) && (name.indexOf(")") != -1))
                return this.name.substring(this.name.indexOf("(")+1, this.name.indexOf(")"));
            else
                return this.name;                    
        }
    }
    
    public void setView(String view) {
        this.view = view;
    }
    
    public String getView() {
        return this.view;
    }
    
    public void setEdit(String edit) {
        this.edit = edit;
    }
    
    public String getEdit() {
        return this.edit;
    }
}
