/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.pepstock.jem.gwt.client.security.LoggedUser;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.rest.entities.Account;
import org.pepstock.jem.gwt.server.rest.entities.LoggedUserContent;
import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Rest service to get logged user, to log in and log out.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
@Path("/"+LoginManagerImpl.LOGIN_MANAGER_PATH)
public class LoginManagerImpl extends DefaultServerResource  {
	
	/**
	 * Key to define the path to bind this services
	 */
	public static final String LOGIN_MANAGER_PATH = "loginManager";
	
	/**
	 * Key to define the path to bind user management method
	 */
	public static final String LOGIN_MANAGER_GET_USER_PATH = "getUser";
	
	/**
	 * Key to define the path to bind login management method
	 */
	public static final String LOGIN_MANAGER_LOGIN_PATH = "login";
	
	/**
	 * Key to define the path to bind logoff management method
	 */
	public static final String LOGIN_MANAGER_LOGOFF_PATH = "logoff";

	private LoginManager loginManager = null;

	/**
	 * Returns the logged user if already authenticated, otherwise returns <code>null</code>
	 * 
	 * @return the logged user if already authenticated, otherwise returns <code>null</code>
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@GET
	@Path("/"+LOGIN_MANAGER_GET_USER_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public LoggedUserContent getUser() throws JemException{
		LoggedUserContent content = new LoggedUserContent();
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			LoggedUser user = loginManager.getUser();
			content.setLoggedUser(user);
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			content.setExceptionMessage(msg);
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
	@Path("/"+LOGIN_MANAGER_LOGIN_PATH)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public LoggedUserContent login(Account account) throws JemException{
		LoggedUserContent content = new LoggedUserContent();
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			try {
				LoggedUser user = loginManager.login(account.getUserid(), account.getPassword());
				content.setLoggedUser(user);
            } catch (Exception e) {
            	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
	            content.setExceptionMessage(e.getMessage());
            }
		} else {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG003E, SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			String msg = UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName());
			content.setExceptionMessage(msg);
		}
		return content;
	}


	/**
	 * Logs off from JEM.
	 * 
	 * @throws JemException if JEM group is not available or not authorized 
	 */
	@DELETE
	@Path("/"+LOGIN_MANAGER_LOGOFF_PATH)
	public void logoff() throws JemException {
		if (isEnable()){
			if (loginManager == null){
				initManager();
			}
			loginManager.logoff(null);
		}
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