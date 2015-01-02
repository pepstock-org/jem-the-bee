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
package org.pepstock.jem.util.locks;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.InstanceDestroyedException;

/**
 * Implements a distributed write lock, leveraging on Hazelcast features
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class WriteLock extends ConcurrentLock{
	
	/**
	 * Creates a distributed write lock
	 * @param instance Hazelcast instance
	 * @param queueName map or queuename to be locked 
	 */
	public WriteLock(HazelcastInstance instance, String queueName) {
		super(instance, queueName);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.locks.ConcurrentLock#acquire()
	 */
	@Override
	public void acquire() throws LockException {
		try {
			// trying acquire the lock on semaphore
			// for no waiting
			getNoWaiting().acquire();
		} catch (InstanceDestroyedException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
		try {
			// trying acquire the lock on semaphore
			// for no accessing
			getNoAccessing().acquire();
		} catch (InstanceDestroyedException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		} finally {
			// always release the nowaiting
			getNoWaiting().release();
		}
	} 

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.locks.ConcurrentLock#release()
	 */
	@Override
	public void release() throws LockException {
		// release the no accessing
		getNoAccessing().release();
	}
}