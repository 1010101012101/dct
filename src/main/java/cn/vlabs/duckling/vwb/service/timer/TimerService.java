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

package cn.vlabs.duckling.vwb.service.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * @date May 8, 2010
 * @author xiejj@cnic.cn
 */
public class TimerService {
	private static final Logger log = Logger.getLogger(TimerService.class);
	private Collection<Task> m_tasks;
	private NotifierThread m_thread;

	public void init() {
		m_tasks = new HashSet<Task>();
		m_thread = new NotifierThread(this);
		m_thread.start();
	}

	public void destroy() {
		m_thread.shutdown();
		m_thread = null;
	}

	public void removeTask(Task task) {
		synchronized (m_tasks) {
			m_tasks.remove(task);
		}
		log.info(task.toString() + " has been removed.");
	}

	public void addTask(Task task) {
		synchronized (m_tasks) {
			m_tasks.add(task);
		}
		log.info("new Task has been added:" + task.toString());
	}

	public Collection<Task> getTasks() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		synchronized (m_tasks) {
			tasks.addAll(m_tasks);
		}
		return tasks;
	}
}
