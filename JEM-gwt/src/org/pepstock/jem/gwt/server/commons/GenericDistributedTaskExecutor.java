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
package org.pepstock.jem.gwt.server.commons;

import java.util.concurrent.Callable;

import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.GenericCallBack;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class GenericDistributedTaskExecutor extends DistributedTaskExecutor<ExecutionResult> {

	/**
	 * @param callable
	 * @param member
	 */
	public GenericDistributedTaskExecutor(Callable<ExecutionResult> callable, Member member) {
		super(callable, member);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor#getResult()
	 */
    @Override
    public ExecutionResult getResult() throws ServiceMessageException {
	    throw new UnsupportedOperationException("Use method \"execute\"!");
    }
	/**
	 * 
	 * @throws ServiceMessageException
	 */
	public void execute() throws ServiceMessageException {
		// creates cancel executor and execute it
		IExecutorService executorService = SharedObjects.getHazelcastInstance().getExecutorService(Queues.JEM_EXECUTOR_SERVICE);
		executorService.submitToMember(getCallable(), getMember(), new GenericCallBack());
	}

}
