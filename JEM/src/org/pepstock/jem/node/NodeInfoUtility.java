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
 * A set of methods to manage the shared map with all nodes of cluster
 * 
 * @author Andrea "Stock" Stocchero
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

        String hostname = StringUtils.substringAfter(info.getProcessId(), "@");
        info.setHostname(hostname);

        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        info.getNodeInfoBean().setSystemArchitecture(bean.getArch());
        info.getNodeInfoBean().setAvailableProcessors(bean.getAvailableProcessors());
        info.getNodeInfoBean().setSystemName(bean.getName());

        try {
        	info.getNodeInfoBean().setTotalMemory(SIGAR.getMem().getTotal());
			ProcCredName cred = SIGAR.getProcCredName(SIGAR.getPid());
			info.setUser(cred.getUser());
		} catch (SigarException e) {
			throw new NodeException(e.getMessage(), e);
		}
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
			LogAppl.getInstance().emit(NodeMessage.JEMC184W);
		} catch (URISyntaxException e) {
			LogAppl.getInstance().emit(NodeMessage.JEMC184W, e);
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
     * 
     * @param address
     * @param info
     * @throws ConfigurationException
     * 
     */
    public static synchronized void checkAndStoreNodeInfo(NodeInfo info) throws ConfigurationException {
        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);

        StringBuilder sb = new StringBuilder();
        sb.append("label = '").append(info.getLabel()).append("'");

        Collection<NodeInfo> nodeInfos = membersMap.values(new SqlPredicate(sb.toString()));

        try {
            membersMap.lock(info.getKey());
            if (!nodeInfos.isEmpty()) {
                for (NodeInfo prevNodeInfo : nodeInfos) {
                    membersMap.remove(prevNodeInfo.getKey());
                }
            }
            membersMap.put(info.getKey(), info);

            boolean checkVersion = Parser.parseBoolean(System.getProperty(ConfigKeys.JEM_CHECK_VERSION), false);
            // check if node release version inside the cluster are different
            Collection<NodeInfo> allNodes = membersMap.values();
            for (NodeInfo currNodeInfo : allNodes) {
                if (!currNodeInfo.getJemVersion().equals(info.getJemVersion())) {
                    if (checkVersion) {
                        throw new ConfigurationException(NodeMessage.JEMC191E.toMessage().getFormattedMessage());
                    } else {
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
            membersMap.unlock(info.getKey());
        }
    }

    /**
     * 
     * @param info
     */
    public static void storeNodeInfo(NodeInfo info) {
        storeNodeInfo(info, false);
    }

    /**
     * 
     * @param info
     * @param hasToStore
     */
    public static synchronized void storeNodeInfo(NodeInfo info, boolean hasToStore) {
        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
        try {
            membersMap.lock(info.getKey());
            if (!membersMap.containsKey(info.getKey())){
                membersMap.put(info.getKey(), info);
            } else {
                membersMap.replace(info.getKey(), info);
            }
            if (hasToStore) {
                MANAGER.store(info.getKey(), info);
            }
        } catch (Exception ex) {
            LogAppl.getInstance().emit(NodeMessage.JEMC174E, ex);
        } finally {
            membersMap.unlock(info.getKey());
        }
    }

    /**
     * 
     */
    public static synchronized void checkAndCleanMapStore() {
        try {
            IMap<String, NodeInfo> nodesMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
            for (String key : MANAGER.loadAllKeys()) {
                try {
                    nodesMap.lock(key);
                    if (!nodesMap.containsKey(key)) {
                        MANAGER.delete(key);
                    }
                } finally {
                    nodesMap.unlock(key);
                }
            }
        } catch (Exception e) {
            LogAppl.getInstance().emit(NodeMessage.JEMC229E, e);
        }
    }

    /**
     * 
     * @param key
     * @return NodeInfo
     */
    public static NodeInfo getNodeInfoFromMapStore(String key) {
        try {
            return MANAGER.load(key);
        } catch (Exception e) {
        	LogAppl.getInstance().debug(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 
     * @param key
     */
    public static void removeNodeInfoFromMapStore(String key) {
        try {
            MANAGER.delete(key);
        } catch (Exception e) {
        	LogAppl.getInstance().debug(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param statusList the list of the status used to filtered the NODES_MAP
     * @param notIn if set to true the filtering will be for status Not In the
     *            list of status passed as parameter
     * @param includeSupernode if set to true also the supernode will be
     *            considered
     * @return a List of NodeInfo present in the Queues.NODES_MAP (eventually an
     *         empty list) filtered by status present in the statusList passed
     *         as parameter. If the status List is null or empty return all the
     *         NodeInfo present in the map
     */
    public static List<NodeInfo> getNodesInfoByStatus(List<Status> statusList, boolean notIn) {

        IMap<String, NodeInfo> membersMap = Main.getHazelcast().getMap(Queues.NODES_MAP);
        List<NodeInfo> nodesInfo = new ArrayList<NodeInfo>(membersMap.values());
        if (statusList == null || statusList.isEmpty()) {
            return nodesInfo;
        }
        List<NodeInfo> nodesInfoToReturn = new ArrayList<NodeInfo>();
        for (int j = 0; j < nodesInfo.size(); j++) {
            NodeInfo currNodeInfo;
            currNodeInfo = nodesInfo.get(j);
            if (statusList.contains(currNodeInfo.getStatus()) && !notIn) {
                nodesInfoToReturn.add(currNodeInfo);
            }
            if (!statusList.contains(currNodeInfo.getStatus()) && notIn) {
                nodesInfoToReturn.add(currNodeInfo);
            }
        }
        return nodesInfoToReturn;
    }

    /**
     * Drains the node
     */
    public static void drain() {
        DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Drain(), Main.getHazelcast().getCluster().getLocalMember());
        ExecutorService executorService = Main.getHazelcast().getExecutorService();
        task.setExecutionCallback(new GenericCallBack());
        executorService.execute(task);
    }

    /**
     * Drains the node
     */
    public static void start() {
        DistributedTask<ExecutionResult> task = new DistributedTask<ExecutionResult>(new Start(), Main.getHazelcast().getCluster().getLocalMember());
        ExecutorService executorService = Main.getHazelcast().getExecutorService();
        task.setExecutionCallback(new GenericCallBack());
        executorService.execute(task);
    }
}