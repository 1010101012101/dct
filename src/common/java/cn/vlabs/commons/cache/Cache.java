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
package cn.vlabs.commons.cache;

/**
 * Cache接口目的是提供一个类似于CPU的Cache机制的容器。
 * 这个容器能够实现类完成以下几点特性：
 * <ul>
 * 	<li>这是一个有限空间的容器。</li>
 *  <li>这个容器可以人为的将某个缓存的数据清除。</li>
 *  <li>在超出空间限制之后，每种实现有自己的清除策略，选择清除合适的缓存数据。</li>
 *  <li>容器接收一个以某个对象作为关键字,来查找和存放要管理的数据。</li>
 * </ul>
 * 根据传统的80-20理论，希望通过这样的缓存来提高系统的性能，同时不消耗过多的内存资源。
 * 
 * @author 谢建军(xiejj@cnic.cn)
 * @created Sep 9, 2008
 * @param <KEY_TYPE> 关键字类型
 * @param <T> 缓存的数据类型。
 */
public interface Cache<KEY_TYPE, T> {
	/**
	 * 让缓存失效
	 * 
	 * @param key
	 *            缓存对应的关键字
	 */
	void invalid(KEY_TYPE key);

	/**
	 * 存放一个缓存
	 * 
	 * @param key
	 *            缓存对应的关键字
	 * @param cache
	 *            要缓存的对象
	 */
	void add(KEY_TYPE key, T cache);

	/**
	 * 查询缓存
	 * 
	 * @param key
	 *            缓存对应的关键字
	 * @return 关键字对应的缓存。
	 */
	T get(KEY_TYPE key);

	/**
	 * 查询当前缓存中使用的空间数
	 * 
	 * @return
	 */
	int size();

	/**
	 * 清除缓存
	 * 
	 */
	void clear();
}