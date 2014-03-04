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
package org.pepstock.jem.node.executors.stats;

import org.hyperic.sigar.Sigar;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.stats.LightMapStats;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.node.stats.MemberSample;

import com.hazelcast.monitor.LocalMapStats;


/**
 * Returns the statistics of node, taking a snapshot of all figures.
 *   
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 *
 */
public class GetRealtimeSample extends DefaultExecutor<LightMemberSample> {

	private static final long serialVersionUID = 1L;
	
	private LightSample newSample = null;

	/**
	 * Constructs object using a light sample 
	 * @param newSample sample to update
	 * 
	 */
	public GetRealtimeSample(LightSample newSample) {
		this.newSample = newSample;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public LightMemberSample execute() throws ExecutorException {
		// if node is null, skip!
		// could happen if the executor is running in a starting node 
		if (Main.getNode() == null){
			return null;
		}
	
		Sigar sigar = new Sigar();

		// creates member sample, adding node info
		MemberSample memberSample = new MemberSample();
		memberSample.setMemberKey(Main.getNode().getKey());
		memberSample.setMemberLabel(Main.getNode().getLabel());
		memberSample.setMemberHostname(Main.getNode().getHostname());
		memberSample.setPid(sigar.getPid());
	
		// reads all figures
		return createLightMemberSample(newSample, memberSample);
	}
	
	/**
	 * loads all info 
	 * @param sample light sample (container)
	 * @param msample set of data for specific member
	 * @return a light sample for member
	 */
	private LightMemberSample createLightMemberSample(LightSample sample, MemberSample msample){
		
		LightMemberSample memberSample = new LightMemberSample();
		memberSample.setMemberKey(msample.getMemberKey());
		memberSample.setMemberLabel(msample.getMemberLabel());
		memberSample.setMemberHostname(msample.getMemberHostname());
		memberSample.setPid(msample.getPid());
		memberSample.setKey(sample.getKey());
		memberSample.setTime(sample.getTime());
		
		/**
		 * Input
		 */
		memberSample.getMapsStats().put(Queues.INPUT_QUEUE, createLightMapStats(Queues.INPUT_QUEUE));

		memberSample.getMapsStats().put(Queues.OUTPUT_QUEUE, createLightMapStats(Queues.OUTPUT_QUEUE));

		memberSample.getMapsStats().put(Queues.RUNNING_QUEUE, createLightMapStats(Queues.RUNNING_QUEUE));

		memberSample.getMapsStats().put(Queues.ROUTING_QUEUE, createLightMapStats(Queues.ROUTING_QUEUE));
		
		/**
		 * INTERNALS MAPS
		 */
		memberSample.getInternalMapsStats().put(Queues.COMMON_RESOURCES_MAP, createLightMapStats(Queues.COMMON_RESOURCES_MAP));

		memberSample.getInternalMapsStats().put(Queues.ROLES_MAP, createLightMapStats(Queues.ROLES_MAP));

		memberSample.getInternalMapsStats().put(Queues.ROUTED_QUEUE, createLightMapStats(Queues.ROUTED_QUEUE));

		memberSample.getInternalMapsStats().put(Queues.STATS_MAP, createLightMapStats(Queues.STATS_MAP));

		memberSample.getInternalMapsStats().put(Queues.USER_PREFERENCES_MAP, createLightMapStats(Queues.USER_PREFERENCES_MAP));
		
		return memberSample;

	}
	
	/**
	 * Reads statistics info of Hazelcast MAPS
	 * 
	 * @param mapName map name to access to Hazelcast
	 * @return set of statistics of map
	 */
	private LightMapStats createLightMapStats(String mapName){
		LocalMapStats stats = Main.getHazelcast().getMap(mapName).getLocalMapStats();
		
		LightMapStats lmap = new LightMapStats();
		lmap.setName(mapName);
	
		lmap.setOwnedEntryCount(stats.getOwnedEntryCount());
		lmap.setOwnedEntryMemoryCost(stats.getOwnedEntryMemoryCost());
		
		return lmap;
	}
}