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

package cn.vlabs.duckling.vwb.spi;

import java.util.Collection;
import java.util.Map;

import org.apache.pluto.driver.config.db.DBPortalPageService;

import cn.vlabs.duckling.vwb.FetchToSession;
import cn.vlabs.duckling.vwb.service.attach.AttachmentService;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
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
import cn.vlabs.duckling.vwb.service.login.AuthenticationService;
import cn.vlabs.duckling.vwb.service.myspace.MySpaceService;
import cn.vlabs.duckling.vwb.service.plugin.PluginService;
import cn.vlabs.duckling.vwb.service.render.RendableFactory;
import cn.vlabs.duckling.vwb.service.resource.FunctionService;
import cn.vlabs.duckling.vwb.service.resource.IResourceService;
import cn.vlabs.duckling.vwb.service.share.SharePageAccessService;
import cn.vlabs.duckling.vwb.service.share.SharePageMailService;
import cn.vlabs.duckling.vwb.service.site.ISiteManageService;
import cn.vlabs.duckling.vwb.service.site.SiteMetaInfo;
import cn.vlabs.duckling.vwb.service.skin.SkinService;
import cn.vlabs.duckling.vwb.service.template.TemplateService;
import cn.vlabs.duckling.vwb.service.timer.TimerService;
import cn.vlabs.duckling.vwb.service.user.IUserService;
import cn.vlabs.duckling.vwb.service.user.NoPermissionException;
import cn.vlabs.duckling.vwb.service.variable.VariableService;
import cn.vlabs.duckling.vwb.service.viewport.ViewPortService;
import cn.vlabs.duckling.vwb.ui.map.RequestMapperFactory;

/**
 * VWB容器对象
 * 
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public interface VWBContainer {
	public static final int ADMIN_SITE_ID = 1;

	SiteMetaInfo createSite(String siteAdmin, Map<String, String> params)
			throws NoPermissionException;

	boolean createTemplate(int siteId, String templateName);

	/**
	 * 销毁站点
	 * 
	 * @param siteId
	 */
	void destroySite(int siteId);

	/**
	 * 返回管理站点
	 * 
	 * @return
	 */
	SiteMetaInfo getAdminSite();

	/**
	 * 查询附件服务
	 * 
	 * @return AttachmentService
	 */
	AttachmentService getAttachmentService();

	/**
	 * 查询站点的认证服务
	 * 
	 * @return AuthenticationManager
	 */
	AuthenticationService getAuthenticationService();

	/**
	 * Brief Intro Here
	 * 
	 * @param
	 */
	AuthorizationService getAuthorizationService();

	/**
	 * 获取站点的Banner服务对象
	 * 
	 * @return
	 */
	BannerService getBannerService();

	/**
	 * 获取全局配置服务
	 * 
	 * @return
	 */
	IContainerConfig getConfig();

	/**
	 * 查询站点的内容比较服务
	 * 
	 * @return 内容比较服务
	 */
	DifferenceService getDifferenceService();

	/**
	 * 获取站点的域名使用情况查询服务
	 * 
	 * @return DomainNameService
	 */
	DomainNameService getDomainService();

	/**
	 * 页面服务
	 * 
	 * @return
	 */
	DPageService getDpageService();

	/**
	 * 邮件订阅管理服务
	 * 
	 * @return
	 */
	EmailSubscriberService getEmailSubscriberService();

	FaviconService getFaviconService();

	FetchToSession getFetcher();

	/**
	 * 获取系统服务功能
	 * 
	 * @return
	 */
	FunctionService getFunctionService();
	
	/**
	 *获取 缓存服务
	 * 
	 * @return
	 */
	VWBCacheService getCacheService();
	

	/**
	 * 获取系统配置的国际化服务
	 */
	DucklingMessage getI18nService();

	/**
	 * 请求转发表。
	 * 
	 * @param
	 */
	RequestMapperFactory getMapFactory();

	MySpaceService getMySpaceService();

	/**
	 * 页面插件服务
	 * 
	 * @return PluginServiceInterface
	 */
	PluginService getPluginService();

	/**
	 * 获取Portal页面的管理服务
	 * 
	 * @return
	 */
	DBPortalPageService getPortalPageService();

	/**
	 * 获取站点的Rendable信息
	 * 
	 * @return
	 */
	RendableFactory getRenderFactory();

	/**
	 * 获取站点渲染服务
	 * 
	 * @return RenderingService
	 */
	RenderingService getRenderingService();

	IResourceService getResourceService();

	/**
	 * 获取站点临时保存服务
	 * 
	 * @return 临时保存服务
	 */
	SaveTempDpageService getSaveTempDpageService();

	/**
	 * 共享页面hash服务
	 * 
	 * @return
	 */
	SharePageAccessService getSharePageAccessService();

	/**
	 * 共享页面邮件服务
	 * 
	 * @return
	 */
	SharePageMailService getSharePageMailService();

	/**
	 * 查询站点ID对应的Site
	 * 
	 * @param id
	 * @return
	 */
	SiteMetaInfo getSite(int id);

	/**
	 * 查询域名对应的Site.
	 * 
	 * @param
	 */
	SiteMetaInfo getSite(String domain);

	ISiteConfig getSiteConfig();

	/**
	 * 获取站点初始化服务
	 * 
	 * @return
	 */
	SiteInitService getSiteInitService();

	/**
	 * 获取站点管理服务
	 * 
	 * @return
	 */
	ISiteManageService getSiteManagerService();

	SkinService getSkinService();

	TemplateService getTemplateService();

	/**
	 * 获取定时服务
	 * 
	 * @return
	 */
	TimerService getTimerService();

	/**
	 * 获取用户管理服务
	 * 
	 * @return UserServiceInterface
	 */
	IUserService getUserService();

	/**
	 * 获取变量服务
	 * 
	 * @return VariableService
	 */
	VariableService getVariableService();

	/**
	 * 获取ViewPortService
	 * 
	 * @return ViewPortService
	 */
	ViewPortService getViewPortService();

	/**
	 * 查询站点
	 * 
	 * @param id
	 * @return
	 */
	SiteMetaInfo loadSite(int id);

	/**
	 * 启动容器
	 */
	void start();

	/**
	 * 停止容器
	 */
	void stop();

	/**
	 * @return
	 */
	Collection<SiteItem> getAllSiteItems();

	SiteMetaInfo getInactiveSite(int siteId);
	
	FlexSessionService getFlexSessionService();
	LoginSessionService getLoginSessionService();
	
	/**
	 * 将文件上传到DDL上
	 * @return
	 */
	DDLService getDDLService();
}
