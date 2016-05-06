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

/**
 * <p> Permission to perform an global wiki operation, such as self-registering
 * or creating new pages. Permission actions include: <code>createGroups</code>,
 * <code>createPages</code>, <code>editPreferences</code>,
 * <code>editProfile</code> and <code>login</code>. </p> <p>The target is
 * a given wiki. The syntax for the target is the wiki name. "All wikis" can be
 * specified using a wildcard (*). Page collections may also be specified using
 * a wildcard. For pages, the wildcard may be a prefix, suffix, or all by
 * itself. <p> Certain permissions imply others. Currently,
 * <code>createGroups</code> implies <code>createPages</code>. </p>
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public final class UserPermission extends Permission
{
    private static final long          serialVersionUID        = 1L;

    public static final String         CREATE_GROUPS_ACTION    = "createGroups";

    public static final String         CREATE_PAGES_ACTION     = "createPages";

    public static final String         LOGIN_ACTION            = "login";

    public static final String         EDIT_PREFERENCES_ACTION = "editPreferences";

    public static final String         EDIT_PROFILE_ACTION     = "editProfile";
    
    //Modified by morrise 20080402
    public static final String         UPLOAD_ACTION           = "upload";
    
    public static final String         PORTLET_ACTION          = "portlet";
    
    public static final String         UPLOADBANNER_ACTION     = "uploadbanner";

    public static final String         WILDCARD                = "*";

    protected static final int         CREATE_GROUPS_MASK      = 0x1;

    protected static final int         CREATE_PAGES_MASK       = 0x2;

    protected static final int         EDIT_PREFERENCES_MASK   = 0x4;

    protected static final int         EDIT_PROFILE_MASK       = 0x8;

    protected static final int         LOGIN_MASK              = 0x10;
    
    //Added by morrise 20080402
    protected static final int         UPLOAD_MASK             = 0x20;
    
    protected static final int         UPLOADBANNER_MASK       = 0x40;
    
    protected static final int         PORTLET_MASK            = 0x80;

    public static final UserPermission CREATE_GROUPS           = new UserPermission( WILDCARD, CREATE_GROUPS_ACTION );

    public static final UserPermission CREATE_PAGES            = new UserPermission( WILDCARD, CREATE_PAGES_ACTION );

    public static final UserPermission LOGIN                   = new UserPermission( WILDCARD, LOGIN_ACTION );

    public static final UserPermission EDIT_PREFERENCES        = new UserPermission( WILDCARD, EDIT_PREFERENCES_ACTION );

    public static final UserPermission EDIT_PROFILE            = new UserPermission( WILDCARD, EDIT_PROFILE_ACTION );

    //Added by morrise 20080402
    public static final UserPermission UPLOAD                  = new UserPermission( WILDCARD, UPLOAD_ACTION );

    //Added by morrise 20080716
    public static final UserPermission UPLOADBANNER            = new UserPermission( WILDCARD, UPLOADBANNER_ACTION );

    
    public static final UserPermission PORTLET                 = new UserPermission( WILDCARD, PORTLET_ACTION );
   
    private final String               m_actionString;

    private final String               m_wiki;

    private final int                  m_mask;

    /**
     * Creates a new WikiPermission for a specified set of actions.
     * @param actions the actions for this permission
     */
    public UserPermission( String wiki, String actions )
    {
        super( wiki );
        String[] pageActions = actions.toLowerCase().split( "," );
        Arrays.sort( pageActions, String.CASE_INSENSITIVE_ORDER );
        m_mask = createMask( actions );
        StringBuffer buffer = new StringBuffer();
        for( int i = 0; i < pageActions.length; i++ )
        {
            buffer.append( pageActions[i] );
            if ( i < ( pageActions.length - 1 ) )
            {
                buffer.append( "," );
            }
        }
        m_actionString = buffer.toString();
        m_wiki = ( wiki == null ) ? WILDCARD : wiki;
    }

    /**
     * Two WikiPermission objects are considered equal if their wikis and
     * actions (after normalization) are equal.
     * @param obj the object to test
     * @return the result
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals( Object obj )
    {
        if ( !( obj instanceof UserPermission ) )
        {
            return false;
        }
        UserPermission p = (UserPermission) obj;
        return  p.m_mask == m_mask && p.m_wiki != null && p.m_wiki.equals( m_wiki );
    }

    /**
     * Returns the actions for this permission: "createGroups", "createPages",
     * "editPreferences", "editProfile", or "login". The actions
     * will always be sorted in alphabetic order, and will always appear in
     * lower case.
     * @return the actions
     * @see java.security.Permission#getActions()
     */
    public final String getActions()
    {
        return m_actionString;
    }

    /**
     * Returns the name of the wiki containing the page represented by this
     * permission; may return the wildcard string.
     * @return the wiki
     */
    public final String getWiki()
    {
        return m_wiki;
    }

    /**
     * Returns the hash code for this WikiPermission.
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return m_mask + ( ( 13 * m_actionString.hashCode() ) * 23 * m_wiki.hashCode() );
    }

    /**
     * WikiPermission can only imply other WikiPermissions; no other permission
     * types are implied. One WikiPermission implies another if all of the other
     * WikiPermission's actions are equal to, or a subset of, those for this
     * permission.
     * @param permission the permission which may (or may not) be implied by
     * this instance
     * @return <code>true</code> if the permission is implied,
     * <code>false</code> otherwise
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public final boolean implies( Permission permission )
    {
        // Permission must be a WikiPermission
        if ( !( permission instanceof UserPermission ) )
        {
            return false;
        }
        UserPermission p = (UserPermission) permission;

        // See if the wiki is implied
        boolean impliedWiki = PagePermission.isSubset( m_wiki, p.m_wiki );

        // Build up an "implied mask" for actions
        int impliedMask = impliedMask( m_mask );

        // If actions aren't a proper subset, return false
        return impliedWiki && ( impliedMask & p.m_mask ) == p.m_mask;
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
        return "(\"" + this.getClass().getName() + "\",\"" + m_wiki + "\",\"" + getActions() + "\")";
    }

    /**
     * Creates an "implied mask" based on the actions originally assigned: for
     * example, <code>createGroups</code> implies <code>createPages</code>.
     * @param mask the initial mask
     * @return the implied mask
     */
    protected static final int impliedMask( int mask )
    {
        if ( ( mask & CREATE_GROUPS_MASK ) > 0 )
        {
            mask |= CREATE_PAGES_MASK;
        }
        return mask;
    }

    /**
     * Private method that creates a binary mask based on the actions specified.
     * This is used by {@link #implies(Permission)}.
     * @param actions the permission actions, separated by commas
     * @return binary mask representing the permissions
     */
    protected static final int createMask( String actions )
    {
        if ( actions == null || actions.length() == 0 )
        {
            throw new IllegalArgumentException( "Actions cannot be blank or null" );
        }
        int mask = 0;
        String[] actionList = actions.split( "," );
        for( int i = 0; i < actionList.length; i++ )
        {
            String action = actionList[i];
            if ( action.equalsIgnoreCase( CREATE_GROUPS_ACTION ) )
            {
                mask |= CREATE_GROUPS_MASK;
            }
            else if ( action.equalsIgnoreCase( CREATE_PAGES_ACTION ) )
            {
                mask |= CREATE_PAGES_MASK;
            }
            else if ( action.equalsIgnoreCase( LOGIN_ACTION ) )
            {
                mask |= LOGIN_MASK;
            }
            else if ( action.equalsIgnoreCase( EDIT_PREFERENCES_ACTION ) )
            {
                mask |= EDIT_PREFERENCES_MASK;
            }
            else if ( action.equalsIgnoreCase( EDIT_PROFILE_ACTION ) )
            {
                mask |= EDIT_PROFILE_MASK;
            }
            //Added by morrise 20080402
            else if ( action.equalsIgnoreCase( UPLOAD_ACTION ) )
            {
                mask |= UPLOAD_MASK;
            }
            else if ( action.equalsIgnoreCase( UPLOADBANNER_ACTION ) )
            {
                mask |= UPLOADBANNER_MASK;
            }else if ( action.equalsIgnoreCase( PORTLET_ACTION ))
            {
                mask |= PORTLET_MASK;
            }
            else
            {
                throw new IllegalArgumentException( "Unrecognized action: " + action );
            }
        }
        return mask;
    }
}