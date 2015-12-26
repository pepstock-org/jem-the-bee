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

import org.pepstock.jem.node.NodeInfo;
import org.pepstock.jem.node.hazelcast.ConfigFactory;

import com.hazelcast.config.MapConfig;

/**
 * Persistent manager for NodeInfos map.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.1
 * 
 */
public class NodesMapManager extends AbstractMapManager<NodeInfo>{
	
	private static NodesMapManager INSTANCE = null;

	/**
	 * Construct the object using a DBManager
	 * @param dbManager db manager
	 */
	private NodesMapManager(DatabaseManager<NodeInfo> dbManager) {
		super(dbManager, false);
	}
	
	/**
	 * Creates the instance of map store if not already initialized
	 * @param dbManager database manger to use for persistence
	 * @return the map store
	 */
	public static NodesMapManager createInstance(DatabaseManager<NodeInfo> dbManager){
		if (INSTANCE == null){
			INSTANCE = new NodesMapManager(dbManager);
		}
		return INSTANCE;
	}
	
	/**
	 * @return the iNSTANCE
	 */
	public static NodesMapManager getInstance() {
		return INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.AbstractMapManager#getMapConfig()
	 */
	@Override
	public MapConfig getMapConfig() {
		return ConfigFactory.createMapConfig(getQueueName());
	}
	
	
}