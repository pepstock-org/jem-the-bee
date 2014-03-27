/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node;

import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public final class DataPathsContainer {
	
	private static final DataPathsContainer INSTANCE = new DataPathsContainer();
	
	private DataPathsManager manager = null;

	/**
	 * 
	 */
	private DataPathsContainer() {
		
	}
	
	public static final void createInstance(DataPathsManager manager){
		if (INSTANCE.manager != null){
			INSTANCE.manager = manager;
		}
	}
	
	public static final DataPathsContainer getInstance(){
		return INSTANCE;
	}

    public PathsContainer getPaths(String fileName) throws InvalidDatasetNameException{
    	return manager.getPaths(fileName);
    }
    
    public String getAbsoluteDataPath(String fileName){
    	return manager.getAbsoluteDataPath(fileName);
    }
}
