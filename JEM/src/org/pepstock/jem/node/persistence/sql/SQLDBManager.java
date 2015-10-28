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
package org.pepstock.jem.node.persistence.sql;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.persistence.DatabaseException;
import org.pepstock.jem.util.MemorySize;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public enum SQLDBManager {

	PREJOB(new PreJobDBManager()),
	INPUT(new InputDBManager()),
	RUNNING(new RunningDBManager()),
	OUTPUT(new OutputDBManager()),
	ROUTING(new RoutingDBManager()),
	RESOURCES(new CommonResourcesDBManager()),
	ROLES(new RolesDBManager()),
	ROUTING_CONFIG(new RoutingConfigDBManager()),
	USER_PREFERENCES(new UserPreferencesDBManager()),
	NODES(new NodesDBManager());
	
	private AbstractDBManager<?> manager = null;

	/**
	 * @param manager
	 */
	private SQLDBManager(AbstractDBManager<?> manager) {
		this.manager = manager;
	}
	
	/**
	 * @return the manager
	 */
	private AbstractDBManager<?> getManager() {
		return manager;
	}

	/**
	 * @param clazz 
	 * @return the manager
	 */
	@SuppressWarnings("unchecked")
	public <T> AbstractDBManager<T> getManager(Class<T> clazz) {
		return (AbstractDBManager<T>) manager;
	}
	
	public static void setSQLContainer(String queueName, SQLContainer container){
		for (SQLDBManager man : values()){
			if (man.getManager().getQueueName().equalsIgnoreCase(queueName)){
				man.getManager().setSqlContainer(container);
				return;
			}
		}
	}
	
	public static void initAll() throws DatabaseException{
		for (SQLDBManager man : values()){
			man.getManager().checkAndCreate();
		}
	}
	
	public static long getSize() throws DatabaseException{
		long total = 0;
		for (SQLDBManager man : values()){
			long size = man.getManager().getSize();
			LogAppl.getInstance().emit(NodeMessage.JEMC085I, man.getManager().getQueueName(), size / MemorySize.KB);
			total += size;
		}
		return total;
	}
}
