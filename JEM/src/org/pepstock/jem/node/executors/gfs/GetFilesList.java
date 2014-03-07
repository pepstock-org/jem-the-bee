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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
public class GetFilesList extends Get<Collection<GfsFile>> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Saves the type of GFS to read and the folder
	 * 
	 * @param type could a integer value
	 * @see GfsFile
	 * @param path the folder (relative to type of GFS) to use to read files and directories
	 * 
	 */
	public GetFilesList(int type, String path) {
		super(type, path);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResult(java.io.File)
	 */
	@Override
	public Collection<GfsFile> getResult(String parentPath, File parent) throws ExecutorException {
		if (!parent.isDirectory()){
			throw new ExecutorException(NodeMessage.JEMC187E, parent.toString());
		}
		
		// creates the collection of files and reads them
		Collection<GfsFile> list = new ArrayList<GfsFile>();
		File[] files = parent.listFiles();
		if (files != null && files.length > 0){
			// scans all files and loads them into a collection, normalizing the names
			for (int i=0; i<files.length; i++){
				boolean isDirectory = files[i].isDirectory();
				String name = files[i].getName();
				
				String longName = StringUtils.removeStart(FilenameUtils.normalize(files[i].getAbsolutePath(), true), 
						FilenameUtils.normalize(parentPath, true)).substring(1);

				GfsFile file = new GfsFile();
				file.setDirectory(isDirectory);
				file.setName(name);
				file.setLongName(longName);
				file.setLength((isDirectory) ? -1 : files[i].length());
				file.setLastModified(files[i].lastModified());

				list.add(file);
			}
		}
		return list;
	}

}