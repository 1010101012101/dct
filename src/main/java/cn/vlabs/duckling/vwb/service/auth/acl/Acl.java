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

package cn.vlabs.duckling.vwb.service.auth.acl;

import java.io.Serializable;
import java.security.Permission;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/**
 * JSPWiki implementation of an Access Control List.
 * 
 * @date Feb 3, 2010
 * @author zzb
 */
public class Acl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Vector<AclEntry> m_entries = new Vector<AclEntry>();

	/**
	 * Constructs a new AclImpl instance.
	 */
	public Acl() {
	}

	/**
	 * Returns all Principal objects assigned a given Permission in the access
	 * control list. The Princiapls returned are those that have been granted
	 * either the supplied permission, or a permission implied by the supplied
	 * permission. Principals are not "expanded" if they are a role or group.
	 * 
	 * @param permission
	 *            the permission to search for
	 * @return an array of Principals posessing the permission
	 */
	public Principal[] findPrincipals(Permission permission) {
		Vector<Principal> principals = new Vector<Principal>();
		Enumeration<AclEntry> entries = entries();

		while (entries.hasMoreElements()) {
			AclEntry entry = (AclEntry) entries.nextElement();
			Enumeration<Permission> permissions = entry.permissions();
			while (permissions.hasMoreElements()) {
				Permission perm = (Permission) permissions.nextElement();
				if (perm.implies(permission)) {
					principals.add(entry.getPrincipal());
				}
			}
		}
		return (Principal[]) principals
				.toArray(new Principal[principals.size()]);
	}

	private boolean hasEntry(AclEntry entry) {
		if (entry == null) {
			return false;
		}

		for (Iterator<AclEntry> i = m_entries.iterator(); i.hasNext();) {
			AclEntry e = (AclEntry) i.next();

			Principal ep = e.getPrincipal();
			Principal entryp = entry.getPrincipal();

			if (ep == null || entryp == null) {
				throw new IllegalArgumentException(
						"Entry is null; check code, please (entry=" + entry
								+ "; e=" + e + ")");
			}

			if (ep.getName().equals(entryp.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds an ACL entry to this ACL. An entry associates a principal (e.g., an
	 * individual or a group) with a set of permissions. Each principal can have
	 * at most one positive ACL entry, specifying permissions to be granted to
	 * the principal. If there is already an ACL entry already in the ACL, false
	 * is returned.
	 * 
	 * @param entry
	 *            - the ACL entry to be added to this ACL
	 * @return true on success, false if an entry of the same type (positive or
	 *         negative) for the same principal is already present in this ACL
	 */
	public synchronized boolean addEntry(AclEntry entry) {
		if (entry.getPrincipal() == null) {
			throw new IllegalArgumentException("Entry principal cannot be null");
		}

		if (hasEntry(entry)) {
			return false;
		}

		m_entries.add(entry);

		return true;
	}

	/**
	 * Removes an ACL entry from this ACL.
	 * 
	 * @param entry
	 *            the ACL entry to be removed from this ACL
	 * @return true on success, false if the entry is not part of this ACL
	 */
	public synchronized boolean removeEntry(AclEntry entry) {
		return m_entries.remove(entry);
	}

	/**
	 * Returns an enumeration of the entries in this ACL. Each element in the
	 * enumeration is of type AclEntry.
	 * 
	 * @return an enumeration of the entries in this ACL.
	 */
	public Enumeration<AclEntry> entries() {
		return m_entries.elements();
	}

	/**
	 * Returns an AclEntry for a supplied Principal, or <code>null</code> if the
	 * Principal does not have a matching AclEntry.
	 * 
	 * @param principal
	 *            the principal to search for
	 * @return the AclEntry associated with the principal, or <code>null</code>
	 */
	public AclEntry getEntry(Principal principal) {
		for (Enumeration<AclEntry> e = m_entries.elements(); e
				.hasMoreElements();) {
			AclEntry entry = (AclEntry) e.nextElement();

			if (entry.getPrincipal().getName().equals(principal.getName())) {
				return entry;
			}
		}

		return null;
	}

	/**
	 * Returns a string representation of the contents of this Acl.
	 * 
	 * @return the string representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (Enumeration<AclEntry> myEnum = entries(); myEnum.hasMoreElements();) {
			AclEntry entry = (AclEntry) myEnum.nextElement();

			Principal pal = entry.getPrincipal();

			if (pal != null)
				sb.append("  user = " + pal.getName() + ": ");
			else
				sb.append("  user = null: ");

			sb.append("(");
			for (Enumeration<Permission> perms = entry.permissions(); perms
					.hasMoreElements();) {
				Permission perm = (Permission) perms.nextElement();
				sb.append(perm.toString());
			}
			sb.append(")\n");
		}

		return sb.toString();
	}

	/**
	 * Returns <code>true</code>, if this Acl is empty.
	 * 
	 * @return the result
	 * @since 2.4.68
	 */
	public boolean isEmpty() {
		return m_entries.isEmpty();
	}

}
