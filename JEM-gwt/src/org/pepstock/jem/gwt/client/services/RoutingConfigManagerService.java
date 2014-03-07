/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
import org.pepstock.jem.node.configuration.SwarmConfiguration;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for confs
 * management
 */
@RemoteServiceRelativePath(Services.ROUTING_CONFIG)
public interface RoutingConfigManagerService extends RemoteService {

	/**
	 * Returns the confs using a filter by conf name.
	 * 
	 * @param name
	 *            conf name filter
	 * @return list of confs
	 * @throws JemException
	 *             if error occurs
	 */
	SwarmConfiguration getSwarmConfiguration(String name) throws JemException;

	/**
	 * Updates an existing conf
	 * 
	 * @param conf
	 *            conf instance to update
	 * @return always true
	 * @throws JemException
	 *             if error occurs
	 */
	SwarmConfiguration updateSwarmConfiguration(SwarmConfiguration conf) throws JemException;

}