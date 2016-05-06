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
package cn.vlabs.duckling.vwb.ui.action;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.dml.html2dml.HtmlStringToDMLTranslator;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class SavePageAction extends BaseDispatchAction
{
	private static final Logger   log = Logger.getLogger(SavePageAction.class);
    /**
     * Method execute
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward save(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
    	
    	String htmlText = request.getParameter("htmlPageText");
    	Resource res = this.getSavedViewPort(request);
    	
		VWBContext context = VWBContext.createContext(request, DPageCommand.VIEW, res);
    	ResourceBundle rb=context.getBundle("templates/default");
		if(!context.hasAccess(response)){
			writeToResponse(response, rb.getString("action.savepage.noright") );
			return null;
		}
		//冲突
    	String lockVersion=request.getParameter("lockVersion");
		DPage lockeddpage=(DPage)res;
		Date mverion=lockeddpage.getTime();
		if(!lockVersion.equals(String.valueOf(mverion))){
			writeToResponse(response, rb.getString("action.savepage.conflict") );
			return null;
		}
		
		if(!HtmlValidateUtil.checkHtmlTextValidate(htmlText)){
			writeToResponse(response, rb.getString("action.savepage.containInvalidateForm") );
			return null;
		}
		
		String textWithoutMetaData="";
	    if( htmlText != null) {
	    	context.setUseDData(true);
	        textWithoutMetaData = new HtmlStringToDMLTranslator().translate(htmlText,context);
	    }
	    DPage dpage=new DPage();
	    dpage.setResourceId(res.getResourceId());
	    UserPrincipal  p = (UserPrincipal)context.getCurrentUser();
	    dpage.setAuthor(p.getFullName()+"("+p.getName()+")");
	    dpage.setTitle(request.getParameter("pageTitle"));
	    dpage.setContent(textWithoutMetaData);
	    dpage.setSiteId(context.getSite().getId());
	    boolean isDpageExists=getDPageService(context).isDpageExist(context.getSite().getId(),res.getResourceId());
	    if(isDpageExists){
	    	getDPageService(context).updateDpage(dpage);
	    }else{
	    	getDPageService(context).createDpage(dpage);
	    }
	    
	    Date redate= getDPageService(context).getDpageVersionContent(context.getSite().getId(),res.getResourceId(), VWBContext.LATEST_VERSION).getTime();
	    //清除临时保存
	    VWBContext.getContainer().getSaveTempDpageService().cleanTempPage(context.getSite().getId(),res.getResourceId(), p);
    	writeToResponse(response, rb.getString("action.savepage.success")+"|"+redate );
		return null;
	}


	private DPageService getDPageService(VWBContext context) {
		return VWBContext.getContainer().getDpageService();
	}
    
    
    public ActionForward autosave(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
    	String htmlText = request.getParameter("htmlPageText");
    	Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request, DPageCommand.VIEW, res);
		if(!context.hasAccess(response))return null;
    	ResourceBundle rb=context.getBundle("templates/default");
        if(!context.hasAccess(response)) {
        	writeToResponse(response, rb.getString("action.savepage.noright"));
            return null;
        }
        
        if(!HtmlValidateUtil.checkHtmlTextValidate(htmlText)){
			writeToResponse(response, rb.getString("action.savepage.containInvalidateForm") );
			return null;
		}
        
		String textWithoutMetaData="";
	    if( htmlText != null) {
	        textWithoutMetaData = new HtmlStringToDMLTranslator().translate(htmlText,context);
	    }
	    DPage dpage=new DPage();
	    dpage.setResourceId(res.getResourceId());
	    UserPrincipal  p = (UserPrincipal)context.getCurrentUser();
	    dpage.setAuthor(p.getFullName()+"("+p.getName()+")");
	    dpage.setTitle(request.getParameter("pageTitle"));
	    dpage.setContent(textWithoutMetaData);
	    VWBContext.getContainer().getSaveTempDpageService().saveTempDpage(context.getSite().getId(),dpage, textWithoutMetaData);
    	writeToResponse(response, rb.getString("action.autosavepage.success") );
		return null;
	}
    private void writeToResponse(HttpServletResponse response, String xml)
    {
        response.setContentType("text/html;charset=UTF-8");
        try
        {
            //String output = "<result>" + xml + "</result>";
            Writer wr = response.getWriter();
            wr.write(xml);
            wr.close();
        }
        catch (IOException e)
        {
            log.debug("Write xml to response error!", e);
        }
    }    
    

}