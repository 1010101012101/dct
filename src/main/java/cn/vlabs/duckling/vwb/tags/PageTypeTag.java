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
import cn.vlabs.duckling.vwb.service.attach.CLBAttachment;
import cn.vlabs.duckling.vwb.service.dpage.DPage;

/**
 *  Includes the body, if the current page is of proper type.
 *
 *  <B>Attributes</B>
 *  <UL>
 *   <LI>type - either "page", "attachment" or "weblogentry"
 *  </UL>
 *
 *  @author Yong Ke
 */
public class PageTypeTag
    extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;
    
    private String m_type;

    public void initTag()
    {
        super.initTag();
        m_type = null;
    }

    public void setType( String arg )
    {
        m_type = arg.toLowerCase();
    }

    public final int doVWBStart()
        throws IOException
    {
    	DPage   page   = (DPage)vwbcontext.getResource();

        if( page != null )
        {
            if( m_type.equals("attachment") && page instanceof Attachment )
            {
                return EVAL_BODY_INCLUDE;
            }
            //kevin add begin at 20080121
            if( m_type.equals("clbattachment") && page instanceof CLBAttachment )
            {
                return EVAL_BODY_INCLUDE;
            }
            //kevin added end
            
            if( m_type.equals("page") && !(page instanceof Attachment) )
            {
                return EVAL_BODY_INCLUDE;
            }

            if( m_type.equals("weblogentry") && !(page instanceof Attachment) && page.getTitle().indexOf("_blogentry_") != -1 )
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
