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
package org.pepstock.jem.gwt.server.services;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.certificates.GetCryptedValueAndHash;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterToken;
import org.pepstock.jem.util.filters.fields.ResourceFilterFields;
import org.pepstock.jem.util.filters.predicates.ResourcePredicate;

import com.hazelcast.core.IMap;

/**
 * This service manages all common resources inside of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CommonResourcesManager extends DefaultService {

	/**
	 * Puts a resource in JEM, adding if new or replacing if exists.<br>
	 * Checks if the user is authorized to add and update resources
	 * 
	 * @param resource
	 *            resource to add
	 * @return <code>true</code> if the resource has been added otherwise
	 *         <code>false</code> if replaced
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any exception occurs
	 */
	public boolean put(Resource resource) throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();

		// checks if the key (resource name) exists
		IMap<String, Resource> map = getInstance().getMap(Queues.COMMON_RESOURCES_MAP);
		if (map.containsKey(resource.getName())) {
			// checks if the user is authorized to update resource
			// if not, this method throws an exception
			checkAuthorization(new StringPermission(Permissions.RESOURCES_UPDATE));
			try {
				// locks the key
				map.lock(resource.getName());

				// gets old object and checks user
				// this is necessary to check if the new object
				// is the same and none has updated it in the meantime
				Resource oldResource = map.get(resource.getName());
				// checks if the user is the same
				// if not, throws an exception.
				// that means the someone else changed the object
				if (oldResource.getUser() != null && !oldResource.getUser().equalsIgnoreCase(resource.getUser())) {
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldResource, resource);
					throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldResource, resource);
				}
				// checks last modified
				// this is necessary to check if the new object
				// is the same and none has updated it in the meantime
				// checks if the same
				// if not, throws an exception.
				// that means the someone else changed the object
				if (oldResource.getLastModified() != null && !oldResource.getLastModified().equals(resource.getLastModified())) {
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG047E, oldResource, resource);
					throw new ServiceMessageException(UserInterfaceMessage.JEMG047E, oldResource, resource);
				}

				// here the update is consistent so
				// gets user info and time storing that on
				// object
				Subject currentUser = SecurityUtils.getSubject();
				User userPrincipal = (User) currentUser.getPrincipal();
				String userId = userPrincipal.getId();
				resource.setUser(userId);
				resource.setLastModified(new Date());
				// replaces on map
				map.replace(resource.getName(), resource);
			} finally {
				// unlocks always the key
				map.unlock(resource.getName());
			}
			// returns false because it updates
			// a key already present in map
			return false;
		} else {
			// checks if the user is authorized to create resources
			// if not, this method throws an exception
			checkAuthorization(new StringPermission(Permissions.RESOURCES_CREATE));
			try {
				// locks the key
				map.lock(resource.getName());

				// gets user info and time storing that on
				// object
				Subject currentUser = SecurityUtils.getSubject();
				User userPrincipal = (User) currentUser.getPrincipal();
				String userId = userPrincipal.getId();
				resource.setUser(userId);
				resource.setLastModified(new Date());
				// puts on map
				map.put(resource.getName(), resource);
			} finally {
				// unlocks always the key
				map.unlock(resource.getName());
			}
			// returns true because it creates
			// new object
			return true;
		}
	}

	/**
	 * Gets the resource by name
	 * 
	 * @param name
	 *            name of resource
	 * @return resource instance
	 * @throws ServiceMessageException
	 *             if any exception occurs
	 */
	public Resource get(String name) throws ServiceMessageException {
		// checks if the user is authorized to read resources information
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.RESOURCES_READ));
		IMap<String, Resource> map = getInstance().getMap(Queues.COMMON_RESOURCES_MAP);
		// checks if the key (resource name) exists
		if (map.containsKey(name)) {
			Resource resource = null;
			try {
				// locks the key
				map.lock(name);
				// gets the resource
				// to return
				resource = map.get(name);
			} finally {
				// unlocks always the key
				map.unlock(name);
			}
			return resource;
		}
		// resource is not in map. Returns null
		return null;
	}

	/**
	 * Removes a resource by name.
	 * 
	 * @param name
	 *            name of resource
	 * @return <code>true</code> if the resource has been removed otherwise
	 *         <code>false</code> if not
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any exception occurs
	 */
	public boolean remove(String name) throws ServiceMessageException {
		// checks if the user is authorized to delete resources
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.RESOURCES_DELETE));
		IMap<String, Resource> map = getInstance().getMap(Queues.COMMON_RESOURCES_MAP);
		// checks if the key (resource name) exists
		if (map.containsKey(name)) {
			Resource resource = null;
			try {
				// locks the key
				map.lock(name);
				// gets the resource removing it
				// from map
				resource = map.remove(name);
			} finally {
				// unlocks always the key
				map.unlock(name);
			}
			// returns true if object was in the map
			// otherwise false
			return resource != null;
		}
		// key is not in map
		// then return false
		return false;
	}

	/**
	 * Gets all resources list defined in JEM
	 * 
	 * @param filter
	 *            filter of resources
	 * @return collection of resources
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any exception occurs
	 */
	public Collection<Resource> values(String filter) throws ServiceMessageException {
		// checks if the user is authorized to read resources information
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.RESOURCES_READ));

		IMap<String, Resource> map = getInstance().getMap(Queues.COMMON_RESOURCES_MAP);
		// creates a Resource predicate
		// using filter filled on UI
		ResourcePredicate predicate;
		try {
			predicate = new ResourcePredicate(Filter.parse(filter));
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			// default case, all resources with empty filter by name
			Filter all = new Filter();
			all.add(new FilterToken(ResourceFilterFields.NAME.getName(), StringUtils.EMPTY));
			predicate = new ResourcePredicate(all);
		}

		Collection<Resource> result = null;
		// locks all map to have a consistent collection
		// only for 10 seconds otherwise
		// throws an exception
		boolean isLock = false;
		Lock lock = getInstance().getLock(Queues.COMMON_RESOURCES_MAP_LOCK);
		try {
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				// performs predicate to have the collection
				result = map.values(predicate);
			} else {
				// timeout exception
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.COMMON_RESOURCES_MAP);
			}
		} catch (InterruptedException e) {
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.COMMON_RESOURCES_MAP);
		} finally {
			// unlocks always the map
			if (isLock){
				lock.unlock();
			}
		}
		// returns a collection
		return result;

	}

	/**
	 * Returns a container of a secret, crypted and hashed.<br>
	 * This uses a key automatically creates for whole cluster.<br>
	 * This is necessary to crypt possible password when you define new
	 * resources
	 * 
	 * @param secret
	 *            secret value to crypt and hash
	 * @return a container of crypted and hash
	 * @throws ServiceMessageException
	 *             if any exception occurs
	 */
	public CryptedValueAndHash getEncryptedSecret(String secret) throws ServiceMessageException {
		// checks if the user is authorized to create resources
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.RESOURCES_CREATE));

		DistributedTaskExecutor<CryptedValueAndHash> task = new DistributedTaskExecutor<CryptedValueAndHash>(new GetCryptedValueAndHash(secret), getMember());
		return task.getResult();
	}

}