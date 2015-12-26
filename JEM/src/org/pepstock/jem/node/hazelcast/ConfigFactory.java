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
package org.pepstock.jem.node.hazelcast;

import org.pepstock.jem.node.configuration.Eviction;
import org.pepstock.jem.node.listeners.DataLossListener;
import org.pepstock.jem.util.Numbers;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.MapStore;

/**
 * Factory to create the standard Hazelcast maps internally defined and used into JEM
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class ConfigFactory {

	/**
	 * To avoid any instantiation
	 */
	private ConfigFactory() {
	}

	/**
	 * Creates the map config with the map store for the map which needs the persistence and the eviction 
	 * @param name name of map
	 * @param mapstore  instance to manage the persistence of map
	 * @param eviction eviction configuration
	 * @return map config to add to Hazelcast config
	 */
	public static MapConfig createMapConfig(String name, MapStore<?,?> mapstore, Eviction eviction){
		// gets default map config with mapstore
		MapConfig config = createMapConfig(name, mapstore);
		loadEviction(config, eviction);
		return config;
	}

	/**
	 * Creates the map config with the eviction configuration
	 * @param name name of map
	 * @param eviction eviction configuration
	 * @return map config to add to Hazelcast config
	 */
	public static MapConfig createMapConfig(String name, Eviction eviction){
		// gets default map config 
		MapConfig config = createMapConfig(name);
		loadEviction(config, eviction);
		return config;
	}

	/**
	 * Creates the map config with the map store for the map which needs the persistence 
	 * @param name name of map
	 * @param mapstore instance to manage the persistence of map
	 * @return map config to add to Hazelcast config
	 */
	public static MapConfig createMapConfig(String name, MapStore<?,?> mapstore){
		// gets default map config
		MapConfig config = createMapConfig(name);
		// sets the mapstore
		MapStoreConfig mapStoreConfig = new MapStoreConfig();
		mapStoreConfig.setEnabled(true);
		//PAY ATTENTION
		// MUST BE SET TO 1 to avoid deadlocks
		// minimum is 1 second
		mapStoreConfig.setWriteDelaySeconds(1);
		mapStoreConfig.setImplementation(mapstore);
		config.setMapStoreConfig(mapStoreConfig);
		// adds data loss listener
		// which realods data into cache from database
		MapPartitionLostListenerConfig plConfig = new MapPartitionLostListenerConfig();
		plConfig.setImplementation(new DataLossListener());
		config.addMapPartitionLostListenerConfig(plConfig);
		return config;
	}

	/**
	 * Creates the STANDARD map config, with backup e standard config for all maps 
	 * @param name name of map
	 * @return map config to add to Hazelcast config
	 */
	public static MapConfig createMapConfig(String name){
		// creates teh map config
		MapConfig config = new MapConfig();
		// sets map name
		config.setName(name);
		// sets backup default
		// and also the eviction if if is not used by dfault
		config.setBackupCount(MapConfig.MAX_BACKUP_COUNT);
		config.setEvictionPercentage(MapConfig.DEFAULT_EVICTION_PERCENTAGE);
		config.setEvictionPolicy(EvictionPolicy.NONE);
		// sets size of map even if not used without eviction
		MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
		maxSizeConfig.setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.PER_NODE);
		maxSizeConfig.setSize(MaxSizeConfig.DEFAULT_MAX_SIZE);
		config.setMaxSizeConfig(maxSizeConfig);
		// reads backup to be fast 
		// and activates stats
		config.setReadBackupData(true);
		config.setStatisticsEnabled(true);
		return config;
	}
	
	/**
	 * Loads eviction config to the map
	 * @param config map config to change
	 * @param eviction eviction configuration
	 */
	private static void loadEviction(MapConfig config, Eviction eviction){
		// checks if the eviction is correct
		if (eviction != null && eviction.getMaxSize() > 0 && eviction.getPercentage() < Numbers.N_100 && eviction.getPercentage() > 0){
			// by default is LRU
			config.setEvictionPolicy(EvictionPolicy.LRU);
			// sets eviction parameters
			config.setEvictionPercentage(eviction.getPercentage());
			MaxSizeConfig maxSizeConfig = config.getMaxSizeConfig();
			maxSizeConfig.setSize(eviction.getMaxSize());
		}
	}
	
}
