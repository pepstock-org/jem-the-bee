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
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.CommonPaths;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public final class RequestBuilder {

	private AbstractRestManager manager = null;

	private String contentType = null;

	private String acceptType = null;
	
	private Map<String, String> queryParams = new HashMap<String, String>();

	/**
	 * @param path
	 */
	private RequestBuilder(AbstractRestManager manager, String contentType, String acceptType) {
		this.manager = manager;
		this.contentType = contentType;
		this.acceptType = acceptType;
	}

	static RequestBuilder media(AbstractRestManager manager){
		return RequestBuilder.media(manager, MediaType.APPLICATION_JSON);
	}

	
	static RequestBuilder media(AbstractRestManager manager, String acceptType){
		return RequestBuilder.media(manager, acceptType, MediaType.APPLICATION_JSON );
	}

	static RequestBuilder media(AbstractRestManager manager, String acceptType, String contentType){
		RequestBuilder replacer = new RequestBuilder(manager, contentType, acceptType);
		return replacer;
	}

	RequestBuilder filter(String filter){
		if (filter != null){
			queryParams.put(CommonPaths.FILTER_QUERY_STRING, filter);
		}
		return this;
	}
	
	RequestBuilder query(Map<String, String> queryParams){
		if (queryParams != null){
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
		// gets the web resource
		WebResource resource = manager.getClient().getBaseWebResource();
		resource =  resource.path(manager.getMainPath()).path(service);
		if (queryParams != null && !queryParams.isEmpty()){
			for (Entry<String, String> entry : queryParams.entrySet()){
				resource = resource.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return resource.type(contentType).accept(acceptType);
	}

	ClientResponse get(String service){
		return getResource(service).get(ClientResponse.class);
	}

	ClientResponse put(String service){
		return getResource(service).put(ClientResponse.class);
	}

	ClientResponse put(String service, Object parm){
		if (parm == null){
			return put(service);
		}
		return getResource(service).put(ClientResponse.class, parm);
	}
	
	ClientResponse post(String service, Object parm){
		return getResource(service).post(ClientResponse.class, parm);
	}

	ClientResponse delete(String service){
		return getResource(service).delete(ClientResponse.class);
	}

	ClientResponse delete(String service, Object parm){
		if (parm == null){
			return delete(service);
		}
		return getResource(service).delete(ClientResponse.class, parm);
	}
	
	Boolean putAndGetBoolean(String path) throws RestException{
		return putAndGetBoolean(path, null);
	}

	/**
	 * 
	 * @param path
	 * @param parm
	 * @return
	 * @throws RestException
	 */
	Boolean putAndGetBoolean(String path, Object parm) throws RestException{
		try {
			// creates the returned object
			ClientResponse response = null;
			if (parm != null){
				response = put(path, parm);
			} else {
				response = put(path);
			}

			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(Boolean.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return false;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
		} catch (Exception e){
			LogAppl.getInstance().debug(e.getMessage(), e);
			throw new RestException(e);
		}
	}
}
