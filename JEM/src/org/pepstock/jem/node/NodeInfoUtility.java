/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.executors.ExecutionResult;
import org.pepstock.jem.node.executors.GenericCallBack;
import org.pepstock.jem.node.executors.nodes.Drain;
import org.pepstock.jem.node.executors.nodes.Start;
import org.pepstock.jem.node.persistence.NodesMapManager;
import org.pepstock.jem.util.Parser;

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.query.SqlPredicate;

/**
 * A set of methods to manage the shared map with all nodes of cluster.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 * @see org.pepstock.jem.node.NodeInfo
 */
public class NodeInfoUtility {

    private static final Sigar SIGAR = new Sigar();

    private static final NodesMapManager MANAGER = new NodesMapManager();

    /**
	 * Private constructor to avoid new instantiations
	 */
	private NodeInfoUtility() {
	}

	/**
     * Factory creates a NodeInfo copying a set of information from Member
     * object of Hazelcast framework. NodeInfo will use Uuid of Member as the
     * key.
     * 
     * @see org.pepstock.jem.node.NodeInfo
     * @param member member object of Hazelcast framework
     * @param info node info to load
     * @throws NodeException if any exception occurs
     */
    public static final void loadNodeInfo(Member member, NodeInfo info) throws NodeException{
    	String jemVersion = getManifestAttribute(ConfigKeys.JEM_MANIFEST_VERSION);
    	// sets the version
        if (jemVersion != null){
            info.setJemVersion(jemVersion);
        }
        // set uuid of member of hazelcast as key
        info.setKey(member.getUuid());
        // set status starting at the beginning
        info.setStatus(Status.STARTING);
        // sets boolean if has affinity loader

        // for net info of member, loads all info inside of nodeinfo
        // port of RMI will be set later
        InetSocketAddress address = member.getInetSocketAddress();
        info.setPort(address.getPort());
        info.setIpaddress(address.getAddress().getHostAddress());

        // sets label to be displayed by GRS
        info.setLabel(info.getIpaddress() + ":" + info.getPort());

        // sets execution environment
        info.setExecutionEnvironment(Main.EXECUTION_ENVIRONMENT);
        // use JMX to extract current process id
        info.setProcessId(ManagementFactory.getRuntimeMXBean().getName());
        
        // extracts the name using the MXBean result
        String hostname = StringUtils.substringAfter(info.getProcessId(), "@");
        info.setHostname(hostname);

        // extracts from operating ssytem MXbean all info about the ssytem
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        info.getNodeInfoBean().setSystemArchitecture(bean.getArch());
        info.getNodeInfoBean().setAvailableProcessors(bean.getAvailableProcessors());
        info.getNodeInfoBean().setSystemName(bean.getName());

        // uses SIGAR to get total memory and the user used by JEM to start
        try {
        	info.getNodeInfoBean().setTotalMemory(SIGAR.getMem().getTotal());
			ProcCredName cred = SIGAR.getProcCredName(SIGAR.getPid());
			info.setUser(cred.getUser());
		} catch (SigarException e) {
			throw new NodeException(e.getMessage(), e);
		}
        // informs the node itself that it has been loaded
        info.loaded();
    }
    
    /**
     * Extracts from manifest file the attribute passed by argument 
     * @param what name of attribute to get
     * @return attribute value or null, if doesn't exist
     */
    public static String getManifestAttribute(String what){
		JarFile jarFile = null;
		try {
			// gets JAR file
			jarFile = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
			
			// gets attributes
			Attributes at = (Attributes) jarFile.getManifest().getAttributes(ConfigKeys.JEM_MANIFEST_SECTION);
			// gets version
			return at.getValue(ConfigKeys.JEM_MANIFEST_VERSION);
		} catch (IOException e) {
			// ignore the stack trace
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(NodeMessage.JEMC184W);
		} catch (URISyntaxException e) {
			// ignore the stack trace
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(NodeMessage.JEMC184W);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					// debug
					LogAppl.getInstance().debug(e.getMessage(), e);
				}
			}
		} 
		return null;
    }

    /**
     * Stores the node on Hazelcast map for nodes.
     * Before doing it, it checks if there is already the instance store on Hazelcast (maybe do to crashed)
     * and removes it and loads the new one.
     * 
     * @param info node information to store
     * @throws ConfigurationException if any configuration error occurs
     * 
     */
    public static synchronized void checkAndStoreNodeInfo(NodeInfo info) throws ConfigurationException {
    	// gets HC map
        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);

        // builds the query to use to get nodes
        StringBuilder sb = new StringBuilder();
        sb.append("label = '").append(info.getLabel()).append("'");

        // performs the query
        Collection<NodeInfo> nodeInfos = membersMap.values(new SqlPredicate(sb.toString()));
        
        try {
        	// locks map to avoid that other nodes change at the same time
            membersMap.lock(info.getKey());
            // if the query gave a result
            if (!nodeInfos.isEmpty()) {
            	// removes all nodes, result of the query
                for (NodeInfo prevNodeInfo : nodeInfos) {
                    membersMap.remove(prevNodeInfo.getKey());
                }
            }
            // adds new node
            membersMap.put(info.getKey(), info);

            // checks if the CHECKVERSION system property is present. Default false
            boolean checkVersion = Parser.parseBoolean(System.getProperty(ConfigKeys.JEM_CHECK_VERSION), false);
            // check if node release version inside the cluster are different
            Collection<NodeInfo> allNodes = membersMap.values();
            // scans all node to check the version
            for (NodeInfo currNodeInfo : allNodes) {
            	// if there is some mismatch on the version
                if (!currNodeInfo.getJemVersion().equals(info.getJemVersion())) {
                	// and it must check the version
                    if (checkVersion) {
                    	// throws an exception
                        throw new ConfigurationException(NodeMessage.JEMC191E.toMessage().getFormattedMessage());
                    } else {
                    	// otherwise put just a warning
                        LogAppl.getInstance().emit(NodeMessage.JEMC185W);
                        break;
                    }
                }
            }
        } catch (ConfigurationException ex) {
        	throw ex;
        } catch (Exception ex) {
            LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
        } finally {
        	// always unlock
            membersMap.unlock(info.getKey());
        }
    }

    /**
     * Stores the nodes on Hazelcast map without forcing the persistence
     * @param info node instance
     */
    public static void storeNodeInfo(NodeInfo info) {
        storeNodeInfo(info, false);
    }

    /**
     * Stores the node on Hazelcast map, asking if the nodes must persist on database, by Mapstore
     * 
     * @param info node instance
     * @param hasToStore if <code>true</code>, the nodes must persist on database
     */
    public static synchronized void storeNodeInfo(NodeInfo info, boolean hasToStore) {
    	// gets HC map
        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
        try {
        	// lock map
            membersMap.lock(info.getKey());
            if (!membersMap.containsKey(info.getKey())){
                membersMap.put(info.getKey(), info);
            } else {
                membersMap.replace(info.getKey(), info);
            }
            // if it must be persit, persist!!!
            if (hasToStore) {
                MANAGER.store(info.getKey(), info);
            }
        } catch (Exception ex) {
            LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
        } finally {
        	// always unlock
            membersMap.unlock(info.getKey());
        }
    }

    /**
     * checks all nodes on Hazelcast map and clean all old member not longer on the local map
     */
    public static synchronized void checkAndCleanMapStore() {
    	// gets map
        IMap<String, NodeInfo> nodesMap = Main.getHazelcast().getMap(Queues.NODES_MAP);

        try {
        	// scans all nodes on databases
            for (String key : MANAGER.loadAllKeys()) {
                try {
                	// locks map and delete if not exist
                    nodesMap.lock(key);
                    if (!nodesMap.containsKey(key)) {
                        MANAGER.delete(key);
                    }
                } finally {
                	// always unlock
                    nodesMap.unlock(key);
                }
            }
        } catch (Exception e) {
            LogAppl.getInstance().emit(NodeMessage.JEMC229E, e);
        }
    }

    /**
     * gets the node reading the inforamtion from mapstore
     * 
     * @param key key of node
     * @return NodeInfo node instance or null if is not on the database
     */
    public static NodeInfo getNodeInfoFromMapStore(String key) {
        try {
        	// loads the node from database
            return MANAGER.load(key);
        } catch (Exception e) {
        	LogAppl.getInstance().debug(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Removes a node information by key from mapstore
     * @param key unique key of node insatnce
     */
    public static void removeNodeInfoFromMapStore(String key) {
        try {
        	// removes from database
            MANAGER.delete(key);
        } catch (Exception e) {
        	LogAppl.getInstance().debug(e.getMessage(), e);
        }
    }

    /**
     * Returns all nodes by a lit of status 
     * @param statusList the list of the status used to filtered the NODES_MAP
     * @param notIn if set to true the filtering will be for status Not In the
     *            list of status passed as parameter
     * @return a List of NodeInfo present in the Queues.NODES_MAP (eventually an
     *         empty list) filtered by status present in the statusList passed
     *         as parameter. If the status List is null or empty return all the
     *         NodeInfo present in the map
     */
    public static List<NodeInfo> getNodesInfoByStatus(List<Status> statusList, boolean notIn) {
    	// gets HC map
        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
        // gets all nodes from map
        List<NodeInfo> nodesInfo = new ArrayList<NodeInfo>(membersMap.values());
        // if list of status is empty or null,
        // returns all nodes
        if (statusList == null || statusList.isEmpty()) {
            return nodesInfo;
        }
        // creats the list ot be returned
        List<NodeInfo> nodesInfoToReturn = new ArrayList<NodeInfo>();
        // scans all nodes
        for (int j = 0; j < nodesInfo.size(); j++) {
            NodeInfo currNodeInfo;
            currNodeInfo = nodesInfo.get(j);
            // checks if status matches 
            if (statusList.contains(currNodeInfo.getStatus()) && !notIn) {
                nodesInfoToReturn.add(currNodeInfo);
            }
            // checks if status matches 
            if (!statusList.contains(currNodeInfo.getStatus()) && notIn) {
                nodesInfoToReturn.add(currNodeInfo);
            }
        }
        return nodesInfoToReturn;
    }

    /**
     * Drains the node itself
     */
    public static void drain() {
    	// creates the distributed task to drain itself
        DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Drain(), Main.getHazelcast().getCluster().getLocalMember());
        ExecutorService executorService = Main.getHazelcast().getExecutorService();
        task.setExecutionCallback(new GenericCallBack());
        executorService.execute(task);
    }

    /**
     * Starts the node
     */
    public static void start() {
    	// creates the distributed task to start itself
        DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Start(), Main.getHazelcast().getCluster().getLocalMember());
        ExecutorService executorService = Main.getHazelcast().getExecutorService();
        task.setExecutionCallback(new GenericCallBack());
        executorService.execute(task);
    }
}