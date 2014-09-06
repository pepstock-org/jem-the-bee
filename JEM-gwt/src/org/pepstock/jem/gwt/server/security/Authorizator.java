/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pepstock.jem.gwt.server.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.RegExpPermission;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.RolesQueuePredicate;
import org.pepstock.jem.node.security.User;

import com.hazelcast.core.IMap;

/**
 * Simple class to extract the permission from Hazelcast queue (by user name)
 * and adds all to the account.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Authorizator {

	/**
	 * Constructor
	 */
	public Authorizator() {
	}

	/**
	 * Methods usually used by Shiro to get all authorizations.
	 * 
	 * @param realm
	 *            realm which is the caller
	 * @param principals
	 *            principals to check
	 * @return account with all roles and permissions
	 * @throws JemException
	 *             if any errors occurs
	 */
	public AuthorizationInfo doGetAuthorizationInfo(Realm realm, PrincipalCollection principals) throws JemException {
		// gets user object from principal
		User user = (User) getAvailablePrincipal(realm, principals);

		// creates account (without credentials)
		SimpleAccount account = new SimpleAccount(user, "nothing", realm.getName());

		// creates Hazelcast predicate to extract all roles and permissions
		// assigned to user
		RolesQueuePredicate predicate = new RolesQueuePredicate();
		predicate.setUser(user);
		try {
			// gets map and performs predicate!
			IMap<String, Role> roles = SharedObjects.getInstance().getHazelcastClient().getMap(Queues.ROLES_MAP);
			Collection<Role> myroles = null;
			boolean isLock = false;
			Lock lock = SharedObjects.getInstance().getHazelcastClient().getLock(Queues.ROLES_MAP_LOCK);
			try {
				isLock = lock.tryLock(10, TimeUnit.SECONDS);
				if (isLock) {
					myroles = roles.values(predicate);
				} else {
					throw new MessageException(UserInterfaceMessage.JEMG022E, Queues.ROLES_MAP);
				}
			} catch (InterruptedException e) {
				throw new MessageException(UserInterfaceMessage.JEMG022E, e, Queues.ROLES_MAP);
            } finally {
				if (isLock) {
					lock.unlock();
				}
			}

			Collection<Permission> perms = new ArrayList<Permission>();
			// scans roles
			for (Role role : myroles) {
				// adds roles
				account.addRole(role.getName());
				// scans permissions
				for (String permission : role.getPermissions()) {
					// if the permission is for SEARCH, uses a regular
					// expression permission
					if (permission.startsWith(Permissions.SEARCH) || 
							permission.startsWith(Permissions.DATASOURCES) || 
							permission.startsWith(Permissions.FILES_READ) ||
							permission.startsWith(Permissions.FILES_WRITE) ||
							permission.startsWith(Permissions.FILES_EXECUTE) ||
							permission.startsWith(Permissions.SURROGATE)) {
						RegExpPermission perm = new RegExpPermission(permission);
						account.addObjectPermission(perm);
						perms.add(perm);
					} else {
						// otherwise a wildcard permisison
						account.addStringPermission(permission);
						// at the moment not added
					}
				}
			}
			user.setPermissions(perms);

		} catch (MessageException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG031E, e, user.getId());
		}
		return account;
	}

	/**
	 * Gets the available principal, represented by a USER object
	 * 
	 * @param realm
	 *            realm which is the caller
	 * @param principals
	 *            principals to check
	 * @return user object from principal, typically added during the
	 *         authentication
	 */
	private Object getAvailablePrincipal(Realm realm, PrincipalCollection principals) {
		Object primary = null;
		if (!CollectionUtils.isEmpty(principals)) {
			@SuppressWarnings("rawtypes")
			Collection thisPrincipals = principals.fromRealm(realm.getName());
			if (!CollectionUtils.isEmpty(thisPrincipals)) {
				primary = thisPrincipals.iterator().next();
			} else {
				// no principals attributed to this particular realm. Fall back
				// to the 'master' primary:
				primary = principals.getPrimaryPrincipal();
			}
		}
		return primary;
	}

}