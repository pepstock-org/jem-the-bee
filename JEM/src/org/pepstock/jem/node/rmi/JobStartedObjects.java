/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.rmi;

import java.io.Serializable;
import java.util.Collection;

import org.pepstock.jem.node.DataPathsManager;
import org.pepstock.jem.node.security.Role;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class JobStartedObjects implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Collection<Role> roles = null;
	
	private DataPathsManager storageGroupsManager = null;

	/**
	 * 
	 */
	public JobStartedObjects() {
	}

	/**
	 * @return the roles
	 */
	public Collection<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @return the storageGroupsManager
	 */
	public DataPathsManager getStorageGroupsManager() {
		return storageGroupsManager;
	}

	/**
	 * @param storageGroupsManager the storageGroupsManager to set
	 */
	public void setStorageGroupsManager(DataPathsManager storageGroupsManager) {
		this.storageGroupsManager = storageGroupsManager;
	}
	
}
