/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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

/**
 * Contains all constants, keys of Hazelcast collections for swarm environment.
 * This environment is used to connect cloud of cloud.
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public final class SwarmQueues {

	/**
	 * Key for the collection used to collect all swarm nodes.
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, NodeInfo&gt; <br>
	 * where key value is node key value (Uuid of Member objects of cluster). <br>
	 * 
	 * @see org.pepstock.jem.node.NodeInfo#NodeInfo()
	 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
	 */
	public static final String NODES_MAP = "org.pepstock.jem.swarm.nodes";
	
	/**
	 * Lock to use to lock the NODES_MAP
	 */
	public static final String NODES_MAP_LOCK = "org.pepstock.jem.swarm.nodes.lock";

	/**
	 * To avoid any instantiation
	 */
	private SwarmQueues() {
	}

}