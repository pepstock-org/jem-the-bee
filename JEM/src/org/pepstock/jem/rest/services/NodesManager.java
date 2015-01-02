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
package org.pepstock.jem.rest.services;

import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.ConfigurationFileContent;
import org.pepstock.jem.rest.entities.Nodes;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.paths.NodesManagerPaths;
import org.pepstock.jem.util.filters.Filter;

import com.sun.jersey.api.client.GenericType;

/**
 * REST Client side of NODES service.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class NodesManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient
	 *            REST client instance
	 */
	public NodesManager(RestClient restClient) {
		super(restClient);
	}


	/**
	 * Returns the list of all normal nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param filter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws JemException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodes(String filter) throws JemException {
		NodesPostService<Nodes, String> service = new NodesPostService<Nodes, String>(NodesManagerPaths.LIST);
		GenericType<JAXBElement<Nodes>> generic = new GenericType<JAXBElement<Nodes>>() {

		};
		Nodes beans = service.execute(generic, filter);
		return beans.getNodes();
	}
	
	/**
	 * Returns the list of all swarm nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param filter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws JemException if any exception occurs
	 */
	public Collection<NodeInfoBean> getSwarmNodes(String filter) throws JemException {
		NodesPostService<Nodes, String> service = new NodesPostService<Nodes, String>(NodesManagerPaths.SWARM_LIST);
		GenericType<JAXBElement<Nodes>> generic = new GenericType<JAXBElement<Nodes>>() {

		};
		Nodes beans = service.execute(generic, filter);
		return beans.getNodes();
	}
	
	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not returned
	 * 
	 * @param filter a String that will be parsed as a {@link Filter}
	 * @return collection of nodes
	 * @throws JemException if any exception occurs
	 */
	public Collection<NodeInfoBean> getNodesByFilter(String filter) throws JemException {
		NodesPostService<Nodes, String> service = new NodesPostService<Nodes, String>(NodesManagerPaths.LIST_BY_FILTER);
		GenericType<JAXBElement<Nodes>> generic = new GenericType<JAXBElement<Nodes>>() {

		};
		Nodes beans = service.execute(generic, filter);
		return beans.getNodes();
	}

	/**
	 * Update the domain or static affinities of node
	 * @param node node to update
	 * @return always true
	 * @throws JemException if any exception occurs
	 */
	public boolean update(NodeInfoBean node) throws JemException {
		NodesPostService<BooleanReturnedObject, NodeInfoBean> service = new NodesPostService<BooleanReturnedObject, NodeInfoBean>(NodesManagerPaths.UPDATE);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		BooleanReturnedObject result = service.execute(generic, node);
		return result.isValue();
	}
	
	/**
	 * Returns the top command result
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
	 * @throws JemException if any exception occurs
	 */
	public String top(NodeInfoBean node) throws JemException {
		NodesPostService<StringReturnedObject, NodeInfoBean> service = new NodesPostService<StringReturnedObject, NodeInfoBean>(NodesManagerPaths.TOP);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, node);
		return result.getValue();
	}
	
	/**
	 * Returns part of JEM node log
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
	 * @throws JemException if any exception occurs
	 */
	public String log(NodeInfoBean node) throws JemException {
		NodesPostService<StringReturnedObject, NodeInfoBean> service = new NodesPostService<StringReturnedObject, NodeInfoBean>(NodesManagerPaths.LOG);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, node);
		return result.getValue();
	}
	
	/**
	 * Returns the HAZELCAST cluster status which is the list of all members.<br>
	 * This is a sampl output format:<br>
	 * <code>
	 *  Members [2] {
    	    Member [127.0.0.1]:5710 this
    	    Member [127.0.0.1]:5711 
    	}
	 * </code>
	 * 
	 * @param node node where execute a future task to get top command 
	 * @return content file in String
	 * @throws JemException if any exception occurs
	 */
	public String displayCluster(NodeInfoBean node) throws JemException {
		NodesPostService<StringReturnedObject, NodeInfoBean> service = new NodesPostService<StringReturnedObject, NodeInfoBean>(NodesManagerPaths.DISPLAY_CLUSTER);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, node);
		return result.getValue();
	}
	
	/**
	 * Returns the configuration file for the node
	 * 
	 * @param node node where execute a future task to get the config file 
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile getNodeConfigFile(NodeInfoBean node, String what) throws JemException {
		if (node == null || what == null){
			throw new JemException("node or what is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setNode(node);
		parm.setWhat(what);
		
		NodesPostService<ConfigurationFileContent, ConfigurationFileContent> service = new NodesPostService<ConfigurationFileContent, ConfigurationFileContent>(NodesManagerPaths.GET_NODE_CONFIG_FILE);
		GenericType<JAXBElement<ConfigurationFileContent>> generic = new GenericType<JAXBElement<ConfigurationFileContent>>() {

		};
		ConfigurationFileContent result = service.execute(generic, parm);
		return result.getFile();
	}
	
	/**
	 * Saves the configuration file for the node
	 * 
	 * @param node node where execute a future task to get the config file 
	 * @param file configuration file to save
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what) throws JemException {
		if (node == null || what == null || file == null){
			throw new JemException("node or what or file is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setNode(node);
		parm.setWhat(what);
		parm.setFile(file);
		
		NodesPostService<ConfigurationFileContent, ConfigurationFileContent> service = new NodesPostService<ConfigurationFileContent, ConfigurationFileContent>(NodesManagerPaths.SAVE_NODE_CONFIG_FILE);
		GenericType<JAXBElement<ConfigurationFileContent>> generic = new GenericType<JAXBElement<ConfigurationFileContent>>() {

		};
		ConfigurationFileContent result = service.execute(generic, parm);
		return result.getFile();
	}
	
	/**
	 * Returns the configuration file for the environment
	 * 
	 * @param what type of configuration file to return
	 * @return Configuration file container
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile getEnvConfigFile(String what) throws JemException {
		if (what == null){
			throw new JemException("what is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setWhat(what);
		
		NodesPostService<ConfigurationFileContent, ConfigurationFileContent> service = new NodesPostService<ConfigurationFileContent, ConfigurationFileContent>(NodesManagerPaths.GET_ENV_CONFIG_FILE);
		GenericType<JAXBElement<ConfigurationFileContent>> generic = new GenericType<JAXBElement<ConfigurationFileContent>>() {

		};
		ConfigurationFileContent result = service.execute(generic, parm);
		return result.getFile();
	}
	
	/**
	 * Returns the configuration file for the environment after saving it
	 * 
	 * @param file configuration file to save 
	 * @param what type of configuration file to return
	 * @return Configuration new file container
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile saveEnvConfigFile(ConfigurationFile file, String what) throws JemException {
		if (what == null || file == null){
			throw new JemException("what or file is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setWhat(what);
		parm.setFile(file);
		
		NodesPostService<ConfigurationFileContent, ConfigurationFileContent> service = new NodesPostService<ConfigurationFileContent, ConfigurationFileContent>(NodesManagerPaths.SAVE_ENV_CONFIG_FILE);
		GenericType<JAXBElement<ConfigurationFileContent>> generic = new GenericType<JAXBElement<ConfigurationFileContent>>() {

		};
		ConfigurationFileContent result = service.execute(generic, parm);
		return result.getFile();
	}
	
	/**
	 * Checks if syntax of content is correct.
	 * @param content content of configuration file
	 * @param what type of config file
	 * @return always true
	 * @throws JemException if any exception occurs
	 */
	public Boolean checkConfigFile(String content, String what) throws JemException {
		if (what == null || content == null){
			throw new JemException("what or content is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setWhat(what);
		parm.setContent(content);
		
		NodesPostService<BooleanReturnedObject, ConfigurationFileContent> service = new NodesPostService<BooleanReturnedObject, ConfigurationFileContent>(NodesManagerPaths.SAVE_ENV_CONFIG_FILE);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		BooleanReturnedObject result = service.execute(generic, parm);
		return result.isValue();
	}
	
	/**
	 * Checks if syntax of affinity loader policy content is correct.
	 * @param node node where execute a future task  
	 * @param content type of affinity policy
	 * @return always true
	 * @throws JemException if any exception occurs
	 */
	public Result checkAffinityPolicy(NodeInfoBean node, String content) throws JemException {
		if (node == null || content == null){
			throw new JemException("node or content is null!");
		}
		ConfigurationFileContent parm = new ConfigurationFileContent();
		parm.setNode(node);
		parm.setContent(content);
		
		NodesPostService<ConfigurationFileContent, ConfigurationFileContent> service = new NodesPostService<ConfigurationFileContent, ConfigurationFileContent>(NodesManagerPaths.SAVE_NODE_CONFIG_FILE);
		GenericType<JAXBElement<ConfigurationFileContent>> generic = new GenericType<JAXBElement<ConfigurationFileContent>>() {

		};
		ConfigurationFileContent result = service.execute(generic, parm);
		return result.getResult();
	}
	
	/**
	 * Inner service, which extends post the default post service.
	 *  
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class NodesPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * Constructs the REST service, using HTTP client and service and subservice paths, passed as argument
		 * 
		 * @param subService subservice path
		 * 
		 */
		public NodesPostService(String subService) {
			super(NodesManager.this.getClient(), NodesManagerPaths.MAIN, subService);
		}

	}

}