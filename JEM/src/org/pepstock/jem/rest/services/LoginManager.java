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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.RestException;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

import com.sun.jersey.api.client.ClientResponse;

/**
 * REST service to manage user authentication and get and set of user preferences.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
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
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// performs REST call
		ClientResponse response = builder.get(LoginManagerPaths.GET);
		// if HTTP status code is OK,parses the result to a logged user object
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(LoggedUser.class);
		} else if (response.getStatus() == Status.NOT_FOUND.getStatusCode()){
			// the user id is not logged 
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
	 * Performs the login to JEM, by userid and password.
	 * 
	 * @param account userid and password
	 * @return logged user object
	 * @throws RestException if any exception occurs
	 */
	public LoggedUser login(Account account) throws RestException{
		// creates a request builder with the APPLICATION/JSON media type as
		// accept type (the default)
		RequestBuilder builder = RequestBuilder.media(this);
		// performs REST call adding the account information
		ClientResponse response = builder.put(LoginManagerPaths.LOGIN, account);
		// if HTTP status code is OK,parses the result to a logged user object
		if (response.getStatus() == Status.OK.getStatusCode()){
			return response.getEntity(LoggedUser.class);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			// IT MUST CONSUME the response
			// otherwise there is a HTTP error
			throw new RestException(response.getStatus(), getValue(response, String.class));
		}
	}

	/**
	 * Performs the logoff from JEM.
	 * @return <code>true</code> is logoff is done
	 * @throws RestException if any exception occurs
	 */
	public boolean logoff() throws RestException {
		// creates a request builder with the TEXT/PLAIN media type as accept
		// type
		RequestBuilder builder = RequestBuilder.media(this, MediaType.TEXT_PLAIN);
		// performs REST call
		ClientResponse response = builder.delete(LoginManagerPaths.LOGOFF);
		// because of the accept type is always TEXT/PLAIN
		// it gets the string		
		String value = response.getEntity(String.class);
		// if HTTP status code is ok, returns the boolean value
		if (response.getStatus() == Status.OK.getStatusCode()){
			return Boolean.parseBoolean(value);
		} else {
			// otherwise throws the exception using the
			// body of response as message of exception
			throw new RestException(response.getStatus(), value);
		}
	}
}