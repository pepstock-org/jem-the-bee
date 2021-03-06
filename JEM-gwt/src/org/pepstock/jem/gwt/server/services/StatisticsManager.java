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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.executors.stats.GetRealtimeSample;
import org.pepstock.jem.node.hazelcast.ExecutorServices;
import org.pepstock.jem.node.hazelcast.Locks;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.node.stats.Sample;
import org.pepstock.jem.node.stats.SampleComparator;
import org.pepstock.jem.util.DateFormatter;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * This service provides all statistics metrics to client about the status of JEM environment.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StatisticsManager extends InternalsManager{
	
	private SampleComparator sampleComparator = new SampleComparator();

	/**
	 * Returns the collection of all active samples in JEM. 
	 * 
     * @return collection of samples
	 * @throws ServiceMessageException if any exception occurs
     */
    public Collection<LightSample> getSamples() throws ServiceMessageException {
    	// Remember that it uses QUEUES CURRENT permission to check if
    	// it can get statistics
		// checks if the user is authorized
		// if not, this method throws an exception
    	try{
    		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_FOLDER));
    	} catch (Exception e){
    		LogAppl.getInstance().ignore(e.getMessage(), e);
    		try{
    			checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_FOLDER));
    		} catch (Exception e1){
    			LogAppl.getInstance().ignore(e1.getMessage(), e1);
    			checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_QUEUES_FOLDER));
    		}
    	}
		IMap<String, LightSample> samples = getInstance().getMap(Queues.STATS_MAP);
		Lock lock = getInstance().getLock(Locks.STATS_MAP);
		boolean isLock=false;
		List<LightSample> list = null;
		try{
			// locks all map to have a consistent collection
			// only for 10 seconds otherwise
			// throws an exception
			isLock=lock.tryLock(Locks.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){
			// gets data...
			list = new ArrayList<LightSample>(samples.values());
			// ... and sorts them
			Collections.sort(list, sampleComparator);
			} else {
				// timeout exception
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.STATS_MAP);
			}
		} catch (InterruptedException e) {
			// timeout exception
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.STATS_MAP);
        } finally{
			// unlocks always the map
			if(isLock){
				lock.unlock();
			}
		}
	    return list;
    }
    
	/**
	 * Calculates and returns last sample of JEM statistics
	 * 
     * @return last sample of statistics
	 * @throws ServiceMessageException if any exception occurs 
     */
    public LightSample getCurrentSample() throws ServiceMessageException {
    	// Remember that it uses QUEUES CURRENT permission to check if
    	// it can get statistics 
		// checks if the user is authorized
		// if not, this method throws an exception
    	try{
    		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_FOLDER));
    	} catch (Exception e){
    		LogAppl.getInstance().ignore(e.getMessage(), e);
    		try{
    			checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_FOLDER));
    		} catch (Exception e1){
    			LogAppl.getInstance().ignore(e1.getMessage(), e1);
    			checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_QUEUES_FOLDER));
    		}
    	}

    	LightSample lightSample = null;
    	
    	// extracts and collects all members 
		Cluster cluster = getInstance().getCluster();
		Set<Member> listOfNodes = new HashSet<Member>();
		for (Member member : cluster.getMembers()){
			listOfNodes.add(member);
		}
		// if collections is not empty (must be!)
		if (!listOfNodes.isEmpty()){
			
			//creates the sample 
			// setting all attributes necessary
			String key = DateFormatter.getCurrentDate(Sample.FORMAT);
			String[] times = StringUtils.split(key, ' ');
	
			lightSample = new LightSample();
			lightSample.setKey(key);
			lightSample.setDate(times[0]);
			lightSample.setTime(times[1]);
			
			// creates the distributed task
			// and schedule the task on all members
			IExecutorService executorServie = getInstance().getExecutorService(ExecutorServices.NODE);
			Map<Member, Future<LightMemberSample>> futures = executorServie.submitToMembers(new GetRealtimeSample(lightSample), listOfNodes);
			try {
				// gets the results from nodes
				for (Future<LightMemberSample> future : futures.values()) {
					LightMemberSample result = future.get();
					// adds all results on the light sample
					// for each member
					if (result != null) {
						lightSample.getMembers().add(result);
					}
				}
			} catch (Exception e) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG018E, e);
			}
		}
		return lightSample;
    }


}