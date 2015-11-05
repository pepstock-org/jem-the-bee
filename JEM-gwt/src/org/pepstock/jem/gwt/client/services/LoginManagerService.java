/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreferences;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all login actions
 */
@RemoteServiceRelativePath(Services.LOGIN)
public interface LoginManagerService extends RemoteService {

	/**
	 * Called when client application starts to have the already logged user of
	 * null (if not authenticated)
	 * 
	 * @return already logged user or <code>null</code>
	 * @throws JemException
	 *             if JEM group is not available
	 */
	LoggedUser getUser() throws JemException;

	/**
	 * Logs in using userid and password. Returns logged user.
	 * 
	 * @param userid
	 *            userid of user
	 * @param password
	 *            password
	 * @return logged user
	 * @throws JemException
	 *             if authentication error occurs
	 */
	LoggedUser login(String userid, String password) throws JemException;

	/**
	 * Logs out the user
	 * 
	 * @param preferences
	 *            to store
	 * @return always true
	 * @throws JemException
	 *             if authentication error occurs
	 */
	Boolean logoff(UserPreferences preferences) throws JemException;

	/**
	 * Stores user preferences
	 * 
	 * @param preferences
	 *            preferences to store
	 * @return always true
	 * @throws JemException
	 *             if any error occurs
	 */
	Boolean storePreferences(UserPreferences preferences) throws JemException;

}