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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
import org.pepstock.jem.rest.entities.Account;
import org.pepstock.jem.rest.paths.LoginManagerPaths;

import com.sun.jersey.spi.resource.Singleton;

/**
 * Rest service to get logged user, to log in and log out.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
@Singleton
@Path(LoginManagerPaths.MAIN)
public class LoginManagerImpl extends DefaultServerResource {

	private LoginManager loginManager = null;

	/**
	 * Returns the logged user if already authenticated, otherwise returns
	 * <code>null</code>
	 * 
	 * @return the logged user if already authenticated, otherwise returns
	 *         <code>null</code>
	 */
	@GET
	@Path(LoginManagerPaths.GET)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser() {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// get user
				LoggedUser user = loginManager.getUser();
				// if not null, the user is already inside
				if (user != null) {
					// creates a clone of logged user
					// because it doesn't set preferences
					// to avoid to change them
					// and create inconsistency on them
					// because they are readable ONLY from JEM
					LoggedUser newUser = new LoggedUser();
					newUser.setId(user.getId());
					newUser.setName(user.getName());
					newUser.setOrganizationalUnit(user.getOrganizationalUnit());
					newUser.setAuthorized(user.getAuthorized());
					// sets null preferences
					newUser.setPreferences(null);
					return ResponseBuilder.JSON.ok(newUser);
				} else {
					// otherwise user not logged
					return ResponseBuilder.JSON.notFound("loggedUser");
				}
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Logs in the user in JEM.
	 * 
	 * @see Account
	 * @see LoggedUser
	 * @param account
	 *            account object
	 * @return logged user with permissions
	 */
	@PUT
	@Path(LoginManagerPaths.LOGIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(Account account) {
		// it uses JSON response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.JSON);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// performs the login
				LoggedUser user = loginManager.login(account.getUserid(), account.getPassword());
				// creates a clone of logged user
				// because it doen't set preferences
				// to avoid to change them
				// and create inconsistency on them
				// because they are readable ONLY from JEM
				LoggedUser newUser = new LoggedUser();
				newUser.setId(user.getId());
				newUser.setName(user.getName());
				newUser.setOrganizationalUnit(user.getOrganizationalUnit());
				newUser.setAuthorized(user.getAuthorized());
				// sets null preferences
				newUser.setPreferences(null);
				// returns OK!
				return ResponseBuilder.JSON.ok(newUser);
			} catch (ServiceMessageException e) {
				// if here, there is a uthorization exception
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.JSON.unauthorized(e);
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return ResponseBuilder.JSON.serverError(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/**
	 * Logs off from JEM.
	 * 
	 * @return
	 * @throws JemException
	 *             if JEM group is not available or not authorized
	 */
	@DELETE
	@Path(LoginManagerPaths.LOGOFF)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response logoff() {
		// it uses PLAIN TEXT response builder
		// also checking the common status of REST services
		Response resp = check(ResponseBuilder.PLAIN);
		// if response not null means we have an exception
		if (resp == null) {
			try {
				// performs logoff and return true if OK
				return ResponseBuilder.PLAIN.ok(loginManager.logoff(null).toString());
			} catch (Exception e) {
				// catches the exception and return it
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, e, e.getMessage());
				return ResponseBuilder.PLAIN.unauthorized(e);
			}
		} else {
			// returns an exception
			return resp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.server.rest.DefaultServerResource#initManager()
	 */
	@Override
	boolean init() throws JemException {
		try {
	        if (loginManager == null) {
	        	loginManager = new LoginManager();
	        }
	        return true;
        } catch (Exception e) {
        	throw new JemException(e.getMessage(), e);
        }
	}
}