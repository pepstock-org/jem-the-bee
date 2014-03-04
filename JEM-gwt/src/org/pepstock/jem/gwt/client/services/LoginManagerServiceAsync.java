/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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

import java.util.Map;

import org.pepstock.jem.gwt.client.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreference;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async service.
 * 
 * @see LoginManagerService
 */
public interface LoginManagerServiceAsync {
	/**
	 * @see LoginManagerService#getUser()
	 * @param callback
	 */
	void getUser(AsyncCallback<LoggedUser> callback);

	/**
	 * @see LoginManagerService#login(String, String)
	 * @param userid
	 * @param password
	 * @param callback
	 */
	void login(String userid, String password, AsyncCallback<LoggedUser> callback);

	/**
	 * LoginManagerService#logoff()
	 * 
	 * @param preferences
	 * @param callback
	 */
	void logoff(Map<String, UserPreference> preferences, AsyncCallback<Boolean> callback);

	/**
	 * @see LoginManagerService#storePreferences()
	 * @param preferences
	 * @param callback
	 */
	void storePreferences(Map<String, UserPreference> preferences, AsyncCallback<Boolean> callback);
}