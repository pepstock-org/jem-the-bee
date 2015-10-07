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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.CommonPaths;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Utility to creates a right REST request, defining the accept and content type of REST service to call.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class RequestBuilder {

	private AbstractRestManager manager = null;

	private String contentType = null;

	private String acceptType = null;
	
	private Map<String, String> queryParams = new HashMap<String, String>();

	/**
	 * Creates the object with the client REST manager, content type and accept type
	 * @param manager rest manager
	 * @param contentType HTTP content type value
	 * @param acceptType HTTP accept type value
	 * @see MediaType
	 */
	private RequestBuilder(AbstractRestManager manager, String contentType, String acceptType) {
		this.manager = manager;
		this.contentType = contentType;
		this.acceptType = acceptType;
	}

	/**
	 * Creates a request manager with content and accept type to APPLICATION/JSON
	 * @param manager rest manager
	 * @return the builder for further calls
	 */
	static RequestBuilder media(AbstractRestManager manager){
		return RequestBuilder.media(manager, MediaType.APPLICATION_JSON);
	}

	/**
	 * Creates a request manager with accept type passed as argument and content type to APPLICATION/JSON
	 * @param manager rest manager
	 * @param acceptType HTTP accept type value
	 * @return the builder for further calls
	 */
	static RequestBuilder media(AbstractRestManager manager, String acceptType){
		return RequestBuilder.media(manager, acceptType, MediaType.APPLICATION_JSON );
	}

	/**
	 *  Creates a request manager with accept and content types passed as arguments
	 * @param manager rest manager
	 * @param contentType HTTP content type value
	 * @param acceptType HTTP accept type value
	 * @return the builder for further calls
	 */
	static RequestBuilder media(AbstractRestManager manager, String acceptType, String contentType){
		RequestBuilder replacer = new RequestBuilder(manager, contentType, acceptType);
		return replacer;
	}

	/**
	 * Adds a HTTP query parameter value to the parameter "filter".
	 * @see CommonPaths.FILTER_QUERY_STRING
	 * @param filter filter value to pass
	 * @return the builder for further calls
	 */
	RequestBuilder filter(String filter){
		if (filter != null){
			queryParams.put(CommonPaths.FILTER_QUERY_STRING, filter);
		}
		return this;
	}
	
	/**
	 * Adds a complete map of HTTP query parameters (key/value).
	 * @param queryParams map of HTTP query parameters
	 * @return the builder for further calls
	 */
	RequestBuilder query(Map<String, String> queryParams){
		if (queryParams != null && !queryParams.isEmpty()){
			this.queryParams.putAll(queryParams);
		}
		return this;
	}

	/**
	 * Return the web resource builder to create the resource to submit by REST
	 * @param service part of URL of REST service
	 * @return web resource builder
	 */
	private WebResource.Builder getResource(String service){
		// gets the web resource from manager
		WebResource resource = manager.getClient().getBaseWebResource();
		// sets the main path and service
		resource =  resource.path(manager.getMainPath()).path(service);
		// checks if there is any query parameter
		if (queryParams != null && !queryParams.isEmpty()){
			// loads all query parameter
			for (Entry<String, String> entry : queryParams.entrySet()){
				resource = resource.queryParam(entry.getKey(), entry.getValue());
			}
		}
		// returns the REST resource
		// setting content and accept types
		return resource.type(contentType).accept(acceptType);
	}

	/**
	 * Performs GET REST call
	 * @param service service to call
	 * @return REST client response
	 */
	ClientResponse get(String service) throws RestException{
		try {
			return getResource(service).get(ClientResponse.class);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	/**
	 * Performs PUT REST call
	 * @param service service to call
	 * @return REST client response
	 */
	ClientResponse put(String service) throws RestException{
		try{
			return getResource(service).put(ClientResponse.class);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	/**
	 * Performs PUT REST call
	 * @param service service to call
	 * @param parm object to serialize on the body to send
	 * @return REST client response
	 */
	ClientResponse put(String service, Object parm) throws RestException{
		try{
			if (parm == null){
				return put(service);
			}
			return getResource(service).put(ClientResponse.class, parm);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	/**
	 * Performs POST REST call
	 * @param service service to call
	 * @param parm object to serialize on the body to send
	 * @return REST client response
	 */
	ClientResponse post(String service, Object parm) throws RestException{
		try{
			return getResource(service).post(ClientResponse.class, parm);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	/**
	 * Performs DELETE REST call
	 * @param service service to call
	 * @return REST client response
	 */
	ClientResponse delete(String service) throws RestException{
		try{
			return getResource(service).delete(ClientResponse.class);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}

	/**
	 * Performs DELETE REST call
	 * @param service service to call
	 * @param parm object to serialize on the body to send
	 * @return REST client response
	 */
	ClientResponse delete(String service, Object parm) throws RestException{
		try{
			if (parm == null){
				return delete(service);
			}
			return getResource(service).delete(ClientResponse.class, parm);
		} catch (Exception e) {
			throw new RestException(e);
		}
	}
}
