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
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.BooleanReturnedObject;
import org.pepstock.jem.rest.entities.Nodes;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.entities.StringReturnedObject;
import org.pepstock.jem.rest.entities.SwarmConfig;
import org.pepstock.jem.rest.paths.SwarmNodesManagerPaths;

import com.sun.jersey.api.client.GenericType;

/**
 * Client side of NODES service.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SwarmNodesManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient
	 *            REST client instance
	 */
	public SwarmNodesManager(RestClient restClient) {
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
		SwarmNodesPostService<Nodes, String> service = new SwarmNodesPostService<Nodes, String>(SwarmNodesManagerPaths.LIST);
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
		SwarmNodesPostService<Nodes, String> service = new SwarmNodesPostService<Nodes, String>(SwarmNodesManagerPaths.LIST_BY_FILTER);
		GenericType<JAXBElement<Nodes>> generic = new GenericType<JAXBElement<Nodes>>() {

		};
		Nodes beans = service.execute(generic, filter);
		return beans.getNodes();
	}

	/**
	 * 
	 * @return 
	 * @throws JemException if any exception occurs
	 */
	public boolean start() throws JemException {
		SwarmNodesGetService<BooleanReturnedObject> service = new SwarmNodesGetService<BooleanReturnedObject>(SwarmNodesManagerPaths.START);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		BooleanReturnedObject result = service.execute(generic, null);
		return result.isValue();
	}
	
	/**
	 * 
	 * @return 
	 * @throws JemException if any exception occurs
	 */
	public boolean drain() throws JemException {
		SwarmNodesGetService<BooleanReturnedObject> service = new SwarmNodesGetService<BooleanReturnedObject>(SwarmNodesManagerPaths.DRAIN);
		GenericType<JAXBElement<BooleanReturnedObject>> generic = new GenericType<JAXBElement<BooleanReturnedObject>>() {

		};
		BooleanReturnedObject result = service.execute(generic, null);
		return result.isValue();
	}
	
	/**
	 * 
	 * @return 
	 * @throws JemException if any exception occurs
	 */
	public String getStatus() throws JemException {
		SwarmNodesGetService<StringReturnedObject> service = new SwarmNodesGetService<StringReturnedObject>(SwarmNodesManagerPaths.STATUS);
		GenericType<JAXBElement<StringReturnedObject>> generic = new GenericType<JAXBElement<StringReturnedObject>>() {

		};
		StringReturnedObject result = service.execute(generic, null);
		return result.getValue();
	}
	
	/**
	 * 
	 * @param method
	 * @param filter
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public SwarmConfiguration getSwarmConfiguration(String name) throws JemException {
		SwarmNodesPostService<SwarmConfig, String> service = new SwarmNodesPostService<SwarmConfig, String>(SwarmNodesManagerPaths.GET_CONFIG);
		GenericType<JAXBElement<SwarmConfig>> generic = new GenericType<JAXBElement<SwarmConfig>>() {

		};
		SwarmConfig result = service.execute(generic, name);
		return result.getConfiguration();
	}
	
	/**
	 * 
	 * @param conf 
	 * @return
	 * @throws JemException if any exception occurs
	 */
	public SwarmConfiguration updateSwarmConfiguration(SwarmConfiguration conf) throws JemException {
		SwarmNodesPostService<SwarmConfig, SwarmConfig> service = new SwarmNodesPostService<SwarmConfig, SwarmConfig>(SwarmNodesManagerPaths.UPDATE_CONFIG);
		GenericType<JAXBElement<SwarmConfig>> generic = new GenericType<JAXBElement<SwarmConfig>>() {

		};
		if (conf != null){
			SwarmConfig config = new SwarmConfig();
			config.setConfiguration(conf);
			SwarmConfig result = service.execute(generic, config);
			return result.getConfiguration();
		}
		return null;
	}
	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class SwarmNodesGetService<T extends ReturnedObject> extends DefaultGetService<T, String> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public SwarmNodesGetService(String subService) {
			super(SwarmNodesManager.this.getClient(), SwarmNodesManagerPaths.MAIN, subService);
		}

	}

	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.2
	 */
	class SwarmNodesPostService<T extends ReturnedObject, S> extends DefaultPostService<T, S> {

		/**
		 * @param client
		 * @param service
		 * @param subService
		 */
		public SwarmNodesPostService(String subService) {
			super(SwarmNodesManager.this.getClient(), SwarmNodesManagerPaths.MAIN, subService);
		}

	}

}