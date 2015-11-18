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

import org.pepstock.jem.node.Main;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;

/**
 * Utility to check if the HC map is configured with eviction.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class EvictionHelper {

	/**
	 * to avoid any instantiation 
	 */
	private EvictionHelper() {
	}

	/**
	 * Checks if the HC map is configured with eviction 
	 * @param mapName map name to check
	 * @return true if the HC configuration has got the eviction, otherwise false
	 */
	public static boolean isEvicted(String mapName){
		boolean isEvicted = true;
		// gets the map config
		MapConfig config = Main.getHazelcastConfig().getMapConfig(mapName);
		// if has got the eviction
		if (config.getEvictionPolicy() != null){
			// checks the eviction policy
			isEvicted = isEvicted && !config.getEvictionPolicy().equalsIgnoreCase(MapConfig.DEFAULT_EVICTION_POLICY);
		} else {
			// otherwise not evicted
			return false;
		}
		// gets the size config
		MaxSizeConfig size = config.getMaxSizeConfig();
		// if there is
		if (size != null){
			// checks if the max amount of objects has been set
			isEvicted = isEvicted && size.getSize() > 0 && size.getSize() < Integer.MAX_VALUE;
		} else {
			// otherwise not evicted
			return false;
		}
		return isEvicted;
	}
}
