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
import javax.xml.bind.JAXBElement;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.AbstractRestManager;
import org.pepstock.jem.rest.RestClient;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.entities.LoggedUserContent;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.entities.UserPreferencesContent;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

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
	    super(restClient);
    }

	/**
	 * Returns the user already logged otherwise null.
	 * 
	 * @return the user already logged otherwise null
	 * @throws JemException if any exception occurs
	 */
	public LoggedUser getUser() throws JemException{
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
	    GenericType<JAXBElement<LoggedUserContent>> generic = new GenericType<JAXBElement<LoggedUserContent>>() {
	    };
	    try {
	    	// creates the complete path of REST service, setting also the output format (XML)
	    	JAXBElement<LoggedUserContent> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.GET_USER).accept(MediaType.APPLICATION_XML).get(generic);
	    	// gets the returned object
	    	LoggedUserContent object = jaxbContact.getValue();
	    	// checks if has got any exception
	    	// Exception must be saved as attribute of returned object
			if (object.hasException()){
				throw new JemException(object.getExceptionMessage());
			}
			// returns the logged user
			return object.getLoggedUser();
	    } catch (UniformInterfaceException e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
	    	// checks http status 
	    	if (e.getResponse().getStatus() != 204){
	    		throw new JemException(e.getMessage(), e);
	    	}
	    	return null;
	    }
	}

	/**
	 * Performs the login to JEM, by userid and password.
	 * 
	 * @param account userid and password
	 * @return logged user
	 * @throws JemException if any exception occurs
	 */
	public LoggedUser login(Account account) throws JemException{
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
		GenericType<JAXBElement<LoggedUserContent>> generic = new GenericType<JAXBElement<LoggedUserContent>>() {
		};
		// creates the complete path of REST service, setting also the output format (XML)
		JAXBElement<LoggedUserContent> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGIN).accept(MediaType.APPLICATION_XML).put(generic, account);
		// gets the returned object
		LoggedUserContent object = jaxbContact.getValue();
    	// checks if has got any exception
    	// Exception must be saved as attribute of returned object		
		if (object.hasException()){
			throw new JemException(object.getExceptionMessage());
		}
		return object.getLoggedUser();
	}

	/**
	 * Performs the logoff from JEM.
	 * 
	 * @throws JemException if any exception occurs
	 */
	public void logoff() throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the complete path of REST service, setting also the output format (XML)
		resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGOFF).accept(MediaType.APPLICATION_XML).delete();
	}
	
	/**
	 * Performs the logoff from JEM, storing the user preferences.
	 * 
	 * @param userPreferences map of user preferences
	 * @throws JemException if any exception occurs
	 */
	public void logoff(UserPreferencesContent userPreferences) throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the complete path of REST service, setting also the output format (XML)
		resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGOFF_SAVING_PREFERENCES).accept(MediaType.APPLICATION_XML).delete(userPreferences);
	}
	
	/**
	 * Stores into JEM the user preferences.\
	 * 
	 * @param userPreferences map of user preferences
	 * @throws JemException if any exception occurs
	 */
	public void storePreferences(UserPreferencesContent userPreferences) throws JemException {
		// gets the web resource
		WebResource resource = getClient().getBaseWebResource();
		// creates the returned object
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {
		};
		// creates the complete path of REST service, setting also the output format (XML)
		JAXBElement<ReturnedObject> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.SAVE_PREFERENCES).accept(MediaType.APPLICATION_XML).post(generic, userPreferences);
		// gets the returned object
		ReturnedObject object = jaxbContact.getValue();
    	// checks if has got any exception
    	// Exception must be saved as attribute of returned object		
		if (object.hasException()){
			throw new JemException(object.getExceptionMessage());
		}
	}	
}