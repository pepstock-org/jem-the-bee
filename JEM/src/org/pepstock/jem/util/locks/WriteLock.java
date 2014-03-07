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
import com.hazelcast.core.InstanceDestroyedException;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class WriteLock extends ConcurrentLock{
	
	/**
	 * 
	 * @param instance
	 * @param queueName
	 */
	public WriteLock(HazelcastInstance instance, String queueName) {
		super(instance, queueName);
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
		try {
			getNoAccessing().acquire();
		} catch (InstanceDestroyedException e) {
			throw new JemException(e);
		} catch (InterruptedException e) {
			throw new JemException(e);
		} finally {
			getNoWaiting().release();
		}
	} 

	@Override
	public void release() throws JemException {
		getNoAccessing().release();
	}
}