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
import org.pepstock.jem.UpdateNode;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.NodesManagerPaths;
import org.pepstock.jem.util.filters.Filter;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST Client side of NODES service.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
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
		super(restClient, NodesManagerPaths.MAIN);
	}


	/**
	 * Returns the list of all normal nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param filter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<NodeInfoBean> getNodes(String filter) throws RestException {
	    try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
	    	RequestBuilder builder = RequestBuilder.media(this);
	    	// performs the request adding the filter query param
			ClientResponse response = builder.filter(filter).get(NodesManagerPaths.LIST);
			// if HTTP status code is OK,parses the result to list of nodes
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<NodeInfoBean>)JsonUtil.getInstance().deserializeList(response, NodeInfoBean.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
	    } catch (IOException e){
	    	// throw an exception of JSON parsing
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Returns the list of all swarm nodes joined the cluster. UNKNOWN members are not returned.
	 * 
	 * @param filter filter contains all tokens to performs filtering
	 * @return collection of nodes
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<NodeInfoBean> getSwarmNodes(String filter) throws RestException {
	    try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
	    	RequestBuilder builder = RequestBuilder.media(this);
	    	// performs the request adding the filter query param
			ClientResponse response = builder.filter(filter).get(NodesManagerPaths.SWARM_LIST);
			// if HTTP status code is OK,parses the result to list of nodes
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<NodeInfoBean>)JsonUtil.getInstance().deserializeList(response, NodeInfoBean.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
	    } catch (IOException e){
	    	// throw an exception of JSON parsing
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Returns the list of all nodes joined the cluster. UNKNOWN members are not returned
	 * 
	 * @param filter a String that will be parsed as a {@link Filter}
	 * @return collection of nodes
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<NodeInfoBean> getNodesByFilter(String filter) throws RestException {
	    try {
			// creates a request builder with the APPLICATION/JSON media type as
			// accept type (the default)
	    	RequestBuilder builder = RequestBuilder.media(this);
	    	// performs the request adding the filter query param
			ClientResponse response = builder.filter(filter).get(NodesManagerPaths.LIST_BY_FILTER);
			// if HTTP status code is OK,parses the result to list of nodes
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<NodeInfoBean>)JsonUtil.getInstance().deserializeList(response, NodeInfoBean.class);
			} else {
				// otherwise throws the exception using the
				// body of response as message of exception
				// IT MUST CONSUME the response
				// otherwise there is a HTTP error
				throw new RestException(response.getStatus(), getValue(response, String.class));
			}
	    } catch (IOException e){
	    	// throw an exception of JSON parsing
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Update some attributes of a node
	 * @param key node key where performs the action
	 * @param update set of attributes to change on node
	 * @return <code>true</code> if action ended correctly, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public boolean update(String key, UpdateNode update) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.UPDATE).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call passing the update node attributes
		ClientResponse response = builder.put(path, update);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Starts a node
	 * @param key node key where performs the action
	 * @return <code>true</code> if action ended correctly, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public boolean start(String key) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.START).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}
	
	/**
	 * Drains a node
	 * @param key node key where performs the action
	 * @return <code>true</code> if action ended correctly, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public boolean drain(String key) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.DRAIN).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.put(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return false;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns the top command result
	 * 
	 * @param key node key where performs the action
	 * @return content of command result
	 * @throws RestException if any exception occurs
	 */
	public String top(String key) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.TOP).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return result;
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}
	
	/**
	 * Returns part of JEM node log
	 * 
	 * @param key node key where performs the action
	 * @return content of command result
	 * @throws RestException if any exception occurs
	 */
	public String log(String key) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.LOG).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return result;
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
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
	 * @param key node key where performs the action
	 * @return content of command result
	 * @throws RestException if any exception occurs
	 */
	public String displayCluster(String key) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.DISPLAY_CLUSTER).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return result;
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}

	/**
	 * Returns single node by its key
	 * 
	 * @param key node key to search
	 * @return Configuration file container
	 * @throws RestException if any exception occurs
	 */
	public NodeInfoBean getNode(String key) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.GET).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// if HTTP status code is ok, returns the node info object
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(NodeInfoBean.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			String result = getValue(response, String.class);
			LogAppl.getInstance().debug(result);
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}
	
	/**
	 * Returns the configuration file for the node
	 * 
	 * @param key node key to search
	 * @param type type of configuration file to return
	 * @return Configuration file container
	 * @throws RestException if any exception occurs
	 */
	public ConfigurationFile getNodeConfigFile(String key, String type) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// replaces on the path the node key and the type of configuration file needed
		String path = PathReplacer.path(NodesManagerPaths.GET_NODE_CONFIG).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).
				replace(NodesManagerPaths.TYPE_PATH_PARAM, type).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// if HTTP status code is ok, returns the configuration file
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(ConfigurationFile.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			String result = getValue(response, String.class);
			LogAppl.getInstance().debug(result);
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}
	
	/**
	 * Returns the configuration file for the environment
	 * 
	 * @param type type of configuration file to return
	 * @return Configuration file container
	 * @throws RestException if any exception occurs
	 */
	public ConfigurationFile getEnvConfigFile(String type) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// replaces on the path the type of configuration file needed
		String path = PathReplacer.path(NodesManagerPaths.GET_ENV_CONFIG).replace(NodesManagerPaths.TYPE_PATH_PARAM, type).build();
		// performs REST call
		ClientResponse response = builder.get(path);
		// if HTTP status code is ok, returns the configuration file
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(ConfigurationFile.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			String result = getValue(response, String.class);
			LogAppl.getInstance().debug(result);
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}
	
	/**
	 * Checks if syntax of content is correct.
	 * @param content content of configuration file
	 * @param type type of configuration file to return
	 * @return  <code>true</code> if action ended correctly, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Boolean checkConfigFile(String content, String type) throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type and the TEXT/PLAIN media type as content type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN);
		// replaces on the path the type of configuration file needed
		String path = PathReplacer.path(NodesManagerPaths.CHECK_CONFIG).replace(NodesManagerPaths.TYPE_PATH_PARAM, type).build();
		// performs REST call passing the content to check
		ClientResponse response = builder.put(path, content);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string
		String result = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean result
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(result);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), result);
		}
	}
	
	/**
	 * Checks if syntax of affinity loader policy content is correct.
	 * @param key node key to search
	 * @param content type of affinity policy
	 * @return <code>true</code> if action ended correctly, otherwise false
	 * @throws RestException if any exception occurs
	 */
	public Result checkAffinityPolicy(String key, String content) throws RestException {
		// creates a request builder with the APPLICATION/JSON media type as accept
		// type and the TEXT/PLAIN media type as content type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
		// replaces on the path the node key
		String path = PathReplacer.path(NodesManagerPaths.CHECK_AFFINITY_POLICY).replace(NodesManagerPaths.NODEKEY_PATH_PARAM, key).build();
		// performs REST call passing the content to check
		ClientResponse response = builder.post(path, content);
		// if HTTP status code is ok, returns the result of affinity
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(Result.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the node key passed as path parameters hasn't identified any node
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			String result = getValue(response, String.class);
			LogAppl.getInstance().debug(result);
			return null;
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}
}