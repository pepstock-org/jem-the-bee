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
package org.pepstock.jem.gwt.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.services.InfoService.Indexes;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.DisplayRequestors;
import org.pepstock.jem.node.executors.GetAbout;
import org.pepstock.jem.node.executors.clients.Count;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * This service gets information about internals engine of JEM, like GRS (if activated) or 
 * REDo statements in queue.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InternalsManager extends DefaultService{
	
	/**
	 * Returns a formatted string with all the contentions on resources.<br>
	 * If GRS is not activated, return that GRS is not activated!
	 * 
     * @param resourceKey resource pattern to check 
     * @return a formatted string with all contentions information
	 * @throws ServiceMessageException if any exception occurs
     */
    public String displayRequestors(String resourceKey) throws ServiceMessageException {
		// checks if the user is authorized to see cluster grs view
		// if not, this method throws an exception
    	checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_GRS));
		
		// prepares the executor.
		// if resourceKey is null, asks for all resources
		DisplayRequestors executor = null;
		if (resourceKey == null){
			executor = new DisplayRequestors();
		} else if ("*".equalsIgnoreCase(resourceKey.trim())){
			executor = new DisplayRequestors();
		} else {
			executor = new DisplayRequestors(resourceKey);
		}
		
		DistributedTaskExecutor<String> task = new DistributedTaskExecutor<String>(executor, getMember());
		return task.getResult();
    }

    
	/**
	 * Gets a collection of REDO statement if the cluster is waiting to store and persist objects.<br>
	 * It happens when the database to persist is not reachable.
	 * 
     * @return collection of REDO statements
	 * @throws ServiceMessageException if it's not able to lock the map to have a consistent view
     */
    public Collection<RedoStatement> getAllRedoStatements() throws ServiceMessageException{
		// checks if the user is authorized to see cluster redo view
		// if not, this method throws an exception
    	checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_REDO));
    	
		IMap<Long, RedoStatement> redos = getInstance().getMap(Queues.REDO_STATEMENT_MAP);
		List<RedoStatement> list = null;
		Lock lock = getInstance().getLock(Queues.REDO_STATEMENT_MAP_LOCK);
		boolean isLock = false;
		try {
			// locks all map to have a consistent collection
			// only for 10 seconds otherwise
			// throws an exception
			isLock = lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){
				list = new ArrayList<RedoStatement>(redos.values());
				// sorts the result to have a right table
				Collections.sort(list, new Comparator<RedoStatement>() {
					/**
					 * Compares REDO statement IDs to sort the collection
					 * 
					 * @param r1
					 *            first redo statement to compare
					 * @param r2
					 *            other redo statement to compare
					 * @return the value 0 if the redo statement ids are equals; a
					 *         value less than 0 if redo statement id is
					 *         lexicographically less than the other; and a value
					 *         greater than 0 if edo statement id is
					 *         lexicographically greater than the other.
					 */
					@Override
					public int compare(RedoStatement r1, RedoStatement r2) {
						return r1.getId().compareTo(r2.getId());
					}
				});
			} else {
				// timeout exception
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.COMMON_RESOURCES_MAP);
			}
		} catch (InterruptedException e) {
			// timeout exception
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.COMMON_RESOURCES_MAP);
        } finally {
			// unlocks always the map
			if (isLock){
				lock.unlock();
			}
		}
		// returns list
	    return list;
    }
    
    /**
     * Returns a about object with information about version, creation and licenses
     * @return about instance
     * @throws ServiceMessageException if any exception occurs
     */
    public About getAbout() throws ServiceMessageException {
		// checks if the user is authenticated
		// if not, this method throws an exception
		checkAuthentication();

		DistributedTaskExecutor<About> task = new DistributedTaskExecutor<About>(new GetAbout(), getMember());
		return task.getResult();
    }
    
    /**
     * Returns the amount of clients connected to JEM
     * @return amount of clients
     * @throws ServiceMessageException if any exception occurs
     */
    public int getClients() throws ServiceMessageException {
		DistributedTaskExecutor<Integer> task = new DistributedTaskExecutor<Integer>(new Count(), getMember());
		return task.getResult();
    } 
    
    /**
     * Returns an array of system information
     * @return an array of system information
     * @throws ServiceMessageException if any exception occurs
     */
    public String[] getEnvironmentInformation() throws ServiceMessageException {

		// creates array
		String[] infos = new String[Indexes.INFO_SIZE.getIndex()];
		
		HazelcastInstance localMember = SharedObjects.getInstance().getHazelcastClient();
		// Name of JEM GROUP
		infos[Indexes.NAME.getIndex()] = SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName();

		infos[Indexes.NODES_COUNT.getIndex()] = String.valueOf(localMember.getCluster().getMembers().size());

		// Exec job count
		IMap<String, Job> jobs = localMember.getMap(Queues.RUNNING_QUEUE);
		infos[Indexes.EXECUTION_JOB_COUNT.getIndex()] = String.valueOf(jobs.size());

		// Uptime
		// gets the coordinator of JEM cluster (the oldest one)
		Member oldest = localMember.getCluster().getMembers().iterator().next();
		IMap<String, NodeInfo> nodes = localMember.getMap(Queues.NODES_MAP);

		// to get the uptime
		// uses the started time information of JEM node info
		// try locks by uuid
		if (nodes.tryLock(oldest.getUuid(), Queues.LOCK_TIMEOUT, TimeUnit.SECONDS)) {
			try {
				// if coordinator is not on map (mustn't be!!)
				// set not available
				NodeInfo oldestInfo = nodes.get(oldest.getUuid());
				if (oldestInfo != null){
					infos[Indexes.STARTED_TIME.getIndex()] = String.valueOf(oldestInfo.getStartedTime().getTime());	
				} else {
					infos[Indexes.STARTED_TIME.getIndex()] = "N/A";
				}
			} finally {
				// unlocks always the key
				nodes.unlock(oldest.getUuid());
			}
		} else {
			infos[Indexes.STARTED_TIME.getIndex()] = "N/A";
		}
		
		// gets the current time. 
		// this is helpful because ould be some time differences 
		// between client and servers
		infos[Indexes.CURRENT_TIME.getIndex()] = String.valueOf(System.currentTimeMillis());

		boolean hasEviction = SharedObjects.getInstance().getMapEvictionInfo().containsKey(Queues.OUTPUT_QUEUE) ?
				SharedObjects.getInstance().getMapEvictionInfo().get(Queues.OUTPUT_QUEUE) : false;
		
		// gets if eviction is activated
		infos[Indexes.EVICTION.getIndex()] = String.valueOf(hasEviction);
		return infos;
    }
}