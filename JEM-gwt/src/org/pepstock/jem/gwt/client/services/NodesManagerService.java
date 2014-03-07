/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.util.filters.Filter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for nodes
 * management
 */
@RemoteServiceRelativePath(Services.NODES)
public interface NodesManagerService extends RemoteService {

	/**
	 * Returns the nodes using a filter by hostname or ip address.
	 * 
	 * @param nodesFilter
	 *            filter for nodes
	 * @return list of nodes
	 * @throws JemException
	 *             if errors occurs
	 */
	Collection<NodeInfoBean> getNodes(String nodesFilter) throws JemException;

	/**
	 * Returns the nodes using a filter.
	 * 
	 * @param nodesFilter
	 *            a String that will be parsed as a {@link Filter}
	 * @return list of nodes
	 * @throws JemException
	 *             if errors occurs
	 */
	Collection<NodeInfoBean> getNodesByFilter(String filterString) throws JemException;

	/**
	 * Drains a list of nodes.
	 * 
	 * @param nodes
	 *            list of node to be drained
	 * @return always true
	 * @throws JemException
	 */
	Boolean drain(Collection<NodeInfoBean> nodes) throws JemException;

	/**
	 * Starts a list of nodes.
	 * 
	 * @param nodes
	 *            list of node to be drained
	 * @return always true
	 * @throws JemException
	 */
	Boolean start(Collection<NodeInfoBean> nodes) throws JemException;

	/**
	 * Returns the config file used to configure the node. Could be affinity
	 * loader policy file or JEM one.
	 * 
	 * @param node
	 *            node to get config file
	 * @param what
	 *            type (affinity loader policy or jem)
	 * @return config file
	 * @throws JemException
	 *             if an error occurs
	 */
	ConfigurationFile getNodeConfigFile(NodeInfoBean node, String what) throws JemException;

	/**
	 * Returns the config file used to save the configuration of node. Could be
	 * affinity loader policy file or JEM one.
	 * 
	 * @param node
	 *            node to get config file
	 * @param file
	 *            configuration file to save
	 * @param what
	 *            type (affinity loader policy or jem)
	 * @return config file
	 * @throws JemException
	 *             if an error occurs
	 */
	ConfigurationFile saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what) throws JemException;

	/**
	 * Returns the config file used to configure the environment. Could be
	 * Hazelcast config file or JEM one.
	 * 
	 * @param what
	 *            type (hazelcast or jem)
	 * @return config file
	 * @throws JemException
	 *             if an error occurs
	 */
	ConfigurationFile getEnvConfigFile(String what) throws JemException;

	/**
	 * Returns the config file used to save the configuration of env. Could be
	 * Hazelcast config file or JEM one.
	 * 
	 * @param file
	 *            configuration file to save
	 * @param what
	 *            type (hazelcast or jem)
	 * @return config file
	 * @throws JemException
	 *             if an error occurs
	 */
	ConfigurationFile saveEnvConfigFile(ConfigurationFile file, String what) throws JemException;

	/**
	 * Checks if the config file content, updated by UI, is correct
	 * 
	 * @param content
	 *            content of config file
	 * @param what
	 *            type
	 * @return always true
	 * @throws JemException
	 *             if an error occurs
	 */
	Boolean checkConfigFile(String content, String what) throws JemException;

	/**
	 * Checks if the affintiy policy content, updated by UI, is correct
	 * 
	 * @param node
	 *            node where policy is executed
	 * @param content
	 *            content of policy
	 * @return result of policy
	 * @throws JemException
	 *             if an error occurs
	 */
	Result checkAffinityPolicy(NodeInfoBean node, String content) throws JemException;

	/**
	 * Returns the result of TOP command
	 * 
	 * @param node
	 *            node where to perform the TOP cmd
	 * @return result of TOP
	 * @throws JemException
	 *             if an error occurs
	 */
	String top(NodeInfoBean node) throws JemException;

	/**
	 * Returns the log (last 100 rows)
	 * 
	 * @param node
	 *            node to get log
	 * @return result of log
	 * @throws JemException
	 *             if an error occurs
	 */
	String log(NodeInfoBean node) throws JemException;

	/**
	 * Display of all Hazelcast members
	 * 
	 * @param node
	 *            node to get result
	 * @return all members of Hazelcast group
	 * @throws JemException
	 *             if error occurs
	 */
	String displayCluster(NodeInfoBean node) throws JemException;

	/**
	 * Update domain or static affinities of node
	 * 
	 * @param node
	 *            node to update
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	Boolean update(NodeInfoBean node) throws JemException;
}