/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.RoleFilterFields;
import org.pepstock.jem.util.filters.predicates.RolePredicate;

import com.hazelcast.core.IMap;

/**
 * This service manages all roles and their authorizations and users relations inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RolesManager extends DefaultService{

	/**
	 * Returns a collection of roles, by a filter (a set of key-values).
	 * 
	 * @param filter
	 *            string which contains a set of key-values
	 * @return a collection of roles, matching the filter
	 * @throws ServiceMessageException
	 *             if any exception occurs
	 */
	public Collection<Role> getRoles(String filter) throws ServiceMessageException {
		// checks if the user is authorized to read roles
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ROLES_READ));

		IMap<String, Role> roles = getInstance().getMap(Queues.ROLES_MAP);
		RolePredicate predicate;
		try {
			// creates predicate by filter string
			predicate = new RolePredicate(Filter.parse(filter));
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// default case, all roles
			Filter all = new Filter();
			all.add(new FilterToken(RoleFilterFields.NAME.getName(), StringUtils.EMPTY));
			predicate = new RolePredicate(all);
		}

		List<Role> list = null;
		// locks all map to have a consistent collection
		// only for 10 seconds otherwise
		// throws an exception
		boolean isLock = false;
		Lock lock = getInstance().getLock(Queues.ROLES_MAP_LOCK);
		try {
			isLock = lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock) {
				// applies predicate
				list = new ArrayList<Role>(roles.values(predicate));
			} else {
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.ROLES_MAP);
			}
		} catch (InterruptedException e) {
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.ROLES_MAP);
		} finally {
			// unlocks always the map
			if (isLock) {
				lock.unlock();
			}
		}
		return list;
	}

	/**
	 * Adds a new role in JEM.
	 * 
	 * @param role
	 *            role definition
	 * @return always ture
	 * @throws ServiceMessageException
	 *             if any exception occurs
	 */
    public Boolean addRole(Role role) throws ServiceMessageException {
		// checks if the user is authorized to create roles
		// if not, this method throws an exception    	
    	checkAuthorization(new StringPermission(Permissions.ROLES_CREATE));
    	
    	IMap<String, Role> roles = getInstance().getMap(Queues.ROLES_MAP);
    	// if role name is already present, throws an exception
    	if (roles.containsKey(role.getName())){
    		throw new ServiceMessageException(UserInterfaceMessage.JEMG026E, role.getName());
    	}
    	
    	try {
    		// lock the key (role name)
    		roles.lock(role.getName());
			// gets user info and time storing that on
			// object
			Subject currentUser = SecurityUtils.getSubject();
			User userPrincipal = (User)currentUser.getPrincipal();
			String userId = userPrincipal.getId();
			role.setUser(userId);
			role.setLastModified(new Date());
			// puts on map
    		roles.put(role.getName(), role);
    	} finally {
    		// unlocks always the key
    		roles.unlock(role.getName());
    	}
    	return Boolean.TRUE;
    }

    /**
     * Updates an already present role. Checks the role is consistent with 
     * the present one. That means role previously serialized to client must be
     * the same in the map. Otherwise means the someone else has already updated it,
     * so becomes inconsistent.
     * 
     * @param role updated role
     * @return always true
     * @throws ServiceMessageException 
     * @throws Exception if any excetion occurs or if the updated role is not consistent
     */
    public Boolean updateRole(Role role) throws ServiceMessageException  {
		// checks if the user is authorized to update roles
		// if not, this method throws an exception 
    	checkAuthorization(new StringPermission(Permissions.ROLES_UPDATE));
    	
    	IMap<String, Role> roles = getInstance().getMap(Queues.ROLES_MAP);
    	// if role doesn't exist, 
    	if (!roles.containsKey(role.getName())){
    		throw new ServiceMessageException(UserInterfaceMessage.JEMG027E, role.getName());
    	}
    	try {
    		// locks the key (role name)
    		roles.lock(role.getName());
    		// gets old role and checks if is consistent with the new one.
    		// checks if last user who updated it is the same
    		// and last modified time is the same
			Role oldRole = roles.get(role.getName());
			if (oldRole.getUser() != null && !oldRole.getUser().equalsIgnoreCase(role.getUser())){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldRole, role);
				throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldRole, role);
			}
			if (oldRole.getLastModified() != null && !oldRole.getLastModified().equals(role.getLastModified())){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldRole, role);
				throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldRole, role);
			}
			
			// sets new values both user and time
			Subject currentUser = SecurityUtils.getSubject();
			User userPrincipal = (User)currentUser.getPrincipal();
			String userId = userPrincipal.getId();
			role.setUser(userId);
			role.setLastModified(new Date());
			// replaces role
    		roles.replace(role.getName(), role);
    	} finally {
    		// unlolcks always the key
    		roles.unlock(role.getName());
    	}
    	return Boolean.TRUE;
    }


    /**
     * Removes a collection of roles from JEM.
     * 
     * @param roles collection of roles to be removed 
     * @return always true
     * @throws ServiceMessageException 
     * @throws Exception if any exception
     */
    public Boolean removeRole(Collection<Role> roles) throws ServiceMessageException {
		// checks if the user is authorized to delete roles
		// if not, this method throws an exception
    	
    	checkAuthorization(new StringPermission(Permissions.ROLES_DELETE));
    	IMap<String, Role> map = getInstance().getMap(Queues.ROLES_MAP);
    	// scans all roles
    	for (Role role : roles){
    		// checks if roles is removable
    		// all custom roles are removables
    		// only out-of-the-box roles can't be removed
    		if (role.isRemovable() && map.containsKey(role.getName())) {
    			try{
    				// locks the key (role name)
    				map.lock(role.getName());
    				// removes it
    				map.remove(role.getName());
    			} finally {
    				// unlocks the key
    				map.unlock(role.getName());		
    			}
    		}
    	}
    	return Boolean.TRUE;
    }

}