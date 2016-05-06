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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/*
 * @date Feb 1, 2010
 * @author Xiejj
 */

public class VWBStartupListener implements ServletContextListener {
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		FileSystemXmlApplicationContext factory= (FileSystemXmlApplicationContext)context.getAttribute(Attributes.APPLICATION_CONTEXT_KEY);
		if (factory!=null){
			context.removeAttribute(Attributes.APPLICATION_CONTEXT_KEY);
			VWBContainerImpl.findContainer().stop();
			factory.close();
			VWBContainerImpl.setBeanFactory(null);
			factory=null;
		}
	}
	
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		initLog4j(context);
		initWebRoot(context);
		initApplicationContext(context);
	}


	private void initApplicationContext(ServletContext context) {
		String contextxml = context.getInitParameter("vwbContextConfigLocation");
		if (contextxml==null)
			contextxml="/WEB-INF/conf/VWBContext.xml";
		
		String realpath = getRealPath(context, contextxml);
		try{
			FileSystemXmlApplicationContext  factory= new FileSystemXmlApplicationContext(realpath);
			context.setAttribute(Attributes.APPLICATION_CONTEXT_KEY, factory);
			VWBContainerImpl container = (VWBContainerImpl) factory.getBean(Attributes.CONTAINER_KEY);
			VWBContainerImpl.setBeanFactory(factory);
			container.start();
		}catch (Throwable e){
			log.error("Startup failed:"+e);
		}
	}


	private String getRealPath(ServletContext context, String contextxml) {
		//FIX the bug in linux
		String realpath = context.getRealPath(contextxml);
		if (realpath!=null && realpath.startsWith("/")){
			realpath="/"+realpath;
		}
		return realpath;
	}
	
	private void initWebRoot(ServletContext context){
		String webRootKey = context.getInitParameter("webAppRootKey");
		if (webRootKey==null){
			webRootKey="dct.root";
		}
		String webroot=getRealPath(context, "/");
		log.info("set web root path["+webRootKey+"]:"+webroot);
		System.setProperty(webRootKey,webroot);
	}
	
	private void initLog4j(ServletContext context){
		String log4jConfig = context.getInitParameter("log4jConfigLocation");
		if (log4jConfig==null)
			log4jConfig = "/WEB-INF/conf/log4j.properties";
		PropertyConfigurator.configure(getRealPath(context, log4jConfig));
		log=Logger.getLogger(VWBStartupListener.class);
	}
	private Logger log;
}
