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

import cn.vlabs.duckling.vwb.service.attach.Attachment;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 *  Returns the currently requested page name.
 *
 *  @author Yong Ke
 */
public class PageTitleTag
    extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;
    
    public final int doVWBStart()
        throws IOException
    {
        
        DPage   page   = (DPage)vwbcontext.getResource();

        if( page != null )
        {
            if( page instanceof Attachment )
            {
                pageContext.getOut().print( ((Attachment)page).getFileName() );
            }
            else
            {
                pageContext.getOut().print( vwbcontext.getResource().getTitle());
            }
        }

        return SKIP_BODY;
    }
}
