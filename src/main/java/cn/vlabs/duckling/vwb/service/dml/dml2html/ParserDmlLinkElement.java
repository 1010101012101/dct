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

package cn.vlabs.duckling.vwb.service.dml.dml2html;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

import cn.vlabs.duckling.common.util.Base64;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.viewport.ViewPort;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */


public class ParserDmlLinkElement extends AbstractParseDmlElement {


    static final String[] c_externalLinks = {
        "http:", "ftp:", "https:", "mailto:",
        "news:", "file:", "rtsp:", "mms:", "ldap:",
        "gopher:", "nntp:", "telnet:", "wais:",
        "prospero:", "z39.50s", "z39.50r", "vemmi:",
        "imap:", "nfs:", "acap:", "tip:", "pop:",
        "dav:", "opaquelocktoken:", "sip:", "sips:",
        "tel:", "fax:", "modem:", "soap.beep:", "soap.beeps",
        "xmlrpc.beep", "xmlrpc.beeps", "urn:", "go:",
        "h323:", "ipp:", "tftp:", "mupdate:", "pres:",
        "im:", "mtqp", "smb:"};

    private boolean attachmentType=false;

    
    public void printHref(String strAttValue,Dml2HtmlEngine dml2htmlengine,Element element) {
    	
    	String addclass="";
    	
    	if(element.getAttribute("class")!=null){
    		addclass=element.getAttribute("class").getValue();
    		if(addclass!=null){
    			addclass=addclass.replace("attachment", "");
        		addclass=addclass.replace("JSP", "");
        		addclass=addclass.replace("Portal", "");
        		addclass=addclass.replace("NoResourceType", "");
        		addclass=addclass.replace("DPage", "");
        		addclass=addclass.replace("createpage", "");
        		
        		addclass=addclass.trim();
        		
        		if(!"".equals(addclass)){
        			addclass=" "+addclass;
        		}
    		}else{
    			addclass="";
    		}
    	}
    	
        
        if(isExternalLink(strAttValue)){//链接地址是正常地址
            
            dml2htmlengine.getM_out().print( " href=\"" + strAttValue + "\"" );
        
        }else if(strAttValue.startsWith("baseurl://")){
            if(dml2htmlengine.isM_wysiwygEditorMode()){
                dml2htmlengine.getM_out().print( " href=\"" + strAttValue + "\"" );
            }else{
                dml2htmlengine.getM_out().print( " href=\"" + dml2htmlengine.getDmlcontext().getBaseUrl()+strAttValue.substring(10) + "\"" );
            }
        } else if( strAttValue.startsWith("#") ){//链接地址是页面锚点
           //dml2htmlengine.getM_out().print( " href=\"#\"" );
        	//edit by diyanliang 2011-2-16
            dml2htmlengine.getM_out().print( " href=\"" + strAttValue + "\"" );
        }else{//链接地址是本地附件或者页面
            
            String attachment = findAttachment( strAttValue,dml2htmlengine );
            if(attachment != null){//链接地址是附件
                if(dml2htmlengine.isM_wysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
                    dml2htmlengine.getM_out().print(" class=\"attachment\" href=\""+fixClbUrl(strAttValue)+"\"");
                }else{
                    dml2htmlengine.getM_out().print(" class=\"attachment\" href=\""+dml2htmlengine.getDmlcontext().getBaseUrl()+fixClbUrl(strAttValue)+"\"");
                }

                //dml2htmlengine.getM_out().print(" class=\"attachment\" href=\""+strAttValue+"\"");
                attachmentType=true;
            }else {//链接地址是VWB资源
            	int resourceId=0;
            	try{
            		resourceId=Integer.valueOf(strAttValue.trim());
            	}catch(Exception e){
            		String tempstrAttValue=strAttValue.trim();
        			int lastindex=tempstrAttValue.lastIndexOf("/");
        			try{
        				tempstrAttValue=tempstrAttValue.substring(lastindex+1);
                		resourceId=Integer.valueOf(tempstrAttValue);
                	}catch(Exception e2){
                		resourceId=0;
                	}
            	}
            	
            	boolean isDpagaeType=dml2htmlengine.getDmlcontext().isDpagaeType(resourceId);
            	VWBContext context = dml2htmlengine.getDmlcontext().getContext();
            	ViewPort viewport=VWBContext.getContainer().getViewPortService().getViewPort(context.getSite().getId(),resourceId);
            	boolean resourceExists=context.resourceExists(resourceId);
            	if(!resourceExists){
            		dml2htmlengine.getM_out().print("  href=\"#\"  ");
            	}else{
                	if(!isDpagaeType){
                		String resourcetype=VWBContext.getContainer().getViewPortService().getViewPort(context.getSite().getId(),resourceId).getType();
                		if("JSP".equals(resourcetype)){
                			if(dml2htmlengine.isM_wysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
                        		dml2htmlengine.getM_out().print(" class=\"JSP"+addclass+"\" href=\""+resourceId+"\"  title=\""+viewport.getTitle()+"\"");
                        	}else{
                        		dml2htmlengine.getM_out().print(" class=\"JSP"+addclass+"\" href=\""+dml2htmlengine.getDmlcontext().getViewURL(resourceId)+"\"");
                        	}
                		}else if("Portal".equals(resourcetype)){
                			if(dml2htmlengine.isM_wysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
                        		dml2htmlengine.getM_out().print(" class=\"Portal"+addclass+"\" href=\""+resourceId+"\"  title=\""+viewport.getTitle()+"\"");
                        	}else{
                        		dml2htmlengine.getM_out().print(" class=\"Portal"+addclass+"\" href=\""+dml2htmlengine.getDmlcontext().getViewURL(resourceId)+"\"");
                        	}
                		}else{
                			if(dml2htmlengine.isM_wysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
                        		dml2htmlengine.getM_out().print(" class=\"NoResourceType"+addclass+"\" href=\""+resourceId+"\"  title=\""+viewport.getTitle()+"\"");
                        	}else{
                        		dml2htmlengine.getM_out().print(" class=\"NoResourceType"+addclass+"\" href=\""+dml2htmlengine.getDmlcontext().getViewURL(resourceId)+"\"");
                        	}
                			
                		}
                	}else{
                		boolean matchedLink =dml2htmlengine.getDmlcontext().isDpageExists(resourceId);
                    	if( matchedLink ){//页面存在
    	                	if(dml2htmlengine.isM_wysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
                        		dml2htmlengine.getM_out().print(" class=\"DPage"+addclass+"\" href=\""+resourceId+"\"  title=\""+viewport.getTitle()+"\"");
                        	}else{
                        		dml2htmlengine.getM_out().print(" class=\"DPage"+addclass+"\" href=\""+dml2htmlengine.getDmlcontext().getViewURL(resourceId)+"\"");
                        	}
                        }else{//页面不存在
                        	if(dml2htmlengine.isM_wysiwygEditorMode()){
                            	dml2htmlengine.getM_out().print(" class=\"createpage"+addclass+"\" href=\""+resourceId+"\" title=\""+viewport.getTitle()+"\"");
                        	}else{
                            	dml2htmlengine.getM_out().print("  class=\"createpage"+addclass+"\" href=\""+dml2htmlengine.getDmlcontext().getEditURL(resourceId)+"\"");
                        	}
                       }	
                	}
            	}
            }
        }
    }
    
    public void printAttribute(Element e,Dml2HtmlEngine dml2htmlengine) {
        @SuppressWarnings("unchecked")
		List<Attribute> attList=(List<Attribute>)e.getAttributes();
        for(int i=0;i<attList.size();i++){
            String strAttName=((Attribute)attList.get(i)).getName();
            String strAttValue=((Attribute)attList.get(i)).getValue();
            
            
            if(strAttName.equalsIgnoreCase("href")){
                printHref(strAttValue, dml2htmlengine,e);
                
            }else if(strAttName.equalsIgnoreCase("class")&&(strAttValue.equalsIgnoreCase("wikipage")||strAttValue.equalsIgnoreCase("createpage"))){
            	//do nothing??
            }
            else if(!strAttName.equalsIgnoreCase("title")){
                dml2htmlengine.getM_out().print( " " + strAttName+ "=\"" + strAttValue + "\"" );
                
            }
        }
    }

    public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
        dml2htmlengine.getM_out().print("<a ");
        printAttribute(element, dml2htmlengine);
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getM_out().print(">");
        }else{
            dml2htmlengine.getM_out().println(">");
        }
        try {
            d2h.getChildren(element, dml2htmlengine);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JDOMException e1) {
            e1.printStackTrace();
        }
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getM_out().print("</a>");
            //如果是附件 在后面加上附件的图片
            if(attachmentType){
                dml2htmlengine.getM_out().print("<img border=\"0\" src=\""+dml2htmlengine.getDmlcontext().getBaseUrl()+"images/attachment_small.png\" alt=\"(info)\" />");
                attachmentType=false;
            }
        }else{
            dml2htmlengine.getM_out().println("</a>");
            //如果是附件 在后面加上附件的图片
            if(attachmentType){
                dml2htmlengine.getM_out().println("<img border=\"0\" src=\""+dml2htmlengine.getDmlcontext().getBaseUrl()+"images/attachment_small.png\" alt=\"(info)\" />");
                attachmentType=false;
            }
        }
    }
    

//  public static boolean isExternalLink( String link )
//    {
//        int idx = Arrays.binarySearch( c_externalLinks, link
//                                  ,c_startingComparator );
//        if( idx >= 0 && link.startsWith(c_externalLinks[idx]) ) return true;
//        return false;
//    }
    private boolean isExternalLink( String link )
    {
        for( int i = 0; i < c_externalLinks.length; i++ )
        {
            if( link.startsWith( c_externalLinks[i] ) ) return true;
        }

        return false;
    }

    private String findAttachment( String link,Dml2HtmlEngine dml2htmlengine )
    {
        String[] linkarr=link.split("/");
        for(int i=0;i<linkarr.length;i++){
            String strhash=getFromBASE64(linkarr[i]);
            if(strhash!=null && strhash.indexOf("clb")!=-1)
                return link; 
        }
        return null;
    }

    /**
     * Turns a WikiName into something that can be called through using an URL.
     * 
     * @since 1.4.1
     * @param pagename
     *            A name. Can be actually any string.
     * @return A properly encoded name.
     * @see #decodeName(String)
     */
    public static String encodeName(String pagename)
    {
        try
        {
            return URLEncoder.encode(pagename,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return ("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }
    
    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s)
    {
        if (s == null)
            return null;
        try
        {
            byte[] b = Base64.decode(s);
            return new String(b);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public static String fixClbUrl(String str){
        String restr="";
        int beginIndex=str.indexOf("attach/");
        if(beginIndex!=-1)
            restr=str.substring(beginIndex);
        else
            restr=str;
        return restr;
    }
    
    public static String fixPageUrl(String str){
        String restr="";
        int wikiIndex=str.indexOf("Wiki.jsp?page=");
        int editIndex=str.indexOf("Edit.jsp?page=");
       if(wikiIndex!=-1&&str.length()>(wikiIndex+14)){
           restr=str.substring(wikiIndex+14);
       }else if(editIndex!=-1&&str.length()>(editIndex+14)) {
           restr=str.substring(editIndex+14);
       }else{
           restr=str;
       }
        return restr;
    }
}
