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
package org.pepstock.jem.node.tasks;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.node.Main;

/**
 * Utility for JAVA to prepare heap size paramenters and classpath with full path of a set of libraries.<br>
 * For classpath, the method returns a class path with only the necessary jar files, to avoid to 
 * overflow the maximum number of byte for environment variables or command line to start the process.<br>
 * GWT jar files are not compatible with ANT, that means that you have the fllowing message, running a ANT
 * using classapth wildcard:
 * <pre>jcl.xml:3: The following error occurred while executing this line:
jar:file:/JEM/lib/ant/ant.jar!/org/apache/tools/ant/antlib.xml:37: Could not create task or type of type: componentdef.

Ant could not find the task or a class this task relies upon.

This is common and has a number of causes; the usual
solutions are to read the manual pages then download and
install needed JAR files, or fix the build file:
 - You have misspelt 'componentdef'.
   Fix: check your spelling.
 - The task needs an external JAR file to execute
     and this is not found at the right place in the classpath.
   Fix: check the documentation for dependencies.
   Fix: declare the task.
 - The task is an Ant optional task and the JAR file and/or libraries
     implementing the functionality were not found at the time you
     yourself built your installation of Ant from the Ant sources.
   Fix: Look in the ANT_HOME/lib for the 'ant-' JAR corresponding to the
     task and make sure it contains more than merely a META-INF/MANIFEST.MF.
     If all it contains is the manifest, then rebuild Ant with the needed
     libraries present in ${ant.home}/lib/optional/ , or alternatively,
     download a pre-built release version from apache.org
 - The build file was written for a later version of Ant
   Fix: upgrade to at least the latest release version of Ant
 - The task is not an Ant core or optional task
     and needs to be declared using <taskdef>.
 - You are attempting to use a task defined using
    <presetdef> or <macrodef> but have spelt wrong or not
   defined it at the point of use

Remember that for JAR files to be visible to Ant tasks implemented
in ANT_HOME/lib, the files must be in the same directory or on the
classpath
 * </pre>
 * 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class JavaUtils {

	private static final String INITIAL_HEAP_SIZE = "-Xms64M";
	
	private static final String MAXIMUM_HEAP_SIZE = "-Xmx{0}M";
	
	private static String maximumHeapSize = null;

	
	/**
	 * To avoid any instantiation
	 */
	private JavaUtils() {
		
	}

	/**
	 * Returns initial heap size to use (only for java process)
	 * 
	 * @return the initialHeapSize
	 */
	public static String getInitialHeapSize() {
		return INITIAL_HEAP_SIZE;
	}

	/**
	 * Returns maximum heap size to use (only for java process)
	 * 
	 * @return the maximumHeapSize
	 */
	public static String getMaximumHeapSize() {
		if (maximumHeapSize == null){
			maximumHeapSize = MessageFormat.format(MAXIMUM_HEAP_SIZE, String.valueOf(Main.EXECUTION_ENVIRONMENT.getMemory()));
		}
		return maximumHeapSize;
	}

	
	/**
	 * Returns classpath with absolute path of libraries (only for java process)
	 * 
	 * @return the classpath
	 */
	public static String getClassPath() {
		return getClassPath(null);
	}

	/**
	 * Returns classpath with absolute parent path of libraries and java wildcard (only for java process).
	 * 
	 * @param additionalFolders additional folders to add 
	 * @return the classpath
	 */
	public static String getClassPath(String[] additionalFolders) {
		String classPath = null;
		String fileSeparator = File.separator;
		String pathSeparator = System.getProperty("path.separator");
		String classPathProperty = System.getProperty("java.class.path");
		String[] filesNames = classPathProperty.split(pathSeparator);
		for (int i=0; i<filesNames.length; i++){
			File file = new File(filesNames[i]);
			
			// here checks if the file is to add to classpath for new process or not
			if (isToAdd(file, additionalFolders)){
				String ext = FilenameUtils.getExtension(file.getAbsolutePath());
				String parent = null;
				// if is a directory, use it as is
				if (file.isDirectory()){
					parent = file.getAbsolutePath();
				} else if ("jar".equalsIgnoreCase(ext) || "zip".equalsIgnoreCase(ext)){
					// if is ajr or zip, use WILDCARD of java
					parent = file.getParent() + fileSeparator + "*";
				}
				// if parent == null means that no jar/zip and no folder
				if ((parent != null) && (!StringUtils.contains(classPath, parent))){
					if (i==0) {
						classPath = parent;
					} else { 
						classPath = classPath + pathSeparator + parent;
					}
				}
			}
		}
		return classPath;
	}
	
	/**
	 * Checks if the passed file is valid for classpath for process or not.
	 * 
	 * @param file file to check
	 * @param additionalFolders additional folder or null
	 * @return true is the file must be included in classpath, otherwise false
	 */
	private static boolean isToAdd(File file, String[] additionalFolders){
		// all directories are added
		if (file.isDirectory()){
			return true;
		}
		// all files starting with JEM 
		if (file.getName().startsWith("jem")){
			return true;
		}
		
		// gets the folder name
		String path = file.getParentFile().getName();
		// scans all default liberies if matches
		for (Libraries lib : Libraries.values()){
			if (path.equalsIgnoreCase(lib.getPath())){
				return true;
			}
		}
		// checks if there are additional folders to add
		if (additionalFolders != null){
			// scans additional folders
			for (int i=0; i<additionalFolders.length; i++){
				if (path.equalsIgnoreCase(additionalFolders[i])){
					return true;
				}
			}
		}
		return false;
	}
}