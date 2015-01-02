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
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.List;

import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;

/**
 * Datapath manager conatiner, to use inside a job during its execution. This is a wrapper
 * that is able to call easily the right methods of manager.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public final class DataPathsContainer implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private static final DataPathsContainer INSTANCE = new DataPathsContainer();
	
	private DataPathsManager manager = null;

	/**
	 * Empty constructor
	 */
	private DataPathsContainer() {
		
	}
	
	/**
	 * Loads the data path manager, ONLY if is not set
	 * @param manager data paths manager
	 */
	public static final void createInstance(DataPathsManager manager){
		if (INSTANCE.manager == null){
			INSTANCE.manager = manager;
		}
	}
	
	/**
	 * @return the instance of singleton
	 */
	public static final DataPathsContainer getInstance(){
		return INSTANCE;
	}

	/**
     * Gets the path container checking the rules pattern with file name of dataset
     * @param fileName file name of dataset
     * @return path container
	 * @throws InvalidDatasetNameException if file name doesn't match with defined rules
     */
    public PathsContainer getPaths(String fileName) throws InvalidDatasetNameException{
    	return manager.getPaths(fileName);
    }
    
    /**
     * Returns the absolute data path to use for file name argument
     * @param fileName file name to use to get the absolute data path
     * @return the the absolute data path
     */
    public String getAbsoluteDataPath(String fileName){
    	return manager.getAbsoluteDataPath(fileName);
    }
    
    /**
     * Returns a list of string with complete data path defined in JEM configuration file
     * @return a list of string with complete data path defined in JEM configuration file
     */
    public List<String> getDataPaths(){
    	return manager.getDataPaths();
    }
    
    /**
     * Returns a list of string with data path name defined in JEM configuration file
     * @return a list of string with data path name defined in JEM configuration file
     */
    public List<String> getDataPathsNames(){
    	return manager.getDataPathsNames();
    }
    
}
