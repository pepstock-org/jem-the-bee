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
    
 Linking JEM, the BEE statically or dynamically with other modules is making a combined work based on JEM, the BEE. 
 Thus, the terms and conditions of the GNU General Public License cover the whole combination.

 As a special exception, the copyright holders of JEM, the BEE give you permission to combine JEM, the BEE program with 
 free software programs or libraries that are released under the GNU LGPL and with independent modules 
 that communicate with JEM, the BEE solely through the org.pepstock.jem.node.NodeInfo interface. 
 You may copy and distribute such a system following the terms of the GNU GPL for JEM, the BEE and the licenses 
 of the other code concerned, provided that you include the source code of that other code when and as 
 the GNU GPL requires distribution of source code and provided that you do not modify the 
 org.pepstock.jem.node.NodeInfo interface.

 Note that people who make modified versions of JEM, the BEE are not obligated to grant this special exception
 for their modified versions; it is their choice whether to do so. The GNU General Public License
 gives permission to release a modified version without this exception; this exception also makes it
 possible to release a modified version which carries forward this exception. If you modify the 
 org.pepstock.jem.node.NodeInfo interface, this exception does not apply to your modified version of 
 JEM, the BEE, and you must remove this exception when you distribute your modified version.

 This exception is an additional permission under section 7 of the GNU General Public License, version 3
 (GPLv3)  
   
 */
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.pepstock.jem.Job;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.node.configuration.ConfigKeys;

import com.google.gwt.user.client.rpc.GwtTransient;

/**
 * Represents a node of cluster. it uses many information of Hazelcast member
 * class. Extends GRS node so it's able to manage locking on global resource
 * system.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NodeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Default version if no one is found inside the manifest
	 */
	public static final String UNKNOWN_VERSION = "Unknown";

	private String hostname = null;

	private String ipaddress = null;

	private String key = null;

	private String label = null;

	private int port = Integer.MIN_VALUE;

	private int rmiPort = Integer.MIN_VALUE;
	
	private int tcpPort = Integer.MIN_VALUE;

	private String processId = null;

	private String dockerHostAddress = null;

	private String user = null;

	private Status status = Status.UNKNOWN;

	private Map<String, String> jobs = new ConcurrentHashMap<String, String>();

	private boolean isSwarmNode = false;

	@JsonIgnore
	private NodeInfoBean nodeInfoBean = new NodeInfoBean();

	private ExecutionEnvironment executionEnvironment = null;

	private Date startedTime = new Date();

	private String jemVersion = UNKNOWN_VERSION;

	@GwtTransient
	private Map<String, RequestLock> requests = new ConcurrentHashMap<String, RequestLock>();
	
	@GwtTransient
	private PropertiesWrapper initProperties = null;

	private boolean isOperational = true;
	
	private final String javaVendor = System.getProperty(ConfigKeys.JAVA_VENDOR);
			
	private final String javaVersion = System.getProperty(ConfigKeys.JAVA_VERSION);
	
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Constructs the node info object
	 */
	public NodeInfo() {
	}

	/**
	 * Called before to load all data of node
	 * 
	 * @throws NodeException if exception occurs
	 */
	public void loaded() throws NodeException {
		// do nothing
	}

	/**
	 * Called to initialize the node. A set of properties are passed, or a empty
	 * collection if the properties are not defined
	 * 
	 * @param properties properties
	 * @throws NodeException if exception occurs
	 */
	public void init(Properties properties) throws NodeException {
		if (properties != null && !properties.isEmpty()){
			this.initProperties = new PropertiesWrapper();
			for (Entry<Object, Object> entry : properties.entrySet()){
				this.initProperties.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
	}

	/**
	 * Gets unique key to identify the node inside the cluster.
	 * 
	 * @return return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * Sets unique key to identify the node inside the cluster.
	 * 
	 * @param key the key
	 */
	public final void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets readable name of node, used on displays or logs.
	 * 
	 * @return readable name of node
	 */
	public final String getLabel() {
		return label;
	}

	/**
	 * Sets readable name of node, used on displays or logs.
	 * 
	 * @param label readable name
	 */
	public final void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the ipaddress of machine where the node is running
	 * 
	 * @return ipaddress of machine
	 */
	public final String getIpaddress() {
		return ipaddress;
	}

	/**
	 * Sets the ipaddress and port number of machine where the node is running
	 * 
	 * @param ipaddress ipaddress of machine
	 */
	public final void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * Returns the port number what the node is listening on
	 * 
	 * @return the port number what the node is listening on
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * Sets the port number what the node is listening on
	 * 
	 * @param port the port number what the node is listening on
	 */
	public final void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the RMI port number what the node is listening on
	 * 
	 * @return the RMI port number what the node is listening on
	 */

	public final int getRmiPort() {
		return rmiPort;
	}

	/**
	 * Sets the RMI port number what the node is listening on
	 * 
	 * @param rmiPort the RMI port number what the node is listening on
	 */

	public void setRmiPort(int rmiPort) {
		this.rmiPort = rmiPort;
	}

	/**
	 * @return the tcpPort
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * @param tcpPort the tcpPort to set
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	/**
	 * Returns the hostname of machine where the node is running
	 * 
	 * @return hostname of machine
	 */
	public final String getHostname() {
		return hostname;
	}

	/**
	 * Sets the hostname of machine where the node is running
	 * 
	 * @param hostname hostname of machine
	 */
	public final void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Returns the current status of node
	 * 
	 * @see org.pepstock.jem.node.Status#Status(int, String)
	 * @return current status of node
	 */
	public final Status getStatus() {
		return status;
	}

	/**
	 * Sets the current status of node
	 * 
	 * @see org.pepstock.jem.node.Status#Status(int, String)
	 * @param status current status of node
	 */
	public final void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Returns the current jobs which is running managed by node
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @return current jobs
	 */
	public Map<String, String> getJobs() {
		return jobs;
	}

	/**
	 * Set the swarm mode to true. A node is in a swarm mode if it has an active
	 * swarm environment. A swarm environment is an hazelcast cluster that
	 * connect different JEM environments and is used to route jobs.
	 * 
	 * @param isSwarmNode
	 */
	public final void setSwarmNode(boolean isSwarmNode) {
		this.isSwarmNode = isSwarmNode;
	}

	/**
	 * A swarm node is a node with an active swarm environment. A Swarm
	 * environment is a cloud of JEM environment which is used to route jobs
	 * 
	 * @return <code>true</code> if is an active Swarm node, ,
	 *         <code>false</code> otherwise
	 */
	public final boolean isSwarmNode() {
		return isSwarmNode;
	}

	/**
	 * Returns the process id, created to execute the job. JMX method is used
	 * that returns the process id in PID@HOSTNAME format
	 * 
	 * @return process id in pid@name format
	 */
	public final String getProcessId() {
		return processId;
	}

	/**
	 * Sets the process id, created to execute the job. JMX method is used that
	 * returns the process id in PID@HOSTNAME format
	 * 
	 * @param processId process id in pid@name format
	 */
	public final void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Returns the environment, domain and affinity information for the node
	 * 
	 * @return the executionEnviroment
	 */
	public final ExecutionEnvironment getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the environment, domain and affinity information for the node
	 * 
	 * @param executionEnvironment the executionEnviroment to set
	 */
	public final void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	/**
	 * Returns the started time of node
	 * 
	 * @return the startedTime
	 */
	public final Date getStartedTime() {
		return startedTime;
	}

	/**
	 * Sets the started time of node
	 * 
	 * @param startedTime the startedTime to set
	 */
	public final void setStartedTime(Date startedTime) {
		this.startedTime = startedTime;
	}

	/**
	 * @return the requests
	 */
	public Map<String, RequestLock> getRequests() {
		return requests;
	}
	
	/**
	 * @return the initProperties
	 */
	public PropertiesWrapper getInitProperties() {
		return initProperties;
	}

	/**
	 * Creates a request lock every time that a job will be executed
	 * 
	 * @param job job which requests for locking (all jobs do that)
	 * @return a request lock
	 */
	public RequestLock createRequestLock(Job job) {
		DefaultRequestLock rLock = new DefaultRequestLock();
		rLock.setRequestorId(job.getId());
		rLock.setRequestorName(job.getName());
		return rLock;
	}

	/**
	 * @return the isOperational
	 */
	public boolean isOperational() {
		return isOperational;
	}

	/**
	 * @param isOperational the isOperational to set
	 */
	public void setOperational(boolean isOperational) {
		this.isOperational = isOperational;
	}

	/**
	 * Unlocks all resources previously locked in case of node failover.
	 */
	public void unlockForFailover() {
		if (!requests.isEmpty()) {
			for (RequestLock request : requests.values()) {
				request.unlock();
			}
		}
	}

	/**
	 * Returns a string with a message that this feature is not supported.
	 * 
	 * @return all locks info
	 */
	public String displayRequestors() {
		return "No locks information is available with this kind of node.";
	}

	/**
	 * Returns a string with a message that this feature is not supported.
	 * 
	 * @param resourceKey resource name (key)
	 * @return all locks info for resource name
	 */
	public String displayRequestors(String resourceKey) {
		return resourceKey+": "+displayRequestors();
	}

	/**
	 * @return the jemVersion present in the manifest of the jem.jar
	 */
	public final String getJemVersion() {
		return jemVersion;
	}

	/**
	 * @param jemVersion the jemVersion present in the manifest of the jem.jar
	 */
	public final void setJemVersion(String jemVersion) {
		this.jemVersion = jemVersion;
	}

	/**
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public final void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the javaVendor
	 */
	public String getJavaVendor() {
		return javaVendor;
	}

	/**
	 * @return the javaVersion
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * @return the lock
	 */
	public ReentrantLock getLock() {
		return lock;
	}

	/**
	 * @return the dockerHostAddress
	 */
	public String getDockerHostAddress() {
		return dockerHostAddress;
	}

	/**
	 * @param dockerHostAddress the dockerHostAddress to set
	 */
	public void setDockerHostAddress(String dockerHostAddress) {
		this.dockerHostAddress = dockerHostAddress;
	}

	/**
	 * Returns the bean which represents the node. Necessary for serialization
	 * reasons to web interface. <br>
	 * 
	 * @return the nodeInfoBean
	 */
	public final NodeInfoBean getNodeInfoBean() {
		nodeInfoBean.setKey(this.getKey());
		nodeInfoBean.setLabel(this.getLabel());
		nodeInfoBean.setHostname(this.getHostname());
		nodeInfoBean.setIpaddress(this.getIpaddress());
		if (!this.getJobs().isEmpty()) {
			for (String jobName : this.getJobs().values()) {
				nodeInfoBean.getJobNames().add(jobName);
			}
		}
		nodeInfoBean.setPort(this.getPort());
		nodeInfoBean.setProcessId(this.getProcessId());
		nodeInfoBean.setRmiPort(this.getRmiPort());
		nodeInfoBean.setTcpPort(this.getTcpPort());
		nodeInfoBean.setStatus(this.getStatus().getDescription());
		nodeInfoBean.setExecutionEnvironment(this.getExecutionEnvironment());
		nodeInfoBean.setStartedTime(startedTime);
		nodeInfoBean.setOperational(this.isOperational());
		nodeInfoBean.setJemVersion(this.getJemVersion());
		nodeInfoBean.setSwarmNode(this.isSwarmNode());
		nodeInfoBean.setType(this.getClass().getName());
		nodeInfoBean.setJavaVendor(this.getJavaVendor());
		nodeInfoBean.setJavaVersion(this.getJavaVersion());
		return nodeInfoBean;
	}

}
