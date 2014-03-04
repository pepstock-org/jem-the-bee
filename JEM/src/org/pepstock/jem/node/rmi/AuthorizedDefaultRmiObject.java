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

import java.rmi.RemoteException;
import java.util.List;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.util.rmi.DefaultRmiObject;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class AuthorizedDefaultRmiObject extends DefaultRmiObject {

	private static final long serialVersionUID = 1L;


	/**
	 * @throws RemoteException
	 */
	public AuthorizedDefaultRmiObject() throws RemoteException {
	}

	
	/**
	 * 
	 * @param jobId 
	 * @param permissionToCheck
	 * @throws RemoteException
	 */
	public void checkAuthorization(String jobId, String permissionToCheck) throws RemoteException{
		CancelableTask task = getCurrentTask(jobId);
		List<Role> roles = task.getJobTask().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)){
				return;
			}
			StringPermission permissionToHave = new StringPermission(permissionToCheck);
			for (String permission : role.getPermissions()) {
				org.apache.shiro.authz.Permission perm = new StringPermission(permission);
				if (perm.implies(permissionToHave)){
					return;
				}
			}
		}
		String userid = null;
		Job job = task.getJobTask().getJob();
		if (job.isUserSurrogated()){
			userid = job.getJcl().getUser();
		} else {
			userid = job.getUser();
		}
		throw new RemoteException(NodeMessage.JEMC121E.toMessage().getFormattedMessage(userid, permissionToCheck));		
	}
}