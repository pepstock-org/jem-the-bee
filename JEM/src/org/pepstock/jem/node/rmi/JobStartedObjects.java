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
package org.pepstock.jem.node.rmi;

import java.io.Serializable;
import java.util.Collection;

import org.pepstock.jem.node.DataPathsManager;
import org.pepstock.jem.node.security.Role;

/**
 * This object has been passes to JOB before starting. It usually is called by step listener 
 * during the job started which asks this object to have the list of roles of job user
 * *to use inside the security manager) and data paths manager to address the files 
 * to the correct path.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class JobStartedObjects implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Collection<Role> roles = null;
	
	private DataPathsManager storageGroupsManager = null;

	/**
	 * Empty constructor
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
