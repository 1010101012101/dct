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

/**
 * <p>
 * Permission to perform all operations on a given wiki.
 * </p>
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public final class AllPermission extends Permission
{
	public static final AllPermission ALL=new AllPermission("*");
	
    private static final long   serialVersionUID = 1L;

    /**
     * Creates a new AllPermission for the given wikis.
     * 
     * @param wiki the wiki to which the permission should apply.  If null, will
     *             apply to all wikis.
     */
    public AllPermission(String name)
    {
    	super(name);
    }

    /**
     * Two AllPermission objects are considered equal if their wikis are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals( Object obj )
    {
        if ( !( obj instanceof AllPermission ) )
        {
            return false;
        }else
        	return true;
    }

    /**
     * No-op; always returns <code>null</code>
     * @see java.security.Permission#getActions()
     */
    public final String getActions()
    {
        return null;
    }

    public final int hashCode()
    {
        return getName().hashCode();
    }

    public final boolean implies( Permission permission )
    {
        if ( !isSystemPermission( permission ) )
        {
            return false;
        }
        return true;
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
        return "(\"" + this.getClass().getName() + "\")";
    }

    protected static final boolean isSystemPermission( Permission permission )
    {
        return   permission instanceof PagePermission ||
                 permission instanceof AllPermission;
    }

}
