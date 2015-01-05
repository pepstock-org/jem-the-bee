/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.node.security;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.Resource;

/**
 * Is the SecurityManager under which the job is running. Every user that launch
 * a job will have a set of roles each of which will have a set of permissions.
 * The BatchSecurityManager verify if the job is doing what the user is
 * permitted to do.
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public abstract class BatchSecurityManager extends SecurityManager {
	
	private List<org.apache.shiro.authz.Permission> permissions;

	private SecurityUtils utils = new SecurityUtils();
	
	/**
	 * @param roles the roles of the current user executing the jcl
	 */
	protected BatchSecurityManager(Collection<Role> roles) {
		super();
		loadPermissions(roles);
	}

	/**
	 * Load all the user permissions from roles
	 * 
	 * @param roles
	 */
	private void loadPermissions(Collection<Role> roles) {
		permissions = new ArrayList<org.apache.shiro.authz.Permission>();
		for (Role role : roles) {
			for (String permission : role.getPermissions()) {
				org.apache.shiro.authz.Permission perm = null;
				if (permission.equalsIgnoreCase(Permissions.FILES_READ_ALL) ||
						permission.equalsIgnoreCase(Permissions.FILES_WRITE_ALL) ||
						permission.equalsIgnoreCase(Permissions.FILES_EXECUTE_ALL)){
					perm = new StringPermission(permission);
				} else if (permission.startsWith(Permissions.FILES_READ)  ||
						permission.startsWith(Permissions.FILES_WRITE) ||
						permission.startsWith(Permissions.FILES_EXECUTE)){
					perm = new RegExpPermission(permission);
				} else {
					perm = new StringPermission(permission);
				}
				permissions.add(perm);
			}
		}
	}

	/**
	 * Check the batch permission
	 * 
	 * @param permission is a shiro permission that can be:
	 *            <p>
	 * @see org.pepstock.jem.node.security.RegExpPermission or
	 *      <p>
	 * @see org.pepstock.jem.node.security.StringPermission
	 * @return true if user has the right permission, false otherwise
	 */
	public boolean checkBatchPermission(String permission) {
		StringPermission sPermission = new StringPermission(permission);
		for (org.apache.shiro.authz.Permission perm : permissions) {
			if (perm.implies(sPermission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the user has permissions to use the required resource
	 * 
	 * @param resource resource name
	 * @return <code>true</code> if user has permission, otherwise <code>false</code>
	 */
	public final boolean checkResource(Resource resource){
		// checks permissions
		String permission = Permissions.DATASOURCES +
				Permissions.PERMISSION_SEPARATOR +
				resource.getType() +
				Permissions.PERMISSION_SEPARATOR +
				resource.getName();
		return checkBatchPermission(permission);
	}
	
	/**
	 * Checks if the user has permissions to act with nodes
	 * 
	 * @param permission permission related to node command
	 * @return <code>true</code> if user has permission, otherwise <code>false</code>
	 */
	public final boolean checkNodesCommand(String permission){
		return checkBatchPermission(permission);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	@Override
	public void checkPermission(Permission perm) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission,
	 * java.lang.Object)
	 */
	@Override
	public final void checkPermission(Permission perm, Object context) {
		checkPermission(perm);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkExec(java.lang.String)
	 */
	@Override
	public final void checkExec(String cmd) {
		String permission = Permissions.FILES_EXECUTE + FilenameUtils.normalize(cmd, true);
		if (!checkBatchPermission(permission)){
			LogAppl.getInstance().emit(NodeMessage.JEMC104E, cmd);
			throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(cmd));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String)
	 */
	@Override
	public void checkRead(String file) {
		String normalizedFile = FilenameUtils.normalize(file, true);
		int result = utils.checkReadFileName(normalizedFile);
		if (result == SecurityUtils.TO_BE_CHECKED){
			if (!checkBatchPermission(Permissions.FILES_READ + utils.normalizeFileName(normalizedFile))){
				LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
				throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));
			}
		} else if (result == SecurityUtils.TO_BE_REJECTED){
			LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
			throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public final void checkRead(String file, Object context) {
		checkRead(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.lang.String)
	 */
	@Override
	public void checkWrite(String file) {
		String normalizedFile = FilenameUtils.normalize(file, true);
		int result = utils.checkWriteFileName(normalizedFile);
		if (result == SecurityUtils.TO_BE_CHECKED){
			if (!checkBatchPermission(Permissions.FILES_WRITE + utils.normalizeFileName(normalizedFile))){
				LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
				throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));
			}
		} else if (result == SecurityUtils.TO_BE_REJECTED){
			LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
			throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));
		} else if ((result == SecurityUtils.TO_BE_GFS_CHECKED) && (!checkBatchPermission(utils.getGfsPermission(file)))){
			LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
			throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));
		} else if ((result == SecurityUtils.TO_BE_LOCAL_FS_CHECKED) && (!checkBatchPermission(Permissions.LOCAL_FILE_SYSTEM_ACCESS))){
			LogAppl.getInstance().emit(NodeMessage.JEMC104E, normalizedFile);
			throw new SecurityException(NodeMessage.JEMC104E.toMessage().getFormattedMessage(normalizedFile));			
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkDelete(java.lang.String)
	 */
	@Override
	public final void checkDelete(String file) {
		checkWrite(file);
	}
}