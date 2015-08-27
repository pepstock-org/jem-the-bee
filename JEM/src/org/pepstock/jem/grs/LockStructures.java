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
package org.pepstock.jem.grs;

/**
 * Contains all constants, keys of Hazelcast collections, locks and
 * notifications of GRS
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public interface LockStructures {

	/**
	 * Key for the collection used to collect all latch information. The
	 * collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, LatchInfo&gt; <br>
	 * where key value is the resource name, requested for locking.
	 * 
	 * @see LatchInfo
	 */
	public static final String COUNTER_MUTEX = "org.pepstock.jem.grs.counter_mutex";

	/**
	 * Lock to use to lock the COUNTER_MUTEX
	 */
	public static final String COUNTER_MUTEX_LOCK = "org.pepstock.jem.grs.counter_mutex.lock";

	/**
	 * Semaphore to synchronize all requests for locking
	 */
	public static final String LOCK_REQUEST = "org.pepstock.jem.grs.lock_request";

}