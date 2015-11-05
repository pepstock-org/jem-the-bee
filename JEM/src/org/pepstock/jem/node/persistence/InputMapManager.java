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

import org.pepstock.jem.Job;

/**
 * Persistent manager for INPUT queue.<br>
 * It uses database Manager instance to perform all operations.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class InputMapManager extends AbstractMapManager<Job> {
	
	private static InputMapManager INSTANCE = null; 
	
	/**
	 * Construct the object using a database Manager
	 * @param dbManager database manager
	 */
	private InputMapManager(DataBaseManager<Job> dbManager) {
		super(dbManager, true);
	}
	
	/**
	 * Creates the instance of map store if not already initialized
	 * @param dbManager database manger to use for persistence
	 * @return the map store
	 */
	public static InputMapManager createInstance(DataBaseManager<Job> dbManager){
		if (INSTANCE == null){
			INSTANCE = new InputMapManager(dbManager);
		}
		return INSTANCE;
	}

	/**
	 * @return the iNSTANCE
	 */
	public static InputMapManager getInstance() {
		return INSTANCE;
	}
	
}