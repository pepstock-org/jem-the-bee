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

import javax.ws.rs.core.Response;

import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.LogAppl;

/**
 * Abstract REST server resource, which provides a helpful method to check if
 * the JEM group is available or not and a preliminary check if everything is ok
 * before calling the the logic.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class DefaultServerResource {

	// boolean to check if it has to call the init of the managers or not
	private boolean managerLoaded = false;

	// sync object
	private final Object SYNC = new Object();

	/**
	 * Performs the same checks for all services
	 * 
	 * @return if there is any error, return the response, otherwise null.
	 */
	final Response check(ResponseBuilder builder) {
		// checks if environment is available
		if (isEnable()) {
			try {
				// checks if manager is loaded
				if (!managerLoaded) {
					// sync to avoid multi instantiation of manager
					synchronized (SYNC) {
						if (!managerLoaded) {
							// initialized
							managerLoaded = init();
						}
					}
				}
				// everything is ok, there for return null
				return null;
			} catch (Exception e) {
				// throws a HTTP severe error
				LogAppl.getInstance().ignore(e.getMessage(), e);
				return builder.severeError(e);
			}
		} else {
			// return the response that
			// the environment is not ready
			return builder.unableException();
		}
	}

	/**
	 * Method called to init the service
	 * 
	 * @return true is ok, otherwise false
	 * @throws Exception
	 *             if any error occurs
	 */
	abstract boolean init() throws Exception;

	/**
	 * Returns <code>true</code> if JEM group is available (at least one member
	 * up and running).
	 * 
	 * @return <code>true</code> if JEM group is available (at least one member
	 *         up and running), otherwise <code>false</code>
	 */
	final boolean isEnable() {
		return SharedObjects.getInstance().isDataClusterAvailable();
	}

}