/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.NodeInfoUtility;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.node.hazelcast.ExecutorServices;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.swarm.listeners.NodeListener;
import org.pepstock.jem.util.TimeUtils;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.PartitionGroupConfig.MemberGroupType;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

/**
 * Is the Main class used to handle the hazelcast instance of the swarm
 * environment and other object used to hanlde the routhing phase.
 * 
 * @author Simone "Busy" Businaro
 * 
 */
public class Swarm {
	
	private static final int MAX_ACTIVIE = 64;

	/**
	 * It represent the hazelcast configuration for the swarm environment
	 */
	private SwarmConfiguration activeConfiguration = null;

	/**
	 * The hazelcast instance for the swarm environment
	 */
	private HazelcastInstance swarmInstance = null;

	/**
	 * It's a listener for the ROUTING QUEUE.
	 * 
	 * @see org.pepstock.jem.node.hazelcast.Queues#ROUTING_QUEUE
	 */
	private RoutingQueueManager routingQueueManager = null;

	/**
	 * It's a listener for the OUTPUT QUEUE
	 * 
	 * @see org.pepstock.jem.node.hazelcast.Queues#OUTPUT_QUEUE
	 */
	private OutputQueueManager outputQueueManager = null;

	/**
	 * It's a listener for the SWARM NODES MAP
	 * 
	 * @see org.pepstock.jem.gwt.server.swarm.SwarmQueues#NODES_MAP
	 */
	private MapSwarmNodesManager mapSwarmNodesManager = null;

	/**
	 * It is a bean that describe this member of the cluster
	 */
	private NodeInfo nodeInfo = new NodeInfo();

	/**
	 * It is the status of the node.
	 */
	private Status status = Status.DRAINED;

	/**
	 * Constructor that initialized the MAIN instance
	 */
	public Swarm() {
		
	}

	/**
	 * 
	 * @return the SwarmConfiguration that represent the the hazelcast
	 *         configuration for the swarm environment
	 */
	public SwarmConfiguration getActiveConfiguration() {
		IMap<String, SwarmConfiguration> routingConfigMap = Main.getHazelcast().getMap(Queues.ROUTING_CONFIG_MAP);
		// if there is no routing configuration insert default one, it will
		// happen the first time
		if (!routingConfigMap.containsKey(SwarmConfiguration.DEFAULT_NAME)) {
			activeConfiguration = new SwarmConfiguration();
			try {
				routingConfigMap.lock(SwarmConfiguration.DEFAULT_NAME);
				routingConfigMap.put(SwarmConfiguration.DEFAULT_NAME, activeConfiguration);
			} finally {
				routingConfigMap.unlock(SwarmConfiguration.DEFAULT_NAME);
			}
		} else {
			try {
				routingConfigMap.lock(SwarmConfiguration.DEFAULT_NAME);
				activeConfiguration = routingConfigMap.get(SwarmConfiguration.DEFAULT_NAME);
			} finally {
				routingConfigMap.unlock(SwarmConfiguration.DEFAULT_NAME);
			}
		}
		return activeConfiguration;
	}

	/**
	 * @return the hazelcast instance for the swarm environment. The swarm
	 *         environment is the environment composed by all the light members
	 *         of different environments
	 */
	public HazelcastInstance getHazelcastInstance() {
		return swarmInstance;
	}

	/**
	 * @return the RoutingQueueManager that is a listener for the routing map
	 *         and it also contains the logic to route job
	 */
	public RoutingQueueManager getRoutingQueueManager() {
		return routingQueueManager;
	}

	/**
	 * Start the hazelcast instance for the swarm environment only if this node
	 * is in the list of nodes present in the configuration and MainSwarm is in
	 * {@link Status#DRAINED}. The swarm environment is the environment composed
	 * by all the light members of different environments.
	 * @return true if node is started or if node is node part of the configured swarm nodes.
	 * @throws SwarmException 
	 * 
	 * @throws Exception
	 * 
	 */
	public boolean start() throws SwarmException {
		getActiveConfiguration();
		List<String> allowHosts = activeConfiguration.getNetworks();
		String localHost = Main.getHazelcast().getCluster().getLocalMember().getSocketAddress().getAddress().getHostAddress();
		
		if (allowHosts.contains(localHost) && getStatus().equals(Status.DRAINED)) {
			setStatus(Status.STARTING);
			if (activeConfiguration.isEnabled()) {
				LogAppl.getInstance().emit(SwarmNodeMessage.JEMO001I);
				// start up hazelcast swarm instace
				try {
					startUpHazelcast();
				} catch (SwarmException e) {
					setStatus(Status.DRAINED);
					throw e;
				}
				Cluster cluster = swarmInstance.getCluster();
				Member member = cluster.getLocalMember();
				// initialized nodeInputBean
				initNodeInfo(member);
				setStatus(Status.ACTIVE);
				// initialized RoutingManager
				routingQueueManager = new RoutingQueueManager();
				// initialized OutputQueueManager
				outputQueueManager = new OutputQueueManager();
				// initialized MapSwarmNodeManager
				mapSwarmNodesManager = new MapSwarmNodesManager();
				registerNode();
				Main.getNode().setSwarmNode(true);
				NodeInfoUtility.storeNodeInfo(Main.getNode());
				return true;
			} else {
				setStatus(Status.DRAINED);
				LogAppl.getInstance().emit(SwarmNodeMessage.JEMO002I);
				return false;
			}
		}
		return false;
	}

	/**
	 * Shut down the hazelcast instance for the swarm environment only if
	 * MainSwarm is in {@link Status#ACTIVE}. The swarm environment is the
	 * environment composed by all the light members of different environments.
	 * <p>
	 * Before shutting down check if a job is been routed or is been send back
	 * to the routing environment. If so wait last operations and then shut down
	 * the member.
	 * @return <code>true</code> if it was able to close the swarm
	 * 
	 */
	public boolean shutDown() {
		if (getStatus().equals(Status.ACTIVE)) {
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO003I);
			setStatus(Status.SHUTTING_DOWN);
			// wait until the end of last route job and the end of last
			// notify
			// output
			// job
			while (!getRoutingQueueManager().isRouteEnded() || !getOutputQueueManager().isNotifyOutputEnded()) {
				try {
					Thread.sleep(TimeUtils.SECOND);
				} catch (InterruptedException e) {
					LogAppl.getInstance().emit(SwarmNodeMessage.JEMO015E, e);
				}
			}
			swarmInstance.getLifecycleService().shutdown();
			swarmInstance = null;
			LogAppl.getInstance().emit(SwarmNodeMessage.JEMO004I);
			setStatus(Status.DRAINED);
			Main.getNode().setSwarmNode(false);
			NodeInfoUtility.storeNodeInfo(Main.getNode());
			return true;
		} 
		return false;
	}

	/**
	 * Start up the hazelcast for the swarm environment. The swarm environment
	 * is the environment made by all the ligth members of differents
	 * environment.
	 * 
	 * @throws Exception
	 */
	private void startUpHazelcast() throws SwarmException {
		Config cfg = new Config();
		cfg.getGroupConfig().setName(activeConfiguration.getGroupName());
		cfg.getGroupConfig().setPassword(activeConfiguration.getGroupPassword());

		// NETWORK COnfig
		NetworkConfig network = cfg.getNetworkConfig();
		network.setPort(activeConfiguration.getPort());
		network.setPortAutoIncrement(true);

		network.getInterfaces().setEnabled(true).addInterface(Main.getNetworkInterface().getAddress().getHostAddress());
		

		// JOIN
		JoinConfig join = network.getJoin();
		join.getTcpIpConfig().setEnabled(true);
		join.getMulticastConfig().setEnabled(false);
		if (activeConfiguration.getNetworks().isEmpty()) {
			throw new SwarmException(SwarmNodeMessage.JEMO020E);
		}
		for (String tcpNode : activeConfiguration.getNetworks()) {
			join.getTcpIpConfig().addMember(tcpNode);
		}

		// ALL OTHER
		cfg.getPartitionGroupConfig().setEnabled(true);
		cfg.getPartitionGroupConfig().setGroupType(MemberGroupType.HOST_AWARE);
		cfg.getProperties().setProperty("hazelcast.logging.type", "log4j");
		ExecutorConfig executor = new ExecutorConfig(ExecutorServices.SWARM, MAX_ACTIVIE);
		cfg.addExecutorConfig(executor);
		swarmInstance = Hazelcast.newHazelcastInstance(cfg);
		swarmInstance.getCluster().addMembershipListener(new NodeListener());
	}

	/**
	 * Register the NodeInfo inside the SWARM NODES MAP	 * 
	 * @see org.pepstock.jem.gwt.server.swarm.SwarmQueues#NODES_MAP
	 */
	private void registerNode() {
		IMap<String, NodeInfo> map = swarmInstance.getMap(Queues.SWARM_NODES_MAP);
		map.put(nodeInfo.getKey(), nodeInfo);
	}

	/**
	 * Initializes the nodeInfo that will be store in the SWARM NODES MAP
	 * 
	 * @see org.pepstock.jem.gwt.server.swarm.SwarmQueues#NODES_MAP
	 * @param member this hazelcast member of the swarm environment
	 */
	private void initNodeInfo(Member member) {
		// set uuid of member of hazelcast as key
		nodeInfo.setKey(member.getUuid());
		// set port and ip address
		InetSocketAddress address = member.getSocketAddress();
		nodeInfo.setPort(address.getPort());
		nodeInfo.setIpaddress(address.getAddress().getHostAddress());
		// sets label to be displayed by GRS
		nodeInfo.setLabel(nodeInfo.getIpaddress() + ":" + nodeInfo.getPort());
		// use JMX to extract current process id
		nodeInfo.setProcessId(ManagementFactory.getRuntimeMXBean().getName());
		// set hostname
		String hostname = StringUtils.substringAfter(nodeInfo.getProcessId(), "@");
		nodeInfo.setHostname(hostname);
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		nodeInfo.getNodeInfoBean().setSystemArchitecture(bean.getArch());
		nodeInfo.getNodeInfoBean().setAvailableProcessors(bean.getAvailableProcessors());
		nodeInfo.getNodeInfoBean().setSystemName(bean.getName());
		ExecutionEnvironment executionEnvironment = new ExecutionEnvironment();
		// the environment will be that of jem one not the swarm one.
		executionEnvironment.setEnvironment(Main.EXECUTION_ENVIRONMENT.getEnvironment());
		nodeInfo.setExecutionEnvironment(executionEnvironment);
		nodeInfo.setJemVersion(Main.getNode().getJemVersion());
	}

	/**
	 * @return the nodeInfo, a bean that describe this member of the cluster
	 */
	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	/**
	 * @return the mapSwarmNodesManager, the manager responsible for handle
	 *         event associated with the SWARM NODES MAP
	 * 
	 * @see org.pepstock.jem.gwt.server.swarm.SwarmQueues#NODES_MAP
	 */
	public MapSwarmNodesManager getMapSwarmNodesManager() {
		return mapSwarmNodesManager;
	}

	/**
	 * @return the outputQueueManager, the manager responsible for handle event
	 *         associated with the OUTPUT QUEUE relative to the routing
	 *         operation
	 * 
	 * @see org.pepstock.jem.node.hazelcast.Queues#OUTPUT_QUEUE
	 */
	public OutputQueueManager getOutputQueueManager() {
		return outputQueueManager;
	}

	/**
	 * @return the status of this node that belong to the Swarm Environment.
	 *         Status is used to see if specific operation can be done relative
	 *         to this node of the Swarm Environment.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set. Is the status of this node that belong
	 *            to the Swarm Environment. Status is used to see if specific
	 *            operation can be done relative to this node of the Swarm
	 *            Environment.
	 */
	public void setStatus(Status status) {
		this.status = status;
		nodeInfo.setStatus(status);
		if (swarmInstance != null && swarmInstance.getLifecycleService().isRunning()) {
			// update Map if node is present
			IMap<String, NodeInfo> membersMap = swarmInstance.getMap(Queues.SWARM_NODES_MAP);
			String key = nodeInfo.getKey();
			if (key != null && membersMap.containsKey(key)) {
				try {
					membersMap.lock(key);
					NodeInfo info = membersMap.get(key);
					info.setStatus(status);
					membersMap.replace(info.getKey(), info);
				} finally {
					membersMap.unlock(key);
				}
			}
		}
	}
}