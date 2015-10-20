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
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public enum RecoverableManager {
	
	JCL_CHECKING(PreJobDBManager.getInstance(), Queues.JCL_CHECKING_QUEUE),
	INPUT(InputMapManager.getInstance(), Queues.INPUT_QUEUE),
	RUNNING(RunningMapManager.getInstance(), Queues.RUNNING_QUEUE),
	OUTPUT(OutputMapManager.getInstance(), Queues.OUTPUT_QUEUE),
	ROUTING(RoutingMapManager.getInstance(), Queues.ROUTING_QUEUE),
	RESOURCES(CommonResourcesMapManager.getInstance(), Queues.COMMON_RESOURCES_MAP),
	ROLES(RolesMapManager.getInstance(), Queues.ROLES_MAP);
	
	private Recoverable recoverable = null;
	
	private String queueName = null;

	/**
	 * @param recoverable
	 * @param queueName
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

	public static void recover(RedoStatement statement) throws Exception{
		for (RecoverableManager rec : values()){
			if (rec.getQueueName().equalsIgnoreCase(statement.getQueueName())){
				rec.getRecoverable().recover(statement);
				return;
			}
		}
	}
	
}
