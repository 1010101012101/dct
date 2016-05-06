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
package cn.vlabs.duckling.vwb.service.viewport.cache;
/**
 * 树状继承缓存接口
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 31, 2010 11:06:42 AM
 */
public interface ITreeCache{
    /**
     * 查询缓存的继承信息
     * @param key   被查询的页面
     * @return 该页面所使用相关值, 当没有在缓存中找到时，返回null
     */
	Integer get(Integer key);
    /**
     * 保存缓存信息
     * @param key    被缓存页面的Key，可能是页面名
     * @param parent 该页面使用的值，对应的父级页面。   
     */
    void put(Integer key, Integer parent, int generations);
    /**
     * 清除页面的缓存信息
     * @param key   页面的关键字
     */
    void remove(Integer key);
    /**
     * 清除缓存
     *
     */
    void clear();
}
