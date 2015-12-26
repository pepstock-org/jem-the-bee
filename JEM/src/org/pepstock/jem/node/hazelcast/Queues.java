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

import org.pepstock.jem.grs.LatchInfo;


/**
 * Contains all constants, keys of Hazelcast collections
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
	 * Key for the collection used to collect all user preferences.
	 * The collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, HashMap&lt;String, UserPreference&gt;&gt; <br>
	 * where key value is user id string.
	 * 
	 */
	public static final String USER_PREFERENCES_MAP = "org.pepstock.jem.user.preferences";
	
	/**
	 * Key for the collection used to collect all latch information. The
	 * collection is a Map with following structure:<br>
	 * <br>
	 * IMap&lt;String, LatchInfo&gt; <br>
	 * where key value is the resource name, requested for locking.
	 * 
	 * @see LatchInfo
	 */
	public static final String GRS_COUNTER_MUTEX_MAP = "org.pepstock.jem.grs.counter_mutex";
	
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
	public static final String SWARM_NODES_MAP = "org.pepstock.jem.swarm.nodes";

	/**
	 * To avoid any instantiation
	 */
	private Queues() {
	}

}
