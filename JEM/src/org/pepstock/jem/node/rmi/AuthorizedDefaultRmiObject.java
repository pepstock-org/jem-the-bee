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
 * Extension of a RMI object with common methods to check if the user
 * is authorized to perform the action required.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0	
 *
 */
public class AuthorizedDefaultRmiObject extends DefaultRmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor which extends the parent one
	 * @throws RemoteException if any RMI errors occurs
	 */
	public AuthorizedDefaultRmiObject() throws RemoteException {
	}
	
	/**
	 * Common method which checks the the user is authorized to perform 
	 * something, using the permission string passed as argument
	 * 
	 * @param jobId job ID which is in execution
	 * @param permissionToCheck permission string to be checkd
	 * @throws RemoteException if any errors occurs
	 * mainly if the user is not authorized to call the method
	 */
	public void checkAuthorization(String jobId, String permissionToCheck) throws RemoteException{
		// gets taks by jobid
		CancelableTask task = getCurrentTask(jobId);
		// gets all roles of job user
		List<Role> roles = task.getJobTask().getRoles();
		// scans all roles
		for (Role role : roles) {
			// if is administrator
			// can do everything 
			if (role.getName().equalsIgnoreCase(Roles.ADMINISTRATOR)){
				return;
			}
			// creates a permission by string
			StringPermission permissionToHave = new StringPermission(permissionToCheck);
			// scans all the permission to check if the user is authorized
			for (String permission : role.getPermissions()) {
				org.apache.shiro.authz.Permission perm = new StringPermission(permission);
				// if authorized, return correctly
				// and the caller method can go on
				if (perm.implies(permissionToHave)){
					return;
				}
			}
		}
		// if here the user is NOT AUTHORIZED
		// extract the userid from job
		// to create an exception which says
		// what user is NOT authorized
		String userid = null;
		Job job = task.getJobTask().getJob();
		// if job has got a surrogated user
		// gets it
		if (job.isUserSurrogated()){
			userid = job.getJcl().getUser();
		} else {
			userid = job.getUser();
		}
		// throws the exception
		throw new RemoteException(NodeMessage.JEMC121E.toMessage().getFormattedMessage(userid, permissionToCheck));		
	}
}