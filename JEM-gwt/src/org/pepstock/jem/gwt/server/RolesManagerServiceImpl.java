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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.pepstock.jem.gwt.client.services.RolesManagerService;
import org.pepstock.jem.gwt.server.services.RolesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.Role;

/**
 * This service manages all roles and their authorizations and users relations inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RolesManagerServiceImpl extends DefaultManager implements RolesManagerService {

	private static final long serialVersionUID = 1L;

	private transient RolesManager rolesManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.UserManagerService#getRoles()
	 */
	@Override
	public Collection<Role> getRoles(String filter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (rolesManager == null){
			initManager();
		}
		try {
			return rolesManager.getRoles(filter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG041E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#addRole(org.pepstock.jem
	 * .gwt.client.security.Role)
	 */
	@Override
	public Boolean addRole(Role role) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (rolesManager == null){
			initManager();
		}
		try {
			Boolean result = rolesManager.addRole(role);
			// if add rolle is ok, clear cache
			if (result){
				clearAuthorizationCache();
			}
			return result;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG041E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#updateRole(org.pepstock
	 * .jem.gwt.client.security.Role)
	 */
	@Override
	public Boolean updateRole(Role role) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (rolesManager == null){
			initManager();
		}
		try {
			Boolean result = rolesManager.updateRole(role);
			// if add rolle is ok, clear cache
			if (result){
				clearAuthorizationCache();
			}
			return result;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG041E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.UserManagerService#removeRole(java.util.
	 * Collection)
	 */
	@Override
	public Boolean removeRole(Collection<Role> roles) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (rolesManager == null){
			initManager();
		}
		try {
			Boolean result = rolesManager.removeRole(roles);
			// if add rolle is ok, clear cache
			if (result){
				clearAuthorizationCache();
			}
			return result;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG041E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}

	}

	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
     */
	private synchronized void initManager() throws JemException {
		if (rolesManager == null) {
			try {
				rolesManager = new RolesManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG041E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

	/**
	 * This method clears all authorizations cache when a role is changed.<br>
	 * That's necessary to invalidate the cache and then to have the updates
	 * soon available.
	 */
	private void clearAuthorizationCache() {
		RealmSecurityManager mgr = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		Collection<Realm> realmCollection = mgr.getRealms();
		for (Realm realm : realmCollection) {
			if (realm instanceof AuthorizingRealm) {
				AuthorizingRealm authz = (AuthorizingRealm) realm;
				if (authz.isAuthorizationCachingEnabled()) {
					authz.getAuthorizationCache().clear();
				}
			}
		}
	}

}