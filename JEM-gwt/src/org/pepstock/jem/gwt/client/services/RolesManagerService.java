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

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.security.Role;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for roles
 * management
 */
@RemoteServiceRelativePath(Services.ROLES)
public interface RolesManagerService extends RemoteService {

	/**
	 * Returns the roles using a filter by role name.
	 * 
	 * @param filter
	 *            role name filter
	 * @return list of roles
	 * @throws JemException
	 *             if error occurs
	 */
	Collection<Role> getRoles(String filter) throws JemException;

	/**
	 * Adds a new role
	 * 
	 * @param role
	 *            new role to add
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean addRole(Role role) throws JemException;

	/**
	 * Updates an existing role
	 * 
	 * @param role
	 *            role instance to update
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean updateRole(Role role) throws JemException;

	/**
	 * Removes a list of roles
	 * 
	 * @param roles
	 *            list of roles to be removed
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean removeRole(Collection<Role> roles) throws JemException;

}