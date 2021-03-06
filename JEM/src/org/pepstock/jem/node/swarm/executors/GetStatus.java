/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.node.swarm.executors;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Status;
import org.pepstock.jem.node.swarm.SwarmException;

/**
 * Is the Callable responsible start the swarm node
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class GetStatus implements Callable<Status>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Start the Swarm node instance
	 * @throws exception 
	 */
	@Override
	public Status call() throws SwarmException  {
		return Main.SWARM.getStatus();
	}

}