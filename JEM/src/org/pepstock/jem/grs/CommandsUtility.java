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
import org.pepstock.jem.node.ResourceLock;

import com.hazelcast.core.IMap;

/**
 * Prints the information, well-formed, about locking inside of GRS.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class CommandsUtility {

	private static String TITLE = "GRS Status";

	private static String RESOURCE = "Resource: \"{0}\"";

	private static String HEADER = "Member-Name          Requestor-Id                            Read/Write Requestor";

	private static String DATA = "{0} {1} {2} {3}";

	private static String NO_LOCKS = "No locks";

	/**
	 * Returns a string with locks for all resources and requestors
	 * 
	 * @return all locks info
	 */
	public static StringBuilder displayRequestors() {
		// creates title
		StringBuilder sb = new StringBuilder(TITLE).append('\n');
		//StringBuilder sb = new StringBuilder();

		// get latch info map reference
		IMap<String, LatchInfo> counter_mutex = GrsManager.getInstance().getHazelcastInstance().getMap(LockStructures.COUNTER_MUTEX);
		// gets all resources names (all keys)

		boolean isLock=false;
		Lock lock = GrsManager.getInstance().getHazelcastInstance().getLock(LockStructures.COUNTER_MUTEX_LOCK);
		Collection<LatchInfo> values = null; 
		try {
			isLock=lock.tryLock(10, TimeUnit.SECONDS);
			values = counter_mutex.values();
		} catch (Exception ex) {
			values = new ArrayList<LatchInfo>();
		} finally {
			if(isLock)
				lock.unlock();
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
		//StringBuilder sb = new StringBuilder();

		// get latch info map reference
		IMap<String, LatchInfo> counter_mutex = GrsManager.getInstance().getHazelcastInstance().getMap(LockStructures.COUNTER_MUTEX);
		Lock lock = GrsManager.getInstance().getHazelcastInstance().getLock(LockStructures.COUNTER_MUTEX_LOCK);
		boolean isLock = false;
		// gets latch info and print it
		try {
			if (isLock = lock.tryLock(10, TimeUnit.SECONDS)) {
				return sb.append(buildDisplay(counter_mutex.get(resourceKey)));
			} else {
				throw new InterruptedException();
			}
		} catch (Exception ex) {
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
					// prints Node label, requestor name, requestor id and lock
					// mode
					sb.append(
							MessageFormat.format(DATA, StringUtils.rightPad(rinfo.getNodeLabel(), 20), StringUtils.rightPad(rinfo.getId(), 39),
									StringUtils.rightPad(((rinfo.getMode() == ResourceLock.READ_MODE) ? "READ" : "WRITE"), 10), rinfo.getName())).append('\n');
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