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
package org.pepstock.jem.node.persistence;

import org.pepstock.jem.node.configuration.SwarmConfiguration;

/**
 * Persistent manager for RoutingConfs map.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RoutingConfigMapManager extends AbstractMapManager<SwarmConfiguration> {
	
	private static RoutingConfigMapManager INSTANCE = null;

	/**
	 * Construct the object instantiating a new DBManager
	 */
	private RoutingConfigMapManager(DatabaseManager<SwarmConfiguration> dbManager) {
		super(dbManager, false);
	}
	
	/**
	 * Creates the instance of map store if not already initialized
	 * @param dbManager database manger to use for persistence
	 * @return the map store
	 */
	public static RoutingConfigMapManager createInstance(DatabaseManager<SwarmConfiguration> dbManager){
		if (INSTANCE == null){
			INSTANCE = new RoutingConfigMapManager(dbManager);
		}
		return INSTANCE;
	}
	
	/**
	 * Is a static method (typical of a singleton) that returns the unique
	 * instance of JobDBManager.<br>
	 * You must ONLY one instance of this per JVM instance.<br>
	 * 
	 * @return manager instance
	 * @throws Exception
	 */
	public static RoutingConfigMapManager getInstance() {
		return INSTANCE;
	}
}