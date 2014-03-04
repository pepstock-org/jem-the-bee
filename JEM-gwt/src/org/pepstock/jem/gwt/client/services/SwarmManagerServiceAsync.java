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

import java.util.Collection;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.JemException;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async service
 * 
 * @see NodesManagerService
 */
public interface SwarmManagerServiceAsync {
	/**
	 * @see NodesManagerService#getNodes(String)
	 * @param nodesFilter
	 * @param callback
	 * @throws JemException
	 */
	void getNodes(String nodesFilter, AsyncCallback<Collection<NodeInfoBean>> callback);

	/**
	 * @see SwarmManagerService#start()
	 * @param callback
	 */
	void start(AsyncCallback<Boolean> callback);

	/**
	 * @see SwarmManagerService#drain()
	 * @param callback
	 * @return always true
	 */
	void drain(AsyncCallback<Boolean> callback);

	/**
	 * @see SwarmManagerService#getNodesByFilter(String)
	 * @param filterString
	 * @param callback
	 */
	void getNodesByFilter(String filterString, AsyncCallback<Collection<NodeInfoBean>> callback);

	/**
	 * @see SwarmManagerService#getStatus()
	 * @param callback
	 */
	void getStatus(AsyncCallback<String> callback);

}