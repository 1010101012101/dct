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

package cn.vlabs.duckling.vwb.tags;

import java.io.IOException;
import java.security.ProviderException;

import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 *  Returns the currently requested page or attachment size.
 *
 *  @author Yong Ke
 */
public class PageSizeTag
    extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;
    
    public final int doVWBStart()
        throws IOException
    {
        DPage   page   = vwbcontext.getPage();
        
        try
        {
            if( page != null )
            {
                long size = page.getSize();

                if( size == -1 && VWBContext.getContainer().getDpageService().isDpageExist(vwbcontext.getSite().getId(),page.getResourceId()) ) // should never happen with attachments
                {
                    size = page.getContent().length();
                }

                pageContext.getOut().write( Long.toString(size) );
            }
        }
        catch( ProviderException e )
        {
            log.warn("Providers did not work: ",e);
            pageContext.getOut().write("Error determining page size: "+e.getMessage());
        }

        return SKIP_BODY;
    }
}
