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
package org.pepstock.jem.node;


/**
 * Contains all constants, keys of Hazelcast collections, locks and
 * notifications
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class Queues {
	/**
	 * Key for the collection used to park PreJob objects waiting for checking
	 * and JCL validation. The collection is a Queue with following structure:<br>
	 * <br>
	 * IQueue&lt;PreJob&gt; <br>
	 * 
	 * @see org.pepstock.jem.PreJob#PreJob()
	 * @see org.pepstock.jem.node.JclCheckingQueueManager#JclCheckingQueueManager()
	 */
	public static final String JCL_CHECKING_QUEUE = "org.pepstock.jem.jcl.checking";
	
	/**
	 * Lock to use to lock the JCL_CHECKING_QUEUE
	 */
	public static final String JCL_CHECKING_QUEUE_LOCK = "org.pepstock.jem.jcl.checking.lock";

	/**
	 * Key for the backup map used inside of Hazelcast for JclChecking queue
	 */
	public static final String JCL_CHECKING_MAP = "org.pepstock.jem.jcl.checking-map";
	
	/**
	 * Lock to use to lock the JCL_CHECKING_MAP
	 */
	public static final String JCL_CHECKING_MAP_LOCK = "org.pepstock.jem.jcl.checking-map.lock";

	/**
	 * Key for the collection used to collect all jobs which are waiting for
	 * execution. This is standard input queue. The collection is a Map with
	 * following structure:<br>
	 * <br>
	 * IMap&lt;String, Job&gt; <br>
	 * where key value is job id string.
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
	 * 
	 */
	public static final String INPUT_QUEUE = "org.pepstock.jem.input";

	/**
	 * Lock to use to lock the INPUT_QUEUE_LOCK
	 */
	public static final String INPUT_QUEUE_LOCK = "org.pepstock.jem.input.lock";

	/**
	 * Key for the collection used to collect all jobs which are currently in
	 * execution. The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, Job&gt; <br>
	 * where key value is job id string.
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @see org.pepstock.jem.node.Submitter#Submitter()
	 * 
	 */
	public static final String RUNNING_QUEUE = "org.pepstock.jem.running";

	/**
	 * Lock to use to lock the RUNNING_QUEUE
	 */
	public static final String RUNNING_QUEUE_LOCK = "org.pepstock.jem.running.lock";

	/**
	 * Key for the collection used to collect all jobs which are already
	 * executed. This is standard output queue. The collection is a Map with
	 * following structure:<br>
	 * <br>
	 * IMap&lt;String, Job&gt; <br>
	 * where key value is job id string.
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @see org.pepstock.jem.node.Submitter#Submitter()
	 * 
	 */
	public static final String OUTPUT_QUEUE = "org.pepstock.jem.output";

	/**
	 * Lock to use to lock the OUTPUT_QUEUE
	 */
	public static final String OUTPUT_QUEUE_LOCK = "org.pepstock.jem.output.lock";

	/**
	 * Key for the collection used to collect all jobs which have different
	 * scheduling environment and for this reason couldn't be executed in the
	 * cluster and move to a temporary queue. The collection is a Map with
	 * following structure:<br>
	 * <br>
	 * IMap&lt;String, Job&gt; <br>
	 * where key value is job id string.
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
	 * 
	 */
	public static final String ROUTING_QUEUE = "org.pepstock.jem.routing";

	/**
	 * Lock to use to lock the ROUTING_QUEUE
	 */
	public static final String ROUTING_QUEUE_LOCK = "org.pepstock.jem.routing.lock";

	/**
	 * Key for the collection used to collect all nodes, both which currently in
	 * the cluster and which left the group.
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, NodeInfo&gt; <br>
	 * where key value is node key value (Uuid of Member objects of cluster). <br>
	 * 
	 * @see org.pepstock.jem.node.NodeInfo#NodeInfo()
	 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
	 */
	public static final String NODES_MAP = "org.pepstock.jem.nodes";

	/**
	 * Lock to use to lock the NODES_MAP
	 */
	public static final String NODES_MAP_LOCK = "org.pepstock.jem.nodes.lock";

	/**
	 * Key for Hazelcast structure which are able to generate unique ID in the
	 * cluster. It is used to create a unique JOB id.
	 * 
	 * @see org.pepstock.jem.Job#setId(String)
	 */
	public static final String JOB_ID_GENERATOR = "org.pepstock.jem.job.id";

	/**
	 * Key for Hazelcast topic structure which are able to notify a message to
	 * all listeners. It is used to notify the end of job execution.
	 * 
	 * @see org.pepstock.jem.client.Client#onMessage(com.hazelcast.core.Message)
	 */
	public static final String ENDED_JOB_TOPIC = "org.pepstock.jem.job.ended";
	
	/**
	 * Lock to use to lock the ENDED_JOB_TOPIC
	 */
	public static final String ENDED_JOB_TOPIC_LOCK = "org.pepstock.jem.job.ended.lock";

	/**
	 * Key for Hazelcast topic structure which are able to notify a message to
	 * all listeners. It is used to notify the end of job execution.
	 * 
	 * @see org.pepstock.jem.client.Client#onMessage(com.hazelcast.core.Message)
	 */
	public static final String REMOVED_NODE_INFO_TOPIC = "org.pepstock.jem.node.info.removed";

	/**
	 * Lock to use to lock the REMOVED_NODE_INFO_TOPIC
	 */
	public static final String REMOVED_NODE_INFO_TOPIC_LOCK = "org.pepstock.jem.node.info.removed.lock";

	/**
	 * Key for the collection used to collect all resources which could be asked by 
	 * a job.
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, Resource&gt; <br>
	 * where key value is resource name value. <br>
	 * 
	 * @see org.pepstock.jem.node.resources.Resource
	 */
	public static final String COMMON_RESOURCES_MAP = "org.pepstock.jem.common.resources";

	/**
	 * Lock to use to lock the COMMON_RESOURCES_MAP_LOCK
	 */
	public static final String COMMON_RESOURCES_MAP_LOCK = "org.pepstock.jem.common.resources.lock";

	/**
	 * Key for the collection used to collect all roles to authorize users on UI and on job
	 * execution 
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, Role&gt; <br>
	 * where key value is role name value. <br>
	 * 
	 * @see org.pepstock.jem.node.security.Role
	 */
	public static final String ROLES_MAP = "org.pepstock.jem.roles";
	
	/**
	 * Lock to use to lock the ROLES_MAP
	 */
	public static final String ROLES_MAP_LOCK = "org.pepstock.jem.roles.lock";
	
	/**
	 * Key for the collection used to collect the routing configuration.
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, SwarmConfiguration&gt; <br>
	 * where key value is config name value. <br>
	 * 
	 * @see org.pepstock.jem.node.configuration.SwarmConfiguration
	 */
	public static final String ROUTING_CONFIG_MAP = "org.pepstock.jem.routingConfig";

	/**
	 * Lock to use to lock the ROUTING_CONFIG_MAP
	 */
	public static final String ROUTING_CONFIG_MAP_LOCK = "org.pepstock.jem.routingConfig.lock";

	/**
	 * Key for the collection used to collect all statistics of all members of
	 * Jem.
	 * 
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, Sample&gt; <br>
	 * where key value is time value of sample. <br>
	 * 
	 * @see org.pepstock.jem.node.stats.LightSample
	 */
	public static final String STATS_MAP = "org.pepstock.jem.stats";
	
	/**
	 * Lock to use to lock the STATS_MAP 
	 */
	public static final String STATS_MAP_LOCK = "org.pepstock.jem.stats.lock";

	/**
	 * Key for the collection used to collect all actions which are waiting for
	 * storing on database. These objects are temporary stored in this map.
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;Long, RedoStatement&gt; <br>
	 * where key value is sequence id string.
	 * 
	 */
	public static final String REDO_STATEMENT_MAP = "org.pepstock.jem.redo";
	
	/**
	 * Lock to use to lock the REDO_STATEMENT_MAP
	 */
	public static final String REDO_STATEMENT_MAP_LOCK = "org.pepstock.jem.redo.lock";

	/**
	 * Key for the collection used to collect all jobs which has been routed
	 * that is they have been passed to the right environment and executed.
	 * Following structure:<br>
	 * <br>
	 * IMap&lt;String, Job&gt; <br>
	 * where key value is job id string.
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @see org.pepstock.jem.node.InputQueueManager#InputQueueManager()
	 * 
	 */
	public static final String ROUTED_QUEUE = "org.pepstock.jem.routed";

	/**
	 * Lock to use to lock the ROUTED_QUEUE
	 */
	public static final String ROUTED_QUEUE_LOCK = "org.pepstock.jem.routed.lock";
	
	/**
	 * Key for the collection used to collect all user preferences.
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, HashMap&lt;String, UserPreference&gt;&gt; <br>
	 * where key value is user id string.
	 * 
	 */
	public static final String USER_PREFERENCES_MAP = "org.pepstock.jem.user.preferences";
	
	/**
	 * Lock to use to lock the USER_PREFERENCES_MAP
	 */
	public static final String USER_PREFERENCES_MAP_LOCK = "org.pepstock.jem.user.preferences.lock";

	/**
	 * Key for asking a lock during the startup of node, to synchronize start-up
	 * 
	 */
	public static final String STARTUP_LOCK = "org.pepstock.jem.startup";
		
	/**
	 *Key for asking a lock during the stopping of node, to synchronize it
	 * 
	 */
	public static final String SHUTDOWN_LOCK = "org.pepstock.jem.shutdown";

	/**
	 * Key for asking a lock during the actions of KEYSTORE, to synchronize read and write
	 * 
	 */
	public static final String KEYSTORE_LOCK = "org.pepstock.jem.keystore";
	
	/**
	 * Key for asking a lock during the actions of affinity loader policy, to synchronize read and write
	 * 
	 */
	public static final String AFFINITY_LOADER_LOCK = "org.pepstock.jem.affinity.loader";
	
	/**
	 * Key for asking a Read/Write lock during the actions of datasets rules, to synchronize read and write
	 * 
	 */
	public static final String DATASETS_RULES_LOCK = "org.pepstock.jem.datasets.rules";

	/**
	 * To avoid any instantiation
	 */
	private Queues() {
	}

}
