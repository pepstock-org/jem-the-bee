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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.Key;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.grs.GrsMapConfigProvider;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.affinity.AffinityLoader;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.node.affinity.SystemInfo;
import org.pepstock.jem.node.configuration.AffinityFactory;
import org.pepstock.jem.node.configuration.CommonResourceDefinition;
import org.pepstock.jem.node.configuration.CommonResourcesDefinition;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.Configuration;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.configuration.Database;
import org.pepstock.jem.node.configuration.Factory;
import org.pepstock.jem.node.configuration.Java;
import org.pepstock.jem.node.configuration.JavaRuntimes;
import org.pepstock.jem.node.configuration.Listener;
import org.pepstock.jem.node.configuration.Node;
import org.pepstock.jem.node.configuration.Paths;
import org.pepstock.jem.node.configuration.StatsManager;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.node.events.JobLifecycleListener;
import org.pepstock.jem.node.executors.nodes.GetDataPaths;
import org.pepstock.jem.node.hazelcast.ConfigProvider;
import org.pepstock.jem.node.hazelcast.ExecutorServices;
import org.pepstock.jem.node.hazelcast.Locks;
import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.listeners.NodeListener;
import org.pepstock.jem.node.multicast.MulticastService;
import org.pepstock.jem.node.persistence.CommonResourcesMapManager;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.node.persistence.InputMapManager;
import org.pepstock.jem.node.persistence.MapManagersFactory;
import org.pepstock.jem.node.persistence.NodesMapManager;
import org.pepstock.jem.node.persistence.OutputMapManager;
import org.pepstock.jem.node.persistence.PreJobMapManager;
import org.pepstock.jem.node.persistence.RecoveryManager;
import org.pepstock.jem.node.persistence.RolesMapManager;
import org.pepstock.jem.node.persistence.RoutingConfigMapManager;
import org.pepstock.jem.node.persistence.RoutingMapManager;
import org.pepstock.jem.node.persistence.RunningMapManager;
import org.pepstock.jem.node.persistence.UserPreferencesMapManager;
import org.pepstock.jem.node.persistence.mongo.DBManager;
import org.pepstock.jem.node.persistence.mongo.MongoFactory;
import org.pepstock.jem.node.persistence.sql.DBPoolManager;
import org.pepstock.jem.node.persistence.sql.SQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.factories.DB2SQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.factories.DefaultSQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.factories.MySqlSQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.factories.OracleSQLContainerFactory;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourcesUtil;
import org.pepstock.jem.node.resources.definition.ResourceDefinitionException;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.node.security.UserPreference;
import org.pepstock.jem.node.security.keystore.KeysUtil;
import org.pepstock.jem.node.sgm.DataPaths;
import org.pepstock.jem.node.sgm.Path;
import org.pepstock.jem.node.stats.StatsMapConfigProvider;
import org.pepstock.jem.node.swarm.RoutedQueueMapConfigProvider;
import org.pepstock.jem.node.swarm.SwarmNodeMapConfigProvider;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.ClassLoaderUtil;
import org.pepstock.jem.util.ObjectAndClassPathContainer;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.VariableSubstituter;
import org.pepstock.jem.util.locks.ConcurrentLock;
import org.pepstock.jem.util.net.InterfacesUtils;
import org.xml.sax.SAXException;

import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.Member;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

/**
 * It starts up loading the configuration from a file, passed by a system
 * property. <br>
 * It initialize the Log4j, using LogAppl and a system property. <br>
 * It loads the execution environment, factories, paths and listeners (if they
 * are defined)<br>
 * Afterwards it initialize Hazelcast cluser checking is the configuration is
 * coherent with the Jem configuration, because the groupname of Hazelcast must
 * be the same of the defined environment in jem conf.<br>
 * 
 * 
 * @see org.pepstock.jem.log.LogAppl#getInstance()
 * @see org.pepstock.jem.node.configuration.ConfigKeys#JEM_CONFIG
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StartUpSystem {
	
	private static final int KB = 1000;
	
	private static final int DOUBLE = 2;
	
	private static final int ONETEN = 10;

	private static final Properties PROPERTIES = new Properties();

	private static final String SEMAPHORE = "org.pepstock.jem.semaphore";
	
	private static Configuration JEM_NODE_CONFIG = null;
	
	private static Configuration JEM_ENV_CONFIG = null;

	/**
	 * To avoid any instantiation
	 */
	private StartUpSystem() {
	}

	/**
	 * Main method which calls JEM configuration loading and then Hazelcast
	 * initialization.
	 * 
	 * @throws ConfigurationException if a configuration error, exception occurs
	 */
	public static void run() throws ConfigurationException {
		// load jem-node.xml configuration from node folder
		loadConfiguration();
		// load jem-env.xml configuration from gfs
		loadEnvConfiguration();
		// initialize Hazelcast
		startHazelcast();
		
		// gets the network interface to use
		try {
			Main.NETWORK_INTERFACE = InterfacesUtils.getInterface(Main.getHazelcast().getConfig());
			LogAppl.getInstance().emit(NodeMessage.JEMC273I, Main.NETWORK_INTERFACE);
		} catch (MessageException e) {
			throw new ConfigurationException(e);
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC012I, ManagementFactory.getRuntimeMXBean().getName());
		// start swarm
		try {
			Main.SWARM.start();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
		// start multicast service
		startMulticastService();
		
		// notifies all factories that
		// node is started
		for (JemFactory factory : Main.FACTORIES_LIST.values()){
			factory.afterNodeStarted();
		}

	}

	/**
	 * Start the JEM multicast service if hazelcast multicast is used. The
	 * service is used by web client node to get information about the running
	 * members
	 */
	private static void startMulticastService() {
		if (Main.getHazelcast().getConfig().getNetworkConfig().getJoin().getMulticastConfig().isEnabled()) {
			MulticastConfig hMulticastConfig = Main.getHazelcast().getConfig().getNetworkConfig().getJoin().getMulticastConfig();
			int multicastPorth = hMulticastConfig.getMulticastPort() + Main.INCREMENT_RMI_PORT;
			MulticastConfig multicastConfig = new MulticastConfig();
			multicastConfig.setEnabled(true);
			multicastConfig.setMulticastGroup(hMulticastConfig.getMulticastGroup());
			multicastConfig.setMulticastPort(multicastPorth);
			MulticastService multicastService = new MulticastService(multicastConfig);
			Main.setMulticastService(multicastService);
			Main.getMulticastService().start();
		}
	}

	/**
	 * starts up of Hazelcast
	 * 
	 * @throws ConfigurationException if a configuration error, exception occurs
	 */
	private static void startHazelcast() throws ConfigurationException {
		LogAppl.getInstance().emit(NodeMessage.JEMC002I);

		// reads Hazecast init parameter
		String hazelcastFile = System.getProperty(ConfigKeys.HAZELCAST_CONFIG);
		if (hazelcastFile == null) {

			LogAppl.getInstance().emit(NodeMessage.JEMC005E, ConfigKeys.HAZELCAST_CONFIG);
			throw new ConfigurationException(NodeMessage.JEMC005E.toMessage().getFormattedMessage(ConfigKeys.HAZELCAST_CONFIG));
		} else {
			// loads configuration file
			FileSystemXmlConfig config;
			FileInputStream fis = null;
			try {
				// XML syntax check
				// because Hazelcast continues if XMl error occurs
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				fis = new FileInputStream(hazelcastFile);
				builder.parse(fis);

				config = new FileSystemXmlConfig(hazelcastFile);
				Properties socketProperties = config.getNetworkConfig().getSocketInterceptorConfig().getProperties();
				for (Object currKey : socketProperties.keySet()) {
					String currValue = socketProperties.getProperty((String) currKey);
					currValue = substituteVariable(currValue);
					socketProperties.setProperty((String) currKey, currValue);
				}

				// checks if the password defined on hazelcast is the same of
				// the
				// constant defined (to avoid to managed all password, is
				// useless)
				String pwdFromConfig = config.getGroupConfig().getPassword();
				if (pwdFromConfig == null || "".equals(pwdFromConfig.trim())) {
					throw new ConfigurationException(NodeMessage.JEMC108E.toMessage().getFormattedMessage());
				}
				
				// defines here all read and writes locks 
				SemaphoreConfig semaphoreConfig1 = new SemaphoreConfig();
				semaphoreConfig1.setName(ConcurrentLock.NO_WAITING_PREFIX+"*");
				semaphoreConfig1.setInitialPermits(Integer.MAX_VALUE);
				semaphoreConfig1.setBackupCount(0);
				semaphoreConfig1.setAsyncBackupCount(0);
				SemaphoreConfig semaphoreConfig2 = new SemaphoreConfig();
				semaphoreConfig2.setName(ConcurrentLock.NO_ACCESSING_PREFIX+"*");
				semaphoreConfig2.setInitialPermits(1);
				semaphoreConfig2.setBackupCount(0);
				semaphoreConfig2.setAsyncBackupCount(0);

				
				config.addSemaphoreConfig(semaphoreConfig1);
				config.addSemaphoreConfig(semaphoreConfig2);
				
				// checks map store configuration
				addHazelcastConfig(config, Queues.INPUT_QUEUE, InputMapManager.getInstance());
				addHazelcastConfig(config, Queues.RUNNING_QUEUE, RunningMapManager.getInstance());	
				addHazelcastConfig(config, Queues.OUTPUT_QUEUE, OutputMapManager.getInstance());
				addHazelcastConfig(config, Queues.ROUTING_QUEUE, RoutingMapManager.getInstance());
				addHazelcastConfig(config, Queues.COMMON_RESOURCES_MAP, CommonResourcesMapManager.getInstance());
				addHazelcastConfig(config, Queues.ROLES_MAP, RolesMapManager.getInstance());
				addHazelcastConfig(config, Queues.ROUTING_CONFIG_MAP, RoutingConfigMapManager.getInstance());
				addHazelcastConfig(config, Queues.USER_PREFERENCES_MAP, UserPreferencesMapManager.getInstance());
				addHazelcastConfig(config, Queues.NODES_MAP, NodesMapManager.getInstance());
				
				addHazelcastConfig(config, Queues.JCL_CHECKING_QUEUE, PreJobMapManager.getInstance());

				addHazelcastConfig(config, Queues.SWARM_NODES_MAP, SwarmNodeMapConfigProvider.getInstance());
				addHazelcastConfig(config, Queues.ROUTED_QUEUE, RoutedQueueMapConfigProvider.getInstance());
				addHazelcastConfig(config, Queues.GRS_COUNTER_MUTEX_MAP, GrsMapConfigProvider.getInstance());
				addHazelcastConfig(config, Queues.STATS_MAP, StatsMapConfigProvider.getInstance());
				
				// saves HC config
				Main.setHazelcastConfig(config);

				// to avoid to loose data, sets Hazelcast shutdown hook disable
				// It must be set before to create Hazelcast Instance
				System.setProperty("hazelcast.shutdownhook.enabled", "false");

				Main.setHazelcast(Hazelcast.newHazelcastInstance(config));

				// creates a key anyway, even if couldn't be necessary, to avoid
				loadKey();
				
			} catch (MessageException e) {
				throw new ConfigurationException(e);
			} catch (FileNotFoundException e) {
				throw new ConfigurationException(e);
			} catch (ParserConfigurationException e) {
				throw new ConfigurationException(e);
			} catch (SAXException e) {
				throw new ConfigurationException(e);
			} catch (IOException e) {
				throw new ConfigurationException(e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}

		}
		ILock lock = Main.getHazelcast().getLock(Locks.STARTUP);
		try {
			lock.lock();
			// get the cluster adding a new listener and extract local member to
			// retrieve information
			Cluster cluster = Main.getHazelcast().getCluster();
			cluster.addMembershipListener(new NodeListener());

			Member member = cluster.getLocalMember();

			// load node information starting from member object
			try {
				NodeInfoUtility.loadNodeInfo(member, Main.getNode());
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC147E, e);
				throw new ConfigurationException(NodeMessage.JEMC147E.toMessage().getContent(), e);
			}
			NodeInfoUtility.storeNodeInfo(Main.getNode(), true);

			LogAppl.getInstance().emit(NodeMessage.JEMC030I, Main.getNode().getStatus());
			LogAppl.getInstance().emit(NodeMessage.JEMC003I, Main.getNode().getKey());

			// checks if the group name is the same of enviroment attributes
			// defined
			// in jem config file
			String group = Main.getHazelcast().getConfig().getGroupConfig().getName();
			if (!group.equalsIgnoreCase(Main.EXECUTION_ENVIRONMENT.getEnvironment())) {
				LogAppl.getInstance().emit(NodeMessage.JEMC010E, Main.EXECUTION_ENVIRONMENT.getEnvironment(), group);
				throw new ConfigurationException(NodeMessage.JEMC010E.toMessage().getFormattedMessage(Main.EXECUTION_ENVIRONMENT.getEnvironment(), group));
			}

			// check if the local member is the first of member list. if yes,
			// it's
			// the coordinator
			// remember that Hazelcast maintains the list the "cluster joining"
			// order
			Member local = cluster.getLocalMember();
			Member first = cluster.getMembers().iterator().next();

			// checks if is coordinator
			if (local.equals(first)) {
				Main.IS_COORDINATOR.set(true);

				// if there are some job listeners, then says it
				if (Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.hasListeners()) {
					LogAppl.getInstance().emit(NodeMessage.JEMC034I);
				}

			} else {
				// if local member is not the first, so it's not coordinator of
				// cluster
				Main.IS_COORDINATOR.set(false);
				// gets the datapaths to check if they are the same
				checkDataPaths();
			}
			checkIfEnoughMembers();
			// load data paths rules
			loadDatasetsRules(JEM_ENV_CONFIG);
			// loads affinities locking access file if scripti affintiy loader is defined
			loadDynamicAffinities();
			
			// load statistics manager after dataset rules
			// because it uses the path to store data
			loadStatisticsManager();
			
			// bring in memory the persisted queue
			loadQueues();
		
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * reads HC configuration and set the mapstore and enables the mapstore, overriding the HC config
	 * @param config HC configuration
	 * @param mapName map to check
	 * @param mapStore MapStore implementation to set it.
	 * @throws MessageException if a mandatory map is missing
	 */
	private static void addHazelcastConfig(Config config, String mapName, ConfigProvider provider) throws MessageException{
		MapConfig newMapConfig = provider.getMapConfig();
		if (newMapConfig != null){
			// get map config
			MapConfig mapConfig = config.findMapConfig(mapName);
			if (mapConfig != null && !"default".equalsIgnoreCase(mapConfig.getName())){
				throw new MessageException(NodeMessage.JEMC294E, mapName);
			}
			// if map has been configured
			config.addMapConfig(newMapConfig);
			return;
		}
		QueueConfig newQueueConfig = provider.getQueueConfig();
		if (newQueueConfig != null){
			// get queue config
			QueueConfig queueConfig = config.findQueueConfig(mapName);
			if (queueConfig != null && !"default".equalsIgnoreCase(queueConfig.getName())){
				throw new MessageException(NodeMessage.JEMC294E, mapName);
			}
			// if map has been configured
			config.addQueueConfig(newQueueConfig);
		}
	}
	
	/**
	 * Loads the unique simmetric key of JEM cluster
	 * @throws ConfigurationException if any error occurs creating or getting the key
	 */
	private static void loadKey() throws ConfigurationException{
		// creates a key anyway, even if couldn't be necessary, to avoid
		// synch
		// in Hazelcast
		Key key;
		try {
			key = KeysUtil.getSymmetricKey();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
		// saves the key
		ResourcesUtil.getInstance().setKey(key);
	}

	/**
	 * If NODES_MAP contain at least a node with status different from STARTING
	 * will return otherwise checks if the cluster have enough nodes to support
	 * the persisted queue in memory. If the nodes are not enough the cluster
	 * will wait for new joining nodes.
	 * 
	 * @throws ConfigurationException
	 */
	private static void checkIfEnoughMembers() throws ConfigurationException {
		// by default the number of permits are 0
		ISemaphore semaphore = Main.getHazelcast().getSemaphore(SEMAPHORE);
		// check if exists a node of the cluster with status different from
		// STARTING if so return.
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.STARTING);
		List<NodeInfo> nodesInfo = NodeInfoUtility.getNodesInfoByStatus(statusList, true);
		if (!nodesInfo.isEmpty()) {
			return;
		}

		// times 2 because in memory there will be a replication copy
		long queueSize = calculateQueueSize() * DOUBLE;
		// calculate the number of nodes (exclude light member)
		Cluster cluster = Main.getHazelcast().getCluster();
		int membersNumber = cluster.getMembers().size();
		MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
		long freeMemoryForNode = bean.getHeapMemoryUsage().getMax() - bean.getHeapMemoryUsage().getUsed();
		// we consider each has at this point the same max and used memory
		// or we could insert this information in nodeInfo ?
		long clusterFreMemory = freeMemoryForNode * membersNumber;
		// we consider clusterFreMemory enough if is grather than the queueSize
		// + 20%
		long neededMemory = queueSize + (queueSize / ONETEN) * DOUBLE;
		if (clusterFreMemory > neededMemory) {
			semaphore.release(membersNumber - 1);
			return;
		} else {
			LogAppl.getInstance().emit(NodeMessage.JEMC086W, clusterFreMemory / KB, neededMemory / KB);
			try {
				semaphore.acquire();
			} catch (Exception e) {
				throw new ConfigurationException(e);
			}
		}
	}

	/**
	 * 
	 * @return the size in byte of the persisted maps.
	 * @throws ConfigurationException
	 */
	private static long calculateQueueSize() throws ConfigurationException {
		try {
			long inputQueueSize = InputMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.INPUT_QUEUE, inputQueueSize / KB);
			long runningQueueSize = RunningMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.RUNNING_QUEUE, runningQueueSize / KB);

			long outputQueueSize = OutputMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.OUTPUT_QUEUE, outputQueueSize / KB);
			long routingQueueSize = RoutingMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.ROUTING_QUEUE, routingQueueSize / KB);
			long rolesSize = RolesMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.ROLES_MAP, rolesSize / KB);
			long resourcesSize = CommonResourcesMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.COMMON_RESOURCES_MAP, resourcesSize / KB);
			long checkingQueueSize = PreJobMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.JCL_CHECKING_QUEUE, checkingQueueSize / KB);
			long routingConfSize = RoutingConfigMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.ROUTING_CONFIG_MAP, routingConfSize / KB);
			long userPrefSize = UserPreferencesMapManager.getInstance().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, Queues.USER_PREFERENCES_MAP, userPrefSize / KB);

			return inputQueueSize + runningQueueSize + outputQueueSize + routingQueueSize + rolesSize + resourcesSize + checkingQueueSize + routingConfSize + userPrefSize;
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * Load the persisted queues by calling them for the first time.
	 * 
	 * @throws ConfigurationException
	 */
	private static void loadQueues() throws ConfigurationException {
		// loads all keys for INPUT, RUNNING, OUTPUT, ROUTING and
		// CommonResources. this
		// call of method
		// will schedule the call on persistent manager
		IMap<String, Job> inputQueue = Main.getHazelcast().getMap(Queues.INPUT_QUEUE);
		inputQueue.size();

		IMap<String, Job> outputQueue = Main.getHazelcast().getMap(Queues.OUTPUT_QUEUE);
		outputQueue.size();

		// this code removes all jobs in running queue if the node is
		// coordinator
		// this is necessary if the cluster crashed due to a failure
		if (Main.IS_COORDINATOR.get()) {
			IMap<String, Job> runningQueue = Main.getHazelcast().getMap(Queues.RUNNING_QUEUE);

			Lock lock = Main.getHazelcast().getLock(Locks.RUNNING_QUEUE);
			boolean isLock = false;
			try {
				isLock = lock.tryLock(Locks.LOCK_TIMEOUT, TimeUnit.SECONDS);
				if (!runningQueue.isEmpty()) {
					for (Job job : runningQueue.values()) {
						org.pepstock.jem.Result result = new org.pepstock.jem.Result();
						result.setReturnCode(org.pepstock.jem.Result.FATAL);
						result.setExceptionMessage("Node is crashed during job was executing");
						job.setResult(result);
						job.setEndedTime(new Date());
						job.setRunningStatus(Job.NONE);
						LogAppl.getInstance().emit(NodeMessage.JEMC190W, job.getName(), job.getId());
						runningQueue.remove(job.getId());
						outputQueue.putIfAbsent(job.getId(), job);
					}
				}
			} catch (Exception e) {
				throw new ConfigurationException(e);
			} finally {
				if (isLock) {
					lock.unlock();
				}
			}
			// Clean up of nodes map store
			NodeInfoUtility.checkAndCleanMapStore();
		}

		IMap<String, Job> routingQueue = Main.getHazelcast().getMap(Queues.ROUTING_QUEUE);
		routingQueue.size();
		
		// starts reccovery manager
		RecoveryManager.getInstance();

		IMap<String, Resource> resourceMap = Main.getHazelcast().getMap(Queues.COMMON_RESOURCES_MAP);
		resourceMap.size();

		IMap<String, Role> rolesMap = Main.getHazelcast().getMap(Queues.ROLES_MAP);
		rolesMap.size();

		IMap<String, SwarmConfiguration> routingConfigMap = Main.getHazelcast().getMap(Queues.ROUTING_CONFIG_MAP);
		routingConfigMap.size();

		// if map is emtpy, a DEFAULT routing config will be added
		if (routingConfigMap.isEmpty()) {
			routingConfigMap.put(SwarmConfiguration.DEFAULT_NAME, new SwarmConfiguration());
		}

		IMap<String, Map<String, UserPreference>> userPreferencesMap = Main.getHazelcast().getMap(Queues.USER_PREFERENCES_MAP);
		userPreferencesMap.size();
			
		// loads all jobs in check queue
		try {
			PreJobMapManager.getInstance().loadAll();
		} catch (MessageException e) {
			throw new ConfigurationException(e);
		}
	}

	/**
	 * loads the environment configuration from configuration file, present in
	 * the gfs, which is defined by a system property.
	 * 
	 * @throws ConfigurationException
	 */
	private static void loadEnvConfiguration() throws ConfigurationException {
		// loads configuration file from node folder.
		// if doesn't exist, exception
		String configFile = System.getProperty(ConfigKeys.JEM_ENV_CONF);
		if (configFile == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC005E, ConfigKeys.JEM_ENV_CONF);
			throw new ConfigurationException(NodeMessage.JEMC005E.toMessage().getFormattedMessage(ConfigKeys.JEM_CONFIG));
		}
		File fileConfig = new File(configFile);
		String xmlConfig = null;
		try {
			xmlConfig = FileUtils.readFileToString(fileConfig, CharSet.DEFAULT_CHARSET_NAME);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC006E);
			throw new ConfigurationException(NodeMessage.JEMC006E.toMessage().getContent(), e);
		}
		PROPERTIES.setProperty(ConfigKeys.JEM_ENV_CONF_FOLDER, FilenameUtils.normalize(fileConfig.getParent(), true));
		
		JEM_ENV_CONFIG = Configuration.unmarshall(xmlConfig);
		LogAppl.getInstance().emit(NodeMessage.JEMC008I, configFile);
		
		loadDatabaseManagers();
		loadNode();
		loadFactories();
		loadListeners();
		loadResourceConfigurations();

	}

	/**
	 * loads the configuration from configuration file, which is defined by a
	 * system property.
	 * 
	 * @see org.pepstock.jem.node.configuration.ConfigKeys#JEM_CONFIG
	 * @see org.pepstock.jem.node.Main#EXECUTION_ENVIRONMENT
	 * @see org.pepstock.jem.node.Main#OUTPUT_SYSTEM
	 * @throws ConfigurationException if a configuration error, exception occurs
	 */
	private static void loadConfiguration() throws ConfigurationException {
		// we set this environment variable as system properties
		handleEnvironmentVariable(ConfigKeys.JEM_NODE, ConfigKeys.JEM_HOME, ConfigKeys.JEM_ENVIRONMENT);
		LogAppl.getInstance().emit(NodeMessage.JEMC192I, System.getProperty(ConfigKeys.JAVA_VERSION), System.getProperty(ConfigKeys.JAVA_HOME), System.getProperty(ConfigKeys.JAVA_VENDOR));
		LogAppl.getInstance().emit(NodeMessage.JEMC001I);
		// loads JVM properties to Properties instance
		// to be able to substitute variables in configuration
		PROPERTIES.putAll(System.getProperties());
		PROPERTIES.putAll(System.getenv());

		// loads configuration file from node folder.
		// if doesn't exist, exception
		String configFile = System.getProperty(ConfigKeys.JEM_CONFIG);
		if (configFile == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC005E, ConfigKeys.JEM_CONFIG);
			throw new ConfigurationException(NodeMessage.JEMC005E.toMessage().getFormattedMessage(ConfigKeys.JEM_CONFIG));
		}
		String xmlConfig=null;
		try {
			xmlConfig = FileUtils.readFileToString(new File(configFile), CharSet.DEFAULT_CHARSET_NAME);
		} catch (IOException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC006E);
			throw new ConfigurationException(NodeMessage.JEMC006E.toMessage().getContent(), e);
		}

		JEM_NODE_CONFIG = Configuration.unmarshall(xmlConfig);
		LogAppl.getInstance().emit(NodeMessage.JEMC008I, configFile);

		loadPaths();
		loadJavaRuntimes();
		
		PROPERTIES.putAll(System.getProperties());
		loadExecutionEnvironment();
	}
	
	/**
	 * Loads the JVM to use and installed in the same machine of the node.
	 * This can be used by JCL from the job.
	 * @throws ConfigurationException if any errors occurs
	 */
	private static void loadJavaRuntimes()  throws ConfigurationException {
		// gets java runtimes configuration
		JavaRuntimes runtimes = JEM_NODE_CONFIG.getJavaRuntimes();
		// if there is		
		if (runtimes != null){
			// gets the affinitity
			// because it adds automatically the JVM tag names as affinity of this node
			String affinities = JEM_NODE_CONFIG.getExecutionEnviroment().getAffinity();
			boolean updated = false;
			// scans all java configuration
			for (Java java : runtimes.getJavas()){
				// creates the file
				File file = new File(java.getPath());
				// if file exists and is a folder ok,
				// otherwise this is a wrong directory 
				if (file.exists() && file.isDirectory()){
					// checks if some static affinities are set
					if (affinities != null){
						// if yes, it adds the JVM tag name
						affinities = affinities + Jcl.AFFINITY_SEPARATOR + java.getName();
					} else {
						// if no, sets as affinity the JVM name
						affinities = java.getName();
					}
					updated = true;
					// normalized the folder for the OS system where JEM node is running
					String normalizedFolder = FilenameUtils.normalizeNoEndSeparator(java.getPath());
					// stores this new JVM to the collection
					Main.getJavaRuntimes().put(java.getName(), normalizedFolder);
					// if the JVM is set as default
					if (java.isDefault() && Main.getDefaultJavaRuntime() == null){
						// sets default and writes a message
						Main.setDefaultJavaRuntime(normalizedFolder);
						LogAppl.getInstance().emit(NodeMessage.JEMC291I, java.getName(), normalizedFolder);
					} else if (java.isDefault() && Main.getDefaultJavaRuntime() != null){
						// the default is already set!
						// ignore the default and writes a warning
						LogAppl.getInstance().emit(NodeMessage.JEMC293W, java.getName());
						// notifies that there is a JVM definition
						LogAppl.getInstance().emit(NodeMessage.JEMC290I, java.getName(), normalizedFolder);	
					} else {
						// notifies that there is a JVM definition
						LogAppl.getInstance().emit(NodeMessage.JEMC290I, java.getName(), normalizedFolder);	
					}
				} else {
					// if java home doesn't exist, exception
					LogAppl.getInstance().emit(NodeMessage.JEMC292E, java.getName(), file.getAbsolutePath());
					throw new ConfigurationException(NodeMessage.JEMC292E.toMessage().getFormattedMessage(java.getName(), file.getAbsolutePath()));
				}
			}
			// if the affinities has been updated
			// stores the new value
			if (updated){
				JEM_NODE_CONFIG.getExecutionEnviroment().setAffinity(affinities);
			}
		}
	}

	/**
	 * load the paths of the jem configuration checking if they are configured
	 * 
	 * @param conf the jem node config
	 * @param substituter a VariableSubstituter containing all the System
	 *            properties
	 * @throws ConfigurationException if some error occurs
	 */
	private static void loadPaths() throws ConfigurationException {
		// load paths, checking if they are configured. If not, exception occurs
		Paths paths = JEM_NODE_CONFIG.getPaths();
		if (paths == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.PATHS_ELEMENT);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.PATHS_ELEMENT));
		}

		// load Paths
		DataPaths dataPath = paths.getData();
		
		String outputPath = paths.getOutput();
		String binaryPath = paths.getBinary();
		String classpathPath = paths.getClasspath();
		String libraryPath = paths.getLibrary();
		String persistencePath = paths.getPersistence();
		String sourcetPath = paths.getSource();

		// check if outputPath is not null. if not, exception occurs
		if (outputPath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_OUTPUT_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_OUTPUT_PATH_NAME));
		}
		// check if dataPath is not null. if not, exception occurs
		if (dataPath == null || dataPath.getPaths().isEmpty()) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.DATA_ELEMENT);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.DATA_ELEMENT));
		}
		// check if binaryPath is not null. if not, exception occurs
		if (binaryPath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_BINARY_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_BINARY_PATH_NAME));
		}
		// check if classpathPath is not null. if not, exception occurs
		if (classpathPath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_CLASSPATH_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_CLASSPATH_PATH_NAME));
		}
		// check if libraryPath is not null. if not, exception occurs
		if (libraryPath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_LIBRARY_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_LIBRARY_PATH_NAME));
		}
		// check if persistencePath is not null. if not, exception occurs
		if (persistencePath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_PERSISTENCE_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_PERSISTENCE_PATH_NAME));
		}
		// check if persistencePath is not null. if not, exception occurs
		if (sourcetPath == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC039E, ConfigKeys.JEM_SOURCE_PATH_NAME);
			throw new ConfigurationException(NodeMessage.JEMC039E.toMessage().getFormattedMessage(ConfigKeys.JEM_SOURCE_PATH_NAME));
		}

		// substitutes variables if present, on data path
		// adds this value in properties of JVM
		for (Path p: dataPath.getPaths()){
			p.setContent(normalizePath(substituteVariable(p.getContent())));
		}
		// loads all storage groups and checks if exist
		Main.DATA_PATHS_MANAGER.setDataPaths(dataPath);
		// substitutes variables if present, on output path
		// adds this value in properties of JVM
		outputPath = substituteVariable(outputPath);
		System.getProperties().setProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME, normalizePath(outputPath));

		// substitutes variables if present, on binary Path
		// adds this value in properties of JVM
		binaryPath = substituteVariable(binaryPath);
		System.getProperties().setProperty(ConfigKeys.JEM_BINARY_PATH_NAME, normalizePath(binaryPath));
		// substitutes variables if present, on classpathPath
		// adds this value in properties of JVM
		classpathPath = substituteVariable(classpathPath);
		System.getProperties().setProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME, normalizePath(classpathPath));

		// substitutes variables if present, on libraryPath
		// adds this value in properties of JVM
		System.getProperties().setProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME, normalizePath(libraryPath));

		// substitutes variables if present, on persistencePath
		// adds this value in properties of JVM
		persistencePath = substituteVariable(persistencePath);
		System.getProperties().setProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME, normalizePath(persistencePath));

		// substitutes variables if present, on sourcePath
		// adds this value in properties of JVM
		sourcetPath = substituteVariable(sourcetPath);
		System.getProperties().setProperty(ConfigKeys.JEM_SOURCE_PATH_NAME, normalizePath(sourcetPath));

		// load ths path values on a static reference on Main class which
		// creates all necessary files and directories
		Main.setOutputSystem(new OutputSystem(outputPath, persistencePath));

	}

	/**
	 * 
	 * @param string the string that may contains the variable to substitute
	 *            with the values contained in the substituter
	 * @return the value of the param string eventually substituted
	 */
	private static String substituteVariable(String string) {
		return VariableSubstituter.substitute(string, PROPERTIES);
	}

	/**
	 * load the listeners of the jem configuration checking if they are
	 * configured
	 * 
	 * @param conf the jem node config
	 * @param substituter a VariableSubstituter containing all the System
	 *            properties
	 * @throws ConfigurationException if some error occurs
	 */
	private static void loadListeners() throws ConfigurationException {
		// load listeners, checking if they are configured. If not, they are
		// optional, so go ahead
		List<Listener> listeners = JEM_ENV_CONFIG.getListeners();
		if (listeners != null && !listeners.isEmpty()) {
			// for all listener checking which have the right className. If
			// not, execption occurs, otherwise it's loaded
			for (Listener listener : listeners) {
				if (listener.getClassName() != null) {
					String className = listener.getClassName();
					try {
						// load by Class.forName of listener
						ObjectAndClassPathContainer oacp = ClassLoaderUtil.loadAbstractPlugin(listener, PROPERTIES);
						Object objectListener = oacp.getObject();

						// check if it's a JobLifecycleListener. if not,
						// exception occurs. if yes, it's loaded on a
						// EventListenerManager
						if (objectListener instanceof JobLifecycleListener) {
							JobLifecycleListener lister = (JobLifecycleListener) objectListener;
							Main.JOB_LIFECYCLE_LISTENERS_SYSTEM.addListener(JobLifecycleListener.class, lister);
							// gets properties defined. If not empty,
							// substitutes the value of property with
							// variables
							Properties propsOfListener = listener.getProperties();
							if (propsOfListener != null) {
								if (!propsOfListener.isEmpty()) {
									// scans all properties
									for (Enumeration<Object> e = propsOfListener.keys(); e.hasMoreElements();) {
										// gets key and value
										String key = e.nextElement().toString();
										String value = propsOfListener.getProperty(key);
										// substitutes variables if present
										// and sets new value for the key
										propsOfListener.setProperty(key, substituteVariable(value));
									}
								}
							} else {
								// creates however an empty collection to
								// avoid null pointer
								propsOfListener = new Properties();
							}
							// initialize the listener passing parameters
							// properties
							lister.init(propsOfListener);
							LogAppl.getInstance().emit(NodeMessage.JEMC037I, className);
						} else {
							LogAppl.getInstance().emit(NodeMessage.JEMC036E, className);
							throw new ConfigurationException(NodeMessage.JEMC036E.toMessage().getFormattedMessage(className));
						}
					} catch (Exception e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC031E, e, className);
						throw new ConfigurationException(NodeMessage.JEMC031E.toMessage().getFormattedMessage(className));
					}
					// in this case the class name is null so ignore,
					// emitting a warning
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC038W, ConfigKeys.LISTENER_ALIAS, listener.toString());
				}
			}
		}
	}

	/**
	 * Load the custom resource definitions of the jem configuration checking if
	 * they are configured
	 * 
	 * @param conf the jem node config
	 * @param substituter a VariableSubstituter containing all the System
	 *            properties
	 * @throws ConfigurationException if some error occurs.
	 */
	private static void loadResourceConfigurations() throws ConfigurationException {
	
		// load custom resource definitions, checking if they are configured. If
		// not, they are
		// optional, so go ahead
		List<CommonResourceDefinition> resourceDefinitions = JEM_ENV_CONFIG.getResourceDefinitions();
		if (resourceDefinitions != null && !resourceDefinitions.isEmpty()) {
			// for all resource definitions checking which have the right
			// className. If
			// not, exception occurs, otherwise it's loaded
			for (CommonResourceDefinition resourceDefinition : resourceDefinitions) {
				if (resourceDefinition.getClassName() != null || resourceDefinition instanceof CommonResourcesDefinition) {
					try {
						Main.RESOURCE_DEFINITION_MANAGER.loadResourceDefinition(resourceDefinition, PROPERTIES);
					} catch (ResourceDefinitionException e) {
						throw new ConfigurationException(e);
					}
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC038W, ConfigKeys.RESOURCE_DEFINITION_ALIAS, resourceDefinition.toString());
				}
			}
		}
	}

	/**
	 * 
	 * @param conf
	 * @throws Exception
	 */
	private static void loadDatabaseManagers() throws ConfigurationException {
		Database database = JEM_ENV_CONFIG.getDatabase();
		if (database == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.DATABASE_ELEMENT);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.DATABASE_ELEMENT));
		}
		// try to substitute vars in URL
		String url = substituteVariable(database.getUrl());
		database.setUrl(url);
		LogAppl.getInstance().emit(NodeMessage.JEMC193I, url);

		// try to substitute vars in user
		String user = substituteVariable(database.getUser());
		database.setUser(user);

		// checks if the database is MONGO
		if (database.getUrl().startsWith(MongoFactory.DATABASE_TYPE)){
			// creates URI for MONGO for connection
			UriBuilder builder = UriBuilder.fromUri(database.getUrl());
			// checks all properteis
			if (database.getProperties() != null && !database.getProperties().isEmpty()){
				// scans proprties 
				for (Entry<Object, Object> entry : database.getProperties().entrySet()){
					// adds to URI as querystring
					builder.queryParam(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			// checks teh user and passwrod are passed
			if (database.getUser() != null && database.getPassword() != null &&
					!"".equalsIgnoreCase(database.getUser().trim()) && !"".equalsIgnoreCase(database.getPassword().trim())){
				// if passed, adds teh user info information to URI
				builder.userInfo(database.getUser()+":"+database.getPassword());
			}
			// creates teh MONGO connection string using the URI created
			// setting a custom MONGO options that sets a server conn timeout.
			// this timeout is mandatory to check if the credentials are ok
			// because MONGO client doen't throw any exception
			MongoClientURI clientUri = new MongoClientURI(builder.build().toString(), 
					MongoClientOptions.builder().serverSelectionTimeout(DBManager.SERVER_SELECTION_TIMEOUT));
			// initializes DB manager for mongo
			// and all mapstores
			try {
				DBManager.createInstance(clientUri);
				MapManagersFactory.createMapManagers();
			} catch (UnknownHostException e) {
				throw new ConfigurationException(NodeMessage.JEMC165E.toMessage().getFormattedMessage(database.getUrl()), e);
			} catch (DatabaseException e) {
				throw new ConfigurationException(NodeMessage.JEMC165E.toMessage().getFormattedMessage(database.getUrl()), e);
			}
		} else {
			// initializes DB manager for SQL db
			loadSQLDatabase(database);
		}
		// initializes all map stores.
		try {
			MapManagersFactory.initAll();
			// if JEM env configuration sets the eviction
			// for output map, sets here
			if (JEM_ENV_CONFIG.getEviction() != null){
				OutputMapManager.getInstance().setEviction(JEM_ENV_CONFIG.getEviction());
			}
		} catch (DatabaseException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC167E, e);
			throw new ConfigurationException(NodeMessage.JEMC167E.toMessage().getFormattedMessage());
		}
	}
	
	/**
	 * Creates the DB manager for SQL database using the DATABASE configuration
	 * @param database JEM configuration of database
	 * @throws ConfigurationException if any error occurs
	 */
	private static void loadSQLDatabase(Database database) throws ConfigurationException{
		String dbType = null;
		try {
			URI url1 = new URI(database.getUrl());
			URI myURL = new URI(url1.getSchemeSpecificPart());
			dbType = myURL.getScheme();
		} catch (URISyntaxException e2) {
			LogAppl.getInstance().emit(NodeMessage.JEMC166E, e2, database.getUrl());
			throw new ConfigurationException(NodeMessage.JEMC166E.toMessage().getFormattedMessage(database.getUrl()));
		}

		SQLContainerFactory engine = null;
		if (dbType.equals(MySqlSQLContainerFactory.DATABASE_TYPE)) {
			engine = new MySqlSQLContainerFactory();
		} else if (dbType.equals(OracleSQLContainerFactory.DATABASE_TYPE)) {
			engine = new OracleSQLContainerFactory();
		} else if (dbType.equals(DB2SQLContainerFactory.DATABASE_TYPE)) {
			engine = new DB2SQLContainerFactory();
		} else {
			engine = new DefaultSQLContainerFactory();
		}
		// load JobManager for input, output, routing
		try {
			DBPoolManager.getInstance().setDriver(database.getDriver());
			DBPoolManager.getInstance().setUrl(database.getUrl());
			DBPoolManager.getInstance().setUser(database.getUser());
			DBPoolManager.getInstance().setPassword(database.getPassword());
			DBPoolManager.getInstance().setProperties(database.getProperties());
			DBPoolManager.getInstance().setKeepAliveConnectionSQL(engine.getKeepAliveConnectionSQL());
			DBPoolManager.getInstance().init();

			MapManagersFactory.createMapManagers(engine);
		} catch (SQLException e) {
			throw new ConfigurationException(NodeMessage.JEMC165E.toMessage().getFormattedMessage(database.getUrl()), e);
		}
	}
	

	/**
	 * load the factories of the jem configuration checking if they are
	 * configured
	 * 
	 * @param conf the jem node config
	 * @param substituter a VariableSubstituter containing all the System
	 *            properties
	 * @throws ConfigurationException if some error occurs
	 */
	private static void loadFactories() throws ConfigurationException {
		// load factories, checking if they are configured. If not, exception
		// occurs
		List<Factory> factories = JEM_ENV_CONFIG.getFactories();
		if (factories == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.FACTORIES_ELEMENT);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.FACTORIES_ELEMENT));
		}
		if (factories.isEmpty()) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.FACTORY_ALIAS);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.FACTORY_ALIAS));
		}

		// for all factories checking which have the right className. If not,
		// execption occurs, otherwise it's loaded
		for (Factory factory : factories) {
			if (factory.getClassName() != null) {
				String className = factory.getClassName();
				try {
					ObjectAndClassPathContainer oacp = null;
					// gets teh link to another classloader
					String classLoaderLink = factory.getClassLoader();
					// if not null and already loaded
					if (classLoaderLink != null && Main.FACTORIES_LIST.containsKey(classLoaderLink)){
						// gets classloader from 
						// the factory already loaded
						JemFactory factoryLoaded = Main.FACTORIES_LIST.get(classLoaderLink);
						oacp = ClassLoaderUtil.loadAbstractPlugin(factory, PROPERTIES, factoryLoaded.getClass().getClassLoader());
					} else {
						// loads the factory
						oacp = ClassLoaderUtil.loadAbstractPlugin(factory, PROPERTIES);
					}
					Object objectFactory = oacp.getObject();

					// check if it's a JemFactory. if not, exception occurs. if
					// yes, it's loaded on a map
					// with all factory. Remember the the key of map is getType
					// result, put to lowercase to ignore case
					// during the search by key
					if (objectFactory instanceof JemFactory) {
						JemFactory jf = (JemFactory) objectFactory;

						// sets the type if has been specified into config file
						if (factory.getType() != null && !"".equalsIgnoreCase(factory.getType().trim())){
							jf.setType(factory.getType());
						}

						// sets the type description if has been specified into config file
						if (factory.getDescription() != null && !"".equalsIgnoreCase(factory.getDescription().trim())){
							jf.setTypeDescription(factory.getDescription());
						}
						
						// gets properties defined. If not empty, substitutes
						// the value of property with variables
						Properties propsOfFactory = factory.getProperties();
						if (propsOfFactory != null) {
							if (!propsOfFactory.isEmpty()) {
								// scans all properties
								for (Enumeration<Object> e = propsOfFactory.keys(); e.hasMoreElements();) {
									// gets key and value
									String key = e.nextElement().toString();
									String value = propsOfFactory.getProperty(key);
									// substitutes variables if present
									// and sets new value for the key
									propsOfFactory.setProperty(key, substituteVariable(value));
								}
							}
						} else {
							// creates however an emtpy collection to avoid null
							// pointer
							propsOfFactory = new Properties();
						}
						
						jf.setClassPath(oacp.getClassPath());
						// initializes the factory with properties defined
						// and puts in the list if everything went good
						jf.init(propsOfFactory);

						Main.FACTORIES_LIST.put(jf.getType().toLowerCase(), jf);

						LogAppl.getInstance().emit(NodeMessage.JEMC032I, className, jf.getType());
					} else {
						LogAppl.getInstance().emit(NodeMessage.JEMC040E, className);
					}

				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC031E, e, className);
				}
				// in this case the class name is null so ignore, emitting a
				// warning
			} else {
				LogAppl.getInstance().emit(NodeMessage.JEMC038W, ConfigKeys.FACTORY_ALIAS, factory.toString());
			}
		}
	}

	/**
	 * load the execution-environment of the jem configuration
	 * 
	 * @param conf the jem node config
	 * @param substituter a VariableSubstituter containing all the System
	 *            properties
	 * @throws ConfigurationException if some error occurs
	 */
	private static void loadExecutionEnvironment() throws ConfigurationException {
		// checks if ExecutionEnvironment is configured
		if (JEM_NODE_CONFIG.getExecutionEnviroment() == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.EXECUTION_ENVIRONMENT_ALIAS);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.EXECUTION_ENVIRONMENT_ALIAS));
		}

		// environment is mandatory. must be not null
		if (JEM_NODE_CONFIG.getExecutionEnviroment().getEnvironment() == null) {
			LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.ENVIRONMENT);
			throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.ENVIRONMENT));
		}

		// domain is optional. if null set default value
		if (JEM_NODE_CONFIG.getExecutionEnviroment().getDomain() == null) {
			JEM_NODE_CONFIG.getExecutionEnviroment().setDomain(Jcl.DEFAULT_DOMAIN);
		}

		// affinity is optional. if null, set default value
		if (JEM_NODE_CONFIG.getExecutionEnviroment().getAffinity() == null) {
			JEM_NODE_CONFIG.getExecutionEnviroment().setAffinity(Jcl.DEFAULT_AFFINITY);
		}

		// gets environment and substitute variables if present
		String environment = JEM_NODE_CONFIG.getExecutionEnviroment().getEnvironment();
		JEM_NODE_CONFIG.getExecutionEnviroment().setEnvironment(substituteVariable(environment));
		// loads environment on shared object
		Main.EXECUTION_ENVIRONMENT.setEnvironment(JEM_NODE_CONFIG.getExecutionEnviroment().getEnvironment());

		// gets domain and substitute variables if present
		String domain = JEM_NODE_CONFIG.getExecutionEnviroment().getDomain();
		JEM_NODE_CONFIG.getExecutionEnviroment().setDomain(substituteVariable(domain));
		// loads domain on shared object
		Main.EXECUTION_ENVIRONMENT.setDomain(JEM_NODE_CONFIG.getExecutionEnviroment().getDomain());

		// parallel jobs is optional. if null, set default value
		if (JEM_NODE_CONFIG.getExecutionEnviroment().getParallelJobs() == null) {
			// parallel jobs not set so uses the default
			Main.EXECUTION_ENVIRONMENT.setParallelJobs(org.pepstock.jem.node.ExecutionEnvironment.DEFAULT_PARALLEL_JOBS);
		} else {
			int value = Parser.parseInt(JEM_NODE_CONFIG.getExecutionEnviroment().getParallelJobs(), Integer.MIN_VALUE);
			if (value == Integer.MIN_VALUE) {
				// not a number
				value = org.pepstock.jem.node.ExecutionEnvironment.DEFAULT_PARALLEL_JOBS;
				LogAppl.getInstance().emit(NodeMessage.JEMC209W, JEM_NODE_CONFIG.getExecutionEnviroment().getParallelJobs());
			} else if (value < org.pepstock.jem.node.ExecutionEnvironment.MINIMUM_PARALLEL_JOBS) {
				// too low
				value = org.pepstock.jem.node.ExecutionEnvironment.DEFAULT_PARALLEL_JOBS;
				LogAppl.getInstance().emit(NodeMessage.JEMC211W, JEM_NODE_CONFIG.getExecutionEnviroment().getParallelJobs(), value);
			} else if (value >= org.pepstock.jem.node.ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS) {
				// too high
				value = org.pepstock.jem.node.ExecutionEnvironment.DEFAULT_PARALLEL_JOBS;
				LogAppl.getInstance().emit(NodeMessage.JEMC210W, JEM_NODE_CONFIG.getExecutionEnviroment().getParallelJobs(), value);
			}
			Main.EXECUTION_ENVIRONMENT.setParallelJobs(value);

		}
		LogAppl.getInstance().emit(NodeMessage.JEMC212I, Main.EXECUTION_ENVIRONMENT.getParallelJobs());

		// memory is optional. if 0, set default value
		if (JEM_NODE_CONFIG.getExecutionEnviroment().getMemory() == null) {
			// memory not set so uses the default
			Main.EXECUTION_ENVIRONMENT.setMemory(Jcl.DEFAULT_MEMORY);
		} else {
			int value = Parser.parseInt(JEM_NODE_CONFIG.getExecutionEnviroment().getMemory(), Integer.MIN_VALUE);
			if (value == Integer.MIN_VALUE) {
				// not a number
				value = Jcl.DEFAULT_MEMORY;
				LogAppl.getInstance().emit(NodeMessage.JEMC213W, JEM_NODE_CONFIG.getExecutionEnviroment().getMemory());
			} else if (value < org.pepstock.jem.node.ExecutionEnvironment.MINIMUM_MEMORY) {
				// too low
				value = Jcl.DEFAULT_MEMORY;
				LogAppl.getInstance().emit(NodeMessage.JEMC215W, JEM_NODE_CONFIG.getExecutionEnviroment().getMemory(), value);
			} else if (value >= org.pepstock.jem.node.ExecutionEnvironment.MAXIMUM_MEMORY) {
				// too high
				value = Jcl.DEFAULT_MEMORY;
				LogAppl.getInstance().emit(NodeMessage.JEMC214W, JEM_NODE_CONFIG.getExecutionEnviroment().getMemory(), value);
			}
			Main.EXECUTION_ENVIRONMENT.setMemory(value);

		}
		LogAppl.getInstance().emit(NodeMessage.JEMC216I, Main.EXECUTION_ENVIRONMENT.getMemory());

		// gets affinity and substitute variables if present
		String affinity = JEM_NODE_CONFIG.getExecutionEnviroment().getAffinity();
		JEM_NODE_CONFIG.getExecutionEnviroment().setAffinity(substituteVariable(affinity));
		// loads affinities on shared object
		String[] affinities = JEM_NODE_CONFIG.getExecutionEnviroment().getAffinity().split(",");
		for (int i = 0; i < affinities.length; i++) {
			Main.EXECUTION_ENVIRONMENT.getStaticAffinities().add(affinities[i].trim().toLowerCase());
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC050I, Main.EXECUTION_ENVIRONMENT);
	}

	/**
	 * Loads affintiies loaders from JEM configuration.
	 */
	private static void loadDynamicAffinities() throws ConfigurationException {
		// load factories, checking if they are configured. If not, exception
		// occurs
		AffinityFactory affinityFactory = JEM_NODE_CONFIG.getExecutionEnviroment().getAffinityFactory();
		if (affinityFactory != null) {

			// load all factories for affinity factory
			// for all factories checking which have the right className. If
			// not,
			// exception occurs, otherwise it's loaded
			if (affinityFactory.getClassName() != null) {
				String className = affinityFactory.getClassName();
				try {
					// load by Class.forName of loader
					Object objectFactory = Class.forName(className).newInstance();

					// check if it's a AffinityLoader. if not, exception occurs.
					if (objectFactory instanceof AffinityLoader) {
						AffinityLoader loader = (AffinityLoader) objectFactory;

						// gets properties defined. If not empty, substitutes
						// the value of property with variables
						Properties propsOfFactory = affinityFactory.getProperties();
						if (!propsOfFactory.isEmpty()) {
							// scans all properties
							for (Enumeration<Object> e = propsOfFactory.keys(); e.hasMoreElements();) {
								// gets key and value
								String key = e.nextElement().toString();
								String value = propsOfFactory.getProperty(key);
								// substitutes variables if present
								// and sets new value for the key
								propsOfFactory.setProperty(key, substituteVariable(value));
							}
						}
						LogAppl.getInstance().emit(NodeMessage.JEMC049I, className);
						// initializes the factory with properties defined
						// and puts in the list if everything went good
						loader.init(propsOfFactory);
						// locks the access to file to avoid multiple accesses

						Result result = loader.load(new SystemInfo());
						if (result != null) {
							Main.EXECUTION_ENVIRONMENT.getDynamicAffinities().addAll(result.getAffinities());
							Main.EXECUTION_ENVIRONMENT.setMemory(result.getMemory());
							Main.EXECUTION_ENVIRONMENT.setParallelJobs(result.getParallelJobs());
						}
						Main.setAffinityLoader(loader);
					} else {
						LogAppl.getInstance().emit(NodeMessage.JEMC089E, className);
					}

				} catch (Exception e) {
					LogAppl.getInstance().emit(NodeMessage.JEMC031E, e, className);
				}
				// in this case the class name is null so ignore, emitting a
				// warning
			} else {
				LogAppl.getInstance().emit(NodeMessage.JEMC038W, ConfigKeys.FACTORY_ALIAS, affinityFactory.toString());
			}
		}
		LogAppl.getInstance().emit(NodeMessage.JEMC050I, Main.EXECUTION_ENVIRONMENT);
	}
	
	/**
	 * 
	 * @param conf
	 * @param substituter
	 * @throws ConfigurationException
	 */
	private static void loadStatisticsManager() throws ConfigurationException {
		StatsManager statsManager = JEM_ENV_CONFIG.getStatsManager();
		if (statsManager != null) {
			// gets path and substitute variables if present
			String path = statsManager.getPath();
			if (path != null) {
				path = substituteVariable(path);
			}
			Main.setStatisticsManager(new StatisticsManager(path));
		} else {
			Main.setStatisticsManager(new StatisticsManager());
		}
	}
	
	/**
	 * Load the file with all datasets rules
	 * 
	 * @param conf the jem env config
	 * @throws ConfigurationException if some error occurs
	 */
	private static void loadDatasetsRules(Configuration conf) throws ConfigurationException {
		// gets the amount of data paths
		int dataPathsCount = Main.DATA_PATHS_MANAGER.getDataPaths().size();

		// load paths, checking if they are configured. If not, exception occurs
		String datasetsRules = conf.getDatasetsRules();
		// checks how mny data paths there are
		// if more than 1 then datasets rules is mandatory
		if (dataPathsCount > 1){
			if (datasetsRules == null) {
				LogAppl.getInstance().emit(NodeMessage.JEMC009E, ConfigKeys.DATASETS_RULES_ALIAS);
				throw new ConfigurationException(NodeMessage.JEMC009E.toMessage().getFormattedMessage(ConfigKeys.DATASETS_RULES_ALIAS));
			}
		} else {
			// datapaths amount is 1
			// and data rules is not defined, it creates
			// a default file with a default rules (ALL FILES)
			if (datasetsRules == null) {
				// gets the configuration folder of JEM env
				String folder = PROPERTIES.getProperty(ConfigKeys.JEM_ENV_CONF_FOLDER);
				// creates the default file name
				File rulesFileOnTheFly = new File(folder, DataPathsManager.DEFAULT_RULES_FILE_NAME);
				// if exists emits a warning
				if (rulesFileOnTheFly.exists()){
					LogAppl.getInstance().emit(NodeMessage.JEMC253W, rulesFileOnTheFly.getAbsolutePath());
				} else {
					// if doesn't exists, it creates a new one
					Main.DATA_PATHS_MANAGER.saveXMLDataSetRules(rulesFileOnTheFly);
				}
				// sets datasets rules here
				datasetsRules = rulesFileOnTheFly.getAbsolutePath();
			}
		}
		// loads datasets rules
		try {
			File rulesFile = new File(substituteVariable(datasetsRules));
			Main.DATA_PATHS_MANAGER.loadRules(rulesFile);
		} catch (MessageException e) {
			throw new ConfigurationException(e.getMessage(), e);
		}

	}
	
	/**
	 * 
	 * @param conf
	 * @throws ConfigurationException
	 */
	private static void checkDataPaths() throws ConfigurationException {
		IExecutorService executorService = Main.getHazelcast().getExecutorService(ExecutorServices.NODE);
		Future<List<String>> task = executorService.submitToMember(new GetDataPaths(), Main.getHazelcast().getCluster().getMembers().iterator().next());
		// gets result
        try {
        	List<String> localDataPaths = Main.DATA_PATHS_MANAGER.getDataPathsNames();
			List<String> dataPaths = task.get();
			// checks if the amount is the same
			if (dataPaths.size() != localDataPaths.size()){
				throw new ConfigurationException(NodeMessage.JEMC258E.toMessage().getFormattedMessage(dataPaths.size(), localDataPaths.size()));
			} else {
				for (String path : localDataPaths){
					if (!dataPaths.contains(path)){
						throw new ConfigurationException(NodeMessage.JEMC259E.toMessage().getFormattedMessage(path));
					}
				}
			}
			
		} catch (InterruptedException e) {
			throw new ConfigurationException(e.getMessage(), e);
		} catch (ExecutionException e) {
			throw new ConfigurationException(e.getMessage(), e);
		}		
	}

	/**
	 * 
	 * @param conf
	 * @param substituter
	 * @throws ConfigurationException
	 */
	private static void loadNode() throws ConfigurationException {
		// load node class, checking if they are configured.
		Node node = JEM_ENV_CONFIG.getNode();
		// load all factories for affinity factory
		// for all factories checking which have the right className. If
		// not,
		// exception occurs, otherwise it's loaded
		if (node != null && node.getClassName() != null) {
			String className = node.getClassName();
			try {
				// load by Class.forName of loader
				Object objectNode = Class.forName(className).newInstance();

				// check if it's a AffinityLoader. if not, exception occurs.
				if (objectNode instanceof NodeInfo) {
					Main.setNode((NodeInfo) objectNode);
					// gets properties defined. If not empty, substitutes
					// the value of property with variables
					Properties propsOfNode = node.getProperties();
					if (propsOfNode != null && !propsOfNode.isEmpty()) {
						// scans all properties
						for (Enumeration<Object> e = propsOfNode.keys(); e.hasMoreElements();) {
							// gets key and value
							String key = e.nextElement().toString();
							String value = propsOfNode.getProperty(key);
							// substitutes variables if present
							// and sets new value for the key
							propsOfNode.setProperty(key, substituteVariable(value));
						}
					}
					// initializes the factory with properties defined
					// and puts in the list if everything went good
					Main.getNode().init(propsOfNode);
					LogAppl.getInstance().emit(NodeMessage.JEMC090I, className);
					return;
				}
				LogAppl.getInstance().emit(NodeMessage.JEMC091E);
				throw new ConfigurationException(NodeMessage.JEMC091E.toMessage().getFormattedMessage(className));
			} catch (Exception e) {
				throw new ConfigurationException(e);
			}
			// in this case the class name is null so ignore, emitting a
			// warning
		}
		Main.setNode(new NodeInfo());
		LogAppl.getInstance().emit(NodeMessage.JEMC090I, NodeInfo.class.getName());
	}

	/**
	 * Jem uses 4 environment variables that are set when the node is launched: <br>
	 * 1. JEM_HOME is the root folder of the jem installation and is set in the
	 * OS system variable<br>
	 * 2. JEM_ENVIRONMENT is the root folder of the environment of the node<br>
	 * 3. JEM_NODE is the root folder of the node<br>
	 * 4. JEM_GFS is the root folder of the Jem Global File Syste<br>
	 * 
	 * @param variables lista on env variables to handle
	 * @throws ConfigurationException if some error occurs
	 */
	private static void handleEnvironmentVariable(String... variables) throws ConfigurationException {
		/*
		 * If the jem node is started with the YAJSW because of some bug of
		 * YAJSW all the environment variable name are set to lower case and
		 * JEM_ENVIRONMENT is going to be wrapper.app.env.jem_environment,
		 * JEM_NODE is going to be wrapper.app.env.jem_node (This because those
		 * variable are set in the wrapper.conf while JEM_HOME is set in the OS
		 * and it will be jem_home). So we fixed this problem here by code.
		 */

		// scans all mandatory env variables
		for (int i = 0; i < variables.length; i++) {
			// reads from enviroment
			String variable = System.getenv().get(variables[i]);
			// if not found it
			if (variable == null) {
				String key = null;
				if (variables[i].equals(ConfigKeys.JEM_HOME)) {
					// read jem_home (see above explaination)
					key = variables[i].toLowerCase();
				} else {
					// read using wrapper prefix (see above explaination)
					key = ConfigKeys.WRAPPER_APP_ENV + variables[i].toLowerCase();
				}
				// gets variable
				variable = System.getenv().get(key);
				// if not found it
				if (variable == null) {
					// writes log and throw an exception because the variables
					// al mandatory
					LogAppl.getInstance().emit(NodeMessage.JEMC058E, variables[i]);
					throw new ConfigurationException(NodeMessage.JEMC058E.toMessage().getFormattedMessage(variables[i]));
				}
			}
			// sets the variable on system and writes log
			System.getProperties().setProperty(variables[i], normalizePath(variable));
			LogAppl.getInstance().emit(NodeMessage.JEMC057I, variables[i], variable);
		}
	}

	/**
	 * Normalize the path put in configuration, changing all separators
	 * 
	 * @param path path to normalize
	 * @return normalized path
	 */
	private static String normalizePath(String path) {
		File file = new File(path);
		return FilenameUtils.normalize(file.getAbsolutePath(), true);
	}
}
