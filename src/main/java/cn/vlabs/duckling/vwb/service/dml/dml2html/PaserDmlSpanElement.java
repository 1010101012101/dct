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
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class PaserDmlSpanElement extends AbstractParseDmlElement {

    @Override
    public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
        List<?> attList=e.getAttributes();
        for(int i=0;i<attList.size();i++){
            String strAttName=((Attribute)attList.get(i)).getName();
            String strAttValue=((Attribute)attList.get(i)).getValue();
            dml2htmlengine.getM_out().print( " " + strAttName+ "=\"" + strAttValue + "\"" );
        }

    }

    @Override
    public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
        //设pre标志位
        if(IsNPreSpan(element)){
            dml2htmlengine.setPreType(dml2htmlengine.getPreType()+1);
        }

        dml2htmlengine.getM_out().print("<span");
        printAttribute(element,  dml2htmlengine);
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

        //设pre标志位
        if(IsNPreSpan(element)){
            dml2htmlengine.setPreType(dml2htmlengine.getPreType()-1);
        }
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getM_out().print("</span>");
        }else{
            dml2htmlengine.getM_out().println("</span>");
        }
    }
    
    public boolean IsNPreSpan(Element e){
        boolean reb=false;
        String classstr=e.getAttributeValue( "style" );
        if(classstr!=null){
            int startnum=classstr.indexOf("white-space");
                if(startnum!=-1){
                    if(classstr.length()>startnum+11){
                        String startstr=classstr.substring(startnum+11);
                        int endnum=startstr.indexOf(";");
                        if(endnum!=-1){
                            String prestr=startstr.substring(0,endnum);
                            if(prestr.indexOf("pre")!=-1)reb=true;
                        }
                    }
                    
                }
//          if("font-family: monospace; white-space: pre;".equals(classstr)||classstr.indexOf("pre;")!=-1)
//              reb=true;
        }
        return reb;

    }

}
