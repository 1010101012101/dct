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

package cn.vlabs.duckling.vwb.service.plugin.impl;

/**
 * Introduction Here.
 * @date Feb 26, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class Heading {
    public static final int HEADING_SMALL  = 1;
    public static final int HEADING_MEDIUM = 2;
    public static final int HEADING_LARGE  = 3;
    public static final int HEADING_BIGLARGE  = 4;

    public int    m_level;
    public String m_titleText;
    public String m_titleAnchor;
    public String m_titleSection;
}
