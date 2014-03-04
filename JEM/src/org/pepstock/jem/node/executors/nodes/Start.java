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
package org.pepstock.jem.node.executors.nodes;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Is the "Start" command implementation.<br>
 * Runs inside the node and, after checking the current status of node, it sets
 * the right status because the node starts
 * 
 * @see org.pepstock.jem.node.Status#INACTIVE
 * @see org.pepstock.jem.node.Status#ACTIVE
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Start extends DefaultExecutor<ExecutionResult>{

	private static final long serialVersionUID = 1L;

	/**
	 * Checks the current status of node.<br>
	 * If node is active or draining, it sets active, because current job
	 * execution must end, otherwise inactive.
	 * 
	 * @return execution result, always successful
	 * @throws Exception occurs if errors
	 */
	@Override
	public ExecutionResult execute() throws ExecutorException {
		// uses this flag
		// to understand if must be check current job in queue
		boolean checkJob = false;
		// locks the access on node
		// to have a consistent status
		Main.getNode().getLock().lock();
		try {
			if (Main.getNode().isOperational()){
				// check if is active or draining. If yes, sets ACTIVIE, otherwise
				// INACTIVE!
				if (Main.getNode().getStatus().equals(Status.ACTIVE) || Main.getNode().getStatus().equals(Status.DRAINING)) {
					Main.getNode().setStatus(Status.ACTIVE);
					// store info into node map
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
					checkJob = true;
				} else if (Main.getNode().getStatus().equals(Status.INACTIVE) || Main.getNode().getStatus().equals(Status.DRAINED)) {
					Main.getNode().setStatus(Status.INACTIVE);
					// store info into node map
					NodeInfoUtility.storeNodeInfo(Main.getNode());
					LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
					// setting INACTIVE, starts the activity (by Input Queue Manager) to
					// check if there's any job ready for execution.
					checkJob = true;
				}
			}
		} finally {
			// always unlock
			Main.getNode().getLock().unlock();
		}

		// if check job, check on input queue
		if (checkJob){
			// checks JOBS for all core threads of pool
			Main.INPUT_QUEUE_MANAGER.checkJobsInQueue();
		}
		return ExecutionResult.SUCCESSFUL;
	}

}