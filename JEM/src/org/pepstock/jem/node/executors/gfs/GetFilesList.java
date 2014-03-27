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
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.GfsFile;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.sgm.InvalidDatasetNameException;
import org.pepstock.jem.node.sgm.PathsContainer;

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
	 * Is constant for root path
	 */
	public static final String ROOT_PATH = ".";
	
	
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
		return getFilesList(parentPath, parent);
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.gfs.Get#getResultForDataPath(java.lang.String)
	 */
	@Override
	public Collection<GfsFile> getResultForDataPath() throws ExecutorException {
		// checks if a data type and rootpath request. If yes, having the storage groups manager
		// scans all defined data paths
		// otherwise asks for the specific folder
		if (ROOT_PATH.equalsIgnoreCase(getItem())){
			Collection<GfsFile> list = new LinkedList<GfsFile>();
			for (String path : Main.DATA_PATHS_MANAGER.getDataPaths()){
				list.addAll(getFilesList(path, new File(path)));
			}
			return list;
		} else {
			String item = FilenameUtils.normalize(getItem().endsWith(File.separator) ? getItem() : getItem() + File.separator, true);
			try {
				PathsContainer paths = Main.DATA_PATHS_MANAGER.getPaths(item);
				String parentPath = paths.getCurrent().getContent();
				File file = new File(parentPath, getItem());
				if (!file.exists() && paths.getOld()!=null){
					parentPath = paths.getOld().getContent();
				}
				file = new File(parentPath, getItem());
				// checks if folder exists and must be a folder (not a file)
				if (!file.exists()){
					throw new ExecutorException(NodeMessage.JEMC186E, getItem());
				}
				return this.getResult(parentPath, file);
			} catch (InvalidDatasetNameException e) {
				throw new ExecutorException(e.getMessageInterface(), getItem());
			}
		}
	}

	/**
	 * 
	 * @param parentPath
	 * @param parent
	 * @return
	 */
	private Collection<GfsFile> getFilesList(String parentPath, File parent){
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