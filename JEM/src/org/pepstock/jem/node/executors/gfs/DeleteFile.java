/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.node.executors.ExecutorException;

/**
 * The executor deletes a file.
 * 
 * @author Simone "Busy" Businaro
 * @version 2.1
 * 
 */
public class DeleteFile extends Get<Boolean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Saves the type of GFS to delete and the folder
	 * 
	 * @param type could a integer value
	 * @see GfsFile
	 * @param item the folder (relative to type of GFS) to use to delete files and directories
	 * @param pathName data path name or null
	 * 
	 */
	public DeleteFile(int type, String item, String pathName) {
		super(type, item, pathName);
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResult(java.lang.String, java.io.File)
	 */
	@Override
	public Boolean getResult(String parentPath, File file) throws ExecutorException {
		return file.delete();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResultForDataPath()
	 */
	@Override
	public Boolean getResultForDataPath() throws ExecutorException {
		return false;
	}
}