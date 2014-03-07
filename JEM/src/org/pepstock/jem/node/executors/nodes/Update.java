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
package org.pepstock.jem.node.executors.nodes;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * Changes domain and affinity attributes of the node.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class Update extends DefaultExecutor<ExecutionResult>{

	private static final long serialVersionUID = 1L;
	
	private NodeInfoBean node = null;

	/**
	 * Constructs the object with node instance
	 * @param node instance of node to update
	 *
	 */
	public Update(NodeInfoBean node) {
		this.node = node;
	}

	/**
	 * Checks if domain or affinity is changed and then perform the update on node.
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
			// verify what is changed otherwise nop!
			if (!node.getExecutionEnvironment().getDomain().equalsIgnoreCase(Main.EXECUTION_ENVIRONMENT.getDomain())){
				Main.EXECUTION_ENVIRONMENT.setDomain(node.getExecutionEnvironment().getDomain());
				checkJob = true;
			// PARALLEL JOBS	
			} else if (node.getExecutionEnvironment().getParallelJobs() != Main.EXECUTION_ENVIRONMENT.getParallelJobs()){
					int value = ExecutionEnvironment.DEFAULT_PARALLEL_JOBS; 
					if (node.getExecutionEnvironment().getParallelJobs() < ExecutionEnvironment.MINIMUM_PARALLEL_JOBS) {
						LogAppl.getInstance().emit(NodeMessage.JEMC211W, node.getExecutionEnvironment().getParallelJobs(), value);
					} else if (node.getExecutionEnvironment().getParallelJobs() >= ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS) {
						LogAppl.getInstance().emit(NodeMessage.JEMC210W, node.getExecutionEnvironment().getParallelJobs(), value);
					} else {
						value = node.getExecutionEnvironment().getParallelJobs();
					}
					Main.EXECUTION_ENVIRONMENT.setParallelJobs(value);
					LogAppl.getInstance().emit(NodeMessage.JEMC212I, Main.EXECUTION_ENVIRONMENT.getParallelJobs());
					checkJob = true;
			// MEMORY		
			} else if (node.getExecutionEnvironment().getMemory() != Main.EXECUTION_ENVIRONMENT.getMemory()){
					int	value = Jcl.DEFAULT_MEMORY;

					if (node.getExecutionEnvironment().getMemory() < ExecutionEnvironment.MINIMUM_MEMORY) {
						// too low
						LogAppl.getInstance().emit(NodeMessage.JEMC215W, node.getExecutionEnvironment().getMemory(), value);
					} else if (node.getExecutionEnvironment().getMemory() >= ExecutionEnvironment.MAXIMUM_MEMORY) {
						// too high
						LogAppl.getInstance().emit(NodeMessage.JEMC214W, node.getExecutionEnvironment().getMemory(), value);
					} else {
						value = node.getExecutionEnvironment().getMemory();
					}
					Main.EXECUTION_ENVIRONMENT.setMemory(value);
					LogAppl.getInstance().emit(NodeMessage.JEMC216I, Main.EXECUTION_ENVIRONMENT.getMemory());
					checkJob = true;				
			} else {
				// if node passed doesn't have new affinities, return!
				if (node.getExecutionEnvironment().getStaticAffinities().isEmpty()){
					return ExecutionResult.SUCCESSFUL;
				}
				// clears affintiies
				Main.EXECUTION_ENVIRONMENT.getStaticAffinities().clear();
				// load new static affinities 
				for (String aff : node.getExecutionEnvironment().getStaticAffinities()) {
					Main.EXECUTION_ENVIRONMENT.getStaticAffinities().add(aff.trim().toLowerCase());
				}
				checkJob = true;
			}
			// store info into node map
			NodeInfoUtility.storeNodeInfo(Main.getNode());			
			LogAppl.getInstance().emit(NodeMessage.JEMC050I, Main.getNode().getExecutionEnvironment().toString());
		} finally {
			// always unlock
			Main.getNode().getLock().unlock();
		}
		// if check job, check on input queue
		if (checkJob){
			Main.INPUT_QUEUE_MANAGER.checkJobsInQueue();
		}

		return ExecutionResult.SUCCESSFUL;
	}

}