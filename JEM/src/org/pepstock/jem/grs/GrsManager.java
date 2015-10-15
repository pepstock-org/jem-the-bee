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
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.node.RequestLock;
import org.pepstock.jem.node.ResourceLock;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * Singleton used to manage the lock requests of a GRS node.<br>
 * Adds itself (by a inner class) as listener of COUNTER_MUTEX map of Hazelcast.<br>
 * Adding as listener, is able to count down request lock of GRS Node, and
 * then complete the request for locking.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class GrsManager {

	private static GrsManager INSTANCE = null;

	private GrsNode node = null;

	private HazelcastInstance hazelcastInstance = null;

	/**
	 * Constructs the object, adding itself (by a inner class) as listener of
	 * COUNTER_MUTEX map of Hazelcast.<br>
	 * Adding as listener, is able to count down request lock of GRS Node, and
	 * then complete the request for locking.
	 */
	private GrsManager(HazelcastInstance hInstance) {
		this.hazelcastInstance = hInstance;
		IMap<String, LatchInfo> counterMutex = this.hazelcastInstance.getMap(LockStructures.COUNTER_MUTEX);
		counterMutex.addEntryListener(new CounterMutexListener(), true);
	}

	/**
	 * Is a static method (typical of a singleton) that create the unique
	 * instance of GRSManager, using a Hazelcast instance.<br>
	 * You must ONLY one instance of this per JVM instance.<br>
	 *  
	 * @param hInstance hazelcast instance
	 * @return manager instance
	 */
	public synchronized static GrsManager createInstance(HazelcastInstance hInstance) {
		if (INSTANCE == null){
			INSTANCE = new GrsManager(hInstance);		
		}
		return INSTANCE;
	}

	/**
	 * Is a static method (typical of a singleton) that returns the unique
	 * instance of GRSManager.<br>
	 * You must ONLY one instance of this per JVM instance.<br>
	 * It means one GRSNODE for one JVM.
	 * 
	 * @return manager instance
	 */
	public static GrsManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the GRS node. For JEM, this node is the same of JEM node.
	 * 
	 * @see org.pepstock.jem.node.NodeInfo
	 * @return GRS node
	 */
	public GrsNode getNode() {
		return node;
	}

	/**
	 * Sets the GRS node. For JEM, this node is the same of JEM node.
	 * 
	 * @see org.pepstock.jem.node.NodeInfo
	 * @param node GRS node
	 */
	public void setNode(GrsNode node) {
		this.node = node;
	}


	/**
	 * Returns the Hazelcast Instance necessary for global resources.
	 * 
	 * @return the hazelcastInstance
	 */
	public HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}

	/**
	 * Returns a list of requestors by resource name. If resource is requested
	 * by none, an empty list is returned.
	 * 
	 * @param resourceName resource name to use for searching
	 * @return list list of requestors
	 */
	public List<RequestorInfo> getRequestors(String resourceName) {
		// gets the map reference and lock by resource name
		IMap<String, LatchInfo> counterMutex = hazelcastInstance.getMap(LockStructures.COUNTER_MUTEX);
		counterMutex.lock(resourceName);

		// get Latch info by resource name and unlock the key
		LatchInfo latch = counterMutex.get(resourceName);
		counterMutex.unlock(resourceName);

		// if latch is null, none is requesting for passed resource name
		// otherwise returns the list of latch object
		if (latch != null){
			return latch.getRequestors();
		} else {
			return new LinkedList<RequestorInfo>();
		}
	}

	/**
	 * Remove a requestor from a specific GRSNode.<br>
	 * Is called by a GRSNode when a node left the cluster without unlokcing the
	 * locks, probably for a crash of node itself.<br>
	 * Without knowing what to do, the node which has been activated after
	 * leaving, release all locks of node which left.
	 * 
	 * @param node node to release locks
	 */
	public void removeRequestor(GrsNode node) {
		// gets request of passed node
		for (RequestLock request : node.getRequests().values()){

			// scans all requested resources of node
			for (ResourceLock resource : request.getResources().values()) {
				// gets shared map reference and lock by resource name
				IMap<String, LatchInfo> counterMutex = hazelcastInstance.getMap(LockStructures.COUNTER_MUTEX);
				counterMutex.lock(resource.getName());

				// checks if the map contains the resource name
				// if not, no locks and so nothing to unlock!!
				LatchInfo latch = null;
				if (counterMutex.containsKey(resource.getName())) {
					// get latch info of resource
					latch = counterMutex.get(resource.getName());

					// scans list of requestors to see if the node is still in the
					// list
					// remember the requestor info contains the node key that here
					// is used to compare
					// with passed node key
					for (Iterator<RequestorInfo> iter = latch.getRequestors().iterator(); iter.hasNext();) {
						// gets requestor info
						RequestorInfo info = iter.next();

						// if node key of requestor is the same of passed node
						// (ignoring case)
						if (node.getKey().equalsIgnoreCase(info.getNodeKey())) {
							// Checks lock mode to update the amount of readers or
							// writes correctly
							// uses requester info lock mode
							if (info.getMode() == ResourceLock.READ_MODE){
								latch.decrementReadersCount();
							} else {
								latch.decrementWritersCount();
							}
							// remove the requestor from list because
							// the node is parent of requestor so if node left,
							// requestors as well
							iter.remove();
						}
					}
					// if the requestors is empty (no more requestors) removes the request
					if (latch.getRequestors().isEmpty()){
						// remove latch info inside shared map
						counterMutex.remove(resource.getName());

					} else {
						// update latch info inside shared map
						counterMutex.replace(resource.getName(), latch);
					}
				}
				// unlock the reosurce by resource name (the key)
				counterMutex.unlock(resource.getName());
			}
		}
	}

	/**
	 * Is the listener of map with latch info of resources for locking.<br>
	 * Catches events (adding, removing and updating, not evicting) and notifies
	 * GRS node by request lock object if the resource name is included in the
	 * list of request.<br>
	 * 
	 * @author Andrea "Stock" Stocchero
	 * 
	 */
	private static class CounterMutexListener implements EntryListener<String, LatchInfo> {

		/**
		 * Catches "EntryADDED" event and checks the resource is included in the
		 * list of current GRS node and requestors requests.
		 * 
		 * @param event Hazelcast map event
		 */
		@Override
		public void entryAdded(EntryEvent<String, LatchInfo> event) {
			check(event);
		}

		/**
		 * Not implemented because useless.
		 * 
		 * @param event Hazelcast map event
		 */
		@Override
		public void entryEvicted(EntryEvent<String, LatchInfo> event) {
			// do nothing
		}

		/**
		 * Catches "EntryREMOVED" event and checks the resource is included in
		 * the list of current GRS node and requestors requests.
		 * 
		 * @param event Hazelcast map event
		 */
		@Override
		public void entryRemoved(EntryEvent<String, LatchInfo> event) {
			check(event);
		}

		/**
		 * Catches "EntryUPDATED" event and checks the resource is included in
		 * the list of current GRS node and requestors requests.
		 * 
		 * @param event Hazelcast map event
		 */
		@Override
		public void entryUpdated(EntryEvent<String, LatchInfo> event) {
			check(event);
		}

		/**
		 * Notifies GRS node by request lock object, starting locking process,
		 * if the resource name is included in the list of request.
		 * 
		 * @param event Hazelcast map event
		 */
		private synchronized void check(EntryEvent<String, LatchInfo> event) {
			// if node is not registered yet, not notification because no locks
			// for it of course
			if (GrsManager.getInstance().getNode() == null){
				return;
			}

			// extracts latch info from Hazelcast event
			LatchInfo latch = event.getValue();

			for (RequestLock dRequest : GrsManager.getInstance().getNode().getRequests().values()){
				GrsRequestLock request = (GrsRequestLock)dRequest;
				check(request, latch);
			}
		}

		private void check(GrsRequestLock request, LatchInfo latch){
			// checks if the notification is helpful to the node.
			// Due to all GRS node are listener, could receive this message but not helpful and
			// furthermore creates problem whene a node is aksing for locks but is waiting
			// in the LOCK structure and in the meantime calls this method
			if (!request.isWaitingForLocks()){
				return;
			}

			// checks if latch has requestors. If not, does nothing
			if (!latch.getRequestors().isEmpty()) {

				// checks if the resource is included the request of GRS node,
				// by resource name of latch info. If not, does nothing
				if (request.getResources().containsKey(latch.getResourceName())) {

					// gets resource lock from request using latch info resourse
					// name
					ResourceLock resourceLock = request.getResources().get(latch.getResourceName());

					// checks if resource is already locked. if yes, does nothing
					if (!resourceLock.isLocked()) {
						// checks lock type for resource, extracted from list of
						// current node
						if (resourceLock.getMode() == ResourceLock.READ_MODE) {
							// if is READ mode, checks current requestors
							for (RequestorInfo info : latch.getRequestors()) {
								// if arrives to this check and is true, it
								// means that has to wait, because
								// there is a requestor in WRITE in list, before
								// the requestor of current node
								if (info.getMode() == ResourceLock.WRITE_MODE){
									return;
								}
								// Checks requestor id. If the same, checks if
								// node is the same
								if (info.getId().equalsIgnoreCase(request.getRequestorId()) &&
										info.getNodeKey().equalsIgnoreCase(GrsManager.getInstance().getNode().getKey())) {
									// notifies current node that resource
									// is available to use
									request.locked(latch.getResourceName());
									return;
								}
							}
						} else {
							// if is WRITE mode, checks the first request in the
							// list
							// if is the same checks the current node is
							// same
							// if YES, means it's time for current node to lock
							// in WRITE the resource!!!
							// is the first of the list
							RequestorInfo rinfo = ((LinkedList<RequestorInfo>)latch.getRequestors()).getFirst();
							if (rinfo.getId().equalsIgnoreCase(request.getRequestorId()) && 
									rinfo.getNodeKey().equalsIgnoreCase(GrsManager.getInstance().getNode().getKey())) {
								// notifies current node that resource is
								// available to use
								request.locked(latch.getResourceName());
								return;
							}
						}
					} else {
						// resource is already locked during a previous cycle.
					}
				} else {
					// do nothing because the event is related to a resource not
					// requested by GRS node of this JVM instance
				}
			} else {
				// latch doesn't contain any requestor should be removed
			}
		}
	}

}