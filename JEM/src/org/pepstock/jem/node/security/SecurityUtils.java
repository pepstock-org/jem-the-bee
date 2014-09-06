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
package org.pepstock.jem.node.security;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.catalog.gdg.Root;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.DataPathsContainer;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class SecurityUtils {
	
	/**
	 * if the file name must be checked with permissions of local file system
	 */
	public static final int TO_BE_LOCAL_FS_CHECKED = 3;
	
	/**
	 * if the file name must be checked with permissions of GFS
	 */
	public static final int TO_BE_GFS_CHECKED = 2;

	/**
	 * if the file name must be checked with permissions 
	 */
	public static final int TO_BE_CHECKED = 1;
	
	/**
	 * No security check
	 */
	public static final int TO_BE_IGNORED = 0;
	
	/**
	 * Not authorized! Exception!
	 */
	public static final int TO_BE_REJECTED = -1;

	private static final String OUTPUT_PATH = System.getProperty(ConfigKeys.JEM_OUTPUT_PATH_NAME);
	
	private static final String SOURCE_PATH = System.getProperty(ConfigKeys.JEM_SOURCE_PATH_NAME);
	
	private static final String LIBRARY_PATH = System.getProperty(ConfigKeys.JEM_LIBRARY_PATH_NAME);
	
	private static final String BINARY_PATH = System.getProperty(ConfigKeys.JEM_BINARY_PATH_NAME);
	
	private static final String CLASS_PATH = System.getProperty(ConfigKeys.JEM_CLASSPATH_PATH_NAME);
	
	private static final String PERSISTENCE_PATH = System.getProperty(ConfigKeys.JEM_PERSISTENCE_PATH_NAME);
	
	private static final String HOME = System.getProperty(ConfigKeys.JEM_HOME);
	
	private String temp = null;
	
	/**
	 * 
	 */
	public SecurityUtils(){
		try {
			temp = FilenameUtils.normalize(File.createTempFile("security", "tmp").getParentFile().getAbsolutePath(), true);
		} catch (IOException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);			
		}
	}
	
	/**
	 * Checks if the file name must be checked, ignored or rejected 
	 * 
	 * @param fileName to check!
	 * @return -1, 0, 1 if authorized or must be checked
	 */
	public int checkReadFileName(String fileName){
		
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);
		if (dataPath != null){
			return TO_BE_CHECKED;
		}

		boolean textFolder = fileName.startsWith(OUTPUT_PATH) ||
				fileName.startsWith(SOURCE_PATH);
		
		boolean binaryFolder = fileName.startsWith(LIBRARY_PATH) ||
				fileName.startsWith(BINARY_PATH) ||
				fileName.startsWith(CLASS_PATH);
		
		if (textFolder || binaryFolder){
			return TO_BE_IGNORED;
		}

		if (fileName.startsWith(PERSISTENCE_PATH)){
			return TO_BE_REJECTED;
		}

		String ext = FilenameUtils.getExtension(fileName);
		if ("class".equalsIgnoreCase(ext) || "jar".equalsIgnoreCase(ext) || "zip".equalsIgnoreCase(ext)){
			return TO_BE_IGNORED;
		}
		
		if (fileName.startsWith(HOME) && FilenameUtils.wildcardMatch(fileName,HOME+File.separator+"*"+File.separator+"config*")){
			return TO_BE_REJECTED;
		}
		return TO_BE_IGNORED;
	}
	
	/**
	 * Checks if the file name must be checked, ignored or rejected 
	 * 
	 * @param fileName to check!
	 * @return -1, 0, 1 if authorized or must be checked
	 */
	public int checkWriteFileName(String fileName){
		
		if (fileName.endsWith(Root.ROOT_FILE_NAME)){
			return TO_BE_REJECTED;
		}
		
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);

		if (dataPath != null){
			return TO_BE_CHECKED;
		}
		
		boolean gfsFolder = fileName.startsWith(LIBRARY_PATH) ||
				fileName.startsWith(BINARY_PATH) ||
				fileName.startsWith(CLASS_PATH) ||
				fileName.startsWith(SOURCE_PATH);
		
		if (gfsFolder){
			return TO_BE_GFS_CHECKED;
		}
		
		if (fileName.startsWith(temp)){
				return TO_BE_IGNORED;
		}
		
		if (fileName.startsWith(OUTPUT_PATH)){
			/**
			 * Following code checks if the parent folder of filename is the same of output.
			 * if yes, it's not allowed. Output data set are written on folder [output]/[step]/[file]
			 * so the capabilities to write SYSOUT is guaranteed 
			 */
			if (FilenameUtils.getFullPathNoEndSeparator(fileName).equalsIgnoreCase(OUTPUT_PATH)){
				return TO_BE_REJECTED;
			}
			return TO_BE_IGNORED;
		}
		
		if (fileName.startsWith(HOME)){
			return TO_BE_REJECTED;
		}

		if (fileName.startsWith(PERSISTENCE_PATH)){
			return TO_BE_REJECTED;
		}

		// if arrives here, means is local file system
		return TO_BE_LOCAL_FS_CHECKED ;
	}
	
	/**
	 * Based on filename, it returns the permission to check when the file is not located to DATA path.
	 * @param fileName to check!
	 * @return permission of GFS to check
	 */
	public String getGfsPermission(String fileName){
		if (fileName.startsWith(LIBRARY_PATH)){
			return Permissions.GFS_LIBRARY;
		} else if(fileName.startsWith(BINARY_PATH)){
			return Permissions.GFS_BINARY;
		} else if(fileName.startsWith(CLASS_PATH)){
			return Permissions.GFS_CLASS;
		} else if(fileName.startsWith(SOURCE_PATH)){
			return Permissions.GFS_SOURCES;
		} 
		return Permissions.GFS_LIBRARY;
	}
	/**
	 * Extract the right file name, used inside the permissions
	 * @param fileName complete path
	 * @return relative file name
	 */
	public String normalizeFileName(String fileName){
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);

		if (dataPath != null){
			String file = StringUtils.substringAfter(fileName, dataPath);
			if (FilenameUtils.separatorsToSystem(file).startsWith(File.separator)){
				return file.substring(1);
			} else {
				return file;
			}
		}
		return fileName;
	}

}