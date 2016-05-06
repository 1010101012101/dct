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

import java.util.HashMap;

/**
 * 这个类实现了Cache接口，采用的淘汰策略是最久未使用的数据优先被清除。
 * 
 * @author 谢建军(xiejj@cnic.cn)
 * @created Dec 5, 2008
 * @param <KEY_TYPE> 关键字类型
 * @param <T> 缓存的数据类型。
 */
public class LessUseFirst<KEY_TYPE, T> implements Cache<KEY_TYPE, T>  {
	public LessUseFirst(int capacity){
		if (capacity<=0)
			throw new IllegalArgumentException("缓存的最大容量太小"+capacity);
		this.capacity=capacity;
		values = new HashMap<KEY_TYPE , CacheItem<KEY_TYPE, T> >();
		
		head = new CacheItem<KEY_TYPE, T>();
		tail = new CacheItem<KEY_TYPE, T>();
		
		head.next=tail;		
		tail.prev=head;
		size=0;
	}
	
	public synchronized void add(KEY_TYPE key, T cache) {
		if (exceed()){
			removeLast();
		}
		CacheItem<KEY_TYPE, T> item = new CacheItem<KEY_TYPE, T>();
		item.value=cache;
		item.key = key;
		
		putToHead(item);
		values.put(key, item);
		size++;
	}


	public synchronized T get(KEY_TYPE key) {
		CacheItem<KEY_TYPE, T> item = values.get(key);
		if (item!=null){
			pickout(item);
			putToHead(item);
			return item.value;
		}else
			return null;
	}

	public synchronized void invalid(KEY_TYPE key) {
		CacheItem<KEY_TYPE, T> item = values.remove(key);
		if (item!=null){
			pickout(item);
			size--;
		}
	}
	
	public synchronized int size(){
		return size;
	}
	
	public synchronized void clear(){
		values.clear();
		head.next=tail;
		head.prev=null;
		tail.prev=head;
		tail.next=null;
	}
	private void removeLast() {
		CacheItem<KEY_TYPE, T> item = tail.prev;
		if (item!=head){
			pickout(item);
			values.remove(item.key);
			size--;
		}
	}
	
	private void pickout(CacheItem<KEY_TYPE,T> item){
		if (item.next!=null)
			item.next.prev=item.prev;
		if (item.prev!=null)
			item.prev.next=item.next;
		
		item.next=null;
		item.prev=null;
	}
	private void putToHead(CacheItem<KEY_TYPE,T> item){
		item.next=head.next;
		head.next.prev=item;
		
		item.prev=head;
		head.next = item;
	}

	private boolean exceed() {
		return size>=capacity;
	}
	
	private HashMap<KEY_TYPE, CacheItem<KEY_TYPE, T> > values;
	private int size;
	private int capacity;
	private CacheItem<KEY_TYPE, T> head;
	private CacheItem<KEY_TYPE, T> tail;
	private static class CacheItem<KEY_TYPE, T>{
		public KEY_TYPE key;
		public T value;
		public CacheItem<KEY_TYPE, T> prev;
		public CacheItem<KEY_TYPE, T> next;
	}
}