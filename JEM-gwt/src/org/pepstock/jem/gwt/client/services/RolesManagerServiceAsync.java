/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.node.security.Role;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async service.
 * 
 * @see RolesManagerService
 */
public interface RolesManagerServiceAsync {
	/**
	 * @see RolesManagerService#getRoles(String)
	 * @param filter
	 * @param callback
	 */
	void getRoles(String filter, AsyncCallback<Collection<Role>> callback);

	/**
	 * @see RolesManagerService#addRole(Role)
	 * @param role
	 * @param callback
	 */
	void addRole(Role role, AsyncCallback<Boolean> callback);

	/**
	 * @see RolesManagerService#updateRole(Role)
	 * @param role
	 * @param callback
	 */
	void updateRole(Role role, AsyncCallback<Boolean> callback);

	/**
	 * @see RolesManagerService#removeRole(Collection)
	 * @param roles
	 * @param callback
	 */
	void removeRole(Collection<Role> roles, AsyncCallback<Boolean> callback);;
}