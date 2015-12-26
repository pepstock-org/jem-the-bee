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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.node.hazelcast.Locks;
import org.pepstock.jem.node.hazelcast.Queues;

import com.hazelcast.core.IMap;

/**
 * Prints the information, well-formed, about locking inside of GRS.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public final class CommandsUtility {
	
	private static final int NODE_LENGTH = 20;

	private static final int JOB_ID_LENGTH = 39;

	private static final int MODE_LENGTH = 10;

	private static String TITLE = "GRS Status";

	private static String RESOURCE = "Resource: \"{0}\"";

	private static String HEADER = "Member-Name          Requestor-Id                            Read/Write Requestor";

	private static String DATA = "{0} {1} {2} {3}";

	private static String NO_LOCKS = "No locks";

	/**
	 * to avoid any instantiation
	 */
	private CommandsUtility() {
	}

	/**
	 * Returns a string with locks for all resources and requestors
	 * 
	 * @return all locks info
	 */
	public static StringBuilder displayRequestors() {
		// creates title
		StringBuilder sb = new StringBuilder(TITLE).append('\n');

		// get latch info map reference
		IMap<String, LatchInfo> counterMutex = GrsManager.getInstance().getHazelcastInstance().getMap(Queues.GRS_COUNTER_MUTEX_MAP);
		// gets all resources names (all keys)

		boolean isLock=false;
		Lock lock = GrsManager.getInstance().getHazelcastInstance().getLock(Locks.GRS_COUNTER_MUTEX);
		Collection<LatchInfo> values = null; 
		try {
			isLock=lock.tryLock(Locks.LOCK_TIMEOUT, TimeUnit.SECONDS);
			values = counterMutex.values();
		} catch (Exception ex) {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			values = new ArrayList<LatchInfo>();
		} finally {
			if (isLock){
				lock.unlock();
			}
		}
		
		if (!values.isEmpty()){
			// scans resources names
			for (LatchInfo info : values){
				// checks if we have requestors and create a display string
				if ((info.getReadersCount() + info.getWritersCount()) > 0) {
					sb.append(buildDisplay(info));
				}
			}
		} else {
			sb.append(NO_LOCKS).append('\n');
		}
		return sb;
	}

	/**
	 * Returns a string with all locks for resource name
	 * 
	 * @param resourceKey resource name (key)
	 * @return all locks info for resource name
	 */
	public static StringBuilder displayRequestors(String resourceKey) {
		// creates title
		StringBuilder sb = new StringBuilder(TITLE).append('\n');

		// get latch info map reference
		IMap<String, LatchInfo> counterMutex = GrsManager.getInstance().getHazelcastInstance().getMap(Queues.GRS_COUNTER_MUTEX_MAP);
		Lock lock = GrsManager.getInstance().getHazelcastInstance().getLock(Locks.GRS_COUNTER_MUTEX);
		boolean isLock = false;
		// gets latch info and print it
		try {
			isLock = lock.tryLock(Locks.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock) {
				return sb.append(buildDisplay(counterMutex.get(resourceKey)));
			} else {
				throw new InterruptedException();
			}
		} catch (Exception ex) {
			LogAppl.getInstance().ignore(ex.getMessage(), ex);
			return sb;
		} finally {
			if (isLock){
				lock.unlock();
			}
		}
	}

	/**
	 * Creates in string the locks situation for a resourcem using latch info
	 * passed
	 * 
	 * @param resourceName resource name (key)
	 * @param info lacth info
	 * @return locks for resource name
	 */
	private static StringBuilder buildDisplay(LatchInfo info) {
		StringBuilder sb = new StringBuilder();
		// checks info is not null
		if (info != null) {
			// prints resource name and header
			sb.append(MessageFormat.format(RESOURCE, info.getResourceName())).append('\n');
			sb.append(HEADER).append('\n');

			// Are there any requestors? if yes, go ahead
			if ((info.getReadersCount() + info.getWritersCount()) > 0) {
				// scans all requestors to print thier info
				for (RequestorInfo rinfo : info.getRequestors()) {
					String node = StringUtils.rightPad(rinfo.getNodeLabel(), NODE_LENGTH);
					String id = StringUtils.rightPad(rinfo.getId(), JOB_ID_LENGTH);
					String lockMode = rinfo.getMode() == ResourceLock.READ_MODE ? "READ" : "WRITE";
					// prints Node label, requestor name, requestor id and lock
					// mode
					sb.append(MessageFormat.format(DATA, node, id, StringUtils.rightPad(lockMode, MODE_LENGTH), rinfo.getName())).append('\n');
				}
			} else {
				sb.append(NO_LOCKS).append('\n');
			}
		} else {
			sb.append(NO_LOCKS).append('\n');
		}
		return sb;
	}

}