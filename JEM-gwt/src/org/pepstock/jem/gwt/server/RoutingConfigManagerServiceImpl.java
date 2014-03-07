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
package org.pepstock.jem.gwt.server;

import org.pepstock.jem.gwt.client.services.RoutingConfigManagerService;
import org.pepstock.jem.gwt.server.services.RoutingConfigManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.configuration.SwarmConfiguration;

/**
 * Is GWT server service which can provide all methods to manage rotuing configuration item
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RoutingConfigManagerServiceImpl extends DefaultManager implements RoutingConfigManagerService {

	private static final long serialVersionUID = 1L;

	private transient RoutingConfigManager routingConfigManager = null;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.RoutingConfigManagerService#getSwarmConfiguration(java.lang.String)
	 */
    @Override
    public SwarmConfiguration getSwarmConfiguration(String name) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (routingConfigManager == null){
			initManager();
		}
		try {
			return routingConfigManager.getSwarmConfiguration(name);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG046E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.RoutingConfigManagerService#updateSwarmConfiguration(org.pepstock.jem.node.configuration.SwarmConfiguration)
	 */
    @Override
    public SwarmConfiguration updateSwarmConfiguration(SwarmConfiguration conf) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (routingConfigManager == null){
			initManager();
		}
		try {
			return routingConfigManager.updateSwarmConfiguration(conf);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG046E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
    }

	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
     */
	private synchronized void initManager() throws JemException {
		if (routingConfigManager == null) {
			try {
				routingConfigManager = new RoutingConfigManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG046E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}
	}
}