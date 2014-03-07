/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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

/**
 * Persistent manager for INPUT queue.<br>
 * It uses DBManager instance to perform all sqls.<br>
 * It throws MapStoreException if the database manager has errors but Hazelcast
 * is not able to catch them, so it logs all errors.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class RunningMapManager extends JobMapManager {
	
	private static RunningMapManager INSTANCE = null; 
	
	/**
	 * Construct the object instantiating a new DBManager
	 */
	public RunningMapManager() {
		super(Queues.RUNNING_QUEUE, RunningDBManager.getInstance());
		RunningMapManager.setInstance(this);
	}

	/**
	 * @return the iNSTANCE
	 */
	public static RunningMapManager getInstance() {
		return INSTANCE;
	}

	/**
	 * @param instance the instance to set
	 */
	private static void setInstance(RunningMapManager instance) {
		INSTANCE = instance;
	}
	
}