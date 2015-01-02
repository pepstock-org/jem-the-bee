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
package org.pepstock.jem.node.executors.gfs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;

/**
 * The executor returns a file from a specific folder, defined in GFS of JEM.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 *
 */
public class GetFile extends Get<String> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Saves the type of GFS to read and the folder
	 * 
	 * @param type could a integer value
	 * @see GfsFile
	 * @param file the folder (relative to type of GFS) to use to read files and directories
	 * @param pathName path data name for the file argument
	 * 
	 */
	public GetFile(int type, String file, String pathName) {
		super(type, file, pathName);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResult(java.io.File)
	 */
	@Override
	public String getResult(String parentPath, File file) throws ExecutorException {
		// it must be a file!!! 
		// Otherwise EXCEPTION
		if (!file.isFile()){
			throw new ExecutorException(NodeMessage.JEMC188E, file);
		}
		// reads all file in a string
		// and return it
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new ExecutorException(NodeMessage.JEMC242E, e, file);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResultForDataPath()
	 */
	@Override
	public String getResultForDataPath() throws ExecutorException {
		// if here, it's reading a file from data path.
		// if path name is not null
		if (getPathName() != null){
			// calculated the right parent path using data paths manager
			String parentPath = Main.DATA_PATHS_MANAGER.getAbsoluteDataPathByName(getPathName());
			// creates a file with parent path and item
			File file = new File(parentPath, getItem());
			// calls getresult to return the file content
			return this.getResult(parentPath, file);
		} else {
			// if here, the path name is undefined
			try {
				// gets paths container with the name of the file
				PathsContainer paths = Main.DATA_PATHS_MANAGER.getPaths(getItem());
				// gets the current value
				String parentPath = paths.getCurrent().getContent();
				// creates the file
				File file = new File(parentPath, getItem());
				// if the file not exists,
				// try to read the file on the OLD data path definition
				// if the old data path is defined
				if (!file.exists() && paths.getOld()!=null){
					parentPath = paths.getOld().getContent();
				}
				// recreates the file
				file = new File(parentPath, getItem());
				// checks if folder exists and must be a folder (not a file)
				if (!file.exists()){
					throw new ExecutorException(NodeMessage.JEMC186E, getItem());
				}
				// calls getresult to return the file content
				return this.getResult(parentPath, file);
			} catch (InvalidDatasetNameException e) {
				// file name doesn't match with data path rules
				throw new ExecutorException(e.getMessageInterface(), e, getItem());
			}
		}
	}
}