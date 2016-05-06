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

package cn.vlabs.duckling.vwb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.pluto.driver.config.db.DBPortalPageService;
import org.springframework.beans.factory.BeanFactory;

import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
import cn.vlabs.duckling.vwb.service.backup.BackupService;
import cn.vlabs.duckling.vwb.service.banner.BannerService;
import cn.vlabs.duckling.vwb.service.cache.VWBCacheService;
import cn.vlabs.duckling.vwb.service.config.DomainNameService;
import cn.vlabs.duckling.vwb.service.config.IContainerConfig;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.config.SiteItem;
import cn.vlabs.duckling.vwb.service.ddl.DDLService;
import cn.vlabs.duckling.vwb.service.diff.DifferenceService;
import cn.vlabs.duckling.vwb.service.dml.RenderingService;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.dpage.SaveTempDpageService;
import cn.vlabs.duckling.vwb.service.emailnotifier.EmailSubscriberService;
import cn.vlabs.duckling.vwb.service.favicon.FaviconService;
import cn.vlabs.duckling.vwb.service.flexsession.FlexSessionService;
import cn.vlabs.duckling.vwb.service.flexsession.LoginSessionService;
import cn.vlabs.duckling.vwb.service.i18n.DucklingMessage;
import cn.vlabs.duckling.vwb.service.init.SiteInitService;
import cn.vlabs.duckling.vwb.service.init.SystemInitService;
import cn.vlabs.duckling.vwb.service.login.AuthenticationService;
import cn.vlabs.duckling.vwb.service.myspace.MySpaceService;
import cn.vlabs.duckling.vwb.service.plugin.PluginService;
import cn.vlabs.duckling.vwb.service.render.RendableFactory;
import cn.vlabs.duckling.vwb.service.resource.FunctionService;
import cn.vlabs.duckling.vwb.service.resource.IResourceService;
import cn.vlabs.duckling.vwb.service.share.SharePageAccessService;
import cn.vlabs.duckling.vwb.service.share.SharePageMailService;
import cn.vlabs.duckling.vwb.service.site.ISiteManageService;
import cn.vlabs.duckling.vwb.service.site.PublishState;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.site.SiteState;
import cn.vlabs.duckling.vwb.service.skin.SkinService;
import cn.vlabs.duckling.vwb.service.template.SiteTemplate;
import cn.vlabs.duckling.vwb.service.template.TemplateService;
import cn.vlabs.duckling.vwb.service.timer.TimerService;
import cn.vlabs.duckling.vwb.service.user.DuplicatedVODisplayExcpetion;
import cn.vlabs.duckling.vwb.service.user.IUserService;
import cn.vlabs.duckling.vwb.service.user.NoPermissionException;
import cn.vlabs.duckling.vwb.service.variable.VariableService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.map.RequestMapperFactory;

/**
 * VWB容器实现类
 * 
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public class VWBContainerImpl implements VWBContainer {
	private static Logger log = Logger.getLogger(VWBContainer.class);

	private static BeanFactory m_beanfactory;

	public static VWBContainer findContainer() {
		return (VWBContainer) m_beanfactory.getBean(Attributes.CONTAINER_KEY);
	}

	public static void setBeanFactory(BeanFactory factory) {
		m_beanfactory = factory;
	}

	private AttachmentService attachService;
	private String backupDir;

	private BannerService bannerService;

	private IContainerConfig config;
	private VWBCacheService cacheService;
	private DifferenceService differenceService;
	private DPageService dpageService;
	private EmailSubscriberService emailSubscriberService;

	private FaviconService faviconService;
	private FetchToSession fetcher;
	private SiteInitService initService;
	private SystemInitService initSystem;
	private AuthorizationService m_athr;
	private AuthenticationService m_auth;
	private BackupService m_backup;
	private DomainNameService m_domain;
	private FunctionService m_functionService;
	private DucklingMessage m_i18n;
	private RequestMapperFactory m_mapFactory;

	private MySpaceService m_mySpaceService;
	private DBPortalPageService m_portalpages;
	private RendableFactory m_rendableFactory;
	private RenderingService m_renderingService;
	private SaveTempDpageService m_saveTempDpageService;
	private SharePageMailService m_shareMail;

	private SharePageAccessService m_sharePageAccessService;
	private ISiteConfig m_siteConfig;

	private TemplateService m_template;

	private TimerService m_timer;

	private IUserService m_userService;

	private VariableService m_variableService;
	private PluginService pluginService;
	private String remoteTemplateDir;
	private IResourceService resourceService;
	private ISiteManageService siteManagerService;
	private SkinService skinService;
	private DDLService ddlService;
	private ViewPortService viewportService;

	private void backup(int siteId) {
		String backupName;
		if (m_domain.getSiteDefaultDomain(siteId) != null) {
			backupName = m_domain.getSiteDefaultDomain(siteId) + "_" + siteId;
		} else {
			backupName = "site_" + siteId;
		}
		String templatePath = backupDir + File.separator + backupName;
		if (m_backup.backup(siteId, templatePath)) {
			try {
				m_template.upload(backupName, templatePath,
						SiteTemplate.TYPE_BACKUP);
			} catch (IOException e) {
				log.error("Error while upload backup to clb", e);
			}
		}
	}

	private void createAdminSite() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(KeyConstants.SITE_TEMPLATE_KEY, "admin");
		initService.init(VWBContainer.ADMIN_SITE_ID, params);
	}

	public SiteMetaInfo createSite(String siteAdmin, Map<String, String> params)
			throws NoPermissionException {
		SiteMetaInfo smi = new SiteMetaInfo();
		smi.setState(SiteState.UNINIT);
		int index = 1;
		String oralSiteName = (String) params.get(KeyConstants.SITE_NAME_KEY);
		smi.setPublished(PublishState.valueOf(params
				.get(KeyConstants.SITE_PUBLISHED)));
		smi = siteManagerService.createSiteMetaInfo(smi);
		String voName = getConfig().getProperty(KeyConstants.APPID_PREFIX_KEY,
				"")
				+ smi.getId();
		boolean checkduplication = true;
		while (checkduplication) {
			try {
				m_userService.createVo(voName,
						(String) params.get(KeyConstants.SITE_NAME_KEY),
						siteAdmin);

				params.put(KeyConstants.SITE_UMT_VO_KEY, voName);
				params.put(KeyConstants.SITE_NAME_KEY, oralSiteName);
				initService.init(smi.getId(), params);
				smi.setState(SiteState.WORK);
				siteManagerService.updateSiteMeta(smi);
				smi.setUmtVo(voName);
				smi.setSiteName((String) params.get(KeyConstants.SITE_NAME_KEY));
				checkduplication = false;
			} catch (DuplicatedVODisplayExcpetion e) {
				voName = voName + index;
				params.put(KeyConstants.SITE_NAME_KEY,
						(String) params.get(KeyConstants.SITE_NAME_KEY) + index);
				index++;
			} catch (NoPermissionException e) {
				throw e;
			}
		}
		return smi;
	}

	public boolean createTemplate(int siteId, String templateName) {
		String templatePath = remoteTemplateDir + File.separator + templateName;
		if (m_backup.backup(siteId, templatePath)) {
			try {
				m_template.upload(templateName, templatePath,
						SiteTemplate.TYPE_TEMPLATE);
				return true;
			} catch (IOException e) {
				log.error("Error while upload template to clb", e);
			}
		}
		return false;
	}

	public void destroySite(int siteId) {
		try {
			SiteMetaInfo dsite = getSite(siteId);
			if (dsite != null) {
				backup(siteId);
				initService.destroy(siteId);
			} else {
				backup(siteId);
				initService.destroy(siteId);
			}
		} catch (Exception e) {
			log.error("destroy site failed.", e);
		}
	}

	public SiteMetaInfo getAdminSite() {
		return getSite(ADMIN_SITE_ID);
	}

	public Collection<SiteItem> getAllSiteItems() {
		return m_domain.getAllSiteItems();
	}

	@Override
	public AttachmentService getAttachmentService() {
		return attachService;
	}

	public AuthenticationService getAuthenticationService() {
		return m_auth;
	}

	public AuthorizationService getAuthorizationService() {
		return this.m_athr;
	}

	
	public BannerService getBannerService() {
		return bannerService;
	}

	public IContainerConfig getConfig() {
		return config;
	}

	/**
	 * @return the differenceService
	 */
	public DifferenceService getDifferenceService() {
		return differenceService;
	}

	public DomainNameService getDomainService() {
		return m_domain;
	}

	@Override
	public DPageService getDpageService() {
		return dpageService;
	}

	public EmailSubscriberService getEmailSubscriberService() {
		return emailSubscriberService;
	}

	@Override
	public FaviconService getFaviconService() {
		return faviconService;
	}

	public FetchToSession getFetcher() {
		return fetcher;
	}

	public FunctionService getFunctionService() {
		return m_functionService;
	}

	public DucklingMessage getI18nService() {
		return m_i18n;
	}

	public SiteMetaInfo getInactiveSite(int siteId) {
		SiteMetaInfo siteMeta = siteManagerService.getSiteInfo(siteId);

		if (siteMeta != null && !siteMeta.isWorking()) {
			return siteMeta;
		} else {
			return null;
		}
	}

	public RequestMapperFactory getMapFactory() {
		return this.m_mapFactory;
	}

	public MySpaceService getMySpaceService() {
		return this.m_mySpaceService;
	}

	public PluginService getPluginService() {
		return this.pluginService;
	}

	public DBPortalPageService getPortalPageService() {
		return this.m_portalpages;
	}

	public RendableFactory getRenderFactory() {
		return m_rendableFactory;
	}

	public RenderingService getRenderingService() {
		return m_renderingService;
	}

	public IResourceService getResourceService() {
		return resourceService;
	}

	public SaveTempDpageService getSaveTempDpageService() {
		return m_saveTempDpageService;
	}

	public SharePageAccessService getSharePageAccessService() {
		return m_sharePageAccessService;
	}

	public SharePageMailService getSharePageMailService() {
		return m_shareMail;
	}

	public SiteMetaInfo getSite(int siteId) {
		SiteMetaInfo siteMeta = siteManagerService.getSiteInfo(siteId);
		if (siteMeta != null && siteMeta.isWorking()) {
			return siteMeta;
		} else {
			return null;
		}
	}

	public SiteMetaInfo getSite(String domain) {
		int siteId = m_domain.findSite(domain);
		if (siteId != -1) {
			SiteMetaInfo site = siteManagerService.getSiteInfo(siteId);
			if (site.isWorking()) {
				return site;
			}
		}
		return null;
	}

	@Override
	public ISiteConfig getSiteConfig() {
		return m_siteConfig;
	}

	public SiteInitService getSiteInitService() {
		return initService;
	}

	public ISiteManageService getSiteManagerService() {
		return siteManagerService;
	}

	@Override
	public SkinService getSkinService() {
		return skinService;
	}

	public TemplateService getTemplateService() {
		return this.m_template;
	}

	public TimerService getTimerService() {
		return m_timer;
	}

	public IUserService getUserService() {
		return m_userService;
	}

	public VariableService getVariableService() {
		return m_variableService;
	}

	@Override
	public ViewPortService getViewPortService() {
		return viewportService;
	}

	public void init() {
		initSystem.initSystemData();
		skinService.loadSharedSkin();
		createAdminSite();
		initSystem = null;
	}

	public SiteMetaInfo loadSite(int id) {
		return siteManagerService.getSiteInfo(id);
	}

	public void setAttachmentService(AttachmentService attach) {
		this.attachService = attach;
	}

	/**
	 * @param authenticationService
	 *            the authenticationService to set
	 */
	public void setAuthenticationService(
			AuthenticationService authenticationService) {
		this.m_auth = authenticationService;
	}

	public void setAuthorizationService(AuthorizationService auth) {
		this.m_athr = auth;
	}

	public void setBackupDir(String backupDir) {
		this.backupDir = backupDir;
	}

	public void setBackupService(BackupService backupService) {
		this.m_backup = backupService;
	}

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}

	public void setConfig(IContainerConfig config) {
		this.config = config;
	}

	public void setDifferenceService(DifferenceService differenceService) {
		this.differenceService = differenceService;
	}

	public void setDomainNameService(DomainNameService service) {
		m_domain = service;
	}

	public void setDPageService(DPageService dpageService) {
		this.dpageService = dpageService;
	}

	public void setEmailSubscriberService(
			EmailSubscriberService emailSubscriberService) {
		this.emailSubscriberService = emailSubscriberService;
	}

	public void setFaviconService(FaviconService service) {
		this.faviconService = service;
	}

	public void setFetcher(FetchToSession fetcher) {
		this.fetcher = fetcher;
	}

	public void setFunctionService(FunctionService service) {
		m_functionService = service;
	}

	public void setI18nService(DucklingMessage i18n) {
		this.m_i18n = i18n;
	}

	public void setInitSystem(SystemInitService initSystem) {
		this.initSystem = initSystem;
	}

	public void setMapFactory(RequestMapperFactory mapFactory) {
		this.m_mapFactory = mapFactory;
	}

	public void setMySpaceService(MySpaceService mySpaceService) {
		this.m_mySpaceService = mySpaceService;
	}

	public void setPluginService(PluginService plugin) {
		this.pluginService = plugin;
	}

	public void setPortalPageService(DBPortalPageService service) {
		this.m_portalpages = service;
	}

	public void setRemoteTemplateDir(String remoteDir) {
		this.remoteTemplateDir = remoteDir;
	}

	public void setRendableFactory(RendableFactory factory) {
		this.m_rendableFactory = factory;
	}

	public void setRenderingService(RenderingService renderingService) {
		this.m_renderingService = renderingService;
	}

	public void setResourceService(IResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setSaveTempDpageService(
			SaveTempDpageService saveTempDpageService) {
		this.m_saveTempDpageService = saveTempDpageService;
	}

	public void setSharePageAccessService(SharePageAccessService service) {
		this.m_sharePageAccessService = service;
	}

	public void setSharePageMailService(SharePageMailService sharemail) {
		this.m_shareMail = sharemail;
	}

	public void setSiteConfigService(ISiteConfig siteConfigService) {
		this.m_siteConfig = siteConfigService;
	}

	public void setSiteInitService(SiteInitService initService) {
		this.initService = initService;

	}

	public void setSiteManagerService(ISiteManageService siteManagerService) {
		this.siteManagerService = siteManagerService;
	}

	public void setSkinService(SkinService service) {
		this.skinService = service;
	}

	public void setTemplateService(TemplateService templateService) {
		this.m_template = templateService;
	}

	public void setTimerService(TimerService timerservice) {
		m_timer = timerservice;
	}

	public void setUserService(IUserService userService) {
		this.m_userService = userService;
	}

	public void setVariableService(VariableService variable) {
		m_variableService = variable;
	}

	public void setViewportService(ViewPortService viewportService) {
		this.viewportService = viewportService;
	}

	public void start() {
		initSystem.setConfig(this.getConfig());
		if (!initSystem.hasInital()) {
			init();
		}
	}

	public void stop() {
		m_timer.destroy();
	}

	private static String getServerName(HttpServletRequest request) {
		if (request.getHeader("host") != null) {
			//Forward by nginx
			return request.getHeader("host");
		} else {
			return request.getServerName();
		}
	}

	public static SiteMetaInfo findSite(HttpServletRequest request) {
		SiteMetaInfo site = (SiteMetaInfo) request
				.getAttribute(Attributes.REQUEST_SITE_KEY);
		if (site == null) {
			// Find by serverName
			String serverName = getServerName(request);
			VWBContainer container = VWBContainerImpl.findContainer();
			site = container.getSite(serverName);

			if (site == null) {
				site = container.getAdminSite();
			}
			request.setAttribute(Attributes.REQUEST_SITE_KEY, site);
		}
		return site;
	}

	public static SiteMetaInfo findSite(PortletRequest request) {
		//always loaded by previous find.
		return (SiteMetaInfo) request
				.getAttribute(Attributes.REQUEST_SITE_KEY);
	}

	private FlexSessionService flexSessionService;

	public void setFlexSessionService(FlexSessionService service) {
		this.flexSessionService = service;
	}

	public FlexSessionService getFlexSessionService() {
		return flexSessionService;
	}

	private LoginSessionService loginSessionService;

	public void setLoginSessionService(LoginSessionService service) {
		loginSessionService = service;
	}

	public LoginSessionService getLoginSessionService() {
		return loginSessionService;
	}
	
	

	public void setCacheService(VWBCacheService cacheService) {
		this.cacheService = cacheService;
	}

	public VWBCacheService getCacheService() {
		return cacheService;
	}

	public void setDDLService(DDLService ddlService){
		this.ddlService = ddlService;
	}
	@Override
	public DDLService getDDLService() {
		return ddlService;
	}
	
}
