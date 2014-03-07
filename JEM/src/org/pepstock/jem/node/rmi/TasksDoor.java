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
import java.util.Collection;

import org.pepstock.jem.Step;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.util.rmi.RmiObject;

/**
 * is RMI interface to communicate to JEM node when job is started and when
 * every step is started and ended.<br>
 * This is used inside of process where job is executing, usually by a step
 * listener.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface TasksDoor extends RmiObject {

	/**
	 * RMI object ID for binding.
	 */
	String NAME = "TASKS_DOOR";

	/**
	 * Sets the process-id of process where the job is executing.<br>
	 * This represents the job starting point.<br>
	 * Login the user that is submitting the job.<br>
	 * Stores the process-id and return a list of roles assigned to user of job.
	 * 
	 * @param jobId
	 * @param processId process-id of process where the job is executing
	 * @return collection of roles with permission for user of job.
	 * @throws RemoteException occurs if errors
	 */
	Collection<Role> setJobStarted(String jobId, String processId) throws RemoteException;

	/**
	 * Called when a step of job is started.
	 * 
	 * @param jobId 
	 * @param step step instance
	 * @throws RemoteException occurs if errors
	 */
	void setStepStarted(String jobId, Step step) throws RemoteException;

	/**
	 * Called when a step of job is ended.
	 * 
	 * @param jobId 
	 * @param step step instance
	 * @throws RemoteException occurs if errors
	 */
	void setStepEnded(String jobId, Step step) throws RemoteException;

	/**
	 * Called when a job is ended.
	 * <p>
	 * Log off the user.
	 * 
	 * @param jobId 
	 * @throws RemoteException occurs if errors
	 */
	void setJobEnded(String jobId) throws RemoteException;

}