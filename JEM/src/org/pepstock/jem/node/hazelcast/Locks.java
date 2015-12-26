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
package org.pepstock.jem.node.hazelcast;


/**
 * Contains all constants, keys of Hazelcast locks
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * 
 */
public final class Locks {
	
	/**
	 * Lock to use to lock the JCL_CHECKING_QUEUE
	 */
	public static final String JCL_CHECKING_QUEUE = "org.pepstock.jem.jcl.checking.lock";


	/**
	 * Lock to use to lock the INPUT_QUEUE
	 */
	public static final String INPUT_QUEUE = "org.pepstock.jem.input.lock";

	/**
	 * Lock to use to lock the RUNNING_QUEUE
	 */
	public static final String RUNNING_QUEUE = "org.pepstock.jem.running.lock";

	/**
	 * Lock to use to lock the OUTPUT_QUEUE
	 */
	public static final String OUTPUT_QUEUE = "org.pepstock.jem.output.lock";

	/**
	 * Lock to use to lock the ROUTING_QUEUE
	 */
	public static final String ROUTING_QUEUE = "org.pepstock.jem.routing.lock";

	/**
	 * Lock to use to lock the NODES_MAP
	 */
	public static final String NODES_MAP = "org.pepstock.jem.nodes.lock";

	/**
	 * Lock to use to lock the ENDED_JOB_TOPIC
	 */
	public static final String ENDED_JOB_TOPIC = "org.pepstock.jem.job.ended.lock";

	/**
	 * Lock to use to lock the REMOVED_NODE_INFO_TOPIC
	 */
	public static final String REMOVED_NODE_INFO_TOPIC = "org.pepstock.jem.node.info.removed.lock";

	/**
	 * Lock to use to lock the COMMON_RESOURCES_MAP
	 */
	public static final String COMMON_RESOURCES_MAP = "org.pepstock.jem.common.resources.lock";

	/**
	 * Lock to use to lock the ROLES_MAP
	 */
	public static final String ROLES_MAP = "org.pepstock.jem.roles.lock";
	
	/**
	 * Lock to use to lock the ROUTING_CONFIG_MAP
	 */
	public static final String ROUTING_CONFIG_MAP = "org.pepstock.jem.routingConfig.lock";

	/**
	 * Lock to use to lock the STATS_MAP 
	 */
	public static final String STATS_MAP = "org.pepstock.jem.stats.lock";

	/**
	 * Lock to use to lock the REDO_STATEMENT_MAP
	 */
	public static final String REDO_STATEMENT_MAP = "org.pepstock.jem.redo.lock";

	/**
	 * Lock to use to lock the ROUTED_QUEUE
	 */
	public static final String ROUTED_QUEUE = "org.pepstock.jem.routed.lock";
	
	/**
	 * Lock to use to lock the USER_PREFERENCES_MAP
	 */
	public static final String USER_PREFERENCES_MAP = "org.pepstock.jem.user.preferences.lock";

	/**
	 * Key for asking a lock during the startup of node, to synchronize start-up
	 * 
	 */
	public static final String STARTUP = "org.pepstock.jem.startup";
		
	/**
	 *Key for asking a lock during the stopping of node, to synchronize it
	 * 
	 */
	public static final String SHUTDOWN = "org.pepstock.jem.shutdown";

	/**
	 * Key for asking a lock during the actions of KEYSTORE, to synchronize read and write
	 * 
	 */
	public static final String KEYSTORE = "org.pepstock.jem.keystore";
	
	/**
	 * Key for asking a lock during the actions of affinity loader policy, to synchronize read and write
	 * 
	 */
	public static final String AFFINITY_LOADER = "org.pepstock.jem.affinity.loader";
	
	/**
	 * Key for asking a Read/Write lock during the actions of datasets rules, to synchronize read and write
	 * 
	 */
	public static final String DATASETS_RULES = "org.pepstock.jem.datasets.rules";
	
	/**
	 * Lock to use to lock the COUNTER_MUTEX
	 */
	public static final String GRS_COUNTER_MUTEX = "org.pepstock.jem.grs.counter_mutex.lock";

	/**
	 * Semaphore to synchronize all requests for locking
	 */
	public static final String GRS_REQUEST = "org.pepstock.jem.grs.request.lock";
	
	/**
	 * Lock to use to lock the NODES_MAP
	 */
	public static final String SWARM_NODES_MAP = "org.pepstock.jem.swarm.nodes.lock";
	
	/**
	 * Amount of secods to wait during a try lock request
	 */
	public static final int LOCK_TIMEOUT = 10;
	
	/**
	 * To avoid any instantiation
	 */
	private Locks() {
	}

}
