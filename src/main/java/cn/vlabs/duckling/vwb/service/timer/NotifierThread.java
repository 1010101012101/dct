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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * Introduction Here.
 * @date Mar 1, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class NotifierThread extends Thread {
	private static final Logger log = Logger.getLogger(NotifierThread.class);
	
	private Date m_nextScheduledDate;
	private TimerService m_service;
	private Thread m_thread;
	private boolean run = true;
	private boolean sleeping=false;
	
	public NotifierThread(TimerService service){
		m_service=service;
		setDaemon(true);
		setName("NotifierThread for: Update Notification");
	}

	private void idleUntilNotificationTime() throws InterruptedException {
		long nowMillis = System.currentTimeMillis();
		long untilMillis = m_nextScheduledDate.getTime();

		long duration = untilMillis - nowMillis;

		if (duration <= 0)
			return;
		
		log.debug("Sleeping until: " + m_nextScheduledDate);
		sleeping=true;
		Thread.sleep(duration);
		sleeping=false;
	}

	private boolean isTimeToNotify() {
		long nowMillis = System.currentTimeMillis();
		long untilMillis = m_nextScheduledDate.getTime();
		return (untilMillis <= nowMillis);
	}

	protected void scheduleNextDate() {
		Calendar c = Calendar.getInstance(); // now
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
	
		c.add(Calendar.HOUR_OF_DAY, 1);// next hour
	
		if (c.getTimeInMillis() < System.currentTimeMillis())
			c.add(Calendar.HOUR_OF_DAY, 1);
	
		m_nextScheduledDate = c.getTime();
	
	}

	public void run() {
		m_thread = Thread.currentThread();
		while (run) {
			try{
				scheduleNextDate();
				idleUntilNotificationTime();
				if (isTimeToNotify()){
					Date now = new Date();
					Collection<Task> tasks = m_service.getTasks();
					for (Task task:tasks){
						task.execute(now);
					}
				}
			}catch (InterruptedException e){
				if (!run){
					log.info("Shutting down Notify Thread.");
				}else{
					log.error("Unknown interrupt signal received.");
				}
			}
		}
	}
	
	public void shutdown() {
		run = false;
		synchronized(this){
			if (sleeping)
				m_thread.interrupt();
		}
	}
}
