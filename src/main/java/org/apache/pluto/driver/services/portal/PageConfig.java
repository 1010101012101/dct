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

package org.apache.pluto.driver.services.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 */
public class PageConfig extends Resource{
	private static final long serialVersionUID = 1L;
	private String uri;
    private Collection<String> portletIds;
    private int orderNumber;

    public PageConfig() {
        this.portletIds = new ArrayList<String>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Collection<String> getPortletIds() {
        return portletIds;
    }

    public void setPortletIds(Collection<String> ids) {
        this.portletIds = ids;
    }

    public void addPortlet(String contextPath, String portletName) {
        synchronized(portletIds) {
            portletIds.add(PortletWindowConfig.createPortletId(contextPath, portletName, createPlacementId()));
        }
    }
    public String findPortletId(int sequence){
    	synchronized(portletIds) {
    		if (sequence<portletIds.size()){
    			Iterator<String> iter = portletIds.iterator();
    			int i = 0;
	    		while(i<sequence) {
    				i++;
    				iter.next();
	    		}
    			return (String)iter.next();
    		}
    		return null;
    	}
    }
    public Collection<String> find(String contextPath, String portletName){
    	String prefix = PortletWindowConfig.createPortletIdWithoutMeta(contextPath, portletName);
    	ArrayList<String> candidates = new ArrayList<String>();
        synchronized(portletIds) {
        	for (Object value:portletIds){
        		String portletId = (String)value;
        		if (portletId.startsWith(prefix)){
        			candidates.add(portletId);
        		}
        	}
        }
        return candidates;
    }
    
    public String find(String context, String portlet, int sequence){
    	String prefix = PortletWindowConfig.createPortletIdWithoutMeta(context, portlet);
    	synchronized(portletIds){
	    	if (sequence>0 && (sequence < portletIds.size())){
	    		Iterator<String> iter = portletIds.iterator();
	    		for (int i=0;i<sequence;i++) {
	    			iter.next();
	    		}
	    		String portletId = (String)iter.next();
	    		if (portletId.startsWith(prefix)){
	    			return portletId;
	    		}
	    	}
    	}
    	return null;
    }

    public void removePortlet(String portletId) {
        portletIds.remove(portletId);
    }

    void setOrderNumber(int number) {
        this.orderNumber = number;
    }

    int getOrderNumber() {
        return orderNumber;
    }

    private String createPlacementId() {
        return Integer.valueOf(getResourceId()).hashCode() + "|"+portletIds.size();
    }

	@Override
	public String getType() {
		return Resource.TYPE_PORTAL;
	}
	
	public PageConfig copyWithOutPortlets(){
		PageConfig pageConfig = new PageConfig();
		pageConfig.orderNumber=this.orderNumber;
		pageConfig.uri=this.uri;
		pageConfig.setTitle(getTitle());
		pageConfig.setResourceId(getResourceId());
		return pageConfig;
	}
}
