/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone Businaro
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
package org.pepstock.jem.ant.tasks;

import java.io.FilePermission;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.UnknownHostException;
import java.security.Permission;
import java.util.Collection;
import java.util.PropertyPermission;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.BatchSecurityManager;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.rmi.RmiKeys;

/**
 * Is the SecurityManager under which the job is running. Every user that launch
 * a job will have a set of roles each of which will have a set of permissions.
 * The BatchSecurityManager verify if the job is doing what the user is
 * permitted to do.
 * 
 * @author Simone Businaro
 * @version 1.0
 * 
 */
public class AntBatchSecurityManager extends BatchSecurityManager {
	
	private boolean isAdministrator = false;
	
	private boolean isGrantor = false;
	
	private boolean internalAction = true;
	/**
	 * @param roles the roles of the current user executing the jcl
	 */
	public AntBatchSecurityManager(Collection<Role> roles) {
		super(roles);
		loadPermissions(roles);
	}

	/**
	 * @return the internalActions
	 */
	final boolean isInternalAction() {
		return internalAction;
	}

	/**
	 * @param internalAction the internalActions to set
	 */
	final void setInternalAction(boolean internalAction) {
		this.internalAction = internalAction;
	}

	/**
	 * @return the isAdministrator
	 */
	final boolean isAdministrator() {
		return isAdministrator;
	}

	/**
	 * @param isAdministrator the isAdministrator to set
	 */
	final void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	/**
	 * @return the isGrantor
	 */
	final boolean isGrantor() {
		return isGrantor;
	}

	/**
	 * @param isGrantor the isGrantor to set
	 */
	final void setGrantor(boolean isGrantor) {
		this.isGrantor = isGrantor;
	}

	/**
	 * Load all the user permissions from roles
	 * 
	 * @param roles
	 */
	private void loadPermissions(Collection<Role> roles) {
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)){
				setAdministrator(true);
			}
			if (role.getName().equalsIgnoreCase(Roles.GRANTOR)){
				setGrantor(true);
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
	@Override
	public final boolean checkBatchPermission(String permission) {
		if (isAdministrator() || isInternalAction()){
			return true;
		}
		return super.checkBatchPermission(permission);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	@Override
	public void checkPermission(Permission perm) {
		// this check is necessary to avoid that someone
		// set jem properties, accessing outside of GFS
		if (perm instanceof PropertyPermission && "write".equalsIgnoreCase(perm.getActions()) && perm.getName().startsWith("jem")){
			LogAppl.getInstance().emit(NodeMessage.JEMC127E);
			throw new SecurityException(NodeMessage.JEMC127E.toMessage().getMessage());
		}
		// checks is administrator. if true return.
		if (isAdministrator() || isInternalAction()){
			return;
		}
		// checks teh file access
		if (perm instanceof FilePermission){
			if ("read".equalsIgnoreCase(perm.getActions())){
				checkRead(perm.getName());
			} else if ("write".equalsIgnoreCase(perm.getActions())){
				checkWrite(perm.getName());
			} else if ("delete".equalsIgnoreCase(perm.getActions())){
				checkDelete(perm.getName());
			} else {
				checkRead(perm.getName());
			}
		} else if (perm instanceof SocketPermission){
			// checks the RMI access.
			// checks to RMI is not allowed if you're not a admin
			SocketPermission sperm = (SocketPermission)perm;
			int port = Parser.parseInt(StringUtils.substringAfter(sperm.getName(), ":"), Integer.MAX_VALUE);
			int portRmi = Parser.parseInt(System.getProperty(RmiKeys.JEM_RMI_PORT), Integer.MIN_VALUE);
			if (port == portRmi && !isInternalAction() && !isGrantor()){
				String hostname = StringUtils.substringBefore(sperm.getName(), ":");
				try {
					String resolved = InetAddress.getByName(hostname).getHostAddress();
					String localhost = InetAddress.getLocalHost().getHostAddress();
					if (resolved.equalsIgnoreCase(localhost) && !checkBatchPermission(Permissions.INTERNAL_SERVICES)){
						LogAppl.getInstance().emit(NodeMessage.JEMC128E);
						throw new SecurityException(NodeMessage.JEMC128E.toMessage().getMessage());
					}
				} catch (UnknownHostException e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC128E);
					throw new SecurityException(NodeMessage.JEMC128E.toMessage().getMessage(), e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String)
	 */
	@Override
	public final void checkRead(String file) {
		if (isAdministrator() || isInternalAction()){
			return;
		}
		super.checkRead(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.lang.String)
	 */
	@Override
	public void checkWrite(String file) {
		if (isAdministrator() || isInternalAction()){
			return;
		}
		super.checkWrite(file);
	}

}