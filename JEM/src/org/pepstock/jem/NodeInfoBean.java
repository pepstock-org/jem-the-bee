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
package org.pepstock.jem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.node.ExecutionEnvironment;


/**
 * Represents a node of cluster. it uses many information but not Hazelcast one,
 * to avoid any errors during serialization
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
@XmlRootElement
public class NodeInfoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String hostname = null;

	private String ipaddress = null;

	private int port = Integer.MIN_VALUE;

	private int rmiPort = Integer.MIN_VALUE;

	private String processId = null;

	private String status = null;

	private List<String> jobNames = new ArrayList<String>();

	private String key = null;

	private String label = null;
	
	private ExecutionEnvironment executionEnvironment = null;

	private String systemArchitecture = null;
	
	private String systemName = null;
	
	private int availableProcessors = 0;
	
	private long totalMemory = 0;
	
	private Date startedTime = null;
	
	private boolean isOperational = true;

	private boolean isSwarmNode = false;
	
	private String type = null;

	private String jemVersion;

	/**
	 * Constructs the node info object
	 */
	public NodeInfoBean() {
	}

	/**
	 * Gets unique key to identify the node inside the cluster.
	 * 
	 * @return return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets unique key to identify the node inside the cluster.
	 * 
	 * @param key the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets readable name of node, used on displays or logs.
	 * 
	 * @return readable name of node
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets readable name of node, used on displays or logs.
	 * 
	 * @param label readable name
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	

	/**
	 * Returns the package of node implementation
	 * @return the type the package of node implementation
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the package of node implementation
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the ipaddress of machine where the node is running
	 * 
	 * @return ipaddress of machine
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * Sets the ipaddress and port number of machine where the node is running
	 * 
	 * @param ipaddress ipaddress of machine
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * Returns the port number what the node is listening on
	 * 
	 * @return the port number what the node is listening on
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port number what the node is listening on
	 * 
	 * @param port the port number what the node is listening on
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the RMI port number what the node is listening on
	 * 
	 * @return the RMI port number what the node is listening on
	 */

	public int getRmiPort() {
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
	 * Returns the hostname of machine where the node is running
	 * 
	 * @return hostname of machine
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets the hostname of machine where the node is running
	 * 
	 * @param hostname hostname of machine
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Returns the current status of node
	 * 
	 * @see org.pepstock.jem.node.Status#Status(int, String)
	 * @return current status of node
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the current status of node
	 * 
	 * @see org.pepstock.jem.node.Status#Status(int, String)
	 * @param status current status of node
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns the current names of jobs which are running managed by node
	 * 
	 * @see org.pepstock.jem.Job#Job()
	 * @return current job
	 */
	public List<String> getJobNames() {
		return jobNames;
	}

	/**
	 * Returns the process id, created to execute the job. JMX method is used
	 * that returns the process id in PID@HOSTNAME format
	 * 
	 * @return process id in pid@name format
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * Sets the process id, created to execute the job. JMX method is used that
	 * returns the process id in PID@HOSTNAME format
	 * 
	 * @param processId process id in pid@name format
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Returns the environment, domain and affinity information for the node
	 * 
	 * @return the executionEnviroment
	 */
	public ExecutionEnvironment getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the environment, domain and affinity information for the node
	 * @param executionEnvironment the executionEnviroment to set
	 */
	public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	/**
	 * @return the systemArchitecture
	 */
	public String getSystemArchitecture() {
		return systemArchitecture;
	}

	/**
	 * @param systemArchitecture the systemArchitecture to set
	 */
	public void setSystemArchitecture(String systemArchitecture) {
		this.systemArchitecture = systemArchitecture;
	}

	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return systemName;
	}

	/**
	 * @param systemName the systemName to set
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * @return the availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * @param availableProcessors the availableProcessors to set
	 */
	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}
	/**
	 * Returns the started time of node
	 * 
	 * @return the startedTime
	 */
	public Date getStartedTime() {
		return startedTime;
	}

	/**
	 * Sets the started time of node
	 * 
	 * @param startedTime the startedTime to set
	 */
	public void setStartedTime(Date startedTime) {
		this.startedTime = startedTime;
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
	 * @return the isSwarm
	 */
	public boolean isSwarmNode() {
		return isSwarmNode;
	}

	/**
	 * @param isSwarmNode the isSwarmNode to set
	 */
	public void setSwarmNode(boolean isSwarmNode) {
		this.isSwarmNode = isSwarmNode;
	}

	/**
	 * @return the jemVersion present in the manifest of the jem.jar
	 */
	public String getJemVersion() {
		return jemVersion;
	}

	/**
	 * @param jemVersion the jemVersion present in the manifest of the jem.jar
	 */
	public void setJemVersion(String jemVersion) {
		this.jemVersion = jemVersion;
	}

	/**
	 * @return the totalMemory
	 */
	public long getTotalMemory() {
		return totalMemory;
	}

	/**
	 * @param totalMemory the totalMemory to set
	 */
	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeInfoBean [hostname=" + hostname + ", ipaddress=" + ipaddress + ", port=" + port + ", rmiPort=" + rmiPort + ", processId=" + processId + ", status=" + status + ", jobNames=" + jobNames + ", isSwarmNode=" + isSwarmNode + ",key=" + key
				+ ", label=" + label + ", executionEnvironment=" + executionEnvironment + ", systemArchitecture=" + systemArchitecture + ", systemName=" + systemName + ", availableProcessors=" + availableProcessors + ", totalMemory=" + totalMemory
				+ ", startedTime=" + startedTime + ", isOperational=" + isOperational + ", jemVersion=" + jemVersion + "]";
	}
	
}