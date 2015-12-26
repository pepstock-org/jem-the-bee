/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.node.swarm;

import java.util.Collection;
import java.util.Random;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.hazelcast.Queues;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.map.listener.EntryAddedListener;

/**
 * Map storn nodes manager
 * 
 * @author Marco "Fuzzo" Cuccato
 */
public class MapSwarmNodesManager implements EntryAddedListener<String, NodeInfo> {

	/**
	 * Constructor
	 */
	public MapSwarmNodesManager() {
		IMap<String, NodeInfo> nodesMap = Main.SWARM.getHazelcastInstance().getMap(Queues.SWARM_NODES_MAP);
		nodesMap.addEntryListener(this, true);
	}

	/**
	 * On entry added 2 action are accomplished:
	 * <p>
	 * 1) {@link RoutingQueueManager#routeJobsByAvailableEnvironments()}
	 * <p>
	 * 2)
	 * {@link OutputQueueManager#notifyEndedRoutedJobsByAvailableEnvironments()}
	 * 
	 */
	@Override
	public void entryAdded(EntryEvent<String, NodeInfo> event) {
		// route jobs
		Main.SWARM.getRoutingQueueManager().routeJobsByAvailableEnvironments();
		// notify ended routed job
		Main.SWARM.getOutputQueueManager().notifyEndedRoutedJobsByAvailableEnvironments();
	}

	
	/**
	 * 
	 * @param nodes of "swarm" cluster
	 * @return one of the "swarm" hazelcast member that refer to one of the
	 *         nodes present in the collection
	 */
	public static Member getMember(Collection<NodeInfo> nodes) {
		if (nodes != null && !nodes.isEmpty()) {
			// select a random node from the collection
			Random rand = new Random();
			int n = rand.nextInt(nodes.size());
			Object[] nodesArray = nodes.toArray();
			NodeInfo selectedNode = (NodeInfo) nodesArray[n];
			// Retrieve the hazelcast instance of the swarm environment
			Cluster cluster = Main.SWARM.getHazelcastInstance().getCluster();
			for (Member currMember : cluster.getMembers()) {
				if (currMember.getUuid().equals(selectedNode.getKey())) {
					return currMember;
				}
			}
			return null;
		}
		return null;
	}

}