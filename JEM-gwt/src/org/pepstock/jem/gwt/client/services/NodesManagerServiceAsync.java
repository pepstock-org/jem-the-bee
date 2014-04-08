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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async service
 * 
 * @see NodesManagerService
 */
public interface NodesManagerServiceAsync {
	/**
	 * @see NodesManagerService#getNodes(String)
	 * @param nodesFilter
	 * @param callback
	 */
	void getNodes(String nodesFilter, AsyncCallback<Collection<NodeInfoBean>> callback);

	/**
	 * @see NodesManagerService#getNodesByFilter(String)
	 * @param filterString
	 * @param callback
	 */
	void getNodesByFilter(String filterString, AsyncCallback<Collection<NodeInfoBean>> callback);

	/**
	 * @see NodesManagerService#drain(Collection)
	 * @param nodes
	 * @param callback
	 */
	void drain(Collection<NodeInfoBean> nodes, AsyncCallback<Boolean> callback);

	/**
	 * @see NodesManagerService#start(Collection)
	 * @param nodes
	 * @param callback
	 */
	void start(Collection<NodeInfoBean> nodes, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @param node
	 * @param what
	 * @param callback
	 */
	void getNodeConfigFile(NodeInfoBean node, String what, AsyncCallback<ConfigurationFile> callback);

	/**
	 * Returns the result of TOP command
	 * 
	 * @param node
	 *            node where to perform the TOP cmd
	 * @param callback
	 * @return result of TOP
	 * @throws JemException
	 *             if an error occurs
	 */
	void top(NodeInfoBean node, AsyncCallback<String> callback);

	/**
	 * 
	 * @param node
	 * @param callback
	 */
	void log(NodeInfoBean node, AsyncCallback<String> callback);

	/**
	 * 
	 * @param node
	 * @param callback
	 */
	void displayCluster(NodeInfoBean node, AsyncCallback<String> callback);

	/**
	 * 
	 * @param what
	 * @param callback
	 */
	void getEnvConfigFile(String what, AsyncCallback<ConfigurationFile> callback);

	/**
	 * 
	 * @param content
	 * @param what
	 * @param callback
	 */
	void checkConfigFile(String content, String what, AsyncCallback<Boolean> callback);

	/**
	 * 
	 * @param node
	 * @param content
	 * @param callback
	 */
	void checkAffinityPolicy(NodeInfoBean node, String content, AsyncCallback<Result> callback);

	/**
	 * 
	 * @param node
	 * @param file
	 * @param what
	 * @param callback
	 */
	void saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what, AsyncCallback<ConfigurationFile> callback);

	/**
	 * 
	 * @param file
	 * @param what
	 * @param callback
	 */
	void saveEnvConfigFile(ConfigurationFile file, String what, AsyncCallback<ConfigurationFile> callback);

	/**
	 * 
	 * @param node
	 * @param callback
	 */
	void update(NodeInfoBean node, AsyncCallback<Boolean> callback);

}