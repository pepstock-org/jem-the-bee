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

import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreference;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST service to manage user authentication and get and set of user preferences.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
public class LoginManager extends AbstractRestManager {

	/**
 	 * Creates a new REST manager using a RestClient
	 * @param restClient REST client instance
	 */
    public LoginManager(RestClient restClient) {
	    super(restClient, LoginManagerPaths.MAIN);
    }

	/**
	 * Returns the user already logged otherwise null.
	 * 
	 * @return the user already logged otherwise null
	 * @throws RestException if any exception occurs
	 */
	public LoggedUser getUser() throws RestException{
	    try {
			// creates the returned object
			ClientResponse response = get(LoginManagerPaths.GET_USER);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(LoggedUser.class);
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
	 * Performs the login to JEM, by userid and password.
	 * 
	 * @param account userid and password
	 * @return logged user
	 * @throws RestException if any exception occurs
	 */
	public LoggedUser login(Account account) throws RestException{
	    try {
			// creates the returned object
			ClientResponse response = put(LoginManagerPaths.LOGIN, account);
			if (response.getStatus() == Status.OK.getStatusCode()){
				return response.getEntity(LoggedUser.class);
			} else {
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}

	/**
	 * Performs the logoff from JEM.
	 * @return <code>true</code> is logoff is done
	 * @throws RestException if any exception occurs
	 */
	public boolean logoff() throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = delete(LoginManagerPaths.LOGOFF);
			Boolean value = response.getEntity(Boolean.class);
			if (response.getStatus() != Status.OK.getStatusCode()){
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
			return value;
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Performs the logoff from JEM, storing the user preferences.
	 * 
	 * @param userPreferences map of user preferences
	 * @return <code>true</code> is logoff is done
	 * @throws RestException if any exception occurs
	 */
	public boolean logoff(Map<String, UserPreference> userPreferences) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = delete(LoginManagerPaths.LOGOFF_SAVING_PREFERENCES, userPreferences);
			Boolean value = response.getEntity(Boolean.class);
			if (response.getStatus() != Status.OK.getStatusCode()){
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
			return value;
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}
	
	/**
	 * Stores into JEM the user preferences.\
	 * 
	 * @return <code>true</code> is logoff is done
	 * @param userPreferences map of user preferences
	 * @throws RestException if any exception occurs
	 */
	public boolean storePreferences(Map<String, UserPreference> userPreferences) throws RestException {
	    try {
			// creates the returned object
			ClientResponse response = post(LoginManagerPaths.SAVE_PREFERENCES, userPreferences);
			Boolean value = response.getEntity(Boolean.class);
			if (response.getStatus() != Status.OK.getStatusCode()){
				throw new RestException(response.getStatus(), response.getEntity(String.class));
			}
			return value;
	    } catch (Exception e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
    		throw new RestException(e);
	    }
	}	
}