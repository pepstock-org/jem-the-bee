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
package org.pepstock.jem.node.rmi;

import java.rmi.RemoteException;
import java.util.List;

import org.pepstock.jem.Job;
import org.pepstock.jem.node.CancelableTask;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.RequestLock;
import org.pepstock.jem.node.ResourceLock;
import org.pepstock.jem.util.rmi.DefaultRmiObject;

import com.hazelcast.core.IMap;

/**
 * Implementation of a ResourceLocker for locking and unlocking resources to
 * GRS.<br>
 * This is used inside of process where job is executing, when data descriptions
 * and datasets are managed. .
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ResourceLockerImpl extends DefaultRmiObject implements ResourceLocker {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty object
	 * 
	 * @throws RemoteException occurs if errors
	 */
	public ResourceLockerImpl() throws RemoteException {
	}

	/**
	 * Returns true, always, if is alive, otherwise a RemoteException should
	 * occur
	 * 
	 * @return <code>true</code> always
	 * @throws RemoteException occurs if errors
	 */
	@Override
	public boolean alive() throws RemoteException {
		return true;
	}

	/**
	 * Asks to GRS to lock resources.<br>
	 * Checks if the resources are already asked for lock, overriding if
	 * necessary.
	 * 
	 * @param resources list of resources to lock
	 * @throws RemoteException occurs if errors
	 */
	@Override
	public void lock(String jobId, List<ResourceLock> resources) throws RemoteException {
		// gets current task by job id
		CancelableTask task = getCurrentTask(jobId);
		
		// gets job object from static reference to print info
		Job job = task.getJobTask().getJob();
		
		RequestLock requestLock = Main.getNode().getRequests().get(jobId);

		// checks if this new list contains resources already loaded
		// if yes, overrides only the resources asked in new list in WRITE mode
		// otherwise in READ and already present, doen't matter because:
		// 1. if they asked before in READ, nothing CHANGE
		// 2. if they asked before in WRITE, WORTE is stronger than READ, so
		// skip!
		for (ResourceLock resource : resources) {
			// resource already ready for lock??
			// if yes check the request mode. if new one is WRITE, override!
			if (requestLock.getResources().containsKey(resource.getName())) {
				if (resource.getMode() == ResourceLock.WRITE_MODE) {
					requestLock.getResources().put(resource.getName(), resource);
				} else {
					// do nothing if in ReAD mode!
				}
			} else {
				// new resource, then adds in list of requests to lock
				requestLock.getResources().put(resource.getName(), resource);
			}
		}
		// sets the job in WAITING for resources
		IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);
		try {
			runningQueue.lock(job.getId());
			Job storedJob = runningQueue.get(job.getId());
			storedJob.setRunningStatus(Job.WAITING_FOR_RESOURCES);
			job.setRunningStatus(Job.WAITING_FOR_RESOURCES);
			// replaces job instance in queue
			runningQueue.replace(storedJob.getId(), storedJob);
		} catch (Exception ex){
			throw new RemoteException(ex.getMessage(), ex);					
		} finally{
			runningQueue.unlock(job.getId());	
		}

		try {
			// locks calling GRS and wait when all resources are available from
			// lock perspective
			requestLock.tryLock();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}

		try {
			runningQueue.lock(job.getId());
			Job storedJob = runningQueue.get(job.getId());
			storedJob.setRunningStatus(Job.RUNNING);
			job.setRunningStatus(Job.RUNNING);
			// replaces job instance in queue
			runningQueue.replace(storedJob.getId(), storedJob);
		} catch (Exception ex){
			throw new RemoteException(ex.getMessage(), ex);					
		} finally{
			runningQueue.unlock(job.getId());	
		}
		
		// print GRS lock requests currently in place (to debug)
		// store node with new requests
		NodeInfoUtility.storeNodeInfo(Main.getNode(), true);
	}

	/**
	 * Asks to GRS to unlock resources, previously locked and in list of request
	 * inside of node.
	 * 
	 * @throws RemoteException occurs if errors
	 */
	@Override
	public void unlock(String jobId) throws RemoteException {
		// unlocks calling GRS and wait for that and clear the collection with
		// resources (for further lock requests)
		RequestLock requestLock = Main.getNode().getRequests().get(jobId);
		requestLock.unlock();
		requestLock.getResources().clear();
		// store node after to have to clear teh requests
		NodeInfoUtility.storeNodeInfo(Main.getNode(), true);
	}

}