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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitException;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.node.executors.PutJobInQueue;
import org.pepstock.jem.node.hazelcast.ExecutorServices;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

/**
 * Utility class to call a deistributed task to add a per job in a queue
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class SubmitPreJob {

	/**
	 * To avoid any instantiation
	 */
	private SubmitPreJob() {
	}

	/**
	 * Executes a distributed task to put a pre jbo in job queue
	 * @param instance Hazelcast instance
	 * @param preJob prejob instance to add to the queue
	 * @throws SubmitException if any errors occurs
	 */
	public static void submit(HazelcastInstance instance, PreJob preJob) throws SubmitException{
		IExecutorService executorService = instance.getExecutorService(ExecutorServices.NODE);
		Future<Boolean> task = executorService.submit(new PutJobInQueue(preJob));
		try {
			// gets result
			Boolean success = task.get();
			if (!success){
				throw new SubmitException(SubmitMessage.JEMW003E);
			}
		} catch (InterruptedException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (ExecutionException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		}
	}
}
