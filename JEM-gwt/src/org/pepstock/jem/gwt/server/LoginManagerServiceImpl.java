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
package org.pepstock.jem.gwt.server;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.pepstock.jem.gwt.client.services.LoginManagerService;
import org.pepstock.jem.gwt.server.services.LoginManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.UserPreference;

/**
 * Is GWT server service which can provide all methods to manage login/logoff
 * and profiling
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class LoginManagerServiceImpl extends DefaultManager implements LoginManagerService {

	private static final long serialVersionUID = 1L;

	private transient LoginManager loginManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.gwt.client.UserManagerService#getUser()
	 */
	@Override
	public LoggedUser getUser() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (loginManager == null) {
			initManager();
		}
		try {
			return loginManager.getUser();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.UserManagerService#login(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public LoggedUser login(String userid, String password) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (loginManager == null) {
			initManager();
		}
		try {
			return loginManager.login(userid, password);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.LoginManagerService#logoff(java.
	 * util.Map)
	 */
	@Override
	public Boolean logoff(Map<String, UserPreference> preferences) throws JemException {
		Boolean result = Boolean.FALSE;
		try {
			if (loginManager != null) {
				if (isEnable()) {
					result = loginManager.logoff(preferences);
				} else {
					result = loginManager.logoff();
				}
			}
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, ex);
		}
		// invalidate HTTP session
		HttpSession session = super.getThreadLocalRequest().getSession();
		if (session != null) {
			session.invalidate();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.LoginManagerService#storePreferences
	 * (java.util.Map)
	 */
	@Override
	public Boolean storePreferences(Map<String, UserPreference> preferences) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (loginManager == null) {
			initManager();
		}
		try {
			return loginManager.storePreferences(preferences);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/**
	 * Initializes a manager
	 * 
	 * @throws JemException
	 *             if any exception occurs
	 */
	private synchronized void initManager() throws JemException {
		if (loginManager == null) {
			try {
				loginManager = new LoginManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG039E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}

}