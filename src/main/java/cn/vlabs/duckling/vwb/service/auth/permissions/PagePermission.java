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

package cn.vlabs.duckling.vwb.service.auth.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public final class PagePermission extends Permission
{
    private static final long          serialVersionUID = 2L;

    public static final String         COMMENT_ACTION = "comment";

    public static final String         DELETE_ACTION  = "delete";

    public static final String         EDIT_ACTION    = "edit";

    public static final String         MODIFY_ACTION  = "modify";

    public static final String         RENAME_ACTION  = "rename";

    public static final String         VIEW_ACTION    = "view";
    
    public static final String         SHARE_ACTION    = "share";

    protected static final int         COMMENT_MASK   = 0x4;

    protected static final int         DELETE_MASK    = 0x10;

    protected static final int         EDIT_MASK      = 0x2;

    protected static final int         MODIFY_MASK    = 0x40;

    protected static final int         RENAME_MASK    = 0x20;

    protected static final int         SHARE_MASK    = 0x8; 

    protected static final int         VIEW_MASK      = 0x1;

    public static final PagePermission COMMENT        = new PagePermission( COMMENT_ACTION );

    public static final PagePermission DELETE         = new PagePermission( DELETE_ACTION );

    public static final PagePermission EDIT           = new PagePermission( EDIT_ACTION );

    public static final PagePermission RENAME         = new PagePermission( RENAME_ACTION );

    public static final PagePermission MODIFY         = new PagePermission( MODIFY_ACTION );

    public static final PagePermission VIEW           = new PagePermission( VIEW_ACTION );

    private static final String        ACTION_SEPARATOR = ",";

    private static final String        WILDCARD       = "*";

    private static final String        ATTACHMENT_SEPARATOR = "/";

    private final String               m_actionString;

    private final int                  m_mask;

    private final String               m_page;

    /**
     * Private convenience constructor that creates a new PagePermission for all wikis and pages
     * (*:*) and set of actions.
     * @param actions
     */
    private PagePermission( String actions )
    {
        this(WILDCARD, actions );
    }

    /**
     * Creates a new PagePermission for a specified page name and set of
     * actions. Page should include a prepended wiki name followed by a colon (:).
     * If the wiki name is not supplied or starts with a colon, the page
     * refers to no wiki in particular, and will never imply any other
     * PagePermission.
     * @param page the wiki page
     * @param actions the allowed actions for this page
     */
    public PagePermission( String page, String actions )
    {
        super( page );

        // Parse wiki and page (which may include wiki name and page)
        // Strip out attachment separator; it is irrelevant.
        
        int pos = page.indexOf( ATTACHMENT_SEPARATOR );
        m_page = ( pos == -1 ) ? page : page.substring( 0, pos );

        // Parse actions
        String[] pageActions = StringUtils.split( actions.toLowerCase(), ACTION_SEPARATOR );
        Arrays.sort( pageActions, String.CASE_INSENSITIVE_ORDER );
        m_mask = createMask( actions );
        StringBuffer buffer = new StringBuffer();
        for( int i = 0; i < pageActions.length; i++ )
        {
            buffer.append( pageActions[i] );
            if ( i < ( pageActions.length - 1 ) )
            {
                buffer.append( ACTION_SEPARATOR );
            }
        }
        m_actionString = buffer.toString();
    }

    /**
     * Creates a new PagePermission for a specified page and set of actions.
     * @param page The wikipage.
     * @param actions A set of actions; a comma-separated list of actions.
     */
/*    public PagePermission( WikiPage page, String actions )
    {
        this( page.getWiki() + WIKI_SEPARATOR + page.getName(), actions );
    }*/

    /**
     * Two PagePermission objects are considered equal if their actions (after
     * normalization), wiki and target are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals( Object obj )
    {
        if ( !( obj instanceof PagePermission ) )
        {
            return false;
        }
        PagePermission p = (PagePermission) obj;
        return  p.m_mask == m_mask && p.m_page.equals( m_page );
    }

    /**
     * Returns the actions for this permission: "view", "edit", "comment",
     * "modify", "upload" or "delete". The actions will always be sorted in alphabetic
     * order, and will always appear in lower case.
     * @see java.security.Permission#getActions()
     */
    public final String getActions()
    {
        return m_actionString;
    }

    /**
     * Returns the name of the wiki page represented by this permission.
     * @return the page name
     */
    public final String getPage()
    {
        return m_page;
    }

    /**
     * Returns the hash code for this PagePermission.
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        //  If the wiki has not been set, uses a dummy value for the hashcode
        //  calculation.  This may occur if the page given does not refer
        //  to any particular wiki
        String page = m_page != null ? m_page : "dummy_value";
        return m_mask + ( ( 13 * m_actionString.hashCode() ) * 23 * page.hashCode() );
    }

    /**
     * <p>
     * PagePermission can only imply other PagePermissions; no other permission
     * types are implied. One PagePermission implies another if its actions if
     * three conditions are met:
     * </p>
     * <ol>
     * <li>The other PagePermission's wiki is equal to, or a subset of, that of
     * this permission. This permission's wiki is considered a superset of the
     * other if it contains a matching prefix plus a wildcard, or a wildcard
     * followed by a matching suffix.</li>
     * <li>The other PagePermission's target is equal to, or a subset of, the
     * target specified by this permission. This permission's target is
     * considered a superset of the other if it contains a matching prefix plus
     * a wildcard, or a wildcard followed by a matching suffix.</li>
     * <li>All of other PagePermission's actions are equal to, or a subset of,
     * those of this permission</li>
     * </ol>
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public final boolean implies( Permission permission )
    {
        // Permission must be a PagePermission
        if ( !( permission instanceof PagePermission ) )
        {
            return false;
        }

        // Build up an "implied mask"
        PagePermission p = (PagePermission) permission;
        int impliedMask = impliedMask( m_mask );

        // If actions aren't a proper subset, return false
        if ( ( impliedMask & p.m_mask ) != p.m_mask )
        {
            return false;
        }
        
        return isSubset( m_page, p.m_page );
    }

    /**
     * Returns a new {@link AllPermissionCollection}.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection()
    {
        return new AllPermissionCollection();
    }

    /**
     * Prints a human-readable representation of this permission.
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return "(\"" + this.getClass().getName() + "\",\"" + m_page + "\",\"" + getActions() + "\")";
    }

    /**
     * Creates an "implied mask" based on the actions originally assigned: for
     * example, delete implies modify, comment, upload and view.
     * @param mask binary mask for actions
     * @return binary mask for implied actions
     */
    protected static final int impliedMask( int mask )
    {
        if ( ( mask & DELETE_MASK ) > 0 )
        {
            mask |= MODIFY_MASK;
        }
        if ( ( mask & RENAME_MASK ) > 0 )
        {
            mask |= EDIT_MASK;
        }
        if ( ( mask & MODIFY_MASK ) > 0 )
        {
            //Modified by morrise 20080402
            //mask |= EDIT_MASK | UPLOAD_MASK;
            mask |= EDIT_MASK;
        }
        if ( ( mask & EDIT_MASK ) > 0 )
        {
            mask |= COMMENT_MASK;
        }
        if ( ( mask & COMMENT_MASK ) > 0 )
        {
            mask |= VIEW_MASK;
        }
        //Modified by morrise 20080402
        //if ( ( mask & UPLOAD_MASK ) > 0 )
        //{
        //    mask |= VIEW_MASK;
        //}
        return mask;
    }

    /**
     * Determines whether one target string is a logical subset of the other.
     * @param superSet the prospective superset
     * @param subSet the prospective subset
     * @return the results of the test, where <code>true</code> indicates that
     *         <code>subSet</code> is a subset of <code>superSet</code>
     */
    protected static final boolean isSubset( String superSet, String subSet )
    {
        // If either is null, return false
        if ( superSet == null || subSet == null )
        {
            return false;
        }

        // If targets are identical, it's a subset
        if ( superSet.equals( subSet ) )
        {
            return true;
        }

        // If super is "*", it's a subset
        if ( superSet.equals( WILDCARD ) )
        {
            return true;
        }

        // If super starts with "*", sub must end with everything after the *
        if ( superSet.startsWith( WILDCARD ) )
        {
            String suffix = superSet.substring( 1 );
            return subSet.endsWith( suffix );
        }

        // If super ends with "*", sub must start with everything before *
        if ( superSet.endsWith( WILDCARD ) )
        {
            String prefix = superSet.substring( 0, superSet.length() - 1 );
            return subSet.startsWith( prefix );
        }

        return false;
    }

    /**
     * Private method that creates a binary mask based on the actions specified.
     * This is used by {@link #implies(Permission)}.
     * @param actions the actions for this permission, separated by commas
     * @return the binary actions mask
     */
    protected static final int createMask( String actions )
    {
        if ( actions == null || actions.length() == 0 )
        {
            throw new IllegalArgumentException( "Actions cannot be blank or null" );
        }
        int mask = 0;
        String[] actionList = StringUtils.split( actions, ACTION_SEPARATOR );
        for( int i = 0; i < actionList.length; i++ )
        {
            String action = actionList[i];
            if ( action.equalsIgnoreCase( VIEW_ACTION ) )
            {
                mask |= VIEW_MASK;
            }
            else if ( action.equalsIgnoreCase( EDIT_ACTION ) )
            {
                mask |= EDIT_MASK;
            }
            else if ( action.equalsIgnoreCase( COMMENT_ACTION ) )
            {
                mask |= COMMENT_MASK;
            }
            else if ( action.equalsIgnoreCase( MODIFY_ACTION ) )
            {
                mask |= MODIFY_MASK;
            }
            //Modified by morrise 20080402
            //else if ( action.equalsIgnoreCase( UPLOAD_ACTION ) )
            //{
            //    mask |= UPLOAD_MASK;
            //}
            else if ( action.equalsIgnoreCase( DELETE_ACTION ) )
            {
                mask |= DELETE_MASK;
            }
            else if ( action.equalsIgnoreCase( RENAME_ACTION ) )
            {
                mask |= RENAME_MASK;
            }
            else if ( action.equalsIgnoreCase( SHARE_ACTION ) )  //added by morrise
            {
                mask |= SHARE_MASK;
            }
            else
            {
                throw new IllegalArgumentException( "Unrecognized action: " + action );
            }
        }
        return mask;
    }
}