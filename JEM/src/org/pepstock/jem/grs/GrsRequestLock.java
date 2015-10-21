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

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.pepstock.jem.node.DefaultRequestLock;
import org.pepstock.jem.node.ResourceLock;

import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;

/**
 * Is the container for all resources that you have to lock.<br>
 * Contains the id and name of requestor, necessary to gets locks.<br>
 * Locks all resources and waits for their availability. Asks in only one
 * request all resources and wait (use count-down latch) for all notifications
 * for each resources<br>
 * Unlocks resource all togheter.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class GrsRequestLock extends DefaultRequestLock {

	private static final long serialVersionUID = 1L;

	private transient CountDownLatch countDown = null;
	
	private boolean isWaitingForLocks = false;

	/**
	 * Construct the request lock
	 */
	public GrsRequestLock() {
		super();
	}
	
	/**
	 * @return the isWaitingForLocks
	 */
	public boolean isWaitingForLocks() {
		return isWaitingForLocks;
	}

	/**
	 * @param isWaitingForLocks the isWaitingForLocks to set
	 */
	public void setWaitingForLocks(boolean isWaitingForLocks) {
		this.isWaitingForLocks = isWaitingForLocks;
	}
	/**
	 * Performs the lock request into GRS.<br>
	 * 
	 * @throws InterruptedException occurs if someone interrupts from wait state
	 */
	public void tryLock() throws InterruptedException {
		//
		// creates a countdown latch with the amount of resources.
		// only when the counter is zero is blokced itself and the requestor has
		// all requested resources
		// and can go ahead
		countDown = new CountDownLatch(getResources().size());

		// uses a common lock to synchronized the requests in the cluster
		ILock lockRequest = GrsManager.getInstance().getHazelcastInstance().getLock(LockStructures.LOCK_REQUEST);
		// waits for semaphore
		lockRequest.lock();
		// locks all resources in one shot!
		
		setWaitingForLocks(true);
		for (ResourceLock resource : getResources().values()) {
			lock(resource);
		}
		// unlocks the semaphore, so other memebers can request locks
		lockRequest.unlock();

		// waits when counter is zero. locked method decrement the counter
		countDown.await();
		setWaitingForLocks(false);
	}

	/**
	 * Protected method, called by event listener of hazelcast when the
	 * requested resource is available and locked by requestor.<br>
	 * Decrements count-down latch and sets it as LOCKED . If counter gets zero,
	 * requestor has all resources and then go ahead
	 * 
	 * @param resourceName
	 */
	synchronized void locked(String resourceName) {
		// get resource lock by name
		ResourceLock resource = getResources().get(resourceName);
		// if resource in not locked, then count down and sets resource "locked"
		if (!resource.isLocked()) {
			resource.setLocked(true);
			// decrement countdownlatch
			countDown.countDown();
		}
	}

	/**
	 * Unlocks all resources previously locked.
	 */
	public void unlock() {
		for (ResourceLock resource : getResources().values()) {
			unlock(resource);
		}
	}

	/**
	 * Locks each resource, getting or creating latch info (container of locks
	 * for the same resource) and requestor info (container of latchs for the
	 * same requestor).
	 * 
	 * @param resource resource to lock
	 */
	private void lock(ResourceLock resource) {
		// gets name and mode of resource
		String resourceName = resource.getName();
		int mode = resource.getMode();

		// get map for counter of writers and readers
		IMap<String, LatchInfo> counterMutex = GrsManager.getInstance().getHazelcastInstance().getMap(LockStructures.COUNTER_MUTEX);
		// locks the key by resource name (use Hazelcast feature)
		counterMutex.lock(resourceName);

		// prepares Latch info
		LatchInfo latch = null;
		// check if has already some requests for the same resource
		if (counterMutex.containsKey(resourceName)) {
			// has already some locks so gets latch info
			latch = counterMutex.get(resourceName);
			
			// create a new requestor info to add into queue of requestors
			RequestorInfo info = new RequestorInfo();

			// sets ID and Name from request (in JEM jobid and jobname)
			info.setId(getRequestorId());
			info.setName(getRequestorName());
			// sets lock mode
			info.setMode(mode);

			// sets id and label for GRS node, responsible of locking (in JEM
			// nodeinfo key and label)
			info.setNodeKey(GrsManager.getInstance().getNode().getKey());
			info.setNodeLabel(GrsManager.getInstance().getNode().getLabel());

			// add requestor to latch
			latch.getRequestors().add(info);

			// checks lock type and increment the counter of latch
			if (mode == ResourceLock.WRITE_MODE){
				latch.incrementWritersCount();
			} else { 
				latch.incrementReadersCount();
			}
			// lacth info exists so replaces it into hazelcast map
			counterMutex.replace(resourceName, latch);

		} else {
			// the resource has no currently lock so create a new latch info
			latch = new LatchInfo();

			// create a new requestor info to add into queue of requestors
			RequestorInfo info = new RequestorInfo();

			// sets ID and Name from request (in JEM jobid and jobname)
			info.setId(getRequestorId());
			info.setName(getRequestorName());
			// sets lock mode
			info.setMode(mode);

			// sets id and label for GRS node, responsible of locking (in JEM
			// nodeinfo key and label)
			info.setNodeKey(GrsManager.getInstance().getNode().getKey());
			info.setNodeLabel(GrsManager.getInstance().getNode().getLabel());

			// sets the resource name to new latch. is logical key
			latch.setResourceName(resourceName);

			// add requestor to latch
			latch.getRequestors().add(info);

			// checks lock type and increment the counter of latch
			if (mode == ResourceLock.WRITE_MODE){
				latch.incrementWritersCount();
			} else {
				latch.incrementReadersCount();
			}
			
			// latch info doesn't exists so puts it into hazelcast map
			counterMutex.put(resourceName, latch);
		}
		// unlock the key of hazelcast map. remember the key is resource name
		counterMutex.unlock(resourceName);
	}

	/**
	 * Unlocks each resource, getting a latch info, removing the requestor
	 * 
	 * @param resource resource to unlock
	 */
	void unlock(ResourceLock resource) {
		// gets name and mode of resource
		String resourceName = resource.getName();
		int mode = resource.getMode();

		// get map for counter of writers and readers
		IMap<String, LatchInfo> counterMutex = GrsManager.getInstance().getHazelcastInstance().getMap(LockStructures.COUNTER_MUTEX);
		
		if (!counterMutex.containsKey(resourceName)){
			// could be a problem because
			// if unlock is asked, should mean 
			// that before a lock should be held
			return;
		}
		// locks the key by resource name (use Hazelcast feature)
		counterMutex.lock(resourceName);

		// gets latch info by resource name. MUST be there!
		LatchInfo latch = counterMutex.get(resourceName);

		// checks lock type and decrement the counter of latch
		if (mode == ResourceLock.WRITE_MODE){
			latch.decrementWritersCount();
		} else {
			latch.decrementReadersCount();
		}
		// scan all requestor and when matches with this requestor id and member
		// id, removes it from list
		// it checks requestor id and member id because you could have the same
		// requestor id on different
		// node of GRS cluster
		for (Iterator<RequestorInfo> iter = latch.getRequestors().iterator(); iter.hasNext();) {
			// gets requestor info
			RequestorInfo info = iter.next();

			// Requestor CHECK
			// the id is the same of this one, ignoring case? if not, checks
			// next requestor
			// Node CHECK
			// the id is the same of this one, ignoring case? if not, checks
			// next requestor
			if (info.getId().equalsIgnoreCase(getRequestorId()) &&
					info.getNodeKey().equalsIgnoreCase(GrsManager.getInstance().getNode().getKey())){
				// if yes, remove it
				iter.remove();
			}
		}
		// if the requestors is empty (no more requestors) removes the request
		if (latch.getRequestors().isEmpty()){
			// remove latch info inside shared map
			counterMutex.remove(resourceName);
			
		} else {
			// replaces (because it must exist) latch info into hazelcast map
			counterMutex.replace(resourceName, latch);
		}
		// unlock the key of hazelcast map. remember the key is resource name
		counterMutex.unlock(resourceName);
	}
}