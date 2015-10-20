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

import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.persistence.database.PreJobDBManager;

/**
 * Enumaration of Recoverable managers, related to own HZ queue/map name.
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public enum RecoverableManager {
	
	/**
	 *  JCL checking queue
	 */
	JCL_CHECKING(PreJobDBManager.getInstance(), Queues.JCL_CHECKING_QUEUE),
	/**
	 * INPUT queue
	 */
	INPUT(InputMapManager.getInstance(), Queues.INPUT_QUEUE),
	/**
	 * RUNNIG queue
	 */
	RUNNING(RunningMapManager.getInstance(), Queues.RUNNING_QUEUE),
	/**
	 * OUTPUT queue
	 */
	OUTPUT(OutputMapManager.getInstance(), Queues.OUTPUT_QUEUE),
	/**
	 * ROUTING queue
	 */
	ROUTING(RoutingMapManager.getInstance(), Queues.ROUTING_QUEUE),
	/**
	 * Common resources map
	 */
	RESOURCES(CommonResourcesMapManager.getInstance(), Queues.COMMON_RESOURCES_MAP),
	/**
	 * Roles map
	 */
	ROLES(RolesMapManager.getInstance(), Queues.ROLES_MAP);
	
	private Recoverable recoverable = null;
	
	private String queueName = null;

	/**
	 * Creates the manager with the recoverable instance and HZ queue/map name
	 * @param recoverable manager instance
	 * @param queueName HZ queue name
	 */
	private RecoverableManager(Recoverable recoverable, String queueName) {
		this.recoverable = recoverable;
		this.queueName = queueName;
	}

	/**
	 * @return the recoverable
	 */
	public Recoverable getRecoverable() {
		return recoverable;
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Calls the recover method of recoverable manager, searching by HZ queue name.
	 * @param statement statement to redo
	 * @throws Exception if any error occurs applying the redo statement on DB
	 */
	public static void recover(RedoStatement statement) throws Exception{
		for (RecoverableManager rec : values()){
			if (rec.getQueueName().equalsIgnoreCase(statement.getQueueName())){
				rec.getRecoverable().recover(statement);
				return;
			}
		}
	}
	
}
