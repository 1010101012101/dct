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

package cn.vlabs.duckling.vwb.service.plugin.impl;

import java.util.Map;

import cn.vlabs.duckling.util.TextUtil;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.plugin.AbstractDPagePlugin;
import cn.vlabs.duckling.vwb.service.plugin.PluginException;

/**
 * Introduction Here.
 * @date 2010-3-11
 * @author Fred Zhang (fred@cnic.cn)
 */
public class MediaPlayerPlugin  extends AbstractDPagePlugin {
	private static final String PARAM_SRC      = "src";
   private static final String PARAM_PTYPE    = "playertype";

   private static final String PARAM_ALIGN    = "align";
   private static final String PARAM_HEIGHT   = "height";
   private static final String PARAM_WIDTH    = "width";
   private static final String PARAM_ALT      = "alt";
   private static final String PARAM_CAPTION  = "caption";
   private static final String PARAM_LINK     = "link";
   private static final String PARAM_STYLE    = "style";
   private static final String PARAM_CLASS    = "class";
   private static final String PARAM_BORDER   = "border";
   private static final String PARAM_CONTROL   = "control";
   private static final String PARAM_AUTOSTART = "autostart";
   private static final String PARAM_AUTOREWIND  = "autorewind";
   private static final String PARAM_COUNT   = "playcount";
   private static final String PARAM_MHEIGHT   = "movieheight";
   private static final String PARAM_MWIDTH    = "moviewidth";


	public String execute(VWBContext context, Map<String,String> params)
			throws PluginException {

        StringBuffer result = new StringBuffer();

        String src   = (String) params.get( PARAM_SRC );
        String ptype = (String) params.get( PARAM_PTYPE );
        String align    = (String) params.get( PARAM_ALIGN );
        String height   = (String) params.get( PARAM_HEIGHT );
        String width        = (String) params.get( PARAM_WIDTH );
        String alt = (String) params.get( PARAM_ALT );
        String caption        = (String) params.get( PARAM_CAPTION );
        String link   = (String) params.get( PARAM_LINK );
        String style        = (String) params.get( PARAM_STYLE );
        String clazz = (String) params.get( PARAM_CLASS );
        String border        = (String) params.get( PARAM_BORDER );
        String control  = (String) params.get( PARAM_CONTROL );
        String autostart  = (String) params.get(  PARAM_AUTOSTART );
        String autorewind  = (String) params.get(  PARAM_AUTOREWIND );
        String playcount  = (String) params.get(  PARAM_COUNT );
        String movieheight  = (String) params.get(  PARAM_MHEIGHT );
        String moviewidth  = (String) params.get(  PARAM_MWIDTH );


        if( src == null )
        {
            throw new PluginException("Parameter 'src' is required for Mediaplayer plugin");
        }

        // -- * section begin * default parameter, if no value input --
        if( ptype == null )
        {
        	ptype = "mediaplayer";
        }

        if( clazz == null ) clazz = "imageplugin";

        src = context.getBaseURL() + "/" + src;

        result.append( "<table border=\"0\" class=\""+clazz+"\"" );

        if( style != null )
        {
            result.append(" style=\""+style);
            // Make sure that we add a ";" to the end of the style string
            if( result.charAt( result.length()-1 ) != ';' ) result.append(";");

            if( align != null && align.equals("center") )
            {
                result.append(" margin-left: auto; margin-right: auto;");
            }

            result.append("\"");
        }
        else
        {
            if( align != null && align.equals("center") ) result.append(" style=\"margin-left: auto; margin-right: auto;\"");
        }

        if( align != null && !(align.equals("center")) ) result.append(" align=\""+align+"\"");

        result.append( ">\n" );

        if( caption != null )
        {
            result.append("<caption align=bottom>"+TextUtil.replaceEntities(caption)+"</caption>\n");
        }


        result.append( "<tr><td>" );

        if( link != null )
        {
            result.append("<a href=\""+link+"\">");
        }

        if(ptype.compareToIgnoreCase("quicktime")==0)
        {

          // html player code part1: ActiveX Object
          result.append( "<object id=\"video\"  border=\"0\" classid=\"clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B\" codebase=\"http://www.apple.com/qtactivex/qtplugin.cab\" " );

          if( height != null )     result.append(" height=\""+height+"\"");
          if( width != null )     result.append(" width=\""+width+"\"");
          if( alt != null )    result.append(" alt=\""+alt+"\"");
          if( border != null ) result.append(" border=\""+border+"\"");

          result.append(" >");

          result.append(" <param name=\"src\" value=\""+src+"\">");
          result.append(" <param name=\"scale\" value=\"tofit\">");

          if( control != null )     result.append(" <param name=\"controller\" value=\""+control+"\">");
          if( autostart != null )     result.append(" <param name=\"autoplay\" value=\""+autostart+"\">");
          if( autorewind != null )     result.append(" <param name=\"loop\" value=\""+autorewind+"\">");
          if( playcount != null )     result.append(" <param name=\"PlayCount\" value=\""+playcount+"\">");

          if( movieheight != null )     result.append(" <param name=\"MovieWindowHeight\" value=\""+movieheight+"\">");
          if( moviewidth != null )     result.append(" <param name=\"MovieWindowWidth\" value=\""+moviewidth+"\">");

          // html player code part2: Embedd
          result.append( "<embed src=\""+src+"\" pluginspage=\"http://www.apple.com/quicktime/download/\" scale=\"tofit\" ");

          if( control != null )     result.append(" controller=\""+control+"\" ");

          if( autostart != null)     result.append(" autoplay=\""+autostart +"\" ");

          if( autorewind != null )     result.append(" loop=\""+autorewind+"\" ");
          if( playcount != null)     result.append(" PlayCount=\""+playcount +"\" ");

          if( movieheight != null )     result.append(" height=\""+movieheight+"\"");
          if( moviewidth != null )     result.append(" width=\""+moviewidth+"\"");

          result.append( "></embed></object>");
        }
        else
        {
          // html player code part1: ActiveX Object
          result.append( "<object id=\"video\"  border=\"0\" classid=\"CLSID:05589FA1-C356-11CE-BF01-00AA0055595A\" " );

          if( height != null )     result.append(" height=\""+height+"\"");
          if( width != null )     result.append(" width=\""+width+"\"");
          if( alt != null )    result.append(" alt=\""+alt+"\"");
          if( border != null ) result.append(" border=\""+border+"\"");

          result.append(" >");

          result.append(" <param name=\"FileName\" value=\""+src+"\">");
          result.append(" <param name=\"ShowDisplay\" value=\"0\">");

          if( control != null )     result.append(" <param name=\"ShowControls\" value=\""+control+"\">");
          if( autostart != null )     result.append(" <param name=\"AutoStart\" value=\""+autostart+"\">");
          if( autorewind != null )     result.append(" <param name=\"AutoRewind\" value=\""+autorewind+"\">");
          if( playcount != null )     result.append(" <param name=\"PlayCount\" value=\""+playcount+"\">");

          if( movieheight != null )     result.append(" <param name=\"MovieWindowHeight\" value=\""+movieheight+"\">");
          if( moviewidth != null )     result.append(" <param name=\"MovieWindowWidth\" value=\""+moviewidth+"\">");

          // html player code part2: Embedd

          result.append( "<embed src=\""+src+"\" type=\"application/x-mplayer2\" ");

          if( control != null )     result.append(" ShowPositionControls=\""+control+"\" ");

          if( autostart != null)     result.append(" AutoStart=\""+autostart +"\" ");

          if( autorewind != null )     result.append(" AutoRewind=\""+autorewind+"\" ");
          if( playcount != null)     result.append(" PlayCount=\""+playcount +"\" ");

          if( movieheight != null )     result.append(" height=\""+movieheight+"\"");
          if( moviewidth != null )     result.append(" width=\""+moviewidth+"\"");

          result.append( "></embed></object>");
        }


        if( link != null )  result.append("</a>");
        result.append("</td></tr>\n");

        result.append("</table>\n");

        return result.toString();
	}

}
