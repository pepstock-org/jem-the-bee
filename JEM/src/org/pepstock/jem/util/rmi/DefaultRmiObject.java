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
package org.pepstock.jem.util.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.Main;

/**
 * Default implementation of RMI object, with the unique implementation of
 * <code>alive</code> method.<br>
 * Makes sense to extend it, adding own interfaces and implementation.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class DefaultRmiObject extends UnicastRemoteObject implements RmiObject {
	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 * 
	 * @throws RemoteException necessary due to parent class
	 */
	public DefaultRmiObject() throws RemoteException {
	}

	/**
	 * Called to know if RMI object is still alive or not. If not, don't return
	 * <code>false</code> but an exception occurs.
	 * 
	 * @return <code>true</code> always
	 * @throws RemoteException occurs if RMI registry, and then object is not
	 *             available
	 */
	@Override
	public boolean alive() throws RemoteException {
		return true;
	}
	

	/**
	 * Returns task related to job, by job id. 
	 * @param jobId job id passed by a RMI call from job process
	 * @return the task in execution
	 * @throws RemoteException if task is null. It should be never null!
	 */
	public CancelableTask getCurrentTask(String jobId) throws RemoteException{
		CancelableTask task = Main.CURRENT_TASKS.get(jobId);
		if (task == null){
			throw new RemoteException("Task for job '"+jobId+"' is missing");
		}
		return task;
	}

}