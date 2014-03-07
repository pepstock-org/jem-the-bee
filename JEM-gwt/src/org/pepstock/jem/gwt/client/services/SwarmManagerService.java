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

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.util.filters.Filter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all actions for swarm
 * management
 */
@RemoteServiceRelativePath(Services.SWARM_NODES)
public interface SwarmManagerService extends RemoteService {

	/**
	 * Returns the swarm nodes using a filter by hostname or ip address.
	 * 
	 * @param nodesFilter
	 *            filter for nodes
	 * @return list of nodes
	 * @throws JemException
	 *             if errors occurs
	 */
	Collection<NodeInfoBean> getNodes(String nodesFilter) throws JemException;

	/**
	 * Returns the nodes using a filter.
	 * 
	 * @param filterString
	 *            a String that will be parsed as a {@link Filter}
	 * @return list of nodes
	 * @throws JemException
	 *             if errors occurs
	 */
	Collection<NodeInfoBean> getNodesByFilter(String filterString) throws JemException;

	/**
	 * Starts all the swarm nodes that belong to current environment.
	 * 
	 * @return always true
	 * @throws JemException
	 */
	Boolean start() throws JemException;

	/**
	 * Shutdowns all the swarm nodes that belong to the current environment.
	 * 
	 * @return always true
	 * @throws JemException
	 */
	Boolean drain() throws JemException;

	/**
	 * Returns the status of swarm.
	 * 
	 * @return status of swarm
	 * @throws JemException
	 *             if any exception occurs
	 */
	String getStatus() throws JemException;

}