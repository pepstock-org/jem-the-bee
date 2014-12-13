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
import org.pepstock.jem.node.OutputSystem;
import org.pepstock.jem.node.configuration.ConfigKeys;

/**
 * Utility class to check if the permission checking on the user of the job (exclude the administrators), 
 * to be authorized to access in READ or WRITE
 * to the folders and than files of JEM cluster, must be performed or not.
 * 
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
	 * Constructs the object creating the temporary folder, using the JEM node
	 * Operating system configuration. 
	 */
	public SecurityUtils(){
		try {
			// creates a temporary file only to the absolute path
			// of the file, used as temporary files folder
			// it doesn't use the system property to avoid errors if not set
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
	 * @return if authorized or must be checked
	 */
	public int checkReadFileName(String fileName){
		
		// checks if filename is located on a data paths
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);
		// if yes (not null!) the permission must be checked
		if (dataPath != null){
			return TO_BE_CHECKED;
		}

		// gets the boolean if is going to source or output folders
		boolean textFolder = fileName.startsWith(OUTPUT_PATH) ||
				fileName.startsWith(SOURCE_PATH);
		// gets the boolean if is going to binary or library or class folders
		boolean binaryFolder = fileName.startsWith(LIBRARY_PATH) ||
				fileName.startsWith(BINARY_PATH) ||
				fileName.startsWith(CLASS_PATH);
		
		// if is going on the previous checked folder, DOESN'T
		// perform any permission check
		if (textFolder || binaryFolder){
			return TO_BE_IGNORED;
		}
		
		// if you're going on the persistence path, a Security exception will be thrown
		// because it's not allowed to read anything on persistence folder
		if (fileName.startsWith(PERSISTENCE_PATH)){
			return TO_BE_REJECTED;
		}

		// if you're reading classes, jar or zip, DOESN'T
		// perform any permission check
		// this is done when JAVA programs are executed
		String ext = FilenameUtils.getExtension(fileName);
		if ("class".equalsIgnoreCase(ext) || "jar".equalsIgnoreCase(ext) || "zip".equalsIgnoreCase(ext)){
			return TO_BE_IGNORED;
		}
		
		// any access to the JEM home to read configuration files are not allowed.
		// Security exception will be thrown
		if (fileName.startsWith(HOME) && FilenameUtils.wildcardMatch(fileName,HOME+File.separator+"*"+File.separator+"config*")){
			return TO_BE_REJECTED;
		}
		// if you are here,  DOESN'T
		// perform any permission check
		return TO_BE_IGNORED;
	}
	
	/**
	 * Checks if the file name must be checked, ignored or rejected 
	 * 
	 * @param fileName to check!
	 * @return if authorized or must be checked
	 */
	public int checkWriteFileName(String fileName){
		// to write a "root.properties" file is not allowed
		// because that file is use for GDG to maintain the catalog
		// and the risk is to create an inconsistent situation on GDG
		if (fileName.endsWith(Root.ROOT_FILE_NAME)){
			return TO_BE_REJECTED;
		}
		
		// checks if filename is located on a data paths
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);
		// if yes (not null!) the permission must be checked
		if (dataPath != null){
			return TO_BE_CHECKED;
		}
		
		// gets if you are going on toher GFS file systems
		boolean gfsFolder = fileName.startsWith(LIBRARY_PATH) ||
				fileName.startsWith(BINARY_PATH) ||
				fileName.startsWith(CLASS_PATH) ||
				fileName.startsWith(SOURCE_PATH);

		// if yes, ad additional permission check must be performed (the user must have
		// the GFS file system permission)
		if (gfsFolder){
			return TO_BE_GFS_CHECKED;
		}
		
		// if you're using the temporary folder, GO! No check
		if (fileName.startsWith(temp)){
				return TO_BE_IGNORED;
		}
		
		// if you're trying to access to the output path
		if (fileName.startsWith(OUTPUT_PATH)){
			// checks if you're access to the OUTPUT path, not to the sub folder
			// because subfolder are ALWAYS accessible in write because used to write
			// user data as SYSOUT
			if (FilenameUtils.getFullPathNoEndSeparator(fileName).equalsIgnoreCase(OUTPUT_PATH)){
				String file = FilenameUtils.getName(fileName);
				// checks you are trying to write on the system files,
				// files managed by JEM node. This is NOT allowed.
				// throws a SECURITY exception
				if (OutputSystem.JCL_FILE.equalsIgnoreCase(file) || 
						OutputSystem.JOB_FILE.equalsIgnoreCase(file) ||
						OutputSystem.JOBLOG_FILE.equalsIgnoreCase(file) ||
						OutputSystem.MESSAGESLOG_FILE.equalsIgnoreCase(file)){
					return TO_BE_REJECTED;
				}
			}
			// if here, you can write on OUTPUT path
			return TO_BE_IGNORED;
		}
		
		// if you are trying to write on JEM home.
		// This is NOT allowed.
		// throws a SECURITY exception
		if (fileName.startsWith(HOME)){
			return TO_BE_REJECTED;
		}

		// if you are trying to write on persistent path.
		// This is NOT allowed.
		// throws a SECURITY exception
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
		// here selects which permission you msut have
		// to access to GFS
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
		// gets data path
		String dataPath = DataPathsContainer.getInstance().getAbsoluteDataPath(fileName);
		// if not a data path, return file name as is
		if (dataPath != null){
			// extract the filename, without the path of DATA path
			// because the permission are based on the file name
			// without the mount point of data path
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