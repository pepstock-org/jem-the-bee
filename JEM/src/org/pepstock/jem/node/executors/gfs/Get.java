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
package org.pepstock.jem.node.executors.gfs;

import java.io.File;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * The executor returns the list of files and/or directories in a specific folder.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 * @param <T>
 *
 */
public abstract class Get<T> extends DefaultExecutor<T> {
	
	private static final long serialVersionUID = 1L;

	private String item = null;
	
	private String pathName = null;
	
	private int type = GfsFile.DATA;
	
	/**
	 * Saves the type of GFS to read and the folder
	 * 
	 * @param type could a integer value
	 * @see GfsFile
	 * @param item the folder (relative to type of GFS) to use to read files and directories or the file to download
	 * @param pathName data path name or null
	 * 
	 */
	public Get(int type, String item, String pathName) {
		this.type = type;
		this.item = item;
		this.pathName = pathName;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	
	/**
	 * @return the pathName
	 */
	public String getPathName() {
		return pathName;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public final T execute() throws ExecutorException {
	
		String parentPath = null;
		File file = null;
		
		// checks here the type of file-system to scan
		switch (type) {
			case GfsFile.DATA:
				return getResultForDataPath();
			case GfsFile.LIBRARY:
				parentPath = System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME);
				break;
			case GfsFile.SOURCE:
				parentPath = System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME);
				break;
			case GfsFile.CLASS:
				parentPath = System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME);
				break;
			case GfsFile.BINARY:
				parentPath = System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
				break;
			default:
				return getResultForDataPath();
		}
		file = new File(parentPath, item);
		// checks if folder exists and must be a folder (not a file)
		if (!file.exists()){
			throw new ExecutorException(NodeMessage.JEMC186E, item);
		}
		return getResult(parentPath, file);
	}
	
	/**
	 * Abstract method which return the result from GFS
	 * @param parentPath Path of different type
	 * @param file file entry to get result
	 * @return result of GFS action
	 * @throws ExecutorException if any exception occurs 
	 */
	public abstract T getResult(String parentPath, File file) throws ExecutorException;

	/**
	 * Abstract method which return the result from GFS data path
	 * @param item  file name 
	 * @return result of GFS data path action
	 * @throws ExecutorException if any exception occurs 
	 */
	public abstract T getResultForDataPath() throws ExecutorException;
	
}