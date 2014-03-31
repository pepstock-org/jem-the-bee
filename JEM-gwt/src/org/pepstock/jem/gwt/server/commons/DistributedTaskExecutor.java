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
import java.util.concurrent.ExecutorService;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.executors.SerializableException;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
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
		DistributedTask<T> task = new DistributedTask<T>(callable, member);
		HazelcastInstance instance = SharedObjects.getHazelcastInstance();
		ExecutorService executorService = instance.getExecutorService();
		// executes it
		executorService.execute(task);
		try {
			// gets result
			return task.get();
		} catch (InterruptedException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG063E, e, task.getClass().getName());
			throw new ServiceMessageException(UserInterfaceMessage.JEMG063E, e, task.getClass().getName());
		} catch (ExecutionException e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG063E, e, task.getClass().getName());
			
			SerializableException ex = getSerializableException(e);		
			if (ex == null){
				throw new ServiceMessageException(UserInterfaceMessage.JEMG063E, e, task.getClass().getName());
			} else {
				throw new ServiceMessageException(UserInterfaceMessage.JEMG067E, ex, ex.getMessage());
			}
		}
	}
	
	
	private SerializableException getSerializableException(Throwable e){
		if (e instanceof SerializableException){
			return (SerializableException)e;
		} else {
			if (e.getCause() == null){
				return null;
			} else {
				return getSerializableException(e.getCause());
			}
		}
	}

}
