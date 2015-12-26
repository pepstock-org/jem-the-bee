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

import org.pepstock.jem.node.configuration.Eviction;
import org.pepstock.jem.node.hazelcast.ConfigFactory;
import org.pepstock.jem.node.security.UserPreferences;

import com.hazelcast.config.MapConfig;

/**
 * Persistent manager for RoutingConfs map.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class UserPreferencesMapManager extends AbstractMapManager<UserPreferences> {
	
	private static final int MAX_SIZE = 200;
	
	private static final int PERCENTAGE = 25;
	
	private static final Eviction DEFAULT_EVICTION = new Eviction(MAX_SIZE, PERCENTAGE);
	
	private static UserPreferencesMapManager INSTANCE = null;

	/**
	 * Construct the object instantiating a new DBManager
	 */
	private UserPreferencesMapManager(DatabaseManager<UserPreferences> dbManager) {
		super(dbManager, false);
	}

	/**
	 * Creates the instance of map store if not already initialized
	 * @param dbManager database manger to use for persistence
	 * @return the map store
	 */
	public static UserPreferencesMapManager createInstance(DatabaseManager<UserPreferences> dbManager){
		if (INSTANCE == null){
			INSTANCE = new UserPreferencesMapManager(dbManager);
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
	public static UserPreferencesMapManager getInstance() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.AbstractMapManager#getMapConfig()
	 */
	@Override
	public MapConfig getMapConfig() {
		return ConfigFactory.createMapConfig(getQueueName(), DEFAULT_EVICTION);
	}
}