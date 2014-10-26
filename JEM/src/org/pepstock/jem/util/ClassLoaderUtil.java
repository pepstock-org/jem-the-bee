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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.configuration.AbstractPluginDefinition;
import org.pepstock.jem.node.configuration.ClassPath;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class ClassLoaderUtil {
	
	private static final String[] EXTENSIONS = new String[]{"jar"};
	
	private static final String ALL_FOLDER = "*";

	private static final String ALL_FOLDER_IN_CASCADE = "**";
	
	/**
	 * Empty to avoid instantiation
	 */
	private ClassLoaderUtil() {
		
	}
	
	/**
	 * 
	 * @param pluginDef
	 * @param props
	 * @return containers with object instatiated and classpath based on URLs
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static ObjectAndClassPathContainer loadAbstractPlugin(AbstractPluginDefinition pluginDef, Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		ObjectAndClassPathContainer result = new ObjectAndClassPathContainer();
		
		if (pluginDef.getClasspath() == null || pluginDef.getClasspath().isEmpty()){
			// load by Class.forName of factory
			result.setObject(Class.forName(pluginDef.getClassName()).newInstance());
		} else {
			Collection<File> files = new LinkedList<File>();
			for (ClassPath classPath : pluginDef.getClasspath()){
				String path = VariableSubstituter.substitute(classPath.getContent(), props);
				File file = new File(path);
				if (path.endsWith(ALL_FOLDER)){
					boolean cascade = path.endsWith(ALL_FOLDER_IN_CASCADE);
					File parent = file.getParentFile();
					Collection<File> newFiles = FileUtils.listFiles(parent, EXTENSIONS, cascade);
					files.addAll(newFiles);
					if (cascade){
						// scan all files to extract folder to add classpath
						for (File newFile : newFiles){
							String parentNormalized = newFile.getParentFile().getAbsolutePath() + File.separator + ALL_FOLDER;
							if (!result.getClassPath().contains(parentNormalized)){
								result.getClassPath().add(parentNormalized);
							}
						}
					} else {
						if (!result.getClassPath().contains(file.getAbsolutePath())){
							result.getClassPath().add(file.getAbsolutePath());
						}					
					}
				} else if (file.isDirectory() && file.exists()) {
					files.add(file);
					if (!result.getClassPath().contains(file.getAbsolutePath())){
						result.getClassPath().add(file.getAbsolutePath());
					}					
				} else if (file.isFile() && file.exists()){
					files.add(file);
					if (!result.getClassPath().contains(file.getAbsolutePath())){
						result.getClassPath().add(file.getAbsolutePath());
					}					
				}
			}
			if (!files.isEmpty()){
					URL[] urls = FileUtils.toURLs(files.toArray(new File[files.size()]));
					ReverseURLClassLoader loader = new ReverseURLClassLoader(urls, Main.class.getClassLoader());
					Class<?> clazz = loader.loadClass(pluginDef.getClassName());
					result.setObject(clazz.newInstance());
			} else {
				throw new IOException(UtilMessage.JEMB009E.toMessage().getMessage());
			}
		}
		return result;
	}

}
