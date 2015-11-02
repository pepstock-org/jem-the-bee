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

import org.pepstock.jem.node.persistence.mongo.CommonResourcesMongoManager;
import org.pepstock.jem.node.persistence.mongo.InputMongoManager;
import org.pepstock.jem.node.persistence.mongo.NodesMongoManager;
import org.pepstock.jem.node.persistence.mongo.OutputMongoManager;
import org.pepstock.jem.node.persistence.mongo.PreJobMongoManager;
import org.pepstock.jem.node.persistence.mongo.RolesMongoManager;
import org.pepstock.jem.node.persistence.mongo.RoutingConfMongoManager;
import org.pepstock.jem.node.persistence.mongo.RoutingMongoManager;
import org.pepstock.jem.node.persistence.mongo.RunningMongoManager;
import org.pepstock.jem.node.persistence.mongo.UserPreferencesMongoManager;
import org.pepstock.jem.node.persistence.sql.CommonResourcesDBManager;
import org.pepstock.jem.node.persistence.sql.InputDBManager;
import org.pepstock.jem.node.persistence.sql.NodesDBManager;
import org.pepstock.jem.node.persistence.sql.OutputDBManager;
import org.pepstock.jem.node.persistence.sql.PreJobDBManager;
import org.pepstock.jem.node.persistence.sql.RolesDBManager;
import org.pepstock.jem.node.persistence.sql.RoutingConfigDBManager;
import org.pepstock.jem.node.persistence.sql.RoutingDBManager;
import org.pepstock.jem.node.persistence.sql.RunningDBManager;
import org.pepstock.jem.node.persistence.sql.SQLContainerFactory;
import org.pepstock.jem.node.persistence.sql.UserPreferencesDBManager;

/**
 * Instantiates all map stores for HC, btoh for SQL and for MONGO.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class MapManagersFactory {
	
	/**
	 * to avoid any instantiation
	 */
	private MapManagersFactory() {
	}

	/**
	 * Creates all map managers leveraging on SQL factory
	 * @param factory DB factory
	 */
	public static void createMapManagers(SQLContainerFactory factory){
		InputMapManager.createInstance(new InputDBManager(factory));
		RunningMapManager.createInstance(new RunningDBManager(factory));
		OutputMapManager.createInstance(new OutputDBManager(factory));
		RoutingMapManager.createInstance(new RoutingDBManager(factory));
		CommonResourcesMapManager.createInstance(new CommonResourcesDBManager(factory));
		NodesMapManager.createInstance(new NodesDBManager(factory));
		PreJobMapManager.createInstance(new PreJobDBManager(factory));
		RolesMapManager.createInstance(new RolesDBManager(factory));
		RoutingConfigMapManager.createInstance(new RoutingConfigDBManager(factory));
		UserPreferencesMapManager.createInstance(new UserPreferencesDBManager(factory));
	}

	/**
	 * Creates all map managers for Mongo backend
	 */
	public static void createMapManagers(){
		InputMapManager.createInstance(new InputMongoManager());
		RunningMapManager.createInstance(new RunningMongoManager());
		OutputMapManager.createInstance(new OutputMongoManager());
		RoutingMapManager.createInstance(new RoutingMongoManager());
		CommonResourcesMapManager.createInstance(new CommonResourcesMongoManager());
		NodesMapManager.createInstance(new NodesMongoManager());
		PreJobMapManager.createInstance(new PreJobMongoManager());
		RolesMapManager.createInstance(new RolesMongoManager());
		RoutingConfigMapManager.createInstance(new RoutingConfMongoManager());
		UserPreferencesMapManager.createInstance(new UserPreferencesMongoManager());
	}

	/**
	 * Initializes the database structures if they don't exist
	 * @throws DatabaseException if any DB error occurs
	 */
	public static void initAll() throws DatabaseException{
		InputMapManager.getInstance().checkAndCreate();
		RunningMapManager.getInstance().checkAndCreate();
		OutputMapManager.getInstance().checkAndCreate();
		RoutingMapManager.getInstance().checkAndCreate();
		CommonResourcesMapManager.getInstance().checkAndCreate();
		NodesMapManager.getInstance().checkAndCreate();
		PreJobMapManager.getInstance().checkAndCreate();
		RolesMapManager.getInstance().checkAndCreate();
		RoutingConfigMapManager.getInstance().checkAndCreate();
		UserPreferencesMapManager.getInstance().checkAndCreate();
	}
	
}
