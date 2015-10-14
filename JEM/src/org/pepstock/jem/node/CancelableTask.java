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
package org.pepstock.jem.node;

import java.util.concurrent.FutureTask;

import org.pepstock.jem.Result;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.JobTask;

/**
 * Is a wrapper of a job task, which is currently executing on machine and
 * manage by node. This wrapper it's necessary for canceling the job.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class CancelableTask extends FutureTask<Result> {

	private JobTask jobTask = null;

	private String processId = null;
	
	private boolean force = false;

	/**
	 * Constructs the wrapper for JobTask, currently executing
	 * 
	 * @see org.pepstock.jem.node.Main#CURRENT_TASK
	 * @see org.pepstock.jem.node.tasks.JobTask#JobTask(org.pepstock.jem.Job)
	 * @param jobTask job task object
	 */
	public CancelableTask(JobTask jobTask) {
		super(jobTask);
		this.jobTask = jobTask;
	}

	/**
	 * Returns the process ID of job submitted, by JobTask.
	 * 
	 * @return process id string
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * Sets the process ID of job submitted, by JobTask.
	 * 
	 * @param processID process id string
	 */
	public void setProcessId(String processID) {
		this.processId = processID;
	}

	/**
	 * Returns the job task which currently is executing
	 * 
	 * @see org.pepstock.jem.node.tasks.JobTask#JobTask(org.pepstock.jem.Job)
	 * @return job task object
	 */
	public JobTask getJobTask() {
		return jobTask;
	}

	/**
	 * Returns true if force is set, to cancel job
	 * @return the force
	 */
	public boolean isForce() {
		return force;
	}

	/**
	 * Sets if to use force attribute on cancel command
	 * 
	 * @param force the force to set
	 */
	public void setForce(boolean force) {
		this.force = force;
	}

	/**
	 * Cancel the execution of job.
	 * 
	 * @see org.pepstock.jem.node.tasks.JobTask#cancel(String)
	 * @param mayInterruptIfRunning parameter for FutureTask
	 * @return <code>false</code> if processID is null (so no job is executing)
	 *         otherwise return boolean of <code>cancel</code> method of
	 *         FutureTask
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (processId != null) {
			if (jobTask.cancel(force)){
				// always to false, otherwise it interrupts the current Thread
				// and we don't have CancelException but an InterrruptException
				return super.cancel(false);
			}
		} else {
			LogAppl.getInstance().emit(NodeMessage.JEMC025W);
		}
		return false;
	}
}
