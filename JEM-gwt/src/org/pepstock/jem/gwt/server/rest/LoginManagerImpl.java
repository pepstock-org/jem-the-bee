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
package org.pepstock.jem.gwt.server.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.gwt.server.services.ServiceMessageException;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreference;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Rest service to get logged user, to log in and log out.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@Singleton
@Path(LoginManagerPaths.MAIN)
public class LoginManagerImpl extends DefaultServerResource  {

	private LoginManager loginManager = null;

	/**
	 * Returns the logged user if already authenticated, otherwise returns <code>null</code>
	 * 
	 * @return the logged user if already authenticated, otherwise returns <code>null</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path(LoginManagerPaths.GET_USER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(){
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try{
				LoggedUser user = loginManager.getUser();
				if (user != null){
					return ResponseBuilder.JSON.ok(user);
				} else {
					return ResponseBuilder.JSON.notFound("loggedUser");
				}
			} catch (Exception e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.severError(e);
			}
		} else {
			return resp;
		}
	}
	/**
	 * Logs in the user in JEM. 
	 * 
	 * @see Account 
	 * @see LoggedUser
	 * @param account account object 
	 * @return logged user with permissions
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@PUT
	@Path(LoginManagerPaths.LOGIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(Account account){
		Response resp = check(ResponseBuilder.JSON);
		if (resp == null){
			try {
				LoggedUser user = loginManager.login(account.getUserId(), account.getPassword());
				return ResponseBuilder.JSON.ok(user);
			} catch(ServiceMessageException e){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.JSON.unauthorized(e);
            } catch (Exception e) {
            	LogAppl.getInstance().ignore(e.getMessage(), e);
            	return ResponseBuilder.JSON.severError(e);
            }
		} else {
			return resp;
		}
	}

	/**
	 * Logs off from JEM.
	 * @return 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@DELETE
	@Path(LoginManagerPaths.LOGOFF)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response logoff() {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				Boolean status = loginManager.logoff(null);
				return ResponseBuilder.PLAIN.ok(status.toString());
			} catch(Exception e){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.PLAIN.unauthorized(e);
			}
		} else {
			return resp;
		}
	}

	/**
	 * Logs off from JEM saving user preferences.
	 * @param preferences user preferences to store
	 * @return 
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@DELETE
	@Path(LoginManagerPaths.LOGOFF_SAVING_PREFERENCES)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response logoff(Map<String, UserPreference> preferences) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				return ResponseBuilder.PLAIN.ok(loginManager.logoff(preferences).toString());
			} catch(Exception e){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.PLAIN.unauthorized(e);
			}
		} else {
			return resp;
		}
	}
	
	/**
	 * Stores the user preferences in JEM.
	 * @param preferences user preferences to store
	 * @return a empty object is everything went ok
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(LoginManagerPaths.SAVE_PREFERENCES)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response storePreferences(Map<String, UserPreference> preferences) {
		Response resp = check(ResponseBuilder.PLAIN);
		if (resp == null){
			try {
				Boolean status = loginManager.storePreferences(preferences);
				return ResponseBuilder.PLAIN.ok(status.toString());
			} catch(Exception e){
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.PLAIN.unauthorized(e);
			}
		} else {
			return resp;
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#initManager()
	 */
    @Override
    boolean init() throws Exception {
		if (loginManager == null) {
			loginManager = new LoginManager();
		}
		return true;
    }
    
}