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

import com.sun.jersey.api.client.GenericType;

/**
 * Client side of NODES service.
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param jobs
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param filter
	 * @return
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile getNodeConfigFile(NodeInfoBean node, String what) throws JemException {
		if (node == null || what == null){
			// TODO message
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile saveNodeConfigFile(NodeInfoBean node, ConfigurationFile file, String what) throws JemException {
		if (node == null || what == null || file == null){
			// TODO message
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile getEnvConfigFile(String what) throws JemException {
		if (what == null){
			// TODO message
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public ConfigurationFile saveEnvConfigFile(ConfigurationFile file, String what) throws JemException {
		if (what == null || file == null){
			// TODO message
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Boolean checkConfigFile(String content, String what) throws JemException {
		if (what == null || content == null){
			// TODO message
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
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public Result checkAffinityPolicy(NodeInfoBean node, String content) throws JemException {
		if (node == null || content == null){
			// TODO message
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
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class NodesPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public NodesPostService(String subService) {
			super(NodesManager.this.getClient(), NodesManagerPaths.MAIN, subService);
		}

	}

}