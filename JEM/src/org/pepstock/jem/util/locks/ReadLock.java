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

import org.pepstock.jem.log.JemException;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.InstanceDestroyedException;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class ReadLock extends ConcurrentLock{
	
	private static final String MAP_KEY = "readers";

	private IMap<String, Long> readers = null;
	
	/**
	 * @param instance
	 * @param queueName
	 */
	public ReadLock(HazelcastInstance instance, String queueName){
		super(instance, queueName);
		readers = instance.getMap("countersMutex."+queueName);
	}
	
	@Override
	public void acquire() throws JemException {
		try {
			getNoWaiting().acquire();
		} catch (InstanceDestroyedException e) {
			throw new JemException(e);
		} catch (InterruptedException e) {
			throw new JemException(e);
		}
		
		Long nReaders = null;
		Long prev = null;
		try {
			readers.lock(MAP_KEY);
			prev = readers.get(MAP_KEY);
			
			if (prev == null){
				prev = Long.valueOf(0);
			}
			
			nReaders = Long.valueOf(prev.longValue() + 1);
			readers.put(MAP_KEY, nReaders);
		} catch (Exception e) {
			getNoWaiting().release();
			throw new JemException(e);
		} finally {
			readers.unlock(MAP_KEY);
		}
		if (prev.longValue() == 0){
			try {
				getNoAccessing().acquire();
			} catch (InstanceDestroyedException e) {
				getNoWaiting().release();
				throw new JemException(e);
			} catch (InterruptedException e) {
				getNoWaiting().release();
				throw new JemException(e);
			}
		}
	} 

	@Override
	public void release() throws JemException{
		Long nReaders = null;
		Long prev = null;
		try {
			readers.lock(MAP_KEY);
			prev = readers.get(MAP_KEY);
			nReaders = Long.valueOf(prev.longValue() - 1);
			readers.put(MAP_KEY, nReaders);
		} finally {
			readers.unlock(MAP_KEY);
		}
		if (nReaders.longValue() == 0){
			getNoAccessing().release();
		}
	}
}