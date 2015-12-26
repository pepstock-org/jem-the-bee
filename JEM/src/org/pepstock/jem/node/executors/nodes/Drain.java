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
package org.pepstock.jem.node.executors.nodes;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Is the "Drain" command implementation.<br>
 * Runs inside the node and, after checking the current status of node, it sets
 * the right status because the node stops or will stop any operation (move in
 * DRAINED status)
 * 
 * @see org.pepstock.jem.node.Status#DRAINED
 * @see org.pepstock.jem.node.Status#DRAINING
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class Drain extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;

	/**
	 * Checks the current status of node.<br>
	 * If node is inactive, it sets drained, otherwise draining, because current
	 * job execution must end.
	 * 
	 * @return execution result, always successful
	 * @throws Exception occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		// locks the access on node
		// to have a consistent status
		Main.getNode().getLock().lock();
		try {

			// if starting or unknown, ignore command is not in access maint
			if (Main.getNode().getStatus().equals(Status.STARTING) || Main.getNode().getStatus().equals(Status.UNKNOWN)){
				Main.getNode().setStatus(Status.DRAINED);
				if (!Main.IS_ACCESS_MAINT.get()){
					return Boolean.TRUE;
				}
			}
				
			// check if is inactive or already drained. If yes, sets DRAINED,
			// otherwise DRAINING!
			if (Main.getNode().getStatus().equals(Status.INACTIVE) || Main.getNode().getStatus().equals(Status.DRAINED)) {
				Main.getNode().setStatus(Status.DRAINED);
				// store info into node map
				NodeInfoUtility.storeNodeInfo(Main.getNode());
			} else {
				Main.getNode().setStatus(Status.DRAINING);
				// store info into node map
				NodeInfoUtility.storeNodeInfo(Main.getNode());
			}
			LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
		} finally {
			// always unlock
			Main.getNode().getLock().unlock();
		}
		return Boolean.TRUE;
	}
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#checkShutDown()
	 */
	@Override
	public void checkShutDown() throws ExecutorException{
		// NOP
		// this removes the checking if is shutting down
		// because drain is called after shut down
	}
}