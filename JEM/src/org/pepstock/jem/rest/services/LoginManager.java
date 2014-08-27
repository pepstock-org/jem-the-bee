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
	 * @return logged user
	 * @throws JemException
	 */
	public LoggedUser getUser() throws JemException{
		WebResource resource = getClient().getBaseWebResource();
	    GenericType<JAXBElement<LoggedUserContent>> generic = new GenericType<JAXBElement<LoggedUserContent>>() {
	    };
	    try {
	    	JAXBElement<LoggedUserContent> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.GET_USER).accept(MediaType.APPLICATION_XML).get(generic);
	    	LoggedUserContent object = jaxbContact.getValue();
			if (object.hasException()){
				throw new JemException(object.getExceptionMessage());
			}
			return object.getLoggedUser();
	    } catch (UniformInterfaceException e){
	    	LogAppl.getInstance().debug(e.getMessage(), e);
	    	if (e.getResponse().getStatus() != 204){
	    		throw new JemException(e.getMessage(), e);
	    	}
	    	return null;
	    }
	}

	/**
	 * 
	 * @param account
	 * @return logged user
	 * @throws JemException
	 */
	public LoggedUser login(Account account) throws JemException{
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<LoggedUserContent>> generic = new GenericType<JAXBElement<LoggedUserContent>>() {
		};
		JAXBElement<LoggedUserContent> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGIN).accept(MediaType.APPLICATION_XML).put(generic, account);
    	LoggedUserContent object = jaxbContact.getValue();
		if (object.hasException()){
			throw new JemException(object.getExceptionMessage());
		}
		return object.getLoggedUser();
	}

	/**
	 * 
	 * @throws JemException
	 */
	public void logoff() throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGOFF).accept(MediaType.APPLICATION_XML).delete();
	}
	
	/**
	 * 
	 * @param userPreferences 
	 * @throws JemException
	 */
	public void logoff(UserPreferencesContent userPreferences) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.LOGOFF_SAVING_PREFERENCES).accept(MediaType.APPLICATION_XML).delete(userPreferences);
	}
	
	/**
	 * 
	 * @param userPreferences 
	 * @throws JemException
	 */
	public void storePreferences(UserPreferencesContent userPreferences) throws JemException {
		WebResource resource = getClient().getBaseWebResource();
		GenericType<JAXBElement<ReturnedObject>> generic = new GenericType<JAXBElement<ReturnedObject>>() {
		};
		JAXBElement<ReturnedObject> jaxbContact = resource.path(LoginManagerPaths.MAIN).path(LoginManagerPaths.SAVE_PREFERENCES).accept(MediaType.APPLICATION_XML).post(generic, userPreferences);
    	ReturnedObject object = jaxbContact.getValue();
		if (object.hasException()){
			throw new JemException(object.getExceptionMessage());
		}
	}
	
}