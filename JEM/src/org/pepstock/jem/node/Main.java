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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.pepstock.jem.factories.JemFactory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.affinity.AffinityLoader;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.events.JobLifecycleListenersSystem;
import org.pepstock.jem.node.multicast.MulticastService;
import org.pepstock.jem.node.resources.definition.ResourceDefinition;
import org.pepstock.jem.node.resources.definition.ResourceDefinitionsManager;
import org.pepstock.jem.node.swarm.Swarm;
import org.pepstock.jem.node.tasks.platform.CurrentPlatform;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.net.Interface;
import org.pepstock.jem.util.rmi.RegistryContainer;

import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * This is the main class of JEM node. Starts all sub component and stays in
 * wait. Contains all common static reference that all can use.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Main {
	
	/**
	 * Increments the Hazelcast port for RMI listener. 
	 * That means maximum 100 nodes per machines
	 */
	public static final int INCREMENT_RMI_PORT = 100;

	/**
	 * Increments the Hazelcast port for HTTP listener. 
	 * That means maximum 100 nodes per machines
	 */
	public static final int INCREMENT_HTTP_PORT = 200;
	
	/**
	 * If <code>true</code>, this instance of node is the oldest one of cluster.<br>
	 * <br>
	 * Hazelcast cluster works as following:<br>
	 * 1. every member in the cluster has the same member list in the same
	 * order.<br>
	 * 2. first member is the oldest member so if the oldest member dies, second
	 * member in the list becomes the first member in the list and the new
	 * oldest member.<br>
	 * <br>
	 * The "COORDINATOR" role is important because if it's <code>true</code>, it
	 * does a lot of things for whole cluster.
	 */
	public static final AtomicBoolean IS_COORDINATOR = new AtomicBoolean(false);

	/**
	 * This is static reference of current execution environment of node
	 * instance.
	 * 
	 * @see org.pepstock.jem.node.ExecutionEnvironment
	 */
	public static final ExecutionEnvironment EXECUTION_ENVIRONMENT = new ExecutionEnvironment();

	/**
	 * This is static refenrece of affinity loader define in JEM config. Could
	 * be null, if there is any affinity loader configuration in JEM config.
	 * 
	 * @see org.pepstock.jem.node.affinity.AffinityLoader
	 */
	private static AffinityLoader AFFINITY_LOADER = null;

	/**
	 * This is static reference of current input queue manager of node instance.
	 * 
	 * @see org.pepstock.jem.node.InputQueueManager
	 */
	public static final InputQueueManager INPUT_QUEUE_MANAGER = new InputQueueManager();

	/**
	 * This is static reference of the multicast service.
	 * 
	 * @see org.pepstock.jem.node.multicast.MulticastService
	 */
	private static MulticastService MULTICAST_SERVICE = null;

	/**
	 * This is static reference of current node information object.
	 * 
	 * @see org.pepstock.jem.node.NodeInfo
	 */
	private static NodeInfo NODE = null;

	/**
	 * This is static reference with list of current job tasks in execution, or
	 * empty if node status is not ACTIVE or DRAINING
	 * 
	 * @see org.pepstock.jem.node.CancelableTask
	 * @see org.pepstock.jem.node.Status
	 */
	public static final Map<String, CancelableTask> CURRENT_TASKS = new ConcurrentHashMap<String, CancelableTask>();

	/**
	 * This is static reference of current output system
	 * 
	 * @see org.pepstock.jem.node.OutputSystem
	 */
	private static OutputSystem OUTPUT_SYSTEM = null;

	/**
	 * This is static reference of map where node has loaded all configured
	 * factories.<br>
	 * The key if the returned value of "getType" method of JemFactory
	 * 
	 * @see org.pepstock.jem.factories.JemFactory#getType()
	 */
	public static final Map<String, JemFactory> FACTORIES_LIST = new LinkedHashMap<String, JemFactory>();

	/**
	 * Contains all listeners configured which will be called when the job
	 * changes its status.
	 * 
	 * @see org.pepstock.jem.node.events.JobLifecycleListenersSystem
	 * @see org.pepstock.jem.node.events.JobLifecycleListener
	 */
	public static final JobLifecycleListenersSystem JOB_LIFECYCLE_LISTENERS_SYSTEM = new JobLifecycleListenersSystem();

	/**
	 * Contains all the resource definitions defined by the user, and has
	 * the method
	 * {@link ResourceDefinitionsManager#loadResourceDefinition(org.pepstock.jem.node.configuration.CustomResourceDefinition, String)}
	 * to load them.
	 * 
	 * @see ResourceDefinitionsManager
	 * @see ResourceDefinition
	 */
	public static final ResourceDefinitionsManager RESOURCE_DEFINITION_MANAGER = new ResourceDefinitionsManager();

	/**
	 * Saves Hazelcast instance to use on all classes which must access to
	 * Hazelcast objects.
	 */
	private static HazelcastInstance HAZELCAST = null;

	/**
	 * Saves Hazelcast configuration because it must be read from DB managers
	 * for some keys to encrypt DB
	 */
	private static FileSystemXmlConfig HAZELCAST_CONFIG = null;

	/**
	 * is the collector of all statistics. Acts only if node is the coordinator
	 */
	private static StatisticsManager STATISTICS_MANAGER = null;

	/**
	 * Sets <code>true</code> if the node is started in maintainance.
	 */
	public static final AtomicBoolean IS_ACCESS_MAINT = new AtomicBoolean(false);

	/**
	 * Sets <code>true</code> if the hook shutdown thread is started, to
	 * shutdown the node.
	 */
	public static final AtomicBoolean IS_SHUTTING_DOWN = new AtomicBoolean(false);

	/**
	 * Count of JCL check done by this node.
	 */
	public static final AtomicLong NUMBER_OF_JCL_CHECK = new AtomicLong(0);

	/**
	 * Count of JOB submission done by this node.
	 */
	public static final AtomicLong NUMBER_OF_JOB_SUBMITTED = new AtomicLong(0);
	
	/**
	 * Data paths manager. It manages all rules and data paths defintion
	 */
	public static final DataPathsManager DATA_PATHS_MANAGER = new DataPathsManager();

	/**
	 * Is the Main class used to handle the hazelcast instance of the swarm
	 * environment and other object used to hanlde the routhing phase.
	 */
	public static final Swarm SWARM = new Swarm();
	
	/**
	 * List of java runtimes installed on the machine
	 */
	private static final Map<String, String> JAVA_RUNTIMES = new HashMap<String, String>();
	
	/**
	 * Contains the default runtime set by node configuration. if null;
	 * it uses for all executed jobs the same java used by JEM node
	 */
	private static String DEFAULT_JAVA_RUNTIME = null;

	// all nodes of cluster must check and validate jcls and prejobs in
	// JCL_CHECKING queue
	static final JclCheckingQueueManager JCL_CHECKER = new JclCheckingQueueManager();
	
	static Interface NETWORK_INTERFACE = null; 

	/**
	 * Constructs JEM node, initializing all sub components
	 */
	public Main() {
		int exitCode = 0;
		try {
			CurrentPlatform.getInstance();

			// reads properties to check if access maint
			IS_ACCESS_MAINT.set(Parser.parseBoolean(System.getProperty(ConfigKeys.JEM_ACCESS_MAINT), false));
			if (IS_ACCESS_MAINT.get()) {
				LogAppl.getInstance().emit(NodeMessage.JEMC189I);
			}

			// sets shutdown hook passing the threads to interrupt
			Runtime.getRuntime().addShutdownHook(new ShutDownHandler());

			// starts the configuration loading and sub component instantiation
			StartUpSystem.run();
			// starts a RMI regirty container for objects necessary from job
			// execution
			// uses the TCP port defined for Hazelcast cluster, adding a
			// constant number 200
			int objectRmiPort = Main.getNode().getPort() + INCREMENT_RMI_PORT;
			LogAppl.getInstance().emit(NodeMessage.JEMC013I, String.valueOf(objectRmiPort));
			RegistryContainer.createInstance(objectRmiPort);
			LogAppl.getInstance().emit(NodeMessage.JEMC014I);
			Main.getNode().setRmiPort(objectRmiPort);
			NodeInfoUtility.checkAndStoreNodeInfo(Main.getNode());

			Main.getStatisticsManager().init();

			RmiStartUp.initialize();
			
			int objectHttpsPort = Main.getNode().getPort() + INCREMENT_HTTP_PORT;
			HttpsInternalSubmitter.start(objectHttpsPort);

			// and at the end, wait this thread...
			waitState();

		} catch (ConfigurationException e) {
			// occurs when we have some misconfiguration. Node ends
			LogAppl.getInstance().emit(NodeMessage.JEMC006E, e);
			exitCode = 12;
		} catch (RemoteException e) {
			// occurs when RMI registry is not able to start. Node ends
			LogAppl.getInstance().emit(NodeMessage.JEMC007E, e);
			exitCode = 12;
		} catch (NodeMessageException e) {
			// occurs when the platform is not supported
			LogAppl.getInstance().emit(NodeMessage.JEMC194E, e);
			exitCode = 12;
		}
		// here starts shutdown hook
		System.exit(exitCode);
	}

	/**
	 * Checks if is running inside of Eclipse (to do it, set a
	 * RUNNING_IN_ECLIPSE env variable to true), otherwise stays in wait mode
	 */
	private synchronized void waitState() {
		// Starts really here jcl checking
		JCL_CHECKER.start();
		// Starts really here submitter
		INPUT_QUEUE_MANAGER.init();

		// checks if the variable is set understanding if is in Eclipse or not
		if (Boolean.parseBoolean(System.getenv("RUNNING_IN_ECLIPSE"))) {
			// writes a message
			LogAppl.getInstance().debug("You're using Eclipse; click in this console and	" + "press ENTER to call System.exit() and run the shutdown routine.");
			try {
				// opens a input stream waiting for an input
				System.in.read();
			} catch (IOException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		} else {
			try {
				// waits
				while (true) {
					wait();
				}
			} catch (InterruptedException e) {
				// debug
				LogAppl.getInstance().debug(e.getMessage(), e);
			}
		}
	}

	/**
	 * @return the javaRuntimes
	 */
	public static Map<String, String> getJavaRuntimes() {
		return JAVA_RUNTIMES;
	}

	/**
	 * @return the defaultJavaRuntime
	 */
	public static String getDefaultJavaRuntime() {
		return DEFAULT_JAVA_RUNTIME;
	}

	/**
	 * @param defaultJavaRuntime the defaultJavaRuntime to set
	 */
	public static void setDefaultJavaRuntime(String defaultJavaRuntime) {
		DEFAULT_JAVA_RUNTIME = defaultJavaRuntime;
	}

	/**
	 * @return the Affinity loader
	 */
	public static AffinityLoader getAffinityLoader() {
		return AFFINITY_LOADER;
	}

	/**
	 * @param affinityLoader the Affinity loader to set
	 */
	static void setAffinityLoader(AffinityLoader affinityLoader) {
		AFFINITY_LOADER = affinityLoader;
	}
	
	/**
	 * @return the multicast service
	 */
	public static MulticastService getMulticastService() {
		return MULTICAST_SERVICE;
	}

	/**
	 * @param multicastService the multicastService to set
	 */
	static void setMulticastService(MulticastService multicastService) {
		MULTICAST_SERVICE = multicastService;
	}

	/**
	 * @return the nODE
	 */
	public static NodeInfo getNode() {
		return NODE;
	}

	/**
	 * @param node the node to set
	 */
	static void setNode(NodeInfo node) {
		NODE = node;
	}
	
	/**
	 * @return the outputSystem
	 */
	public static OutputSystem getOutputSystem() {
		return OUTPUT_SYSTEM;
	}

	/**
	 * @param outputSystem the outputSystem to set
	 */
	static void setOutputSystem(OutputSystem outputSystem) {
		OUTPUT_SYSTEM = outputSystem;
	}
	
	/**
	 * @return the StatisticsManager
	 */
	public static StatisticsManager getStatisticsManager() {
		return STATISTICS_MANAGER;
	}

	/**
	 * @param statisticsManager the statisticsManager to set
	 */
	public static void setStatisticsManager(StatisticsManager statisticsManager) {
		STATISTICS_MANAGER = statisticsManager;
	}
	
	/**
	 * @return the Hazelcast
	 */
	public static HazelcastInstance getHazelcast() {
		return HAZELCAST;
	}

	/**
	 * @return the nETWORK_INTERFACE
	 */
	public static Interface getNetworkInterface() {
		return NETWORK_INTERFACE;
	}

	/**
	 * @param hazelcast the hazelcast to set
	 */
	static void setHazelcast(HazelcastInstance hazelcast) {
		HAZELCAST = hazelcast;
	}

	/**
	 * @return the HazelcastConfig
	 */
	public static FileSystemXmlConfig getHazelcastConfig() {
		return HAZELCAST_CONFIG;
	}

	/**
	 * @param hazelcastConfig the hazelcastConfig to set
	 */
	static void setHazelcastConfig(FileSystemXmlConfig hazelcastConfig) {
		HAZELCAST_CONFIG = hazelcastConfig;
	}
	
	/**
	 * Main method to start!
	 * 
	 * @param args null at the moment
	 */
	public static void main(String[] args) {
		System.setProperty(ConfigKeys.IPV4, Boolean.TRUE.toString());
		new Main();
	}

}
