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
package org.pepstock.jem.grs;

import org.pepstock.jem.node.hazelcast.ConfigFactory;
import org.pepstock.jem.node.hazelcast.ConfigProvider;
import org.pepstock.jem.node.hazelcast.Queues;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;

/**
 * It provides the Hazeclast configuration for GRS map.
 * 
 * @see Queues.GRS_COUNTER_MUTEX_MAP
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class GrsMapConfigProvider implements ConfigProvider {
	
	private static final GrsMapConfigProvider INSTANCE = new GrsMapConfigProvider();

	/**
	 * Singleton method to get the instance
	 * @return the instance of this map config
	 */
	public static final GrsMapConfigProvider getInstance(){
		return INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.hazelcast.ConfigProvider#getMapConfig()
	 */
	@Override
	public MapConfig getMapConfig() {
		return ConfigFactory.createMapConfig(Queues.GRS_COUNTER_MUTEX_MAP);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.hazelcast.ConfigProvider#getQueueConfig()
	 */
	@Override
	public QueueConfig getQueueConfig() {
		return null;
	}

}
