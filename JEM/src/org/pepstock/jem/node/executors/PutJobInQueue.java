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
package org.pepstock.jem.node.executors;

import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.persistence.database.PreJobDBManager;

import com.hazelcast.core.IQueue;

/**
 * Submits PreJob into queue
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.3
 * 
 */
public class PutJobInQueue extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private PreJob preJob = null;
	
	/**
	 * @param preJob
	 */
	public PutJobInQueue(PreJob preJob) {
		super();
		this.preJob = preJob;
	}

	/**
	 * Calls an executor to extract all information
	 * @return bean with all info to show on UI
	 * @throws ExecutorException 
	 * @throws Exception occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		if (preJob != null){
			try {
				PreJobDBManager.getInstance().store(preJob);
				// puts the pre job in a queue for validating and moving to right QUEUE
				// (input if is correct, output if is wrong)
				IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
				try {
					jclCheckingQueue.put(preJob);
				} catch (Exception e) {
					PreJobDBManager.getInstance().delete(preJob);
					throw new ExecutorException(SubmitMessage.JEMW003E, e);
				}
				return Boolean.TRUE;
			} catch (MessageException e) {
				throw new ExecutorException(e.getMessageInterface(), e);
			}
		}
		return Boolean.FALSE;
	}
}