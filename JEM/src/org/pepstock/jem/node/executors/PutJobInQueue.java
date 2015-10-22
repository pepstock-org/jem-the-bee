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
 * @version 2.3
 * 
 */
public class PutJobInQueue extends DefaultExecutor<Boolean>{

	private static final long serialVersionUID = 1L;
	
	private PreJob preJob = null;
	
	/**
	 * Creates the object using a prejob
	 * @param preJob instance to submit
	 */
	public PutJobInQueue(PreJob preJob) {
		super();
		this.preJob = preJob;
	}

	/**
	 * Calls an executor to put the pre job in queue
	 * @return true if ok, otherwise false
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		if (preJob != null){
			try {
				// before puts on DB
				PreJobDBManager.getInstance().store(preJob);
				// if OK, 
				// puts the pre job in a queue for validating and moving to right QUEUE
				putInQueue(preJob);
				return Boolean.TRUE;
			} catch (MessageException e) {
				// says to the client that is not able to submit the job
				throw new ExecutorException(e.getMessageInterface(), e);
			}
		}
		// says to the client that is not able to submit the job
		return Boolean.FALSE;
	}
	
	/**
	 * Puts Pre-job in Hazelcast queue
	 * @param preJob pre job instance to add to queue
	 * @throws ExecutorException if any error occurs
	 */
	private void putInQueue(PreJob preJob) throws ExecutorException{
		IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			jclCheckingQueue.put(preJob);
		} catch (Exception e) {
			// if the "put" went wrong,
			// remove from db
			PreJobDBManager.getInstance().delete(preJob);
			// says to the client that is not able to submit the job
			throw new ExecutorException(SubmitMessage.JEMW003E, e);
		}
	}
}