/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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
package org.pepstock.jem.node.swarm.executors;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.swarm.SwarmException;
import org.pepstock.jem.node.swarm.SwarmQueues;

import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;

/**
 * Is the Callable responsible to retrieve the storm nodes 
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public abstract class AbstractGetNodes implements Callable<Collection<NodeInfoBean>>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public AbstractGetNodes() {
	}
	
	/**
	 * Extracts a list nodes from swarm 
	 * @param predicate HC predicates to filter result
	 * @return a list nodes from swarm 
	 * @throws SwarmException if any exception occurs
	 */
	public final Collection<NodeInfo> getNodes(Predicates.AbstractPredicate predicate) throws SwarmException{
		// gets map of nodes
		IMap<String, NodeInfo> nodes = Main.SWARM.getHazelcastInstance().getMap(SwarmQueues.NODES_MAP);
		// locks all map to have a consistent collection
		// only for 10 seconds otherwise
		// throws an exception
		Collection<NodeInfo> allNodes = null;
		Lock lock = Main.SWARM.getHazelcastInstance().getLock(SwarmQueues.NODES_MAP_LOCK);
		boolean isLock = false;
		try {
			// trying lock
			isLock = lock.tryLock(10, TimeUnit.SECONDS);
			if (isLock) {
				// gets all swarm nodes
				allNodes = nodes.values(predicate);
			} else {
				throw new SwarmException(NodeMessage.JEMC119E, SwarmQueues.NODES_MAP);
			}
			// returns all nodes
			return allNodes;
		} catch (Exception e) {
			throw new SwarmException(NodeMessage.JEMC119E, e, SwarmQueues.NODES_MAP);
		} finally {
			// unlocks always the map
			if (isLock){
				lock.unlock();
			}
		}
	}
}