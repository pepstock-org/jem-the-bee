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
package org.pepstock.jem.node;

import org.pepstock.jem.node.configuration.ConfigurationException;

/**
 * This interface is used to be informed when the node is ready (post Hazelcast startup(
 * and when the node is ending (before to close Hazelcast)
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public interface NodeLifeCycleListener {
	
	
	/**
	 * This method is called after the cluster startup
	 * @throws ConfigurationException if any error occurs during the setup of factory
	 */
	void afterNodeStarted() throws ConfigurationException;
	
	/**
	 * This method is called before the cluster ends, to clean up the resources
	 */
	void beforeNodeStopped();

}
