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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class SimpleParentCache implements ITreeCache{
	private static Logger log = Logger.getLogger(SimpleParentCache.class);
	public SimpleParentCache(int capacity, Comparator<Integer> c){
		if (capacity<0)
			capacity=DEFAULT_CAPACITY;
		
		lru= new HackedLRU(capacity);
		keycompare = c;
		sortCompare= new SortComparator();
	}
	
	public void clear(){
		try{
			lock.lock();
			parents.clear();
			lru.clear();
		}finally{
			lock.unlock();
		}
	}
	
	public Integer get(Integer key) {
		log.info("Get parent of "+key);
		try{
			lock.lock();
			CacheItem item = lru.get(key);
			if (item!=null)
				return (Integer)item.parent;
			else
				return null;
		}finally{
			lock.unlock();
		}
	}

	public void remove(Integer key) {
		try{
			lock.lock();
			
			CacheItem item = lru.remove(key);
			
			if (item!=null){
				if (item.parent.equals(key)){
					ArrayList<CacheItem > children = parents.remove(key);
					if (children!=null){
						for (CacheItem child:children){
							lru.remove(child.page);
						}
					}
				}else{
					ArrayList<CacheItem > children = parents.get(item.parent);
					if (children!=null){
						int index = Collections.binarySearch(children, item, sortCompare);
						if (index!=-1){
							int size = children.size();
							int cur=index;
							for (int i=index;i<size;i++){
								CacheItem child=children.get(cur);
								if (child.nGeneration==item.nGeneration){
									if (child.page.equals(item.page))
										children.remove(cur);
									else
										cur++;
								}else{
									children.remove(index);
									lru.remove(child.page);
								}
							}
						}
						if (children.size()==0){
							parents.remove(item.page);
						}
					}
				}
			}
		}finally{
			lock.unlock();
		}
	}

	public void put(Integer key, Integer parent, int generations) {
		CacheItem item = new CacheItem();
		item.nGeneration=generations;
		item.page=key;
		item.parent=parent;
		try{
			lock.lock();
			lru.put(item.page, item);
			ArrayList<CacheItem> children = parents.get(item.parent);
			if (children==null){
				children =  new ArrayList<CacheItem>();
				parents.put(item.parent, children);
			}
			
			children.add(item);
			Collections.sort(children, sortCompare);
		}finally{
			lock.unlock();
		}
	}
	private class SortComparator implements Comparator< CacheItem>{
		public int compare(CacheItem o1, CacheItem o2) {
			if (o1.nGeneration!=o2.nGeneration){
				return o1.nGeneration-o2.nGeneration;
			}
			return keycompare.compare(o1.page, o2.page);
		}
	}
	
	private class CacheItem{
		public int nGeneration;
		public Integer parent;
		public Integer page;
	}
	private class HackedLRU extends LRULinkedHashMap<Integer, CacheItem>{
		private static final long serialVersionUID = 1L;
		public HackedLRU(int maxCapacity) {
			super(maxCapacity);
		}
		protected boolean removeEldestEntry(java.util.Map.Entry<Integer, CacheItem> eldest) {
			if (super.removeEldestEntry(eldest)){
				CacheItem val = eldest.getValue();
				ArrayList<CacheItem> children = parents.get(val.parent);
				if (children!=null){
					int index = Collections.binarySearch(children, val, sortCompare);
					children.remove(index);
					if (children.size()==0)
						parents.remove(children);
				}
				return true;
			}else
				return false;
		}
	}
	private static final int DEFAULT_CAPACITY=100;
	
	
	private Comparator<Integer> keycompare;
	private SortComparator sortCompare;
	private LRULinkedHashMap<Integer, CacheItem> lru;
	private HashMap<Integer, ArrayList<CacheItem > > parents = new HashMap<Integer, ArrayList<CacheItem> > ();
	private final Lock lock = new ReentrantLock();
}
