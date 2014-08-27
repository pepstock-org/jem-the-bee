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
package org.pepstock.jem.gwt.server.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.entities.LoggedUserContent;
import org.pepstock.jem.rest.entities.ReturnedObject;
import org.pepstock.jem.rest.entities.UserPreferencesContent;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

/**
 * Rest service to get logged user, to log in and log out.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
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
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public LoggedUserContent getUser() throws JemException{
		LoggedUserContent content = new LoggedUserContent();
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			try {
	            LoggedUser user = loginManager.getUser();
	            content.setLoggedUser(user);
            } catch (Exception e) {
            	LogAppl.getInstance().ignore(e.getMessage(), e);
	            content.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(content);
		}
		return content;
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
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public LoggedUserContent login(Account account) throws JemException{
		LoggedUserContent content = new LoggedUserContent();
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			try {
				LoggedUser user = loginManager.login(account.getUserId(), account.getPassword());
				content.setLoggedUser(user);
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
	            content.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(content);
		}
		return content;
	}


	/**
	 * Logs off from JEM.
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@DELETE
	@Path(LoginManagerPaths.LOGOFF)
	public void logoff() throws JemException {
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			loginManager.logoff(null);
		}
	}

	/**
	 * Logs off from JEM saving user preferences.
	 * @param preferences user preferences to store
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@DELETE
	@Path(LoginManagerPaths.LOGOFF_SAVING_PREFERENCES)
	public void logoff(UserPreferencesContent preferences) throws JemException {
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			loginManager.logoff(preferences.getPreferences());
		}
	}
	
	/**
	 * Stores the user preferences in JEM.
	 * @param preferences user preferences to store
	 * @return 
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@POST
	@Path(LoginManagerPaths.SAVE_PREFERENCES)
	public ReturnedObject storePreferences(UserPreferencesContent preferences) throws JemException {
		ReturnedObject ro = new ReturnedObject();
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			try {
	            loginManager.storePreferences(preferences.getPreferences());
            } catch (Exception e) {
            	LogAppl.getInstance().ignore(e.getMessage(), e);
	            ro.setExceptionMessage(e.getMessage());
            }
		} else {
			setUnableExcepton(ro);
		}
		return ro;
	}
	
	/**
	 * Initialize the manager
	 */
	private synchronized void initManager() {
		if (loginManager == null) {
			loginManager = new LoginManager();
		}
	}
}