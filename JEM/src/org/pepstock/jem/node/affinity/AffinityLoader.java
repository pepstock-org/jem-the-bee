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
package org.pepstock.jem.node.affinity;

import java.io.IOException;
import java.util.Properties;

/**
 * Is a interface which could be implemented by a custom class to assign a set of affinities labels.<br>
 * These labels are used by node to evaluate if a job could be executed or not in the node. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public interface AffinityLoader {

	/**
	 * Called to initialize the listener. A set of properties are passed, or a
	 * empty collection if the properties are not defined
	 * 
	 * @param properties properties
	 */
	void init(Properties properties);

	/**
	 * Called to have the list of affinities and memory, using the system information if needed.
	 * 
	 * @param info system information
	 * @return result object with all affinities and memory
	 * @throws IOException if an error occurs
	 */
	Result load(SystemInfo info) throws IOException;
	
}