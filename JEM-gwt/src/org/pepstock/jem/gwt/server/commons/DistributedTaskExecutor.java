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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.executors.SerializableException;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 * @param <T> 
 */
public class DistributedTaskExecutor<T>  {
	
	private Callable<T> callable = null;
	
	private Member member;

	/**
	 * @param callable
	 * @param member
	 */
    public DistributedTaskExecutor(Callable<T> callable, Member member) {
	    super();
	    this.callable = callable;
	    this.member = member;
    }

    /**
	 * @return the callable
	 */
	public Callable<T> getCallable() {
		return callable;
	}

	/**
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
     * 
     * @return
     * @throws ServiceMessageException
     */
	public T getResult() throws ServiceMessageException{
		IExecutorService executorService = SharedObjects.getHazelcastInstance().getExecutorService(Queues.JEM_EXECUTOR_SERVICE);
		Future<T> future = executorService.submitToMember(callable, member);
		try {
			// gets result
			return future.get();
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG063E, e, future.getClass().getName());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG063E, e, future.getClass().getName());
		} catch (ExecutionException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG063E, e, future.getClass().getName());
			
			Exception ex = getCustomException(e);		
			if (ex instanceof SerializableException){
				throw new ServiceMessageException(UserInterfaceMessage.JEMG067E, ex, ex.getMessage());
			} else if (ex instanceof ExecutorException){
				ExecutorException me = (ExecutorException)ex;
				throw new ServiceMessageException(me.getMessageInterface(), me.getObjects());
			} else {
				throw new ServiceMessageException(UserInterfaceMessage.JEMG063E, e, future.getClass().getName());
			}
		}
	}
	
	/**
	 * Extracts from exceptions chain the JEM exception to get message
	 * @param e leaf exception
	 * @return null if not JEM exception
	 */
	private Exception getCustomException(Throwable e){
		if (e instanceof SerializableException){
			return (SerializableException)e;
		} else if (e instanceof MessageException){
			return (ExecutorException)e;
		} else {
			if (e.getCause() == null){
				return null;
			} else {
				return getCustomException(e.getCause());
			}
		}
	}

}
