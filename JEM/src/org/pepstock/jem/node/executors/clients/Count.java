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
package org.pepstock.jem.node.executors.clients;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * 
 * @author Simone "busy" Businaro
 * 
 * Is the executor responsible to get the number of clients currently connected to the cluster.
 *
 */
public class Count extends DefaultExecutor<Integer>{

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 *
	 * @return the number of clients connected to the cluster
	 * @throws ExecutorException occurs if errors
	 */
	@Override
	public Integer execute() throws ExecutorException{
		return Main.getHazelcast().getClientService().getConnectedClients().size();
	}
}