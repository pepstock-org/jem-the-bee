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
package org.pepstock.jem.util.locks;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;

/**
 * Abstract class which implements the read/write lock on distributed environment, using Hazelcast.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public abstract class ConcurrentLock {
	
	/**
	 * Label for no waiting semaphore
	 */
	public static final String NO_WAITING_PREFIX = "noWaiting"; 
	
	/**
	 * Label for no accessing semaphore
	 */
	public static final String NO_ACCESSING_PREFIX = "noAccessing";
	
	private HazelcastInstance instance = null;
	
	private String queueName = null;
	
	private ISemaphore noWaiting = null;
	
	private ISemaphore noAccessing = null;
	
	/**
	 * Creates an instance using Hazelcast instance and queue name 
	 * @param instance Hazelcast instance
	 * @param queueName queue name to be locked
	 */
	public ConcurrentLock(HazelcastInstance instance, String queueName){
		this.instance = instance;
		this.queueName = queueName;
		noWaiting = instance.getSemaphore(NO_WAITING_PREFIX+"."+queueName); 
		noAccessing = instance.getSemaphore(NO_ACCESSING_PREFIX+"."+queueName);
	}
	
	/**
	 * @return the instance
	 */
	public HazelcastInstance getInstance() {
		return instance;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @return the noWaiting
	 */
	public ISemaphore getNoWaiting() {
		return noWaiting;
	}

	/**
	 * @return the noAccessing
	 */
	public ISemaphore getNoAccessing() {
		return noAccessing;
	}

	/**
	 * Abstract method which must implement the right logic to acquire a lock
	 * @throws LockException if any errors occurs
	 */
	public abstract void acquire() throws LockException;
	
	/**
	 * Abstract method which must implement the right logic to release a lock
	 * @throws LockException if any errors occurs
	 */
	public abstract void release() throws LockException;
}