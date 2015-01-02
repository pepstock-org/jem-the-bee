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
package org.pepstock.jem.node.executors.jobs;

import org.pepstock.jem.Job;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Is the "Cancel" command implementation.<br>
 * Runs inside the node and calls <code>cancel</code> method of
 * <code>CancelableTask</code>.
 * 
 * @see org.pepstock.jem.node.CancelableTask#cancel(boolean)
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Cancel extends DefaultExecutor<ExecutionResult>{

	private static final long serialVersionUID = 1L;
	
	private Job job = null;
	
	private String userId = null;
	
	private boolean force = false;
	
	/**
	 * Constructs the command to cancel job
	 * 
	 * @param job job instance to cancel
	 * @param userId userid to log who cancelled job
	 * @param force if <code>true</code>, use force parameter to cancel job
	 */
	public Cancel(Job job, String userId, boolean force) {
		this.job = job;
		this.userId = userId;
		this.force = force;
	}

	/**
	 * Checks if the job, passed as parameter, is the same of current node. If
	 * yes, calls <code>cancel</code> method to cancel it.
	 * 
	 * @see ExecutionResult#SUCCESSFUL
	 * @return execution result, always successful
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public ExecutionResult execute() throws ExecutorException {
		// Checks if the job, passed as parameter, is in the list of current tasks of 
		// node. If not, logs a warning
		if (Main.CURRENT_TASKS.containsKey(job.getId())) {
			CancelableTask task = Main.CURRENT_TASKS.get(job.getId());
			// it shouldn't be null... but to avoid NullPointer
			if (task == null){
				return ExecutionResult.UNSUCCESSUL;
			}
			// sets force to cancel.
			task.setForce(force);
			// cancel it, passing false that means that we don't want to interrupt the thread
			task.cancel(false);
			// reset force after every command
			task.setForce(false);
			// log info of canceling
			LogAppl.getInstance().emit(NodeMessage.JEMC029I, job.toString(), userId, String.valueOf(force));
		} else {
			LogAppl.getInstance().emit(NodeMessage.JEMC028W, job.toString(), job.toString());
		}
		return ExecutionResult.SUCCESSFUL;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#checkShutDown()
	 */
	@Override
	public void checkShutDown() throws ExecutorException{
		// NOP
		// this removes the checking if is shutting down
		// because someone would cancel the job to close the node
	}
}