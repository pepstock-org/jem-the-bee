/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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
import org.pepstock.jem.node.swarm.SwarmException;

/**
 * Is the Callable responsible shut down the swarm node
 * 
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 */
public class Drain implements Callable<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Shut down the Swarm node instance
	 * @throws exception 
	 */
	@Override
	public Boolean call() throws SwarmException {
		return Main.SWARM.shutDown();
	}

}