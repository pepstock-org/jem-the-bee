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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.configuration.SwarmConfiguration;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.SwarmNodesManagerPaths;
import org.pepstock.jem.util.filters.Filter;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST Client side of SWARM NODES service.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class SwarmNodesManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public SwarmNodesManager(RestClient restClient) {
		super(restClient, SwarmNodesManagerPaths.MAIN);
	}

	/**
	 * Returns the list of all nodes joined the cluster.
	 * 
	 * @param filter ipaddress or hostname filter
	 * @return collection of nodes
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<NodeInfoBean> getNodes(String filter) throws RestException {
		try {
			RequestBuilder builder = RequestBuilder.media(this);
			// creates the returned object
			ClientResponse response = builder.filter(filter).get(SwarmNodesManagerPaths.LIST);
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (List<NodeInfoBean>) JsonUtil.getInstance().deserializeList(response, NodeInfoBean.class);
			} else {
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not
	 * returned
	 * 
	 * @param filter a String that will be parsed as a {@link Filter}
	 * @return collection of nodes
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<NodeInfoBean> getNodesByFilter(String filter) throws RestException {
		try {
			RequestBuilder builder = RequestBuilder.media(this);
			// creates the returned object
			ClientResponse response = builder.filter(filter).get(SwarmNodesManagerPaths.LIST_BY_FILTER);
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return (List<NodeInfoBean>) JsonUtil.getInstance().deserializeList(response, NodeInfoBean.class);
			} else {
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (IOException e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}

	/**
	 * Starts swarm nodes, using a future task by executor service of Hazelcast.
	 * 
	 * @return always TRUE
	 * @throws RestException if any exception occurs
	 */
	public boolean start() throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// creates the returned object
		ClientResponse response = builder.put(SwarmNodesManagerPaths.START);
		String result = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else {
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Shuts down all the swarm nodes, using a future task by executor service
	 * of Hazelcast.
	 * 
	 * @return always true
	 * @throws RestException if any exception occurs
	 */
	public boolean drain() throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// creates the returned object
		ClientResponse response = builder.put(SwarmNodesManagerPaths.DRAIN);
		String result = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else {
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns the status of swarm
	 * 
	 * @return status if swarm
	 * @throws RestException if any exception occurs
	 */
	public String getStatus() throws RestException {
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// creates the returned object
		ClientResponse response = builder.get(SwarmNodesManagerPaths.STATUS);
		String result = response.getEntity(String.class);
		if (response.getStatus() == Status.OK.getStatusCode()){
			return result;
		} else {
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns the swarm configuration. It always exist because it always
	 * created after the first startup of JEM. It uses a name to use as a key in
	 * map, but this key is a constant
	 * 
	 * @param name key of configuration object
	 * @return swarm configuration item.
	 * @throws RestException if any exception occurs
	 */
	public SwarmConfiguration getSwarmConfiguration() throws RestException {
		try {
			RequestBuilder builder = RequestBuilder.media(this);
			// creates the returned object
			ClientResponse response = builder.get(SwarmNodesManagerPaths.GET_CONFIG);
			if (response.getStatus() == Status.OK.getStatusCode()) {
				return response.getEntity(SwarmConfiguration.class);
			} else {
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
		} catch (Exception e) {
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}
}