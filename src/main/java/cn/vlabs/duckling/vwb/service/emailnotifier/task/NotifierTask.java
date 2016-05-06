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

package cn.vlabs.duckling.vwb.service.emailnotifier.task;

import java.security.ProviderException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.data.LightDPage;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriber;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriberService;
import cn.vlabs.duckling.vwb.service.mail.MailService;
import cn.vlabs.duckling.vwb.service.site.SiteManageService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;
import cn.vlabs.duckling.vwb.service.timer.Task;
import cn.vlabs.duckling.vwb.service.timer.TimerService;
import cn.vlabs.duckling.vwb.service.url.impl.UrlServiceImpl;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;

/**
 * Introduction Here.
 * 
 * @date Mar 1, 2010
 * @author wkm(wkm@cnci.cn)
 */
public class NotifierTask implements Task {

	private static final Logger log = Logger.getLogger(NotifierTask.class);
	private int m_sendOffset; // 每封邮件间隔时间，秒为单位
	private DPageService dpageService;
	private MailService mailService;
	private SiteManageService siteManageService;
	private EmailSubscriberService subscriberService;
	private ViewPortService viewportService;
	private ISiteConfig siteConfig;
	private TimerService timerService;
	
	private RenderingService htmlService;





	/**
	 * read the subscribers.
	 */
	private Collection<EmailSubscriber> readSubscribers(int siteId) {
		Calendar cal = Calendar.getInstance();
		return subscriberService.findReceiveAt(siteId,
				cal.get(Calendar.HOUR_OF_DAY));
	}

	/**
	 * Compose the notification message and send it off to the subscribers.
	 */
	private void sendNotifications(int siteId, Date scheduledDate) {
		UrlServiceImpl urlConstructor= new UrlServiceImpl(siteId, siteConfig);
		NotificationMail m_mail = new NotificationMail(siteId, mailService,
				urlConstructor);
		m_mail.setViewPortService(viewportService);
		m_mail.setSiteConfig(siteConfig);
		m_mail.setRenderingService(htmlService);
		log.debug("sendNotifications");
		try {
			// If not subscribers for this notification time, just leave don't
			// do needless work...
			Collection<EmailSubscriber> subscriberList = readSubscribers(siteId);
			if (subscriberList.isEmpty()) {
				log.debug("No subscribers, nothing to do.");
				return;
			}

			// If no changed pages in the past 24 hours, just leave don't do
			// needless work...
			Map<Integer, List<LightDPage>> changedPageMap = yesterdaysChangedPageMap(
					siteId, scheduledDate);
			if (changedPageMap.isEmpty()) {
				log.debug("No changed pages in last 24 hours, nothing to do.");
				return;
			}
			// Send each subscriber an email...
			for (EmailSubscriber subscriber : subscriberList) {
				boolean skipNotification = true;
				m_mail.startChangeList();
				for (Integer resoureid : changedPageMap.keySet()) {
					log.debug("Changed page is " + resoureid);
					if (subscriber.subscribeTo(resoureid.toString())) {
						skipNotification = false;
						List<LightDPage> versionList = changedPageMap
								.get(resoureid);
						log.debug("Subscriber to pagename is " + resoureid);
						m_mail.setChangeList(siteId, resoureid, versionList);
					}

				}

				if (skipNotification) {
					log.debug("No subscribed pages for "
							+ subscriber.getReceiver()
							+ " have changed, skip sending email.");
					continue;
				}

				m_mail.endChangeList();
				m_mail.setRecipient(subscriber.getReceiver());
				m_mail.send(siteId);

			}
		} catch (Exception e) {
			log.debug("Error in send notification!");
		}
		try {
			log.debug("Sleep for a while with " + m_sendOffset + " seconds");
			Thread.sleep(m_sendOffset * 1000);
		} catch (InterruptedException e) {
			log.debug("Interrupted!  Ignore, as we are a Daemon thread...");
		}

	}

	/**
	 * Return a Date that is precisely 24 hours before our scheduled
	 * notification date.
	 */
	private Date twentyFourHoursBefore(Date scheduledDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(scheduledDate);
		c.add(Calendar.HOUR, -24);
		return c.getTime();
	}

	/**
	 * Return a Map of the WikiPages that changed in the immediatly preceeding
	 * 24 hour period (yesterday). The key into the map is the WikiPage name,
	 * the associated value is a list of the versions of the WikiPage that were
	 * created in the preceeding 24 hour period.
	 */
	private Map<Integer, List<LightDPage>> yesterdaysChangedPageMap(int siteId,
			Date scheduledDate) {
		Map<Integer, List<LightDPage>> yesterdaysChangeMap = new TreeMap<Integer, List<LightDPage>>();

		Date cuttoffDate = twentyFourHoursBefore(scheduledDate);

		List<LightDPage> changes = dpageService.getDpagesSinceDate(siteId,
				cuttoffDate);
		if (changes != null && changes.size() > 0) {
			for (LightDPage page : changes) {
				if (page.getTime().after(cuttoffDate))
					yesterdaysChangeMap.put(page.getResourceId(),
							yesterdaysVersions(siteId, page, cuttoffDate));

			}
		}
		log.debug("yesterdaysChangedPageMap() found: "
				+ yesterdaysChangeMap.size());
		return yesterdaysChangeMap;
	}

	private List<LightDPage> yesterdaysVersions(int siteId, LightDPage ofPage,
			Date cuttoffDate) {
		List<LightDPage> yesterdaysList = new ArrayList<LightDPage>();
		List<LightDPage> versionList = new ArrayList<LightDPage>();
		try {
			int resourceId = ofPage.getResourceId();
			List<DPage> dpageVersion = dpageService
					.getDpageVersionsByResourceId(siteId, resourceId);
			versionList.addAll(dpageVersion);
		} catch (ProviderException pe) {
			log.warn("yesterdaysVersions() got ProviderException: "
					+ pe.getMessage());
			versionList = new ArrayList<LightDPage>();
			versionList.add(ofPage); // provider couldn't give us versions?
		}

		for (LightDPage page : versionList) {
			if (page.getTime().after(cuttoffDate))
				yesterdaysList.add(page);
		}

		log.debug("yesterdaysList.size(): " + yesterdaysList.size());
		return yesterdaysList;
	}

	public void destroy() {
		timerService.removeTask(this);
	}

	/**
	 * NOTES: synchronized for no good reason other than I do not feel like
	 * thinking it thru. We're busy, period, the monitor is mine.
	 */
	public synchronized void execute(Date scheduledDate) {
		try {
			Collection<SiteMetaInfo> allSites = siteManageService.getAllSites();
			for (SiteMetaInfo site : allSites) {
				if (SiteState.WORK.equals(site.getState())) {
					sendNotifications(site.getId(), scheduledDate);
				}
			}
		} catch (Exception e) {
			log.debug(
					"Unexpected exception whie executing the notifier task, swallowing.",
					e);
		} finally {

		}
	}

	public void init() {
		timerService.addTask(this);
	}

	public void setDpageService(DPageService dpageService) {
		this.dpageService = dpageService;
	}

	public void setRenderingService(RenderingService htmlService) {
		this.htmlService = htmlService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public void setSendOffset(int sendoffset) {
		this.m_sendOffset = sendoffset;
	}

	public void setSiteConfig(ISiteConfig siteConfig) {
		this.siteConfig = siteConfig;
	}

	public void setSiteManagerService(SiteManageService siteManageService) {
		this.siteManageService = siteManageService;
	}

	public void setSubscriberService(EmailSubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public void setTimerService(TimerService service) {
		this.timerService = service;
	}

	public void setViewportService(ViewPortService viewportService) {
		this.viewportService = viewportService;
	}

	public String toString() {
		return "Notifcation Task for all sites.";
	}
}