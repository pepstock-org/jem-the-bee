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
package org.pepstock.jem.node.executors.gfs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.GfsFile;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * The executor returns the list of files and/or directories in a specific folder.
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
	 * 
	 */
	public GetFile(int type, String file) {
		super(type, file);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResult(java.io.File)
	 */
	@Override
	public String getResult(String parentPath, File file) throws ExecutorException {
		if (!file.isFile()){
			throw new ExecutorException(NodeMessage.JEMC188E, file);
		}
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new ExecutorException(NodeMessage.JEMC242E, e, file);
		}
	}
}