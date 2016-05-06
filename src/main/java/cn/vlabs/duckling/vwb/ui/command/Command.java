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
package cn.vlabs.duckling.vwb.ui.command;

import java.security.Permission;

/**
 * 该类代表当前的命令，用作判断权限和显示界面所用。
 * @author xiejj@cnic.cn
 * 
 * @creation Jan 28, 2010 10:14:33 AM
 */
public interface Command {
    /**
     * 当前操作
     * @return 返回非空的字符串，表示当前的操作。
     */
    String getAction();
    /**
     * 返回当前操作所需的权限。
     * @return 如果无需权限，返回null
     */
    Permission getRequiredPermission();
    /**
     * 返回当前命令操作的对象
     * @return
     */
    Object getTarget();
    /**
     * 设置命令的目标
     * @param target 新设置的Target
     */
    Command targetCommand(Object target);
    /**
     * 获取命令所采用的内容模板
     */
    String getContentJSP();
    /**
     * 获取命令的URL构造Pattern
     * 构造的Pattern:
     * <ul>
     * 	<li>%u: baseurl 或者 basepath(ContextPath)，由配置文件决定</li>
     * 	<li>%U: baseurl 总是返回绝对路径</li>
     * 	<li>%p: basepath</li>
     * 	<li>%n: 资源ID</li>
     * </ul>
     * @retrun 返回URL构造的Pattern
     */
    String getURLPattern();
}
