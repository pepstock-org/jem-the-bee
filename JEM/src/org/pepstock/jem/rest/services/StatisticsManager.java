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
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.rest.JsonUtil;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.paths.StatisticsManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST Client side of STATISTICS or ADMINISTRATION services.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StatisticsManager extends AbstractRestManager {

	/**
	 * Creates a new REST manager using a RestClient
	 * 
	 * @param restClient REST client instance
	 */
	public StatisticsManager(RestClient restClient) {
		super(restClient, StatisticsManagerPaths.MAIN);
	}

	/**
	 * Returns the collection of all active samples in JEM. 
	 * 
     * @return collection of samples
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<LightSample> getSamples() throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.GET_SAMPLES);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<LightSample>)JsonUtil.getInstance().deserializeList(response, LightSample.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Calculates and returns last sample of JEM statistics
	 * 
     * @return last sample of statistics
	 * @throws RestException if any exception occurs
	 */
	public LightSample getCurrentSample() throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.GET_CURRENT_SAMPLE);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(LightSample.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Returns a formatted string with all the contentions on resources.<br>
	 * If GRS is not activated, return that GRS is not activated!
	 * 
     * @param resourceKey resource pattern to check 
     * @return a formatted string with all contentions information
	 * @throws RestException if any exception occurs
	 */
	public String displayRequestors(String resourceKey)throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.DISPLAY_REQUESTORS, resourceKey);
			String result = response.getEntity(String.class);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return result;
			} else {
				throw new RestException(response.getStatus(), result);
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Gets a collection of REDO statement if the cluster is waiting to store and persist objects.<br>
	 * It happens when the database to persist is not reachable.
	 * 
     * @return collection of REDO statements
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<RedoStatement> getAllRedoStatements() throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.GET_ALL_REDO_STATEMENTS);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<RedoStatement>)JsonUtil.getInstance().deserializeList(response, RedoStatement.class);
			} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
				String result = response.getEntity(String.class);
				LogAppl.getInstance().debug(result);
				return null;
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
     * Returns a about object with information about version, creation and licenses
     * @return about instance
	 * @throws RestException if any exception occurs
	 */
	public About getAbout()throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.ABOUT);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(About.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
     * Returns an array of system information
     * @return an array of system information
	 * @throws RestException if any exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getEnvironmentInformation()throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = get(StatisticsManagerPaths.INFOS);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return (List<String>)JsonUtil.getInstance().deserializeList(response, String.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
}