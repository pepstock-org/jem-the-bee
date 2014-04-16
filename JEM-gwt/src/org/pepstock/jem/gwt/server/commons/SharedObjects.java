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
package org.pepstock.jem.gwt.server.commons;

import org.pepstock.jem.Service;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.connector.ConnectorServiceFactory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageRuntimeException;
import org.pepstock.jem.node.NodeInfo;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;

/**
 * Container of shared (and then static) references to use every where.<br>
 * It contains node info this node, local member of Hazelcast and a map of
 * hazelcast instances (not used yet).
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SharedObjects {

	private static SharedObjects SHARED_OBJECTS = null;

	private Service connectorService;

	private boolean isDataClusterAvailable = false;

	private HazelcastInstance hazelcastClient = null;

	private String sessionsInstance = null;

	private Config hazelcastConfig = null;

	private Boolean checkVersion = false;

	private String jemVersion = NodeInfo.UNKNOWN_VERSION;

	private String contextPath;
	
	/**
	 * Empty constructor
	 */
	private SharedObjects() {
	}

	/**
	 * Creates a new object, if doesn't exists.
	 * 
	 * @return the shared instance
	 */
	public static synchronized SharedObjects createInstance() {
		if (SHARED_OBJECTS == null) {
			SHARED_OBJECTS = new SharedObjects();
		}
		return SHARED_OBJECTS;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static SharedObjects getInstance() {
		return SHARED_OBJECTS;
	}
	
	/**
	 * Returns the instance of Hazelcast
	 * 
	 * @return the instance
	 */
	public static HazelcastInstance getHazelcastInstance() {
		// if Hazelcast instance is null, throws an exception
		if (SharedObjects.getInstance().getHazelcastClient() == null) {
			throw new MessageRuntimeException(UserInterfaceMessage.JEMG024E);
		}
		return SharedObjects.getInstance().getHazelcastClient();
	}
	
	/**
	 * @return the context path
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * 
	 * @param contextPath to set
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Sets <code>true</code> if hazelcast cluster has node with data.
	 * 
	 * @param isDataClusterAvailable
	 *            <code>true</code> if hazelcast cluster has node with data
	 */
	public void setDataClusterAvailable(boolean isDataClusterAvailable) {
		// sets current JEM group available
		this.isDataClusterAvailable = isDataClusterAvailable;
		if (isDataClusterAvailable) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG004I,
					getHazelcastConfig().getGroupConfig().getName());
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG005E,
					getHazelcastConfig().getGroupConfig().getName());
		}
	}

	/**
	 * @return <code>true</code> if Hazelcast client is connected to the
	 *         cluster, <code>false</code> otherwise
	 */
	public boolean isDataClusterAvailable() {
		return isDataClusterAvailable;
	}

	/**
	 * Shutdown the Hazelcast client
	 */
	public void shutdownHazelcatClient() {
		if (hazelcastClient != null) {
			hazelcastClient.getLifecycleService().shutdown();
			hazelcastClient = null;
		}
	}

	/**
	 * Returns Hazelcast client instance
	 * 
	 * @return the client of Hazelcast
	 */
	public HazelcastInstance getHazelcastClient() {
		return hazelcastClient;
	}

	/**
	 * Sets Hazelcast local instance
	 * 
	 * @param hazelcastClient
	 *            the Hazelcast client to set
	 */
	public void setHazelcastClient(HazelcastInstance hazelcastClient) {
		this.hazelcastClient = hazelcastClient;
	}

	/**
	 * Returns session instance name
	 * 
	 * @return the sessionsInstance name of session sharing, used by SHIRO to
	 *         cache authorization
	 */
	public String getSessionsInstance() {
		return sessionsInstance;
	}

	/**
	 * Sets session instance name
	 * 
	 * @param sessionsInstance
	 *            the sessionsInstance to set, name of session sharing, used by
	 *            SHIRo to cache authorization
	 */
	public void setSessionsInstance(String sessionsInstance) {
		this.sessionsInstance = sessionsInstance;
	}

	/**
	 * Returns if node must check the version of JEM
	 * 
	 * @return the checkVersion
	 *         <p>
	 *         if true the system will check the JEM WEB version against the JEM
	 *         NODES version and if there is a mismatch the system will exit
	 *         with error. If false the system will do the chek and in case of
	 *         failure set a warning
	 */
	public Boolean getCheckVersion() {
		return checkVersion;
	}

	/**
	 * Sets if node must check the version of JEM
	 * 
	 * @param checkVersion
	 *            the checkVersion to set.
	 *            <p>
	 *            if true the system will check the JEM WEB version against the
	 *            JEM NODES version and if there is a mismatch the system will
	 *            exit with error. If false the system will do the chek and in
	 *            case of failure set a warning
	 */
	public void setCheckVersion(Boolean checkVersion) {
		this.checkVersion = checkVersion;
	}

	/**
	 * Returns JEM version
	 * 
	 * @return the jemVersion
	 */
	public String getJemVersion() {
		return jemVersion;
	}

	/**
	 * Sets JEM version
	 * 
	 * @param jemVersion
	 *            the jemVersion to set
	 */
	public void setJemVersion(String jemVersion) {
		this.jemVersion = jemVersion;
	}

	/**
	 * @return the clientConfig
	 */
	public Config getHazelcastConfig() {
		return hazelcastConfig;
	}

	/**
	 * @param hazelcastConfig
	 *            the clientConfig to set
	 */
	public void setHazelcastConfig(Config hazelcastConfig) {
		this.hazelcastConfig = hazelcastConfig;
	}

	/**
	 * @return the connectorService see {@link ConnectorServiceFactory}
	 */
	public Service getConnectorService() {
		return connectorService;
	}

	/**
	 * @param connectorService
	 *            the connectorService to set {@link ConnectorServiceFactory}
	 */
	public void setConnectorService(Service connectorService) {
		this.connectorService = connectorService;
	}

}