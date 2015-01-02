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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.services.NodesManagerService;
import org.pepstock.jem.gwt.server.services.NodesManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;

/**
 * Is GWT server service which can provide all methods to manage nodes.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NodesManagerServiceImpl extends DefaultManager implements NodesManagerService {

	private static final long serialVersionUID = 1L;

	private transient NodesManager nodesManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#getNodes(java
	 * .lang.String)
	 */
	@Override
	public Collection<NodeInfoBean> getNodes(String nodesFilter) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.getNodes(nodesFilter);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#getNodesByFilter
	 * (java.lang.String)
	 */
	@Override
	public Collection<NodeInfoBean> getNodesByFilter(String filterString) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.getNodesByFilter(filterString);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#drain(java.util
	 * .Collection)
	 */
	@Override
	public Boolean drain(Collection<NodeInfoBean> nodes) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.drain(nodes);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#start(java.util
	 * .Collection)
	 */
	@Override
	public Boolean start(Collection<NodeInfoBean> nodes) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.start(nodes);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#update(org.pepstock
	 * .jem.NodeInfoBean)
	 */
	@Override
	public Boolean update(NodeInfoBean node) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.update(node);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#getConfigFile
	 * (org.pepstock.jem.NodeInfoBean, java.lang.String)
	 */
	@Override
	public ConfigurationFile getNodeConfigFile(NodeInfoBean node, String what) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.getNodeConfigFile(node, what);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#getEnvConfigFile
	 * (java.lang.String)
	 */
	@Override
	public ConfigurationFile getEnvConfigFile(String what) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.getEnvConfigFile(what);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#saveNodeConfigFile
	 * (org.pepstock.jem.NodeInfoBean, org.pepstock.jem.node.ConfigurationFile,
	 * java.lang.String)
	 */
	@Override
	public ConfigurationFile saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.saveNodeConfigFile(node, file, what);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#saveEnvConfigFile
	 * (org.pepstock.jem.node.ConfigurationFile, java.lang.String)
	 */
	@Override
	public ConfigurationFile saveEnvConfigFile(ConfigurationFile file, String what) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.saveEnvConfigFile(file, what);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#checkConfigFile
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean checkConfigFile(String content, String what) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.checkConfigFile(content, what);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#checkAffinityPolicy
	 * (org.pepstock.jem.NodeInfoBean, java.lang.String)
	 */
	@Override
	public Result checkAffinityPolicy(NodeInfoBean node, String content) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.checkAffinityPolicy(node, content);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#top(org.pepstock
	 * .jem.NodeInfoBean)
	 */
	@Override
	public String top(NodeInfoBean node) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.top(node);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#log(org.pepstock
	 * .jem.NodeInfoBean)
	 */
	@Override
	public String log(NodeInfoBean node) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.log(node);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.NodesManagerService#displayCluster
	 * (org.pepstock.jem.NodeInfoBean)
	 */
	@Override
	public String displayCluster(NodeInfoBean node) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (nodesManager == null) {
			initManager();
		}
		try {
			return nodesManager.displayCluster(node);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/**
	 * Initializes a manager
	 * 
	 * @throws JemException
	 *             if any exception occurs
	 */
	private synchronized void initManager() throws JemException {
		if (nodesManager == null) {
			try {
				nodesManager = new NodesManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG040E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}