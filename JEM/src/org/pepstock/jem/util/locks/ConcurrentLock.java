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
package org.pepstock.jem.util.locks;

import org.pepstock.jem.log.JemException;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class ConcurrentLock {
	
	private HazelcastInstance instance = null;
	
	private String queueName = null;
	
	
	private ISemaphore noWaiting = null;
	private ISemaphore noAccessing = null;
	
	/**
	 * 
	 * @param instance
	 * @param queueName
	 */
	public ConcurrentLock(HazelcastInstance instance, String queueName){
		this.instance = instance;
		this.queueName = queueName;
		noWaiting = instance.getSemaphore("noWaiting."+queueName); 
		noAccessing = instance.getSemaphore("noAccessing."+queueName);
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
	 * 
	 * @throws JemException
	 */
	public abstract void acquire() throws JemException;
	
	/**
	 * 
	 * @throws JemException
	 */
	public abstract void release() throws JemException;
}