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
import com.hazelcast.core.IMap;
import com.hazelcast.core.InstanceDestroyedException;

/**
 * Implements a distributed read lock, leveraging on Hazelcast features
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class ReadLock extends ConcurrentLock{
	
	private static final String MAP_KEY = "readers";

	private IMap<String, Long> readers = null;
	
	/**
	 * Creates a distributed read lock
	 * @param instance Hazelcast instance
	 * @param queueName map or queuename to be locked 
	 */
	public ReadLock(HazelcastInstance instance, String queueName){
		super(instance, queueName);
		// it creates a HC map to save the number of readers
		readers = instance.getMap("countersMutex."+queueName);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.locks.ConcurrentLock#acquire()
	 */
	@Override
	public void acquire() throws LockException {
		try {
			// gets no waiting lock
			getNoWaiting().acquire();
		} catch (InstanceDestroyedException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
		// claculated here the number of readers
		Long nReaders = null;
		Long prev = null;
		try {
			// locks the map
			readers.lock(MAP_KEY);
			// gets the value
			prev = readers.get(MAP_KEY);
			// if null, is the first
			// therefore starts from 0
			if (prev == null){
				prev = Long.valueOf(0);
			}
			// increments the number of readers
			nReaders = Long.valueOf(prev.longValue() + 1);
			// save on the map
			readers.put(MAP_KEY, nReaders);
		} catch (Exception e) {
			// if exception, free the lock
			getNoWaiting().release();
			throw new LockException(e);
		} finally {
			// unlock ALWAYS the readers
			readers.unlock(MAP_KEY);
		}
		// if is the first
		if (prev.longValue() == 0){
			try {
				// gets the lock for access
				getNoAccessing().acquire();
			} catch (InstanceDestroyedException e) {
				getNoWaiting().release();
				throw new LockException(e);
			} catch (InterruptedException e) {
				getNoWaiting().release();
				throw new LockException(e);
			}
		}
	} 

	/* (non-Javadoc)
	 * @see org.pepstock.jem.util.locks.ConcurrentLock#release()
	 */
	@Override
	public void release() throws LockException {
		// recalculate the number of readers
		Long nReaders = null;
		Long prev = null;
		try {
			// locks the map
			readers.lock(MAP_KEY);
			// gets teh value
			prev = readers.get(MAP_KEY);
			// decrements the number of readers
			nReaders = Long.valueOf(prev.longValue() - 1);
			// save it on map
			readers.put(MAP_KEY, nReaders);
		} catch (Exception e){
			throw new LockException(e);
		} finally {
			// always unlocks the map
			readers.unlock(MAP_KEY);
		}
		// if no further readers
		// then free no accessing
		if (nReaders.longValue() == 0){
			getNoAccessing().release();
		}
	}
}