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

package cn.vlabs.duckling.vwb.service.cache;

import cn.vlabs.duckling.vwb.service.viewport.cache.LRULinkedHashMap;

/**
 * @date 2013-6-9
 * @author xiejj
 */
public class ThreadLocalCache {
	private static ThreadLocal<LRULinkedHashMap<String, Object>> t = new ThreadLocal<LRULinkedHashMap<String, Object>>();

	public static void init(int capacity) {
		if (t.get() == null) {
			LRULinkedHashMap<String, Object> map = new LRULinkedHashMap<String, Object>(
					capacity);
			t.set(map);
		}
		t.get().clear();
	}

	public static void clear() {
		if (t != null) {
			t.get().clear();
			t.set(null);
		}
	}

	public static Object get(String key) {
		if (t.get() != null) {
			return t.get().get(key);
		} else {
			return null;
		}
	}

	public static boolean containKey(String key) {
		if (t.get() != null) {
			return t.get().containsKey(key);
		}
		return false;
	}

	public static void remove(String key) {
		if (t.get() != null) {
			t.get().remove(key);
		}
	}

	public static void put(String key, Object result) {
		if (t.get() != null) {
			t.get().put(key, result);
		}
	}
}
