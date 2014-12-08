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
package org.pepstock.jem.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.configuration.AbstractPluginDefinition;
import org.pepstock.jem.node.configuration.ClassPath;

/**
 * This utility needs to load Abstract plugins from a reverse URL classloader.<br>
 * To allow loading a plugin by custom classloader, it's mandatory to set the
 * CLASSPATH element in the plugin definition.<br>
 * The classpath could be set as following:<br>
 * <ul>
 * <li>[string]: could be a file (only JARs are considered) or a directory
 * <li>[string]/*: indicates the content of a folder will be part of classpath
 * (only jars)
 * <li>[string]/**: indicates the content of a folder and subfolders (in
 * cascade) will be part of classpath (only jars)
 * <ul>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class ClassLoaderUtil {

	/**
	 * Extensions files loaded for classpath
	 */
	public static final List<String> EXTENSIONS = Collections.unmodifiableList(Arrays.asList("jar"));

	/**
	 * Loads all files of a folder, without any cascading
	 */
	public static final String ALL_FOLDER = "*";

	/**
	 * Loads all files of a folder, with cascading on all subfolders
	 */
	public static final String ALL_FOLDER_IN_CASCADE = "**";

	/**
	 * Empty to avoid instantiation
	 */
	private ClassLoaderUtil() {

	}

	/**
	 * Loads all classpath information from plugin configuration and creates a
	 * custom classloader to load the plugin.
	 * 
	 * @param pluginDef plugin defintion
	 * @param props list of properties to used to substitute if necessary
	 * @return containers with object instantiated and class path based on URLs
	 * @throws InstantiationException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 * @throws ClassNotFoundException if any error occurs
	 * @throws IOException if any error occurs
	 */
	public static ObjectAndClassPathContainer loadAbstractPlugin(AbstractPluginDefinition pluginDef, Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		return loadAbstractPlugin(pluginDef, props, null);
	}
	
	/**
	 * Loads all classpath information from plugin configuration and creates a
	 * custom classloader to load the plugin.
	 * 
	 * @param pluginDef plugin defintion
	 * @param props list of properties to used to substitute if necessary
	 * @param knownLoader ClassLoader already created previously
	 * @return containers with object instantiated and class path based on URLs
	 * @throws InstantiationException if any error occurs
	 * @throws IllegalAccessException if any error occurs
	 * @throws ClassNotFoundException if any error occurs
	 * @throws IOException if any error occurs
	 */
	public static ObjectAndClassPathContainer loadAbstractPlugin(AbstractPluginDefinition pluginDef, Properties props, ClassLoader knownLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		// creates the result to return
		ObjectAndClassPathContainer result = new ObjectAndClassPathContainer();

		if (knownLoader != null) {
			// there already a classloader
			// loads the plugin from classloader
			Class<?> clazz = knownLoader.loadClass(pluginDef.getClassName());
			// sets the object
			result.setObject(clazz.newInstance());
			return result;
		} else if (pluginDef.getClasspath() == null || pluginDef.getClasspath().isEmpty()) { 
			// if plugin defintion doesn't have the classpath, that means that the
			// plugin is already placed in JEM classpath
			// therefore it's enough to call it
			// load by Class.forName of factory
			result.setObject(Class.forName(pluginDef.getClassName()).newInstance());
		} else {
			// CLASSPATH has been set therefore it an try to load the plugin by
			// a custom classloader
			// collection of all file of classpath
			Collection<File> files = new LinkedList<File>();
			// scans all strings of classpath
			for (ClassPath classPath : pluginDef.getClasspath()) {
				// substitute variables if there are
				String path = VariableSubstituter.substitute(classPath.getContent(), props);
				// creates the file
				File file = new File(path);
				// if file ends with * could be only this folder or all folders
				// in cascade
				if (path.endsWith(ALL_FOLDER)) {
					// checks if is all folders in cascade
					boolean cascade = path.endsWith(ALL_FOLDER_IN_CASCADE);
					// gets the parent and asks for all JAR files
					File parent = file.getParentFile();
					Collection<File> newFiles = FileUtils.listFiles(parent, EXTENSIONS.toArray(new String[0]), cascade);
					// loads to the collection
					files.addAll(newFiles);
					if (cascade) {
						// scan all files to extract folder to add classpath
						// with *, standard JAVA
						for (File newFile : newFiles) {
							String parentNormalized = newFile.getParentFile().getAbsolutePath() + File.separator + ALL_FOLDER;
							// if the path is not already in the result, load it
							if (!result.getClassPath().contains(parentNormalized)) {
								result.getClassPath().add(parentNormalized);
							}
						}
					} else {
						// loads all files
						if (!result.getClassPath().contains(file.getAbsolutePath())) {
							result.getClassPath().add(file.getAbsolutePath());
						}
					}
				} else if (file.isDirectory() && file.exists()) {
					// if here, we have a directory
					// adds the directory to collection
					files.add(file);
					if (!result.getClassPath().contains(file.getAbsolutePath())) {
						result.getClassPath().add(file.getAbsolutePath());
					}
				} else if (file.isFile() && file.exists()) {
					// if here, a file has been indicated
					// adds the directory to collection
					files.add(file);
					if (!result.getClassPath().contains(file.getAbsolutePath())) {
						result.getClassPath().add(file.getAbsolutePath());
					}
				}
			}
			// checks if the collection is empty.
			// if yes, all classpath definiton is wrong and no files have been
			// loaded
			if (!files.isEmpty()) {
				// exports files in URLs, for our classloader
				final URL[] urls = FileUtils.toURLs(files.toArray(new File[files.size()]));
				// loads a our classloader by access controller
				ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
					public ClassLoader run() {
						return new ReverseURLClassLoader(urls, Main.class.getClassLoader());
					}
				});
				// loads the plugin from classloader
				Class<?> clazz = loader.loadClass(pluginDef.getClassName());
				// sets the object
				result.setObject(clazz.newInstance());
				result.setLoader(loader);
			} else {
				throw new IOException(UtilMessage.JEMB009E.toMessage().getMessage());
			}
		}
		return result;
	}

}
