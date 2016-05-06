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
import cn.vlabs.duckling.vwb.service.resource.Resource;

/**
 *  Returns the parent of the currently requested page.  Weblog entries are recognized
 *  as subpages of the weblog page.
 *
 *  @author Yong Ke
 */
public class ParentPageNameTag
    extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;
    
    public final int doVWBStart()
        throws IOException
    {
    	Resource   page   = vwbcontext.getResource();

        if( page != null )
        {
            if( page instanceof Attachment )
            {
                pageContext.getOut().print(  ((Attachment)page).getParentName() );
                
            }
            else
            {
                String name = page.getTitle();

                int entrystart = name.indexOf("_blogentry_");

                if( entrystart != -1 )
                {
                    name = name.substring( 0, entrystart );
                }

                int commentstart = name.indexOf("_comments_");

                if( commentstart != -1 )
                {
                    name = name.substring( 0, commentstart );
                }

                pageContext.getOut().print( java.net.URLEncoder.encode(name, "UTF-8") );
            }
        }

        return SKIP_BODY;
    }
}
