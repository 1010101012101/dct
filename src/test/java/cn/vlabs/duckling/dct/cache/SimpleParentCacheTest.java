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
package cn.vlabs.duckling.dct.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Comparator;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.vwb.service.viewport.cache.SimpleParentCache;

public class SimpleParentCacheTest {
	private SimpleParentCache cache;
	@Before
	public void setUp() throws Exception {
		cache = new SimpleParentCache(100, new IntegerComparator());
	}

	@After
	public void tearDown() throws Exception {
		cache = null;
	}

	@Test
	public void testGet() {
		cache.put(145, 1, 10);
		assertNotNull(cache.get(145));
		cache.put(123, 1, 5);
		cache.put(2, 1, 5);
		cache.put(122, 1, 8);
		cache.remove(123);
		assertNull(cache.get(145));
		assertNotNull(cache.get(2));
	}

	@Test
	public void testPut() {
		cache.put(1, 1, 0);
		assertNotNull(cache.get(1));
		cache.remove(1);
		assertNull(cache.get(1));
		for (int i=0;i<101;i++){
			cache.put(100+i, 1, 3);
		}
		assertNull(cache.get(100));
		assertNotNull(cache.get(101));
	}

	@Test
	public void noConfilict(){
		long start = System.currentTimeMillis();
		for (int j=0;j<100;j++){
			for (int i=0;i<100;i++){
				cache.put(100+i, 1, 3);
			}
			cache.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println("一万次插入耗时："+(end-start)+"ms");
	}
	
	@Test
	public void slipout(){
		long start = System.currentTimeMillis();
		for (int i=0;i<10000;i++){
			cache.put(100+i, 1, 23);
		}
		long end = System.currentTimeMillis();
		System.out.println("SlipOut:一万次插入耗时："+(end-start)+"ms");
	}
	
	@Test
	public void queryPerform(){
		for (int i=0;i<100;i++){
			cache.put(100+i, 1, 3);
		}
		Random rand = new Random();
		long start = System.currentTimeMillis();
		for (int i=0;i<10000;i++){
			cache.get(100+rand.nextInt(100));
		}
		long end = System.currentTimeMillis();
		System.out.println("一万次查询耗时："+(end-start)+"ms");
	}
	private static class IntegerComparator implements Comparator<Integer>{
		public int compare(Integer o1, Integer o2) {
			if (o1==null || o2==null)
				throw new NullPointerException();
			return o1.compareTo(o2);
		}
		
	}
}