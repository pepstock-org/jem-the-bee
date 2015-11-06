/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.gwt.server.commons.GenericDistributedTaskExecutor;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.affinity.CheckAffinityPolicy;
import org.pepstock.jem.node.executors.affinity.GetAffinityPolicy;
import org.pepstock.jem.node.executors.affinity.SaveAffinityPolicy;
import org.pepstock.jem.node.executors.configuration.CheckHazelcastConfiguration;
import org.pepstock.jem.node.executors.configuration.CheckJemConfiguration;
import org.pepstock.jem.node.executors.configuration.CheckJemEnvConfiguration;
import org.pepstock.jem.node.executors.configuration.GetHazelcastConfiguration;
import org.pepstock.jem.node.executors.configuration.GetJemConfiguration;
import org.pepstock.jem.node.executors.configuration.GetJemEnvConfiguration;
import org.pepstock.jem.node.executors.configuration.SaveHazelcastConfiguration;
import org.pepstock.jem.node.executors.configuration.SaveJemConfiguration;
import org.pepstock.jem.node.executors.configuration.SaveJemEnvConfiguration;
import org.pepstock.jem.node.executors.datasetsrules.CheckDatasetsRules;
import org.pepstock.jem.node.executors.datasetsrules.GetDatasetsRules;
import org.pepstock.jem.node.executors.datasetsrules.SaveDatasetsRules;
import org.pepstock.jem.node.executors.nodes.Drain;
import org.pepstock.jem.node.executors.nodes.GetLog;
import org.pepstock.jem.node.executors.nodes.Shutdown;
import org.pepstock.jem.node.executors.nodes.Start;
import org.pepstock.jem.node.executors.nodes.Top;
import org.pepstock.jem.node.executors.nodes.Update;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.util.filters.Filter;
import org.pepstock.jem.util.filters.FilterFactory;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;
import org.pepstock.jem.util.filters.predicates.NodePredicate;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.query.Predicates.AbstractPredicate;
import com.hazelcast.query.SqlPredicate;


/**
 * Is the manager of all operations to the nodes of JEM. 
 * UNKNOWN members are not returned.
 * 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class NodesManager extends DefaultService {
	
	/**
	 * Returns the list of all normal nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param nodesFilter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodes(String nodesFilter) throws ServiceMessageException{
		return getNodes(nodesFilter, false);
	}

	/**
	 * Returns the list of all swarm nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param nodesFilter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Collection<NodeInfoBean> getSwarmNodes(String nodesFilter) throws ServiceMessageException {
		return getNodes(nodesFilter, true);
	}
	
    /**
     * Stores the node on Hazelcast map for nodes.
     * Before doing it, it checks if there is already the instance store on Hazelcast (maybe do to crashed)
     * and removes it and loads the new one.
     * @param key NodeInfo node information
     * 
     * @param info node information to store
     * @return 
     * @throws ServiceMessageException 
     * @throws ConfigurationException if any configuration error occurs
     * 
     */
    public NodeInfo getNodeByKey(String key) throws ServiceMessageException {
    	// gets HC map
        IMap<String, NodeInfo> nodes = getInstance().getMap(Queues.NODES_MAP);
        boolean isLock=false;
        Lock lock = getInstance().getLock(Queues.NODES_MAP_LOCK);
		try {
			isLock = lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){
				return nodes.get(key);
			} else {
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.NODES_MAP);
			}
		} catch (InterruptedException e) {
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.NODES_MAP);
        } finally {
			// unlocks always the map
			if(isLock){
				lock.unlock();
			}
		}
    }


	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not returned
	 * 
	 * @param nodesFilter  filter contains all tokens to performs filtering
	 * @param swarmNodes if true return only swarmNodes nodes, if false return only nodes that are not swarmNodes nodes
	 * @return collection of nodes
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodes(String nodesFilter, boolean swarmNodes) throws ServiceMessageException {
		// builds permission
		String permission = Permissions.SEARCH_NODES+nodesFilter.toLowerCase();
		// checks if the user is authorized to get nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));
		
		// prepares SQL query to extract the right nodes
		String sqlFilter = nodesFilter.replace('.', '_');
		sqlFilter = sqlFilter.replace('*', '%');
		// creates SQL
		StringBuilder sb = new StringBuilder();
		sb.append("(hostname like '").append(sqlFilter).append("'").append(" OR ");
		sb.append("label like '").append(sqlFilter).append("') ");
		if (swarmNodes) {
			sb.append(" AND isSwarmNode = " + swarmNodes);
		}
		List<NodeInfoBean> list = getNodesButUnknown(new SqlPredicate(sb.toString()));
		// if list is not empty
		if (!list.isEmpty()){
			// sorts the list for KEY
			Collections.sort(list, new Comparator<NodeInfoBean>() {
				@Override
                public int compare(NodeInfoBean o1, NodeInfoBean o2) {
	                return o1.getKey().compareTo(o2.getKey());
                }
			});
		}
		return list;
	}
	
	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not returned
	 * 
	 * @param nodesFilter a String that will be parsed as a {@link Filter}
	 * @return collection of nodes
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodesByFilter(String nodesFilter) throws ServiceMessageException {
		// creates a filter object
		Filter filter = FilterFactory.parse(nodesFilter, FilterFactory.NODE_DEFAULT_FILTER);
		// extract the label or hostname, if it is.
		// necessary to check permission because it is based on
		// label or hostname
		String nodesPermString = filter.getValue(NodeFilterFields.NAME.getName());
		// if label is null, try with hostname
		if ((nodesPermString == null) || (nodesPermString.trim().length() == 0)) {
			nodesPermString = filter.getValue(NodeFilterFields.HOSTNAME.getName());
			// if hostname is null as well, then use *
			if ((nodesPermString == null) || (nodesPermString.trim().length() == 0)) {
				nodesPermString = "*";
			}
		}
		// creates the right permission by jlabel or hostname
		String permission = Permissions.SEARCH_NODES+nodesPermString.toLowerCase();
		// checks if the user is authorized to get nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(permission));
		return getNodesButUnknown(new NodePredicate(filter));
	}

	private List<NodeInfoBean> getNodesButUnknown(AbstractPredicate predicate) throws ServiceMessageException {
		IMap<String, NodeInfo> nodes = getInstance().getMap(Queues.NODES_MAP);
		List<NodeInfoBean> list = new ArrayList<NodeInfoBean>();
		Collection<NodeInfo> allNodes = null;
		boolean isLock=false;
		Lock lock = getInstance().getLock(Queues.NODES_MAP_LOCK);
		try {
			isLock=lock.tryLock(Queues.LOCK_TIMEOUT, TimeUnit.SECONDS);
			if (isLock){ 
				// gets all nodes by predicate
				allNodes = nodes.values(predicate);
			} else {
				throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, Queues.NODES_MAP);
			}
		} catch (InterruptedException e) {
			throw new ServiceMessageException(UserInterfaceMessage.JEMG022E, e, Queues.NODES_MAP);
        } finally {
			// unlocks always the map
			if(isLock){
				lock.unlock();
			}
		}
		if (allNodes != null){
			// gets the nodes and returns them
			// removing the nodes in UNKNOW
			// to avoid misunderstanding on UI
			for (NodeInfo node : allNodes){
				if (!node.getStatus().equals(Status.UNKNOWN)){
					list.add(node.getNodeInfoBean());
				}
			}
		}
		return list;
	}
	
	/**
	 * Drains the list of members, using a future task by executor service of Hazelcast. 
	 * 
	 * @param nodes list of members to drain 
	 * @return always <code>true</code>
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean drain(Collection<NodeInfoBean> nodes) throws ServiceMessageException {
		// checks if the user is authorized to drain nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.NODES_DRAIN));
		return doNodeAction(nodes, new Drain());
	}
	
	/**
	 * Starts the list of members, using a future task by executor service of Hazelcast. 
	 * 
	 * @param nodes nodes list of members to start
	 * @return always <code>true</code>
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean start(Collection<NodeInfoBean> nodes) throws ServiceMessageException{
		// checks if the user is authorized to start nodes
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.NODES_START));
		return doNodeAction(nodes, new Start());
	}


	/**
	 * Shuts down the list of members, using a future task by executor service of Hazelcast.<br>
	 * Is not used.
	 *  
	 * @param nodes list of nodes
	 * @return always <code>true</code>
	 * @throws ServiceMessageException if any exception occurs
	 */
	@SuppressWarnings("unused")
    private Boolean shutdown(Collection<NodeInfoBean> nodes) throws ServiceMessageException{
		checkAuthentication();
		return doNodeAction(nodes, new Shutdown());
	}

	/**
	 * Execute a specific task on a set of Nodes. Be sure you check authorizations before calling this.
	 * @param nodes the target nodes
	 * @param executor the task to be executed
	 * @return always <code>true</code>
	 * @throws ServiceMessageException if any exception occours
	 */
	private Boolean doNodeAction(Collection<NodeInfoBean> nodes, Callable<ExecutionResult> executor) throws ServiceMessageException {
		// gets nodes map instance 
		IMap<String, NodeInfo> membersMap = getInstance().getMap(Queues.NODES_MAP);
		// scans all nodes
		for (NodeInfoBean node : nodes){
			// is not a super node and is not UNKNOWN
			if (!node.getStatus().equalsIgnoreCase(Status.UNKNOWN.getDescription())){
				// gets key
				String key = node.getKey();
				// checks if is in map
				if (membersMap.containsKey(key)){
					// gets the cluster to have member object of Hazelcast
					// to execute the future task
					Cluster cluster = getInstance().getCluster();
					// gets all members and scans them
					Set<Member> set = cluster.getMembers();
					for (Member member : set){
						String memberKey = member.getUuid();
						// is the same member
						if (node.getKey().equalsIgnoreCase(memberKey)){
							GenericDistributedTaskExecutor task = new GenericDistributedTaskExecutor(executor, member);
							task.execute();
						} 
					}
				}
			}
		}
		return Boolean.TRUE;
	}
	
	/**
	 * Update the domain or static affinities of node
	 * @param node node to update
	 * @return always true
	 * @throws ServiceMessageException if any exception occurs
	 */
	public Boolean update(NodeInfoBean node) throws ServiceMessageException{
		// checks if the user is authorized to update a node
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.NODES_UPDATE));

		GenericDistributedTaskExecutor task = new GenericDistributedTaskExecutor(new Update(node), getMember(node.getKey()));
		task.execute();
		
		return Boolean.TRUE;
	}
	
	/**
	 * Returns the configuration file for the node
	 * 
	 * @param node node where execute a future task to get the config file 
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws ServiceMessageException if exception occurs
	 */
	public ConfigurationFile getNodeConfigFile(NodeInfoBean node, String what) throws ServiceMessageException {
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_CONFIGURATION));
		
		// creates the future task
		Callable<ConfigurationFile> executor = null;

		// checks if wants Hazelcast or JEM node configuration
		// by default is JEM node configuration
		if (what != null){
			if (what.equalsIgnoreCase(ConfigKeys.JEM_CONFIG)){
				executor = new GetJemConfiguration();
			} else if (what.equalsIgnoreCase(ConfigKeys.AFFINITY)){
				executor = new GetAffinityPolicy();
			} else {
				executor = new GetJemConfiguration();	
			}
		} else {
			executor = new GetJemConfiguration();
		}

		DistributedTaskExecutor<ConfigurationFile> task = new DistributedTaskExecutor<ConfigurationFile>(executor, getMember(node.getKey()));
		return task.getResult();
	}

	/**
	 * Saves the configuration file for the node
	 * 
	 * @param node node where execute a future task to get the config file 
	 * @param file configuration file to save
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws ServiceMessageException if exception occurs
	 */
	public ConfigurationFile saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what) throws ServiceMessageException {
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_CONFIGURATION));
		
		// creates the future task
		Callable<ConfigurationFile> executor = null;

		// checks if wants Hazelcast or JEM node configuration
		// by default is JEM node configuration
		if (what != null){
			if (what.equalsIgnoreCase(ConfigKeys.JEM_CONFIG)){
				checkConfigFile(file.getContent(), what);
				executor = new SaveJemConfiguration(file);
			} else if (what.equalsIgnoreCase(ConfigKeys.AFFINITY)){
				checkAffinityPolicy(node, file.getContent());
				executor = new SaveAffinityPolicy(file);
			} else {
				executor = new SaveJemConfiguration(file);
			}
		} else {
			executor = new SaveJemConfiguration(file);
		}
		DistributedTaskExecutor<ConfigurationFile> task = new DistributedTaskExecutor<ConfigurationFile>(executor, getMember(node.getKey()));
		return task.getResult();
	}

	/**
	 * Checks if syntax of content is correct.
	 * @param content content of configuration file
	 * @param what type of config file
	 * @return always true
	 * @throws ServiceMessageException if any error parsing content occurs
	 */
	public Boolean checkConfigFile(String content, String what) throws ServiceMessageException {
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		try {
	        checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_CONFIGURATION));
        } catch (Exception e) {
        	LogAppl.getInstance().ignore(e.getMessage(), e);
        	checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION));
        }

		// creates the future task
		Callable<Boolean> executor = null;

		// checks if wants Hazelcast or JEM node configuration
		// by default is JEM node configuration
		if (what != null){
			if (what.equalsIgnoreCase(ConfigKeys.JEM_CONFIG)){
				executor = new CheckJemConfiguration(content);
			} else if (what.equalsIgnoreCase(ConfigKeys.JEM_ENV_CONF)){
				executor = new CheckJemEnvConfiguration(content);
			} else if (what.equalsIgnoreCase(ConfigKeys.HAZELCAST_CONFIG)){
				executor = new CheckHazelcastConfiguration(content);
			} else if (what.equalsIgnoreCase(ConfigKeys.DATASETS_RULES)){
				executor = new CheckDatasetsRules(content);
			} else {
				executor = new CheckJemConfiguration(content);
			}
		} else {
			executor = new CheckJemConfiguration(content);
		}
		DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(executor, getMember());
		return task.getResult();
	}

	
	/**
	 * Checks if syntax of affinity loader policy content is correct.
	 * @param node node where execute a future task  
	 * @param content type of affinity policy
	 * @return always true
	 * @throws ServiceMessageException if any error parsing content occurs
	 */
	public Result checkAffinityPolicy(NodeInfoBean node, String content) throws ServiceMessageException {
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_CONFIGURATION));
		
		DistributedTaskExecutor<Result> task = new DistributedTaskExecutor<Result>(new CheckAffinityPolicy(content), getMember(node.getKey()));
		return task.getResult();
	}

	/**
	 * Returns the configuration file for the environment
	 * 
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws ServiceMessageException if exception occurs
	 */
	public ConfigurationFile getEnvConfigFile(String what) throws ServiceMessageException {
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION));

		// creates the future task
		Callable<ConfigurationFile> executor = null;

		// checks if wants Hazelcast or JEM node configuration
		// by default is JEM node configuration
		if (what != null){
			if (what.equalsIgnoreCase(ConfigKeys.HAZELCAST_CONFIG)){
				executor = new GetHazelcastConfiguration();
			} else if (what.equalsIgnoreCase(ConfigKeys.JEM_ENV_CONF)){
				executor = new GetJemEnvConfiguration();
			} else if (what.equalsIgnoreCase(ConfigKeys.DATASETS_RULES)){
				executor = new GetDatasetsRules();				
			} else {
				executor = new GetJemEnvConfiguration();
			}
		} else {
			executor = new GetJemEnvConfiguration();
		}

		DistributedTaskExecutor<ConfigurationFile> task = new DistributedTaskExecutor<ConfigurationFile>(executor, getMember());
		return task.getResult();
	}
	
	/**
	 * Returns the configuration file for the environment after saving it
	 * 
	 * @param file configuration file to save 
	 * @param what type of configuration file to return
	 * @return Configuration new file container
	 * @throws ServiceMessageException if exception occurs
	 */
	public ConfigurationFile saveEnvConfigFile(ConfigurationFile file, String what) throws ServiceMessageException{
		// checks if the user is authorized to read configuration
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_CLUSTER_CONFIGURATION));

		// creates the future task
		Callable<ConfigurationFile> executor = null;

		// checks if wants Hazelcast or JEM node configuration
		// by default is JEM node configuration
		if (what != null){
			if (what.equalsIgnoreCase(ConfigKeys.HAZELCAST_CONFIG)){
				checkConfigFile(file.getContent(), what);
				executor = new SaveHazelcastConfiguration(file);
			} else if (what.equalsIgnoreCase(ConfigKeys.JEM_ENV_CONF)){
				checkConfigFile(file.getContent(), what);
				executor = new SaveJemEnvConfiguration(file);
			} else if (what.equalsIgnoreCase(ConfigKeys.DATASETS_RULES)){
				checkConfigFile(file.getContent(), what);
				executor = new SaveDatasetsRules(file);
			} else {
				executor = new SaveJemEnvConfiguration(file);
			}
		} else {
			executor = new SaveJemEnvConfiguration(file);
		}
		
		DistributedTaskExecutor<ConfigurationFile> task = new DistributedTaskExecutor<ConfigurationFile>(executor, getMember());
		return task.getResult();
	}
	
	/**
	 * Returns the top command result
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
	 * @throws ServiceMessageException if exception occurs
	 */

	public String top(NodeInfoBean node) throws ServiceMessageException {
		// checks if the user is authorized to performs commands
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_COMMANDS));

		DistributedTaskExecutor<String> task = new DistributedTaskExecutor<String>(new Top(), getMember(node.getKey()));
		return task.getResult();
	}

	/**
	 * Returns part of JEM node log
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
	 * @throws ServiceMessageException if exception occurs
	 */
	public String log(NodeInfoBean node) throws ServiceMessageException {
		// checks if the user is authorized to performs commands
		// if not, this method throws an exception
		checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_COMMANDS));

		DistributedTaskExecutor<String> task = new DistributedTaskExecutor<String>(new GetLog(), getMember(node.getKey()));
		return task.getResult();
	}

    
    /**
	 * Returns the HAZELCAST cluster status which is the list of all members.<br>
	 * This is a sampl output format:<br>
	 * <code>
	 *  Members [2] {
    	    Member [127.0.0.1]:5710 this
    	    Member [127.0.0.1]:5711 
    	}
	 * </code>
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
     * @throws ServiceMessageException if exception occurs
     */
    public String displayCluster(NodeInfoBean node) throws ServiceMessageException {
		// checks if the user is authorized to performs commands
		// if not, this method throws an exception
    	checkAuthorization(new StringPermission(Permissions.ADMINISTRATION_NODES_COMMANDS));

    	// gets hzelcast cluster
    	Cluster cluster =  getInstance().getCluster();
    	
    	// scans all members creating a stringbuilder
    	// with HC format
    	StringBuilder result = new StringBuilder("Members [").append(cluster.getMembers().size()).append("] {").append("\n");
    	for (Member member : cluster.getMembers()){
    		// adds dinamically the label "this"
    		// based on node passed as argument
    		String memberString = StringUtils.remove(member.toString(), " this");
    		if (node.getKey().equals(member.getUuid())){
    			memberString = memberString + " this";
    		}
    		result.append("    ").append(memberString).append("\n");
    	}
    	result.append("}\n");
    	return result.toString();
    }
	
}